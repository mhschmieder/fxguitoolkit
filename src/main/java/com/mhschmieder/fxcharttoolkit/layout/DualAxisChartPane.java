/**
 * Copyright (c) 1996, 2020, Meyer Sound Laboratories Inc.
 * All rights reserved.
 *
 * MEYER SOUND PROVIDES THIS PROGRAM "AS IS" AND WITH ALL FAULTS. MEYER SOUND
 * SPECIFICALLY DISCLAIMS ANY IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS FOR
 * A PARTICULAR USE. MEYER SOUND LABORATORIES, INC. DOES NOT WARRANT THAT THE
 * OPERATION OF THE PROGRAM WILL BE UNINTERRUPTED OR ERROR FREE.
 *
 * This is UNPUBLISHED PROPRIETARY SOURCE CODE of Meyer Sound Laboratories,
 * Inc.; the contents of this file may not be disclosed to third parties, copied
 * or duplicated in any form, in whole or in part, without the prior written
 * permission of Meyer Sound Laboratories, Inc.
 *
 * RESTRICTED RIGHTS LEGEND: Use, duplication or disclosure by the U.S.
 * Government is subject to restrictions set forth in FAR 52.227.19(c)(2)
 * (Commercial Computer Software - Restricted Rights) or subparagraph (c)(1)(ii)
 * of the Rights in Technical Data and Computer Software clause at DFARS
 * 252.227-7013, and/or in similar or successor clauses in the FAR, DOD or NASA
 * FAR Supplement. Unpublished - rights reserved under the Copyright Laws of the
 * United States.
 */
package com.mhschmieder.fxcharttoolkit.layout;

import java.text.NumberFormat;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.fxcharttoolkit.chart.ChartUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxguitoolkit.control.ClickLocation;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * This is an abstract base class that enforces standards for all Dual Axis
 * Chart hosting panes, such as supplying a specific stylized Data Tracker and
 * the event model to handle it.
 */
public abstract class DualAxisChartPane extends StackPane {

    // Declare a generic XY Bottom Chart, so we can customize it on-the-fly.
    public ValueAxis< Number >        _xAxisBottom;
    public ValueAxis< Number >        _yAxisBottom;
    public XYChart< Number, Number >  _xyChartBottom;

    // Declare a generic XY Top Chart, so we can customize it on-the-fly.
    public ValueAxis< Number >        _xAxisTop;
    public ValueAxis< Number >        _yAxisTop;
    public XYChart< Number, Number >  _xyChartTop;

    // Declare unit labels for both axes, for data tracking.
    protected String                  _xUnitLabelShared;
    protected String                  _yUnitLabelBottom;
    protected String                  _yUnitLabelTop;

    // Declare the Data Tracking marker display as a Line, so can stylize it.
    protected Group                   _dataTrackingMarkerGroup;
    protected Line                    _dataTrackingMarker;

    // Declare the Data Tracking data display labels and containers.
    protected Group                   _dataTrackingLabelGroup;
    protected VBox                    _dataTrackingLabelBox;
    protected ObservableList< Label > _dataTrackingLabels;

    // Keep track of the color to be used for Data Tracking.
    protected Color                   _dataTrackerColor;

    // Cache the last Click Location so we can use it to update after data
    // loads from new predictions.
    protected ClickLocation           _clickLocation;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat            _numberFormat;

    // Cache the full Session Context (System Type, Locale, etc.).
    public SessionContext             _sessionContext;

