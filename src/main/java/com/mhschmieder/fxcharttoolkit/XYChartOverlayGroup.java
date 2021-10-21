/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxcharttoolkit;

import java.util.ArrayList;
import java.util.List;

import com.mhschmieder.fxcharttoolkit.chart.CartesianAxis;
import com.mhschmieder.fxguitoolkit.GridResolution;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * This is a wrapper collection for Nodes that emulate the contents of XYCharts
 * in the FX Charts API, in situations where we can't use the stock
 * implementation, such as in Cartesian Space plotting where there's no data and
 * only Graphical Objects.
 * <p>
 * This node in the scene graph does what scene graphs are supposed to do:
 * encapsulates the transform from screen to model coordinates so that all child
 * nodes and their children may operate in model space.
 */
public class XYChartOverlayGroup extends ChartContentGroup {

    /*
     * This is <em>ONLY</em> used (by
     * {@link #_displayToVenueScaleFactorBinding}) when
     * {@link #_zoomBoxMeters} is unset or in other edge cases where a fallback
     * is needed. This default effectively sets 16 user distance units to 1
     * pixel.
     */
    public static final double  DEFAULT_DISPLAY_TO_VENUE_SCALE_FACTOR = 1d / 16d;

    /**
     * Grid Line opacity level is defined a constant, in case we switch to a
     * computed ratio and in case we provide programmatic support for changing
     * its value (this then gives us our defined default value).
     */
    private static final double GRID_LINE_OPACITY_DEFAULT             = 0.825d;

    /**
     * Regardless of whether an image is loaded, plots that can host background
     * images must take different tactics towards grid color than using the
     * background color.
     */
    public static final Color   GRID_COLOR_DEFAULT                    = Color.LIGHTGRAY;

    private final Line          _horizontalZeroLine;
    private final Line          _verticalZeroLine;
    private final Group         _horizontalGridLines;
    private final Group         _verticalGridLines;
    private final Line          _topAxis;
    private final Line          _rightAxis;

    /**
     * The current venue-to-display scale factor for the Cartesian Chart host.
     */
    private double              _venueToDisplayScaleFactor;

    /** @serial Whether to draw the axis zero lines. */
    private boolean             _showAxisZeroLines;

    /** @serial Whether to draw a background grid. */
    private boolean             _gridOn;

    /** @serial The scale applied to the auto-tic generated grid resolution. */
    private double              _gridScale;

    /**
     * Declare variable to keep track of the current grid resolution.
     * <p>
     * :TODO: Eliminate this in favor of changing the tic mark spacing.
     */
    private GridResolution      _gridResolution;

    /** @serial Color of the grid lines. */
    private Color               _gridColor;

    /** Cache the current foreground, to apply to volatile features. */
    protected Color             _foreColor;

