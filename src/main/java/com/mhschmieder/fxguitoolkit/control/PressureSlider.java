/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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

import com.mhschmieder.commonstoolkit.physics.PhysicsConstants;
import com.mhschmieder.commonstoolkit.physics.PressureUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.geometry.Orientation;

public class PressureSlider extends NumberSlider {

    // Declare default minimum, maximum, and initial Pressure.
    public static final double  MINIMUM_PRESSURE_PASCALS_DEFAULT =
                                                                 PhysicsConstants.PRESSURE_MINIMUM_PA;
    public static final double  MAXIMUM_PRESSURE_PASCALS_DEFAULT =
                                                                 PhysicsConstants.PRESSURE_MAXIMUM_PA;
    public static final double  INITIAL_PRESSURE_PASCALS_DEFAULT =
                                                                 PhysicsConstants.PRESSURE_REFERENCE_PA;

    // Default tick spacing in pascals.
    private static final double MAJOR_TICK_SPACING_PASCALS       = 10000d;
    private static final double MINOR_TICK_SPACING_PASCALS       = 2000d;

    // Declare block increment/decrement amount for left and right arrows.
    private static final double BLOCK_INCREMENT_PASCALS          = 1000d;

    // Store the Pressure Unit so we'll know when we need to convert.
    private PressureUnit        _pressureUnit;

    public PressureSlider( final ClientProperties clientProperties ) {
        this( clientProperties,
              MINIMUM_PRESSURE_PASCALS_DEFAULT,
              MAXIMUM_PRESSURE_PASCALS_DEFAULT,
              INITIAL_PRESSURE_PASCALS_DEFAULT,
              MAJOR_TICK_SPACING_PASCALS,
              MINOR_TICK_SPACING_PASCALS,
              BLOCK_INCREMENT_PASCALS );
    }

    public PressureSlider( final ClientProperties clientProperties,
                           final double minimumPressurePa,
                           final double maximumPressurePa,
                           final double initialPressurePa,
                           final double majorTickSpacingPa,
                           final double minorTickSpacingPa,
                           final double blockIncrementPa ) {
        // Always call the superclass constructor first!
        super( clientProperties,
               minimumPressurePa,
               maximumPressurePa,
               initialPressurePa,
               majorTickSpacingPa,
               minorTickSpacingPa,
               blockIncrementPa,
               true );

        _pressureUnit = PressureUnit.defaultValue();

        try {
            initSlider();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current Pressure value from display units to pascals.
    public final double getPressurePa() {
        return UnitConversion.convertPressure( getValue(), _pressureUnit, PressureUnit.PASCALS );
    }

    private final void initSlider() {
        // In lieu of gauges, vertical bars are best for Pressure sliders.
        setOrientation( Orientation.VERTICAL );

        // Make sure the tick labels aren't bunched together.
        // NOTE: We can't do this on the base class in case the slider's
        // orientation is vertical, which we only know per context.
        // NOTE: This is a safety value that should be overloaded by the parent
        // node, per usage context.
        setMinHeight( 150d );

        // Update the Pressure Unit and related resolutions and ranges.
        updatePressureUnit( _pressureUnit );
    }

    // Convert maximum Pressure value from pascals to display units.
    public final void setMaximumPressurePa( final double maximumPressurePa ) {
        setMax( UnitConversion
                .convertPressure( maximumPressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    // Convert minimum Pressure value from pascals to display units.
    public final void setMinimumPressurePa( final double minimumPressurePa ) {
        setMin( UnitConversion
                .convertPressure( minimumPressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    // Convert new Pressure value from pascals to display units.
    public final void setPressurePa( final double pressurePa ) {
        setValue( UnitConversion
                .convertPressure( pressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    public final void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Convert the current Pressure from previous units to new units.
        final double pressureCurrent = UnitConversion
                .convertPressure( getValue(), _pressureUnit, pressureUnit );

        // Store the new Pressure Unit to provide context for next change.
        _pressureUnit = pressureUnit;

        // Set the embedded unit label in the generic number slider.
        setMeasurementUnitString( _pressureUnit.toPresentationString() );

        // NOTE: Sliders must set their adjusted current value before setting
        // the adjusted range, as we pre-set the range to the maximum possible
        // values amongst all available units so that setting an adjusted
        // current value in new units does not cause clamping due to being
        // outside the previous range, resulting in unrecoverable values.
        setValue( pressureCurrent );
        setMinimumPressurePa( PhysicsConstants.PRESSURE_MINIMUM_PA );
        setMaximumPressurePa( PhysicsConstants.PRESSURE_MAXIMUM_PA );

        // Set the tick resolution based on the granularity of the unit.
        switch ( _pressureUnit ) {
        case KILOPASCALS:
            setTickResolution( 10.0d, 2.0d );
            setBlockIncrement( 1.0d );
            break;
        case PASCALS:
            setTickResolution( 10000d, 2000d );
            setBlockIncrement( 1000d );
            break;
        case MILLIBARS:
            setTickResolution( 100d, 20.0d );
            setBlockIncrement( 10.0d );
            break;
        case ATMOSPHERES:
            setTickResolution( 1.0d, 0.25d );
            setBlockIncrement( 0.1d );
            break;
        default:
            break;
        }
    }

}
