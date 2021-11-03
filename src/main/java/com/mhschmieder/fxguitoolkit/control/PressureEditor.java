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
import com.mhschmieder.commonstoolkit.physics.PressureUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;

public class PressureEditor extends DoubleEditor {

    // Declare value increment/decrement amount for up and down arrow keys.
    // NOTE: We increment by 10 pascals as this works well for most units.
    // TODO: Apply a different increment for each Pressure Unit choice?
    public static final double VALUE_INCREMENT_PA = 10d;

    // Store the Pressure Unit so we'll know when we need to convert.
    private PressureUnit       _pressureUnit;

    // //////////////////////////////////////////////////////////////////////////
    // Constructors and Initialization
    public PressureEditor( final ClientProperties clientProperties,
                           final String initialText,
                           final String tooltipText,
                           final double minimumPressurePa,
                           final double maximumPressurePa,
                           final double initialPressurePa ) {
        // Always call the superclass constructor first!
        // NOTE: We use up to four decimal places of precision for displaying
        // pressure, and ten decimal places for parsing Pressure.
        super( clientProperties,
               initialText,
               tooltipText,
               0,
               4,
               0,
               10,
               minimumPressurePa,
               maximumPressurePa,
               initialPressurePa,
               VALUE_INCREMENT_PA );

        _pressureUnit = PressureUnit.defaultValue();

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current Pressure value from display units to pascals.
    // NOTE: This method is unused currently, but is provided in case we
    // change our mind about having the related slider be the data master.
    public final double getPressurePa() {
        return UnitConversion.convertPressure( getValue(), _pressureUnit, PressureUnit.PASCALS );
    }

    private final void initEditor() {
        // Update the Pressure Unit and related resolutions and ranges.
        updatePressureUnit( _pressureUnit );
    }

    // Convert maximum Pressure value from pascals to display units.
    public final void setMaximumPressurePa( final double maximumPressurePa ) {
        setMaximumValue( UnitConversion
                .convertPressure( maximumPressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    // Convert minimum Pressure value from pascals to display units.
    public final void setMinimumPressurePa( final double minimumPressurePa ) {
        setMinimumValue( UnitConversion
                .convertPressure( minimumPressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    // Convert new Pressure value from pascals to display units.
    // NOTE: This method is unused currently, but is provided in case we
    // change our mind about having the related slider be the data master.
    public final void setPressurePa( final double pressurePa ) {
        setValue( UnitConversion
                .convertPressure( pressurePa, PressureUnit.PASCALS, _pressureUnit ) );
    }

    public final void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Store the new Pressure Unit to provide context for next change.
        _pressureUnit = pressureUnit;

        // Set the level of precision based on the granularity of the unit.
        switch ( _pressureUnit ) {
        case KILOPASCALS:
            _numberFormat.setMaximumFractionDigits( 4 );
            break;
        case PASCALS:
            _numberFormat.setMaximumFractionDigits( 1 );
            break;
        case MILLIBARS:
            _numberFormat.setMaximumFractionDigits( 3 );
            break;
        case ATMOSPHERES:
            _numberFormat.setMaximumFractionDigits( 5 );
            break;
        default:
            break;
        }

        // NOTE: Text Editors must set their adjusted range before setting the
        // adjusted current value, as we manage value legality within callbacks
        // that check the locally cached minimum and maximum values.
        // NOTE: Unit conversion is done in the sliders for the doubled-up
        // controls, so ideally we can move that code to these respective
        // editors to help make the editors consistently own the data and the
        // measurement units. The Distance Editor is the model for doing this.
        // NOTE: The attempted consolidation of bindings strategies ended up
        // causing too many conflicts and problems, as we aren't handling
        // sliders and editors consistently so it gets confusing very quickly as
        // to the order of callbacks and events as well as when and whether unit
        // conversion has already been applied when values are synced or bound.
        setMinimumPressurePa( PhysicsConstants.PRESSURE_MINIMUM_PA );
        setMaximumPressurePa( PhysicsConstants.PRESSURE_MAXIMUM_PA );

        // Set the embedded unit label in the generic number editor.
        setMeasurementUnitString( _pressureUnit.toPresentationString() );
    }

}