    /**
     * This is the main constructor.
     */
    public XYChartOverlayGroup() {
        // Always call the superclass constructor first!
        super();

        _horizontalZeroLine = new Line();
        _verticalZeroLine = new Line();

        _horizontalGridLines = new Group();
        _verticalGridLines = new Group();

        _topAxis = new Line();
        _rightAxis = new Line();

        _topAxis.setVisible( false );
        _rightAxis.setVisible( false );

        _venueToDisplayScaleFactor = 1d / DEFAULT_DISPLAY_TO_VENUE_SCALE_FACTOR;

        _showAxisZeroLines = true;

        _gridOn = true;
        _gridScale = 1d;
        _gridResolution = GridResolution.defaultValue();
        _gridColor = GRID_COLOR_DEFAULT;
        _foreColor = Color.GRAY;

        // Set up the desired opacity on elements that are semi-transparent, as
        // this only needs to be done once at startup.
        _horizontalGridLines.setOpacity( GRID_LINE_OPACITY_DEFAULT );
        _verticalGridLines.setOpacity( GRID_LINE_OPACITY_DEFAULT );
        _horizontalZeroLine.setOpacity( GRID_LINE_OPACITY_DEFAULT );
        _verticalZeroLine.setOpacity( GRID_LINE_OPACITY_DEFAULT );

        try {
            initialize();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    protected final void addGridLine( final Number tickValue,
                                      final CartesianAxis otherAxis,
                                      final Orientation gridLineOrientation,
                                      final double strokeWidth,
                                      final Double[] dashArray,
                                      final List< Line > gridLineElements ) {
        final double tickValueDouble = tickValue.doubleValue();

        // Make sure this isn't the Zero Line, if it is set to be visible.
        if ( !_showAxisZeroLines || ( tickValueDouble != 0d ) ) {
            final Line gridLine = new Line();
            switch ( gridLineOrientation ) {
            case HORIZONTAL:
                gridLine.setStartX( otherAxis.getLowerBound() );
                gridLine.setStartY( tickValueDouble );
                gridLine.setEndX( otherAxis.getUpperBound() );
                gridLine.setEndY( tickValueDouble );
                break;
            case VERTICAL:
                gridLine.setStartX( tickValueDouble );
                gridLine.setStartY( otherAxis.getLowerBound() );
                gridLine.setEndX( tickValueDouble );
                gridLine.setEndY( otherAxis.getUpperBound() );
                break;
            default:
                break;
            }

            gridLine.setStroke( _gridColor );
            gridLine.setStrokeWidth( strokeWidth );
            gridLine.setStrokeLineCap( StrokeLineCap.BUTT );
            gridLine.getStrokeDashArray().setAll( dashArray );
            gridLineElements.add( gridLine );
        }
    }

    /**
     * The value of the current venue-to-display scale factor.
     *
     * @return The inverse of the last value used to create the display-to-venue
     *         scale transform that is the primary function of this node.
     */
    public final double getDisplayToVenueScaleFactor() {
        final double venueToDisplayScaleFactor = getVenueToDisplayScaleFactor();
        final double displayToVenueScaleFactor = ( venueToDisplayScaleFactor <= 0d )
            ? 1d
            : 1d / venueToDisplayScaleFactor;
        return displayToVenueScaleFactor;
    }

    /**
     * Function to get the display-to-venue transform, via the inverse of the
     * cached venue-to-display transform.
     * <p>
     * NOTE: This method is no longer used or needed.
     *
     * @return The inverse of {@link #getVenueToDisplayTransform()}.
     */
    public final Transform getDisplayToVenueTransform() {
        try {
            return getVenueToDisplayTransform().createInverse();
        }
        catch ( final NonInvertibleTransformException nite ) {
            // We have to be able to go both ways. If not, this is a serious
            // bug. This NonInvertibleTransformException must never be
            // encountered in production code.
            throw new RuntimeException( nite );
        }
    }

    /**
     * Return the grid color.
     *
     * @return The grid color.
     */
    public final Color getGridColor() {
        return _gridColor;
    }

    /**
     * This is a standard getter method for the Grid Resolution setting.
     *
     * @return The current Grid Resolution setting
     */
    public final GridResolution getGridResolution() {
        return _gridResolution;
    }

    /**
     * Return the grid scale.
     *
     * @return The grid scale.
     */
    public final double getGridScale() {
        return _gridScale;
    }

    /**
     * The scale of the current venue-to-display scale factor.
     *
     * @return The last value used to create the venue-to-display scale
     *         transform that is the primary function of this node.
     */
    public final double getVenueToDisplayScaleFactor() {
        return _venueToDisplayScaleFactor;
    }

    /**
     * Set up the configuration of the Chart Overlay Group.
     */
    private final void initialize() {
        final ObservableList< Node > nodes = getChildren();
        nodes.addAll( _verticalGridLines,
                      _horizontalGridLines,
                      _verticalZeroLine,
                      _horizontalZeroLine,
                      _rightAxis,
                      _topAxis );

        // Initialize the persistent shared attributes of the Chart Overlay
        // Group, which is application managed and is not directly interactive
        // at this time.
        GuiUtilities.initDecoratorNodeGroup( this );
    }

    /**
     * Return whether the grid is drawn.
     *
     * @return True if a grid is drawn.
     */
    public final boolean isGridOn() {
        return _gridOn;
    }

    /**
     * Return whether the axis zero lines are drawn.
     *
     * @return True if the axis zero lines are drawn.
     */
    public final boolean isShowAxisZeroLines() {
        return _showAxisZeroLines;
    }

    /*
     * Setup vertical and horizontal lines, based on major tick spacing.
     *
     * @see javafx.scene.chart.XYChart#layoutChartChildren(double, double,
     *      double, double)
     */
    public final void recalculateGridLines( final Region region,
                                            final CartesianAxis xAxis,
                                            final CartesianAxis yAxis ) {
        final double top = region.snappedTopInset();
        final double left = region.snappedLeftInset();
        final double bottom = region.snappedBottomInset();
        final double right = region.snappedRightInset();
        final double width = region.getWidth();
        final double height = region.getHeight();

        final double contentWidth = width - ( left + right );
        final double contentHeight = height - ( top + bottom );

        final boolean snapToPixel = region.isSnapToPixel();

        // Snap top and left to pixels.
        final double topSnap = snapToPixel ? Math.round( top ) : top;
        final double leftSnap = snapToPixel ? Math.round( left ) : left;

        // Snap content width and content height to pixels.
        final double contentWidthSnap = snapToPixel ? Math.ceil( contentWidth ) : contentWidth;
        final double contentHeightSnap = snapToPixel ? Math.ceil( contentHeight ) : contentHeight;

        // Try and work out width and height of axes, making four passes.
        // NOTE: The multiple passes to stabilize, are probably no longer
        // needed, as we no longer make any dynamic calls in this loop.
        final double xAxisWidth = Math.max( 0d, contentWidthSnap );
        double xAxisHeight = 0d;
        double yAxisWidth = 0d;
        final double yAxisHeight = Math.max( 0d, contentHeightSnap );
        for ( int count = 0; count < 4; count++ ) {
            final double newYAxisWidth = yAxis.prefWidth( yAxisHeight );
            final double newXAxisHeight = xAxis.prefHeight( xAxisWidth );
            if ( ( newYAxisWidth == yAxisWidth ) && ( newXAxisHeight == xAxisHeight ) ) {
                break;
            }

            yAxisWidth = newYAxisWidth;
            xAxisHeight = newXAxisHeight;
        }

        // Round axis sizes up to whole integers to snap to pixel.
        final double xAxisWidthSnap = Math.ceil( xAxisWidth );
        final double xAxisHeightSnap = Math.ceil( xAxisHeight );
        final double yAxisWidthSnap = Math.ceil( yAxisWidth );
        final double yAxisHeightSnap = Math.ceil( yAxisHeight );

        // Calculate X-axis height.
        xAxis.setVisible( true );
        final double xAxisY = topSnap + yAxisHeightSnap;
        final double xAxisX = leftSnap + yAxisWidthSnap;

        // Calculate Y-axis width.
        yAxis.setVisible( true );
        final double yAxisX = leftSnap + 1;
        final double yAxisY = topSnap - 1;

        // Resize axes.
        xAxis.resizeRelocate( xAxisX, xAxisY, xAxisWidthSnap, xAxisHeightSnap );
        yAxis.resizeRelocate( yAxisX, yAxisY, yAxisWidthSnap, yAxisHeightSnap );

        // When the chart is resized, need to specifically call out the axes
        // to lay out as they are unmanaged.
        xAxis.requestAxisLayout();
        xAxis.layout();
        yAxis.requestAxisLayout();
        yAxis.layout();

        // Although this only affects auto-sized Groups (which ours are not), it
        // is safer to go ahead and request the content of each Group to
        // re-layout anyway, in case of future changes to the layout scheme.
        layoutChildren();

        // NOTE: The scale factor seems the inverse of what is expected, so
        // there may be an incorrect scale factor inversion applied elsewhere.
        // It seems that the usage of this variable is in pixels vs. user
        // Distance Units and thus should need to be inverted vs. the cached
        // scale factor.
        final double displayToVenueScaleFactor = 1d / _venueToDisplayScaleFactor;
        final double strokeWidth = 0.75d * displayToVenueScaleFactor;
        final Double[] dashArray = new Double[] {
                                                  0.75d * displayToVenueScaleFactor,
                                                  1.5d * displayToVenueScaleFactor };

        final double clipHeight = yAxisHeightSnap * displayToVenueScaleFactor;
        final double clipWidth = xAxisWidthSnap * displayToVenueScaleFactor;

        // Update the clipping rectangle for the Chart Overlay Group.
        // NOTE: Both width and height have to account for stroke width.
        final double fudgeFactor = strokeWidth;
        _clippingRectangle.setX( leftSnap );
        _clippingRectangle.setY( topSnap );
        _clippingRectangle.setWidth( clipWidth + fudgeFactor );
        _clippingRectangle.setHeight( clipHeight + fudgeFactor );

        // Update the vertical zero line, in model space.
        updateZeroLine( xAxis, yAxis, _verticalZeroLine, Orientation.VERTICAL, strokeWidth );

        // Update the horizontal zero line, in model space.
        updateZeroLine( yAxis, xAxis, _horizontalZeroLine, Orientation.HORIZONTAL, strokeWidth );

        // Update the vertical grid lines, in model space.
        updateGridLines( xAxis,
                         yAxis,
                         _verticalGridLines,
                         _rightAxis,
                         Orientation.VERTICAL,
                         strokeWidth,
                         dashArray );

        // Update the horizontal grid lines, in model space.
        updateGridLines( yAxis,
                         xAxis,
                         _horizontalGridLines,
                         _topAxis,
                         Orientation.HORIZONTAL,
                         strokeWidth,
                         dashArray );
    }

    /**
     * Update the Grid Scale based on current Grid Resolution value.
     */
    public final void rescaleGrid() {
        switch ( _gridResolution ) {
        case OFF:
            setGridScale( 1d );
            break;
        case COARSE:
            setGridScale( 2d );
            break;
        case MEDIUM:
            setGridScale( 1d );
            break;
        case FINE:
            setGridScale( 0.5d );
            break;
        default:
            setGridScale( 1d );
            break;
        }
    }

    public final void setForeground( final Color foreColor ) {
        // Cache the new foreground, to apply to volatile features.
        _foreColor = foreColor;

        // So that they do not become accidentally hidden, make sure the axis
        // zero lines always use the current foreground color.
        _verticalZeroLine.setStroke( foreColor );
        _horizontalZeroLine.setStroke( foreColor );

        // Also, do the same with the secondary axes.
        _rightAxis.setStroke( _foreColor );
        _topAxis.setStroke( _foreColor );

        // Restore the Grid Color, as the Sound Field allows custom colors.
        setGridColor( _gridColor );
    }

    /**
     * Set the grid color.
     *
     * @param gridColor
     *            The grid color.
     */
    public final void setGridColor( final Color gridColor ) {
        // Cache the new Grid Color, so we can save it to User Preferences.
        _gridColor = gridColor;

        // Use the new Grid Color on the Horizontal and Vertical Grid Lines.
        // NOTE: It is best not to also set the Zero Lines as this could
        // obscure the Drawing Limits and Prediction Plane graphics.
        // NOTE: We defer execution, to give CSS style sheets time to load
        // first, as that happens before this method is called in many cases.
        // TODO: Review whether we need runLater() here or not.
        Platform.runLater( () -> {
            _verticalGridLines.getChildren().forEach( verticalGridLine -> {
                if ( verticalGridLine instanceof Shape ) {
                    ( ( Shape ) verticalGridLine ).setStroke( gridColor );
                }
            } );

            _horizontalGridLines.getChildren().forEach( horizontalGridLine -> {
                if ( horizontalGridLine instanceof Shape ) {
                    ( ( Shape ) horizontalGridLine ).setStroke( gridColor );
                }
            } );
        } );
    }

    /**
     * Control whether the grid is drawn.
     *
     * @param gridOn
     *            If true, a grid is drawn.
     */
    public final void setGridOn( final boolean gridOn ) {
        _gridOn = gridOn;

        _verticalGridLines.setVisible( gridOn );
        _horizontalGridLines.setVisible( gridOn );

        // If we are turning the grid back on, need to reassert the grid color.
        if ( gridOn ) {
            setGridColor( _gridColor );
        }
    }

    /**
     * Set the new Grid Resolution, and conditionally re-scale the grid.
     *
     * @param gridResolution
     *            The resolution of the grid lines
     */
    public final void setGridResolution( final GridResolution gridResolution ) {
        _gridResolution = gridResolution;
    }

    /**
     * Set the grid scale.
     *
     * @param gridscale
     *            The grid scale.
     */
    public final void setGridScale( final double gridscale ) {
        _gridScale = gridscale;
    }

    /**
     * Control whether the axis zero lines drawn.
     *
     * @param showAxisZeroLines
     *            If true, the axis zero lines are drawn.
     */
    public final void setShowAxisZeroLines( final boolean showAxisZeroLines ) {
        _showAxisZeroLines = showAxisZeroLines;

        _verticalZeroLine.setVisible( showAxisZeroLines );
        _horizontalZeroLine.setVisible( showAxisZeroLines );
    }

    /**
     * Set the venue-to-display scale factor on this node.
     *
     * @param venueToDisplayScaleFactor
     *            The new venue-to-display scale factor
     */
    public final void setVenueToDisplayScaleFactor( final double venueToDisplayScaleFactor ) {
        // Cache the new venue-to-display scale factor for direct use.
        _venueToDisplayScaleFactor = venueToDisplayScaleFactor;
    }

    protected final void updateGridLines( final CartesianAxis referenceAxis,
                                          final CartesianAxis otherAxis,
                                          final Group gridLines,
                                          final Line secondaryAxis,
                                          final Orientation gridLineOrientation,
                                          final double strokeWidth,
                                          final Double[] dashArray ) {
        // Update the axis grid lines, in model space.
        final List< Line > gridLineElements = new ArrayList<>();
        final List< Number > tickValues = referenceAxis.getUnfilteredTickValues();
        if ( _gridOn ) {
            // Iterate the current zoomed range values for the reference axis.
            tickValues.forEach( tick -> addGridLine( tick,
                                                     otherAxis,
                                                     gridLineOrientation,
                                                     strokeWidth,
                                                     dashArray,
                                                     gridLineElements ) );
        }
        else {
            // Generate a Grid Line for the final Tick mark, to use as the
            // Secondary Axis location and dimensions.
            final int numberOfTickMarks = tickValues.size();
            if ( numberOfTickMarks > 0 ) {
                final Number finalTick = tickValues.get( numberOfTickMarks - 1 );
                addGridLine( finalTick,
                             otherAxis,
                             gridLineOrientation,
                             strokeWidth,
                             dashArray,
                             gridLineElements );
            }
        }

        // Special case for the final Grid Lines in each dimension, so we can
        // customize them to look more like dual axes, sans tick marks and
        // labels. This is a hack, as it is too hard to add decoupled axes.
        // NOTE: These may not match the stroke width of the primary axes.
        final int numberOfGridLines = gridLineElements.size();
        if ( numberOfGridLines > 0 ) {
            final Line finalLine = gridLineElements.remove( numberOfGridLines - 1 );
            secondaryAxis.setStartX( finalLine.getStartX() );
            secondaryAxis.setStartY( finalLine.getStartY() );
            secondaryAxis.setEndX( finalLine.getEndX() );
            secondaryAxis.setEndY( finalLine.getEndY() );

            secondaryAxis.setStroke( _foreColor );
            secondaryAxis.setStrokeWidth( 1.25d * strokeWidth );
            secondaryAxis.setStrokeLineCap( StrokeLineCap.BUTT );
            secondaryAxis.getStrokeDashArray().clear();
            secondaryAxis.setVisible( true );
        }

        gridLines.getChildren().setAll( gridLineElements );

        // Try to improve performance by setting grid lines to ignore the mouse.
        gridLines.setMouseTransparent( true );

        // TODO: Reconsider making an empty local Path Elements list, adding
        // MoveTo/LineTo pairs, then replacing the list in the query below. This
        // is how Oracle implements XYChart, and perhaps this performs better
        // than what we are doing with a Group of Lines, as the Scene Graph has
        // fewer Nodes this way. But I think we switched due to poor
        // performance. We could also replace java calls with CSS style names.
        // final Path path = new Path();
        // final ObservableList<PathElement> pathElements = path.getElements();
    }

    /**
     * Update the Grid On state based on current Grid Resolution value.
     */
    public final void updateGridOn() {
        switch ( _gridResolution ) {
        case OFF:
            setGridOn( false );
            break;
        case COARSE:
        case MEDIUM:
        case FINE:
            setGridOn( true );
            break;
        default:
            setGridOn( false );
            break;
        }
    }

    protected final void updateZeroLine( final ValueAxis< Number > referenceAxis,
                                         final ValueAxis< Number > otherAxis,
                                         final Line zeroLine,
                                         final Orientation zeroLineOrientation,
                                         final double strokeWidth ) {
        if ( !_showAxisZeroLines || Double.isNaN( referenceAxis.getZeroPosition() ) ) {
            zeroLine.setVisible( false );
        }
        else {
            // Update the axis zero line, in model space.
            switch ( zeroLineOrientation ) {
            case HORIZONTAL:
                zeroLine.setStartX( otherAxis.getLowerBound() );
                zeroLine.setStartY( 0d );
                zeroLine.setEndX( otherAxis.getUpperBound() );
                zeroLine.setEndY( 0d );
                break;
            case VERTICAL:
                zeroLine.setStartX( 0d );
                zeroLine.setStartY( otherAxis.getLowerBound() );
                zeroLine.setEndX( 0d );
                zeroLine.setEndY( otherAxis.getUpperBound() );
                break;
            default:
                break;
            }

            zeroLine.setStrokeWidth( strokeWidth );
            zeroLine.setStrokeLineCap( StrokeLineCap.BUTT );
            zeroLine.setVisible( true );
        }

        // Try to improve performance by setting Grid Lines to ignore the mouse.
        zeroLine.setMouseTransparent( true );
    }

}
