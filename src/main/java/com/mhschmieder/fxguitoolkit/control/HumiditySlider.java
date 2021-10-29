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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.physics.HumidityUnit;

import javafx.geometry.Orientation;

public final class HumiditySlider extends NumberSlider {

    // Declare default minimum, maximum, and initial humidity for { 0, 100 }.
    public static final double  MINIMUM_RELATIVE_HUMIDITY_DEFAULT = 0d;
    public static final double  MAXIMUM_RELATIVE_HUMIDITY_DEFAULT = 100d;
    public static final double  INITIAL_RELATIVE_HUMIDITY_DEFAULT = 50d;

    // Default tick spacing in percentiles.
    private static final double MAJOR_TICK_SPACING_PERCENT        = 10d;
    private static final double MINOR_TICK_SPACING_PERCENT        = 2d;

    // Declare block increment/decrement amount for left and right arrows.
    private static final double BLOCK_INCREMENT_PERCENT           = 0.5d;

    public HumiditySlider( final SessionContext sessionContext ) {
        this( sessionContext,
              MINIMUM_RELATIVE_HUMIDITY_DEFAULT,
              MAXIMUM_RELATIVE_HUMIDITY_DEFAULT,
              INITIAL_RELATIVE_HUMIDITY_DEFAULT,
              MAJOR_TICK_SPACING_PERCENT,
              MINOR_TICK_SPACING_PERCENT,
              BLOCK_INCREMENT_PERCENT );
    }

    public HumiditySlider( final SessionContext sessionContext,
                           final double minimumRelativeHumidity,
                           final double maximumRelativeHumidity,
                           final double initialRelativeHumidity,
                           final double majorTickSpacingPercent,
                           final double minorTickSpacingPercent,
                           final double blockIncrementPercent ) {
        // Always call the superclass constructor first!
        super( sessionContext,
               minimumRelativeHumidity,
               maximumRelativeHumidity,
               initialRelativeHumidity,
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

    private void initSlider() {
        // Humidity is generally presented as vertical bar sliders.
        setOrientation( Orientation.VERTICAL );

        // Make sure the tick labels aren't bunched together.
        // NOTE: We can't do this on the base class in case the slider's
        // orientation is vertical, which we only know per context.
        // NOTE: This is a safety value that should be overloaded by the parent
        // node, per usage context.
        setMinHeight( 150d );

        // Cache the string representation of the humidity unit.
        // NOTE: We set this directly as there currently is no unit conversion,
        // for humidity, as relative humidity is what most gauges display.
        setMeasurementUnitString( HumidityUnit.RELATIVE.toPresentationString() );
    }

}
