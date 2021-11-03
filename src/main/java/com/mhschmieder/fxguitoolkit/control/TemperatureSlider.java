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

import com.mhschmieder.commonstoolkit.net.ClientProperties;
import com.mhschmieder.commonstoolkit.physics.PhysicsConstants;
import com.mhschmieder.commonstoolkit.physics.TemperatureUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;

import javafx.geometry.Orientation;

public class TemperatureSlider extends NumberSlider {

    // Declare default minimum, maximum, and initial Temperature.
    public static final double  MINIMUM_TEMPERATURE_KELVIN_DEFAULT =
                                                                   PhysicsConstants.TEMPERATURE_MINIMUM_K;
    public static final double  MAXIMUM_TEMPERATURE_KELVIN_DEFAULT =
                                                                   PhysicsConstants.TEMPERATURE_MAXIMUM_K;
    public static final double  INITIAL_TEMPERATURE_KELVIN_DEFAULT =
                                                                   PhysicsConstants.ROOM_TEMPERATURE_K;

    // Default tick spacing in degrees Kelvin.
    private static final double MAJOR_TICK_SPACING_KELVIN          = 10d;
    private static final double MINOR_TICK_SPACING_KELVIN          = 2d;

    // Declare block increment/decrement amount for left and right arrows.
    private static final double BLOCK_INCREMENT_KELVIN             = 0.5d;

    // Store the Temperature Unit so we'll know when we need to convert.
    private TemperatureUnit     _temperatureUnit;

    public TemperatureSlider( final ClientProperties clientProperties ) {
        this( clientProperties,
              MINIMUM_TEMPERATURE_KELVIN_DEFAULT,
              MAXIMUM_TEMPERATURE_KELVIN_DEFAULT,
              INITIAL_TEMPERATURE_KELVIN_DEFAULT,
              MAJOR_TICK_SPACING_KELVIN,
              MINOR_TICK_SPACING_KELVIN,
              BLOCK_INCREMENT_KELVIN );
    }

    public TemperatureSlider( final ClientProperties clientProperties,
                              final double minimumTemperatureK,
                              final double maximumTemperatureK,
                              final double initialTemperatureK,
                              final double majorTickSpacingK,
                              final double minorTickSpacingK,
                              final double blockIncrementK ) {
        // Always call the superclass constructor first!
        super( clientProperties,
               minimumTemperatureK,
               maximumTemperatureK,
               initialTemperatureK,
               majorTickSpacingK,
               majorTickSpacingK,
               blockIncrementK,
               true );

        _temperatureUnit = TemperatureUnit.defaultValue();

        try {
            initSlider();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current temperature value from display units to Kelvin.
    public final double getTemperatureK() {
        return UnitConversion
                .convertTemperature( getValue(), _temperatureUnit, TemperatureUnit.KELVIN );
    }

    private final void initSlider() {
        // In lieu of gauges, vertical bars are best for Temperature sliders.
        setOrientation( Orientation.VERTICAL );

        // Make sure the tick labels aren't bunched together.
        // NOTE: We can't do this on the base class in case the slider's
        // orientation is vertical, which we only know per context.
        // NOTE: This is a safety value that should be overloaded by the parent
        // node, per usage context.
        setMinHeight( 150d );

        // Update the Temperature Unit and related resolutions and ranges.
        updateTemperatureUnit( _temperatureUnit );
    }

    // Convert maximum Temperature value from Kelvin to display units.
    public final void setMaximumTemperatureK( final double maximumTemperatureK ) {
        setMax( UnitConversion.convertTemperature( maximumTemperatureK,
                                                   TemperatureUnit.KELVIN,
                                                   _temperatureUnit ) );
    }

    // Convert minimum Temperature value from Kelvin to display units.
    public final void setMinimumTemperatureK( final double minimumTemperatureK ) {
        setMin( UnitConversion.convertTemperature( minimumTemperatureK,
                                                   TemperatureUnit.KELVIN,
                                                   _temperatureUnit ) );
    }

    // Convert new Temperature value from Kelvin to display units.
    public final void setTemperatureK( final double temperatureK ) {
        setValue( UnitConversion
                .convertTemperature( temperatureK, TemperatureUnit.KELVIN, _temperatureUnit ) );
    }

    public final void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Convert the current Temperature from previous units to new units.
        final double temperatureCurrent = UnitConversion
                .convertTemperature( getValue(), _temperatureUnit, temperatureUnit );

        // Store the new Temperature Unit to provide context for next change.
        _temperatureUnit = temperatureUnit;

        // Set the embedded unit label in the generic number slider.
        setMeasurementUnitString( _temperatureUnit.toPresentationString() );

        // NOTE: Sliders must set their adjusted current value before setting
        // the adjusted range, as we pre-set the range to the maximum possible
        // values amongst all available units so that setting an adjusted
        // current value in new units does not cause clamping due to being
        // outside the previous range, resulting in unrecoverable values.
        setValue( temperatureCurrent );
        setMinimumTemperatureK( PhysicsConstants.TEMPERATURE_MINIMUM_K );
        setMaximumTemperatureK( PhysicsConstants.TEMPERATURE_MAXIMUM_K );

        // Set the tick resolution based on the granularity of the unit.
        switch ( _temperatureUnit ) {
        case KELVIN:
            setTickResolution( 10d, 2d );
            setBlockIncrement( 0.5d );
            break;
        case CELSIUS:
            setTickResolution( 10d, 2d );
            setBlockIncrement( 0.5d );
            break;
        case FAHRENHEIT:
            setTickResolution( 20d, 5d );
            setBlockIncrement( 1d );
            break;
        default:
            break;
        }
    }

}
