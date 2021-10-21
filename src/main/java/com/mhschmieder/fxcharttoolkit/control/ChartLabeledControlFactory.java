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
package com.mhschmieder.fxcharttoolkit.control;

import java.text.NumberFormat;

import com.mhschmieder.fxcharttoolkit.chart.CartesianAxis;

import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;

/**
 * {@code ChartLabeledControlFactory} is a factory class for minimizing
 * copy/paste code for shared design patterns regarding controls such as action
 * buttons.
 */
public class ChartLabeledControlFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private ChartLabeledControlFactory() {}

    /**
     * Creates and returns a non-auto-ranging CartesianAxis with the given upper
     * and lower bound.
     *
     * @param pAxisLabel
     *            The name to display for this axis
     * @param pLowerBound
     *            The lower bound for this axis, i.e. min plottable value
     * @param pUpperBound
     *            The upper bound for this axis, i.e. max plottable value
     * @param pTickUnit
     *            The tick unit, i.e. space between tick marks
     * @param pMinorTickCount
     *            The number of minor tick divisions to display between each
     *            major tick mark
     * @param pAxisSide
     *            The side of the hose chart to show the axis on
     * @param pTickLabelFormat
     *            The number format to use for the tick mark labels
     * @return A Cartesian Axis bound to the specified limits
     */
    public static CartesianAxis getCartesianAxis( final String pAxisLabel,
                                                  final double pLowerBound,
                                                  final double pUpperBound,
                                                  final double pTickUnit,
                                                  final int pMinorTickCount,
                                                  final Side pAxisSide,
                                                  final NumberFormat pTickLabelFormat ) {
        final CartesianAxis cartesianAxis = new CartesianAxis( pAxisLabel,
                                                               pLowerBound,
                                                               pUpperBound,
                                                               pTickUnit,
                                                               pMinorTickCount,
                                                               pAxisSide,
                                                               pTickLabelFormat );
        return cartesianAxis;
    }

    /**
     * Creates and returns a unitless NumberAxis with the given upper and lower
     * bound.
     *
     * @param pAxisLabel
     *            The name to display for this axis
     * @param pLowerBound
     *            The lower bound for this axis, i.e. min plottable value
     * @param pUpperBound
     *            The upper bound for this axis, i.e. max plottable value
     * @param pTickUnit
     *            The tick unit, i.e. space between tick marks
     * @param pAxisSide
     *            The side of the hose chart to show the axis on
     * @return A unitless Number Axis
     */
    public static NumberAxis getUnitlessAxis( final String pAxisLabel,
                                              final double pLowerBound,
                                              final double pUpperBound,
                                              final double pTickUnit,
                                              final Side pAxisSide ) {
        // Make sure the outer ticks are whole numbers or else all tick labels
        // get too long to avoid overlap (and also are less usable).
        final double axisMin = Math.floor( pLowerBound );
        final double axisMax = Math.ceil( pUpperBound );

        final NumberAxis unitlessAxis = new NumberAxis( pAxisLabel, axisMin, axisMax, pTickUnit );

        unitlessAxis.setTickMarkVisible( true );
        unitlessAxis.setTickLabelsVisible( true );
        unitlessAxis.setMinorTickCount( 6 );

        unitlessAxis.setSide( pAxisSide );

        return unitlessAxis;
    }

}
