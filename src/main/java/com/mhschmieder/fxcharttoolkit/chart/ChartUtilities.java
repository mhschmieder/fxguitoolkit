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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxcharttoolkit.chart;

import java.text.NumberFormat;

import com.mhschmieder.commonstoolkit.text.StringUtilities;
import com.mhschmieder.fxcharttoolkit.layout.ChartLegend;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * This is a utility class for common chart methods that are agnostic to chart
 * type.
 */
public final class ChartUtilities {

    public static String getDataPointValue( final double xValueShared,
                                            final double yValueBottom,
                                            final double yValueTop,
                                            final NumberFormat xValueSharedNumberFormat,
                                            final NumberFormat yValueBottomNumberFormat,
                                            final NumberFormat yValueTopNumberFormat,
                                            final String xUnitLabelShared,
                                            final String yUnitLabelBottom,
                                            final String yUnitLabelTop ) {
        final String dataPointValue = Double.isNaN( yValueTop )
            ? StringUtilities.getFormattedQuantityPair( xValueShared,
                                                        yValueBottom,
                                                        xValueSharedNumberFormat,
                                                        yValueBottomNumberFormat,
                                                        xUnitLabelShared,
                                                        yUnitLabelBottom )
            : StringUtilities.getFormattedQuantityTriplet( xValueShared,
                                                           yValueBottom,
                                                           yValueTop,
                                                           xValueSharedNumberFormat,
                                                           yValueBottomNumberFormat,
                                                           yValueTopNumberFormat,
                                                           xUnitLabelShared,
                                                           yUnitLabelBottom,
                                                           yUnitLabelTop );
        return dataPointValue;
    }

    public static double[] getDataSetXValues( final XYChart< Number, Number > xyChart,
                                              final int dataSetIndex ) {
        if ( !isDataSetValid( xyChart, dataSetIndex ) ) {
            return new double[ 0 ];
        }

        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();
        final XYChart.Series< Number, Number > chartSeries = chartSeriesList.get( dataSetIndex );
        final ObservableList< Data< Number, Number > > chartSeriesData = chartSeries.getData();

        final int numberOfDataPoints = chartSeriesData.size();
        final double[] xValues = new double[ numberOfDataPoints ];
        for ( int dataPointIndex = 0; dataPointIndex < numberOfDataPoints; dataPointIndex++ ) {
            final Data< Number, Number > chartSeriesDataPoint =
                                                              chartSeriesData.get( dataPointIndex );
            xValues[ dataPointIndex ] = chartSeriesDataPoint.getXValue().doubleValue();
        }

        return xValues;
    }

    public static double[] getDataSetYValues( final XYChart< Number, Number > xyChart,
                                              final int dataSetIndex ) {
        if ( !isDataSetValid( xyChart, dataSetIndex ) ) {
            return new double[ 0 ];
        }

        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();
        final XYChart.Series< Number, Number > chartSeries = chartSeriesList.get( dataSetIndex );
        final ObservableList< Data< Number, Number > > chartSeriesData = chartSeries.getData();

        final int numberOfDataPoints = chartSeriesData.size();
        final double[] yValues = new double[ numberOfDataPoints ];
        for ( int dataPointIndex = 0; dataPointIndex < numberOfDataPoints; dataPointIndex++ ) {
            final Data< Number, Number > chartSeriesDataPoint =
                                                              chartSeriesData.get( dataPointIndex );
            yValues[ dataPointIndex ] = chartSeriesDataPoint.getYValue().doubleValue();
        }

        return yValues;
    }

    @SuppressWarnings("nls")
    public static String getFormattedDataPoint( final Series< Number, Number > chartSeries,
                                                final double xValueShared,
                                                final double yValueBottom,
                                                final double yValueTop,
                                                final NumberFormat xValueSharedNumberFormat,
                                                final NumberFormat yValueBottomNumberFormat,
                                                final NumberFormat yValueTopNumberFormat,
                                                final String xUnitLabelShared,
                                                final String yUnitLabelBottom,
                                                final String yUnitLabelTop ) {
        final String dataSetName = chartSeries.getName();
        final String dataPointValue = getDataPointValue( xValueShared,
                                                         yValueBottom,
                                                         yValueTop,
                                                         xValueSharedNumberFormat,
                                                         yValueBottomNumberFormat,
                                                         yValueTopNumberFormat,
                                                         xUnitLabelShared,
                                                         yUnitLabelBottom,
                                                         yUnitLabelTop );
        final StringBuilder formattedDataPoint = new StringBuilder();
        formattedDataPoint.append( dataSetName );
        formattedDataPoint.append( ": " );
        formattedDataPoint.append( dataPointValue );

        return formattedDataPoint.toString();
    }

    public static boolean isChartEmpty( final XYChart< Number, Number > xyChart ) {
        // First, check first for null charts (i.e. not initialized).
        if ( xyChart == null ) {
            return true;
        }

        // Next, check first for empty charts (i.e. no data sets).
        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();
        if ( ( chartSeriesList == null ) || chartSeriesList.isEmpty() ) {
            return true;
        }

        // Finally, check for all-empty data sets within the chart.
        boolean chartEmpty = true;
        for ( final Series< Number, Number > series : chartSeriesList ) {
            final ObservableList< Data< Number, Number > > data = series.getData();
            if ( ( data != null ) && !data.isEmpty() ) {
                chartEmpty = false;
                break;
            }
        }

        return chartEmpty;
    }

    /**
     * Return whether the specified data set is valid or not. Check the argument
     * to ensure that it is a valid data set index. If it is less than zero,
     * does not refer to an existing data set, or the data set is empty, return
     * false. Do not throw exceptions as this overly restricts calling contexts.
     *
     * @param xyChart
     *            The chart whose data sets must be validated
     * @param dataSetIndex
     *            The data set index.
     * @return True if the specified data set is valid; false if not.
     */
    public static boolean isDataSetValid( final XYChart< Number, Number > xyChart,
                                          final int dataSetIndex ) {
        if ( ( xyChart == null ) || ( dataSetIndex < 0 ) ) {
            return false;
        }

        final ObservableList< Series< Number, Number > > chartSeriesList = xyChart.getData();
        if ( ( chartSeriesList == null ) || ( dataSetIndex >= chartSeriesList.size() ) ) {
            return false;
        }

        final XYChart.Series< Number, Number > chartSeries = chartSeriesList.get( dataSetIndex );
        if ( chartSeries == null ) {
            return false;
        }

        final ObservableList< Data< Number, Number > > chartSeriesData = chartSeries.getData();

        return ( ( chartSeriesData != null ) && !chartSeriesData.isEmpty() );
    }

    @SuppressWarnings("nls")
    public static void syncLegendToDataSeries( final ChartLegend legend,
                                               final XYChart< Number, Number > chart ) {
        final ObservableList< ChartLegendItem > legendItems = legend.getItems();
        legendItems.clear();

        final ObservableList< Series< Number, Number > > dataSeries = chart.getData();
        if ( ( dataSeries != null ) && !dataSeries.isEmpty() ) {
            for ( int seriesIndex = 0; seriesIndex < dataSeries.size(); seriesIndex++ ) {
                final Series< Number, Number > series = dataSeries.get( seriesIndex );
                final ChartLegendItem legenditem = new ChartLegendItem( series.getName() );
                legenditem.getSymbol().getStyleClass()
                        .addAll( "chart-line-symbol", "series" + seriesIndex, "default-color" );
                legendItems.add( legenditem );
            }
        }
    }

}