    public DualAxisChartPane( final int maximumNumberOfDataSets,
                              final String xUnitLabelShared,
                              final String yUnitLabelBottom,
                              final String yUnitLabelTop,
                              final SessionContext sessionContext ) {
        // Always call the superclass constructor first!
        super();

        _xUnitLabelShared = xUnitLabelShared;
        _yUnitLabelBottom = yUnitLabelBottom;
        _yUnitLabelTop = yUnitLabelTop;

        _sessionContext = sessionContext;

        // Default Data Tracker to a gray tone that is in between ones that
        // might get chosen as the background color, so that it is unlikely to
        // be hidden against the background by accident at startup.
        _dataTrackerColor = ColorConstants.GRAY45;

        try {
            initPane( maximumNumberOfDataSets );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a data set to the bottom data series, along with setting its name.
     *
     * @param dataSetIndex
     *            The specific data set index of the data series to add
     */
    public final void addBottomDataSet( final int dataSetIndex ) {
        addDataSet( _xyChartBottom, dataSetIndex );
    }

    /**
     * Add a data set to the data series, along with setting its name.
     *
     * @param xyChart
     *            The top or bottom chart
     * @param dataSetIndex
     *            The specific data set index of the data series to add
     */
    private final void addDataSet( final XYChart< Number, Number > xyChart,
                                   final int dataSetIndex ) {
        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();

        // Avoid invalid index exceptions by exiting early if necessary.
        final int lastIndex = chartSeriesList.size() - 1;
        final int nextAvailableIndex = lastIndex + 1;
        if ( dataSetIndex > nextAvailableIndex ) {
            return;
        }

        // Create the series we need to show here, and set its label.
        final XYChart.Series< Number, Number > chartSeries = new XYChart.Series<>();
        final String dataSetName = getDataSetName( dataSetIndex );
        chartSeries.setName( dataSetName );
        chartSeriesList.add( dataSetIndex, chartSeries );
    }

    /**
     * Add a data set to the top data series, along with setting its name.
     *
     * @param dataSetIndex
     *            The specific data set index of the data series to add
     */
    public final void addTopDataSet( final int dataSetIndex ) {
        addDataSet( _xyChartTop, dataSetIndex );
    }

    protected final void adjustDataTrackingLocation( final ClickLocation clickLocation ) {
        // Update the data tracking marker location and "X" data point markers.
        // NOTE: The x-axis fudge factor accounts for the left-side and
        // right-side insets, splitting the difference for tight tracking.
        // TODO: Query the insets instead (checking for null reference first)?
        // TODO: Verify whether this corresponds to y-axis and label widths?
        // TODO: Draw the "X", but that needs to happen inside the data set
        // looper, so we need to have this path element reference available for
        // use inside that loop and to remember the x-axis tracker location.
        // NOTE: As the vertical line is centered in the shared layout parent,
        // mouse coordinates must subtract half the layout width to accommodate.
        // NOTE: Similarly, we need to adjust vertically by half the difference
        // between the overall layout height and the actual chart height.
        // NOTE: The vertical offset is now faked out, as it's too tricky to
        // figure out how to account for the optional title height.
        // TODO: Get this worked out better, even though it's perfect now, as
        // otherwise future changes could break it, and take note that the mid
        // position is exactly half of the height so cancels out that part.
        // NOTE: This will break if a chart has both a title and a legend.
        final int numberOfDataSets = _xyChartBottom.getData().size();
        final String title = _xyChartBottom.getTitle();
        final boolean hasTitle = ( title != null ) && !title.trim().isEmpty();
        final boolean legendVisible = _xyChartBottom.isLegendVisible();
        final Side legendSide = _xyChartBottom.getLegendSide();

        // NOTE: Instead of getWidth(), we might instead need to use
        // _dataTrackingLabelGroup.getLayoutBounds().getWidth() as with the
        // right edge checking in the clipping algorithm below this code?
        final double offsetX = -0.5d * getWidth();
        final double fudgeX = 10d;
        final double lineX = clickLocation.x + offsetX + fudgeX;
        _dataTrackingMarkerGroup.setTranslateX( lineX );

        final double midY = ( hasTitle || ( legendVisible && Side.TOP.equals( legendSide ) ) )
            ? _yAxisBottom.getDisplayPosition( 0.5d
                    * ( _yAxisBottom.getLowerBound() + _yAxisBottom.getUpperBound() ) )
            : _yAxisBottom.getDisplayPosition( _yAxisBottom.getUpperBound() );
        final double offsetY = ( hasTitle || ( legendVisible && Side.TOP.equals( legendSide ) ) )
            ? ( -0.5d * _yAxisBottom.getHeight() )
            : ( ( -0.5d * ( getHeight() - _yAxisBottom.getHeight() ) )
                    + _yAxisBottom.getLayoutY() );
        final double fudgeY = ( hasTitle || ( legendVisible && Side.TOP.equals( legendSide ) ) )
            ? ( numberOfDataSets > 8 ) ? hasTitle ? 3d : 4d : hasTitle ? -10d : -12d
            : 8d;
        final double lineY = midY + offsetY + fudgeY;
        _dataTrackingMarkerGroup.setTranslateY( lineY );
        if ( !_dataTrackingMarkerGroup.isVisible() ) {
            _dataTrackingMarkerGroup.setVisible( true );
        }

        // Keep the labels from clipping and from blocking the Data Tracker from
        // reaching the right edge of the chart, by placing them to the left of
        // the Data Tracker if they would clip, and to the right otherwise.
        final double rightEdgePx = _xAxisBottom.getDisplayPosition( _xAxisBottom.getUpperBound() )
                + _xAxisBottom.getLayoutX() + 4d;
        final boolean beyondRightEdge = ( clickLocation.x
                + _dataTrackingLabelGroup.getLayoutBounds().getMaxX()
                + _yAxisTop.getWidth() ) > rightEdgePx;
        final double labelsX = beyondRightEdge
            ? lineX - ( 0.55d * _dataTrackingLabelBox.getWidth() )
            : lineX + ( 0.55d * _dataTrackingLabelBox.getWidth() );
        final double labelsY = ( legendVisible && Side.TOP.equals( legendSide ) )
            ? 50d
            : hasTitle ? 0d : -10d;
        if ( beyondRightEdge ) {
            _dataTrackingLabelBox.setAlignment( Pos.CENTER_LEFT );
        }
        else {
            _dataTrackingLabelBox.setAlignment( Pos.CENTER );
        }
        _dataTrackingLabelGroup.setTranslateX( labelsX );
        _dataTrackingLabelGroup.setTranslateY( labelsY );
        if ( !_dataTrackingLabelGroup.isVisible() ) {
            _dataTrackingLabelGroup.setVisible( true );
        }
    }

    // Clear the chart of its data points in the specified data set index.
    protected final void clear( final XYChart< Number, Number > xyChart, final int dataSetIndex ) {
        if ( !ChartUtilities.isDataSetValid( xyChart, dataSetIndex ) ) {
            return;
        }

        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();
        final XYChart.Series< Number, Number > chartSeries = chartSeriesList.get( dataSetIndex );

        // TODO: Improve performance by treating hide/show as simple visibility
        // on/off rather than clearing the series and filling it again later on.
        // chartSeries.getNode().setVisible( false );

        final ObservableList< Data< Number, Number > > chartSeriesData = chartSeries.getData();
        chartSeriesData.clear();

        // Make sure to also clear the Data Tracking if there are no longer any
        // active data sets that are still valid.
        updateDataTracking();
    }

    // Clear the bottom chart of its data points in the specified data set
    // index.
    protected final void clearBottom( final int dataSetIndex ) {
        clear( _xyChartBottom, dataSetIndex );
    }

    // Clear the top chart of its data points in the specified data set index.
    protected final void clearTop( final int dataSetIndex ) {
        clear( _xyChartTop, dataSetIndex );
    }

    public final double[] getBottomDataSetXValues( final int dataSetIndex ) {
        return ChartUtilities.getDataSetXValues( _xyChartBottom, dataSetIndex );
    }

    public final double[] getBottomDataSetYValues( final int dataSetIndex ) {
        return ChartUtilities.getDataSetYValues( _xyChartBottom, dataSetIndex );
    }

    /**
     * This method attempts to find the closest available data point to the
     * x-axis value at the click location. Normally this is only computable by
     * the producer of the data sets, but it should be uniform across all data
     * shown in this chart. By default, we return NaN to flag "uncomputed".
     *
     * @param clickLocationXValue
     *            The x-axis value at the click location
     * @return The closest available data point, or NaN if not computable at
     *         this level
     */
    public Double getClosestDataPointToXValue( final double clickLocationXValue ) {
        return Double.NaN;
    }

    /**
     * Return the index in all data sets (uniform as all data sets use the exact
     * same resolution within a given chart), for the x-axis position that is
     * closest to the specified click location value.
     * <p>
     * This involves determining the x-axis value at the given position, which
     * is trivially solvable by reformulating the equation for unknown screen
     * position to solve instead for unknown data set value.
     *
     * @param clickLocation
     *            The @ClickLocation whose x-axis position we will search for in
     *            the data set.
     * @return The index in the specified data set for the closest x-axis
     *         position.
     */
    protected final int getDataPointIndexAtXPosition( final ClickLocation clickLocation ) {
        // Get the actual X-axis value, as that is uniform across all data sets
        // and can then be used to find the appropriate lookup index for the
        // Y-axis values.
        final double clickLocationXValue = getDataPointValueAtXPosition( clickLocation );

        final int dataPointIndexAtXValue = getDataPointIndexAtXValue( clickLocationXValue );

        return dataPointIndexAtXValue;
    }

    /**
     * Return the index in all data sets (uniform as all data sets use the exact
     * same resolution within a given chart), for the x-axis value that is
     * closest to the specified value. If the value is beyond the bounds of the
     * data set, return either the first or last index in the data set,
     * whichever is appropriate. If the data sets are all empty, return -1.
     *
     * @param xvalue
     *            The x-axis value to search for in the data sets.
     * @return The shared index in the data sets for the closest x-axis value.
     */
    protected final int getDataPointIndexAtXValue( final double xvalue ) {
        final ObservableList< Series< Number, Number > > chartSeriesList = _xyChartBottom.getData();
        for ( int dataSetIndex = 0, numberOfDataSets = chartSeriesList
                .size(); dataSetIndex < numberOfDataSets; dataSetIndex++ ) {
            final int dataPointIndex = getDataPointIndexAtXValue( dataSetIndex, xvalue );
            if ( dataPointIndex >= 0 ) {
                return dataPointIndex;
            }
        }

        // The default is to assume the click location was outside axis bounds.
        return -1;
    }

    /**
     * Return the index in the specified data set, for the x-axis value that is
     * closest to the specified value. If the value is beyond the bounds of the
     * data set, return either the first or last index in the data set,
     * whichever is appropriate. Check the argument to ensure that it is a valid
     * data set index. If it is less than zero, does not refer to an existing
     * data set, or the data set is empty, return -1 to indicate no index found.
     *
     * @param dataSetIndex
     *            The data set index.
     * @param xvalue
     *            The x-axis value to search for in the data set.
     * @return The index in the specified data set for the closest x-axis value.
     */
    protected final int getDataPointIndexAtXValue( final int dataSetIndex, final double xvalue ) {
        if ( !ChartUtilities.isDataSetValid( _xyChartBottom, dataSetIndex ) ) {
            return -1;
        }

        final ObservableList< Series< Number, Number > > chartSeriesList = _xyChartBottom.getData();

        final XYChart.Series< Number, Number > chartSeries = chartSeriesList.get( dataSetIndex );
        final ObservableList< Data< Number, Number > > chartSeriesData = chartSeries.getData();

        // Trivially check for when we are beyond or at the lower bound, and
        // return the lower bound index if so.
        final int firstIndex = 0;
        final Data< Number, Number > firstDataPoint = chartSeriesData.get( firstIndex );
        if ( ( xvalue - firstDataPoint.getXValue().doubleValue() ) <= 0d ) {
            return firstIndex;
        }

        // Trivially check for when we are beyond or at the upper bound, and
        // return the upper bound index if so.
        final int lastIndex = chartSeriesData.size() - 1;
        final Data< Number, Number > lastDataPoint = chartSeriesData.get( lastIndex );
        if ( ( xvalue - lastDataPoint.getXValue().doubleValue() ) >= 0d ) {
            return lastIndex;
        }

        // Loop on the full data set, and compare neighboring values for closest
        // match (or no match). Stop before the final index so that we always
        // have two legal indices to look up for legitimate data values.
        for ( int dataPointIndex = firstIndex; dataPointIndex < lastIndex; dataPointIndex++ ) {
            final int dataPointIndex1 = dataPointIndex;
            final Data< Number, Number > dataPoint1 = chartSeriesData.get( dataPointIndex1 );
            final double diff1 = xvalue - dataPoint1.getXValue().doubleValue();

            final int dataPointIndex2 = dataPointIndex + 1;
            final Data< Number, Number > dataPoint2 = chartSeriesData.get( dataPointIndex2 );
            final double diff2 = dataPoint2.getXValue().doubleValue() - xvalue;

            if ( ( diff1 >= 0d ) && ( diff2 >= 0d ) ) {
                return ( diff1 <= diff2 ) ? dataPointIndex1 : dataPointIndex2;
            }
        }

        return -1;
    }

    /**
     * Return the value in all data sets (uniform as all data sets use the exact
     * same resolution within a given chart), for the x-axis position that is
     * closest to the specified click location value.
     *
     * @param clickLocation
     *            The @ClickLocation whose x-axis position we will search for in
     *            the data set.
     * @return The value in the specified data set for the closest x-axis
     *         position.
     */
    protected final double getDataPointValueAtXPosition( final ClickLocation clickLocation ) {
        // Get the click location in terms of its offset from the lower bound of
        // the X-axis. We also fudge a bit to compensate for later rounding.
        final double displayPositionOnAxis = getDisplayPositionOnXAxis( clickLocation );

        // Get the actual X-axis value, as that is uniform across all data sets
        // and can then be used to find the appropriate lookup index for the
        // Y-axis values.
        final double clickLocationXValue = getDataPointValueAtXPosition( displayPositionOnAxis );

        return clickLocationXValue;
    }

    /**
     * Return the value in all data sets (uniform as all data sets use the exact
     * same resolution within a given chart), for the x-axis position that is
     * closest to the specified click location value.
     *
     * @param displayPositionOnAxis
     *            The x-axis display position that we will search for in the
     *            data set.
     * @return The value in the specified data set for the closest x-axis
     *         position.
     */
    protected double getDataPointValueAtXPosition( final double displayPositionOnAxis ) {
        // Get the actual X-axis value, as that is uniform across all data sets
        // and can then be used to find the appropriate lookup index for the
        // Y-axis values.
        final double clickLocationXValue = _xAxisBottom.getValueForDisplay( displayPositionOnAxis )
                .doubleValue();

        return clickLocationXValue;
    }

    // In order to have proper legend labels, data sets must be named.
    public abstract String getDataSetName( final int dataSetIndex );

    /**
     * Return the display position along the x-axis that is closest to the
     * specified click location value.
     *
     * @param clickLocation
     *            The @ClickLocation whose x-axis position we will search for in
     *            the data set.
     * @return The closest x-axis position to the click location.
     */
    protected final double getDisplayPositionOnXAxis( final ClickLocation clickLocation ) {
        // Get the click location in terms of its offset from the lower bound of
        // the X-axis. We also fudge a bit to compensate for later rounding.
        // NOTE: The compensation factor might actually relate to the insets?
        final double displayPositionOnAxis = clickLocation.x - _xAxisBottom.getLayoutX() - 4d;

        return displayPositionOnAxis;
    }

    public final double[] getTopDataSetXValues( final int dataSetIndex ) {
        return ChartUtilities.getDataSetXValues( _xyChartTop, dataSetIndex );
    }

    public final double[] getTopDataSetYValues( final int dataSetIndex ) {
        return ChartUtilities.getDataSetYValues( _xyChartTop, dataSetIndex );
    }

    /**
     * This method initializes the Line Chart shared by all subclasses, so
     * must be invoked after the axes have been instantiated. Only the axes
     * differ by subclass, and only the subclasses need to know legend side.
     */
    protected void initChart() {
        // Construct an initially empty data tracking marker, so that it can be
        // placed in a layout container without having Path Elements yet.
        // NOTE: We fudge the height a bit to account for the thickness of the
        // Axis bounds, but it is better to provide a clipping bounds instead.
        _dataTrackingMarker = new Line();
        _dataTrackingMarker.setStartX( 0d );
        _dataTrackingMarker.startYProperty()
                .bind( _yAxisBottom.heightProperty().negate().divide( 2d ).add( 1d ) );
        _dataTrackingMarker.setEndX( 0d );
        _dataTrackingMarker.endYProperty()
                .bind( _yAxisBottom.heightProperty().divide( 2d ).subtract( 1d ) );

        _dataTrackingMarker.setStroke( _dataTrackerColor );
        _dataTrackingMarker.setStrokeWidth( 2d );
        _dataTrackingMarker.getStrokeDashArray().addAll( 5d, 5d );

        // Hide the Data Tracking Marker Group until it is needed.
        _dataTrackingMarkerGroup = new Group( _dataTrackingMarker );
        _dataTrackingMarkerGroup.setVisible( false );

        // Build an initially empty multi-label Data Tracking layout pane.
        _dataTrackingLabelBox = new VBox();
        _dataTrackingLabelBox.getChildren().setAll( _dataTrackingLabels );
        _dataTrackingLabelBox.setAlignment( Pos.CENTER );

        // Hide the Data Tracking Label Group until it is needed.
        _dataTrackingLabelGroup = new Group( _dataTrackingLabelBox );
        _dataTrackingLabelGroup.setVisible( false );

        // Make sure to set the top chart's y-axis on the right side, so it
        // doesn't overpaint the bottom chart's y-axis and so that the ticks and
        // labels are to the right vs. the left of the axis itself.
        // NOTE: This unfortunately decouples the top and bottom charts so that
        // they are no longer co-located on-screen, so it is backed out for now.
        // _yAxisTop.setSide( Side.RIGHT );

        // Make sure there is room for the dual y-axis on the right.
        setPadding( new Insets( 2d, 12d, 2d, 6d ) );

        // Center the grid for the most balanced layout.
        // :NOTE: The invoker must specify the paint order for the two charts,
        // as it affects what data might get obscured or hidden.
        final ObservableList< Node > layoutNodes = getChildren();
        layoutNodes.addAll( _xyChartBottom,
                            _xyChartTop,
                            _dataTrackingMarkerGroup,
                            _dataTrackingLabelGroup );
        setAlignment( this, Pos.CENTER );

        // Make sure both axis pairs are positioned at the same location.
        // NOTE: We try to get the top chart to show its y-axis on the right,
        // and its x-axis on the top, without actually setting them to those
        // sides, so that things line up consistently within the chart content.
        // NOTE: To avoid perceived line thickening of the top border due to
        // the overlay of a grid line with an unmarked dual x-axis, we subtract
        // one pixel from the dual x-axis's vertical positioning.
        _xAxisTop.translateXProperty().bind( _xAxisBottom.translateXProperty() );
        _xAxisTop.translateYProperty().bind( _xAxisBottom.translateYProperty()
                .subtract( _yAxisBottom.heightProperty().subtract( 1d ) ) );
        _yAxisTop.translateXProperty()
                .bind( _xAxisBottom.translateXProperty().add( _xAxisBottom.widthProperty() ) );
        _yAxisTop.translateYProperty().bind( _yAxisBottom.translateYProperty() );

        // Also make sure that both axis pairs are the exact same size.
        _xAxisTop.prefWidthProperty().bind( _xAxisBottom.widthProperty() );
        _xAxisTop.prefHeightProperty().bind( _xAxisBottom.heightProperty() );
        _yAxisTop.prefWidthProperty().bind( _yAxisBottom.widthProperty() );
        _yAxisTop.prefHeightProperty().bind( _yAxisBottom.heightProperty() );
    }

    private final void initPane( final int maximumNumberOfDataSets ) {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( _sessionContext.locale );

        // Set the precision for floating-point text formatting.
        _numberFormat.setMinimumFractionDigits( 0 );
        _numberFormat.setMaximumFractionDigits( 2 );

        // Build the data tracking labels now, while we still know the maximum
        // number of data sets.
        _dataTrackingLabels = FXCollections.observableArrayList();
        for ( int i = 0; i < maximumNumberOfDataSets; i++ ) {
            final Label label = new Label();
            _dataTrackingLabels.add( label );
        }
    }

    public final boolean isAnimateChartUpdates() {
        return _xyChartBottom.getAnimated();
    }

    public final void setAnimateChartUpdates( final boolean animateChartUpdates ) {
        _xyChartBottom.setAnimated( animateChartUpdates );
        _xyChartTop.setAnimated( animateChartUpdates );
    }

    protected final void setBottomDataSet( final int dataSetIndex,
                                           final XYChart.Series< Number, Number > dataSet ) {
        setDataSet( _xyChartBottom, dataSetIndex, dataSet );
    }

    // NOTE: Only the derived classes know which data set indices are in use.
    protected abstract void setDataSet( final XYChart< Number, Number > xyChart,
                                        final int dataSetIndex );

    // NOTE: Derived classes might need different strategies, as the chart type
    // is not enforced at this level; only the axes types.
    public abstract void setDataSet( final XYChart< Number, Number > xyChart,
                                     final int dataSetIndex,
                                     final double x[],
                                     final double y[],
                                     final int firstIndex,
                                     final int lastIndex );

    protected final void setDataSet( final XYChart< Number, Number > xyChart,
                                     final int dataSetIndex,
                                     final XYChart.Series< Number, Number > dataSet ) {
        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();

        // Avoid invalid index exceptions by exiting early if necessary.
        final int lastIndex = chartSeriesList.size() - 1;
        final int nextAvailableIndex = lastIndex + 1;
        if ( dataSetIndex > nextAvailableIndex ) {
            return;
        }

        // Replace the series as a bulk operation, to avoid excessive
        // callbacks on observable lists at the level of the visible GUI.
        chartSeriesList.set( dataSetIndex, dataSet );

        // Make sure to also update the data tracking in case this data set was
        // being tracked and needs its displayed values to be updated.
        updateDataTracking();
    }

    public final void setDataTrackerColor( final Color dataTrackerColor ) {
        // Cache the new Data Tracker Color.
        _dataTrackerColor = dataTrackerColor;

        // Also update the color for the Data Tracker Marker and Labels.
        _dataTrackingMarker.setStroke( _dataTrackerColor );
        for ( final Label dataTrackingLabel : _dataTrackingLabels ) {
            dataTrackingLabel.setTextFill( _dataTrackerColor );
        }
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        _xyChartBottom.setBackground( background );
        _xAxisBottom.setBackground( background );
        _yAxisBottom.setBackground( background );
    }

    public final void setLegendSide( final Side legendSide ) {
        // NOTE: If we don't set the legend side on both charts, their sizes
        // and positions don't sync properly. Check for visibility though.
        final boolean hasLegend = legendSide != null;
        if ( hasLegend ) {
            _xyChartBottom.setLegendSide( legendSide );
            _xyChartTop.setLegendSide( legendSide );
        }
        _xyChartBottom.setLegendVisible( hasLegend );
        _xyChartTop.setLegendVisible( hasLegend );

        // Update the data tracking marker location and "X" data point markers.
        // NOTE: This is run on a deferred thread, as the chart needs some time
        // to internally update everything affected by changing the legend side.
        // NOTE: That helped but not quite enough, so now we run it twice.
        if ( _clickLocation != null ) {
            Platform.runLater( () -> {
                updateDataTracking( _clickLocation, false );
                updateDataTracking( _clickLocation, false );
            } );
        }
    }

    protected final void setTopDataSet( final int dataSetIndex,
                                        final XYChart.Series< Number, Number > dataSet ) {
        setDataSet( _xyChartTop, dataSetIndex, dataSet );
    }

    /**
     * Update the X/Y data tracking from the cached cursor coordinates.
     */
    public final void updateDataTracking() {
        // Only update if the data tracking is currently active/visible.
        if ( _dataTrackingMarkerGroup.isVisible() ) {
            updateDataTracking( _clickLocation, false );
        }
    }

    /**
     * Update the X/Y data tracking from the supplied cursor coordinates.
     *
     * @param clickLocation
     *            Cursor location in pixels, from the top of the X/Y Chart
     * @param mouseClicked
     *            Flag for whether the mouse was clicked or dragged
     */
    @SuppressWarnings("nls")
    public final void updateDataTracking( final ClickLocation clickLocation,
                                          final boolean mouseClicked ) {
        // If the click location is not present, the calling context may not
        // know that status and thus it is safer to exit early here rather than
        // have every caller add complex logic.
        if ( clickLocation == null ) {
            return;
        }

        // Check for all-empty data sets within both of the charts.
        final boolean allEmptyBottomData = ChartUtilities.isChartEmpty( _xyChartBottom );
        final boolean allEmptyTopData = ChartUtilities.isChartEmpty( _xyChartTop );
        if ( allEmptyBottomData && allEmptyTopData ) {
            // Turn off the Data Tracker if all data sets are empty.
            _dataTrackingMarkerGroup.setVisible( false );
            _dataTrackingLabelGroup.setVisible( false );
            return;
        }

        // Cache the current click location in case we need to update the data
        // values after a change to the underlying data sets while the data
        // tracker is already active but not being dragged.
        // TODO: Determine whether this should happen after the final early
        // exit criteria have been examined.
        _clickLocation = new ClickLocation( clickLocation );

        // Do not update the Data Tracker if we are outside the axis bounds.
        // NOTE: This is not quite an exact match, but is the closest we've
        // come yet, with the main thing being that layout bounds are slightly
        // unstable by a few pixels now and then; helped by the fudge factor.
        final double leftEdgePx = _xAxisBottom.getDisplayPosition( _xAxisBottom.getLowerBound() )
                + _xAxisBottom.getLayoutX() + 4d;
        final double rightEdgePx = _xAxisBottom.getDisplayPosition( _xAxisBottom.getUpperBound() )
                + _xAxisBottom.getLayoutX() + 4d;
        if ( ( clickLocation.x < leftEdgePx ) || ( clickLocation.x > rightEdgePx ) ) {
            // If the user actively clicked outside the chart, remove tracking.
            if ( mouseClicked ) {
                _dataTrackingMarkerGroup.setVisible( false );
                _dataTrackingLabelGroup.setVisible( false );
            }

            return;
        }

        int dataTrackingLabelIndex = 0;
        boolean validData = false;

        // NOTE: It is unlikely we can get a null reference here.
        final ObservableList< Series< Number, Number > > chartSeriesBottomList =
                                                                               ( _xyChartBottom != null )
                                                                                   ? _xyChartBottom
                                                                                           .getData()
                                                                                   : null;
        if ( chartSeriesBottomList != null ) {
            // Get the actual x-axis value, as that is uniform across all data
            // sets and can then be used to find the appropriate lookup index
            // for the Y-axis values.
            final double clickLocationXValue = getDataPointValueAtXPosition( clickLocation );

            // Get the closest x-axis value in the actual data sets (uniform).
            final Double closestXValue = getClosestDataPointToXValue( clickLocationXValue );

            // Get the data point index closest to the x-axis click location.
            final int dataPointIndexAtXValue = getDataPointIndexAtXValue( closestXValue );

            final int numberOfBottomDataSets = chartSeriesBottomList.size();
            final int firstBottomDatasetIndex = 0;
            final int lastBottomDataSetIndex = numberOfBottomDataSets - 1;

            final ObservableList< Series< Number, Number > > chartSeriesTopList =
                                                                                ( _xyChartTop != null )
                                                                                    ? _xyChartTop
                                                                                            .getData()
                                                                                    : null;
            final int numberOfTopDataSets = ( chartSeriesTopList != null )
                ? chartSeriesTopList.size()
                : 0;
            final int lastTopDataSetIndex = numberOfTopDataSets - 1;

            // For all valid Data Sets, show the data values at the Data Set
            // index closest to the x-axis click location, starting from a clean
            // slate.
            // TODO: Make a method for this, once the details are implemented
            // and fully verified as correct and in the right presentation
            // style.
            // NOTE: We separately track the current Data Tracking label index,
            // to make sure we only use vertically consecutive labels vs.
            // leaving gaps.
            // NOTE: We also separately track the Data Set index, as we use an
            // iterator form of the "for" loop to cycle the data sets.
            for ( int dataSetIndex =
                                   firstBottomDatasetIndex; dataSetIndex <= lastBottomDataSetIndex; dataSetIndex++ ) {
                final XYChart.Series< Number, Number > chartSeriesBottom = chartSeriesBottomList
                        .get( dataSetIndex );
                if ( chartSeriesBottom == null ) {
                    continue;
                }

                // Check for missing or empty data, or data overruns from index.
                // TODO: Find out how to exclude hidden data sets.
                final ObservableList< Data< Number, Number > > chartSeriesBottomData =
                                                                                     chartSeriesBottom
                                                                                             .getData();
                if ( ( chartSeriesBottomData == null ) || chartSeriesBottomData.isEmpty()
                        || ( dataPointIndexAtXValue >= chartSeriesBottomData.size() ) ) {
                    continue;
                }

                // Mark that we have valid data for at least one trace.
                validData = true;

                // Add a new line in the text area for this data point, by name.
                // NOTE: Unfortunately, as we can have holes in the data groups
                // for unused chart indices, the label list might sometimes be
                // smaller than the chart series list. Therefore we check
                // against list size.
                if ( dataTrackingLabelIndex < _dataTrackingLabels.size() ) {
                    final Data< Number, Number > chartSeriesBottomDataPoint = chartSeriesBottomData
                            .get( dataPointIndexAtXValue );
                    final XYChart.Series< Number, Number > chartSeriesTop =
                                                                          ( dataSetIndex <= lastTopDataSetIndex )
                                                                              ? ( chartSeriesTopList != null )
                                                                                  ? chartSeriesTopList
                                                                                          .get( dataSetIndex )
                                                                                  : null
                                                                              : null;
                    final ObservableList< Data< Number, Number > > chartSeriesTopData =
                                                                                      ( chartSeriesTop != null )
                                                                                          ? chartSeriesTop
                                                                                                  .getData()
                                                                                          : null;
                    final Data< Number, Number > chartSeriesTopDataPoint =
                                                                         ( ( chartSeriesTopData != null )
                                                                                 && !chartSeriesTopData
                                                                                         .isEmpty()
                                                                                 && ( dataPointIndexAtXValue < chartSeriesTopData
                                                                                         .size() ) )
                                                                                             ? chartSeriesTopData
                                                                                                     .get( dataPointIndexAtXValue )
                                                                                             : null;

                    final double xValueShared = Double.isFinite( closestXValue )
                        ? closestXValue
                        : chartSeriesBottomDataPoint.getXValue().doubleValue();
                    final double yValueBottom =
                                              chartSeriesBottomDataPoint.getYValue().doubleValue();
                    final double yValueTop = ( chartSeriesTopDataPoint != null )
                        ? chartSeriesTopDataPoint.getYValue().doubleValue()
                        : Double.NaN;

                    final String formattedDataPoint = ChartUtilities
                            .getFormattedDataPoint( chartSeriesBottom,
                                                    xValueShared,
                                                    yValueBottom,
                                                    yValueTop,
                                                    _numberFormat,
                                                    _numberFormat,
                                                    _numberFormat,
                                                    _xUnitLabelShared,
                                                    _yUnitLabelBottom,
                                                    _yUnitLabelTop );
                    final Label dataTrackingLabel = _dataTrackingLabels
                            .get( dataTrackingLabelIndex++ );
                    dataTrackingLabel.setText( formattedDataPoint );

                    // Use the CSS tag to lookup the text fill color per data
                    // set.
                    // NOTE: Commented out, as the stroke color settings for
                    // the referenced CSS tags do not appear to affect text fill
                    // color, and this means we would have to resort to the
                    // Reflection API for CSS attributes, which is non-trivial
                    // so should be deferred for now.
                    // final String lineChartColorTag =
                    // ".chart-series-line.series" + String.valueOf(
                    // dataSetIndex );
                    // final String areaChartColorTag =
                    // ".chart-series-area-line.series" + String.valueOf(
                    // dataSetIndex );
                    // dataTrackingLabel.getStyleClass().addAll(
                    // lineChartColorTag, areaChartColorTag );
                }
            }
        }

        // Now clear out any unused labels.
        for ( int i = dataTrackingLabelIndex, numberOfDataTrackingLabels = _dataTrackingLabels
                .size(); i < numberOfDataTrackingLabels; i++ ) {
            final Label dataTrackingLabel = _dataTrackingLabels.get( i );
            dataTrackingLabel.setText( "" );
        }

        // If no valid data, turn off the Data Tracker and exit immediately.
        if ( !validData ) {
            _dataTrackingMarkerGroup.setVisible( false );
            _dataTrackingLabelGroup.setVisible( false );
            return;
        }

        // Update the data tracking marker location and "X" data point markers.
        adjustDataTrackingLocation( clickLocation );
    }

}
