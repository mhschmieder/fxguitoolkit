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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.net.SessionContext;

import javafx.geometry.Orientation;

/**
 * This specialized slider is for opacity, in any context.
 */
public final class OpacitySlider extends NumberSlider {

    // Declare default minimum, maximum, and initial opacity for { 0, 100 }.
    public static final double  MINIMUM_OPACITY_DEFAULT    = 0d;
    public static final double  MAXIMUM_OPACITY_DEFAULT    = 100d;
    public static final double  INITIAL_OPACITY_DEFAULT    = 100d;

    // Default tick spacing in percentiles.
    private static final double MAJOR_TICK_SPACING_PERCENT = 5d;
    private static final double MINOR_TICK_SPACING_PERCENT = 1d;

    // Declare block increment/decrement amount for left and right arrows.
    private static final double BLOCK_INCREMENT_PERCENT    = 2d;

    public OpacitySlider( final SessionContext sessionContext ) {
        this( sessionContext,
              MINIMUM_OPACITY_DEFAULT,
              MAXIMUM_OPACITY_DEFAULT,
              INITIAL_OPACITY_DEFAULT,
              MAJOR_TICK_SPACING_PERCENT,
              MINOR_TICK_SPACING_PERCENT,
              BLOCK_INCREMENT_PERCENT );
    }

    public OpacitySlider( final SessionContext sessionContext,
                          final double minimumOpacity,
                          final double maximumOpacity,
                          final double initialOpacity,
                          final double majorTickSpacingPercent,
                          final double minorTickSpacingPercent,
                          final double blockIncrementPercent ) {
        // Always call the superclass constructor first!
        super( sessionContext,
               minimumOpacity,
               maximumOpacity,
               initialOpacity,
               majorTickSpacingPercent,
               minorTickSpacingPercent,
               blockIncrementPercent,
               true );

        try {
            initSlider();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initSlider() {
        // Opacity is generally presented as horizontal bar sliders.
        setOrientation( Orientation.HORIZONTAL );

        // Make sure the tick labels aren't bunched together.
        // :NOTE: We can't do this on the base class in case the slider's
        // orientation is vertical, which we only know per context.
        // :NOTE: This is a safety value that should be overloaded by the parent
        // node, per usage context.
        setMinWidth( 400d );

        // Cache the string representation of the opacity unit.
        // :NOTE: We set this directly as there currently is no unit conversion,
        // for opacity, as opacity is generally only modeled as percentiles.
        setMeasurementUnitString( "%" );
    }

}
