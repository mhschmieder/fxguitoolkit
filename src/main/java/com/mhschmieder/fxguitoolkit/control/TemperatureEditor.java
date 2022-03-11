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
import com.mhschmieder.commonstoolkit.physics.TemperatureUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

public final class TemperatureEditor extends DoubleEditor {

    // Declare value increment/decrement amount for up and down arrow keys.
    // NOTE: We increment by 0.1 degrees K as this is a typical default.
    // TODO: Apply a different increment if units are Fahrenheit?
    public static final double VALUE_INCREMENT_K = 0.1d;

    // Store the Temperature Unit so we'll know when we need to convert.
    private TemperatureUnit    _temperatureUnit;

    // //////////////////////////////////////////////////////////////////////////
    // Constructors and Initialization
    public TemperatureEditor( final ClientProperties clientProperties,
                              final String initialText,
                              final String tooltipText,
                              final double minimumTemperatureK,
                              final double maximumTemperatureK,
                              final double initialTemperatureK ) {
        // Always call the superclass constructor first!
        // NOTE: We use up to two decimal places of precision for displaying
        // temperature, and six decimal places for parsing Temperature.
        super( clientProperties,
               initialText,
               tooltipText,
               0,
               2,
               0,
               6,
               minimumTemperatureK,
               maximumTemperatureK,
               initialTemperatureK,
               VALUE_INCREMENT_K );

        _temperatureUnit = TemperatureUnit.defaultValue();

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current Temperature value from display units to Kelvin.
    // NOTE: This method is unused currently, but is provided in case we
    // change our mind about having the related slider be the data master.
    public double getTemperatureK() {
        return UnitConversion
                .convertTemperature( getValue(), _temperatureUnit, TemperatureUnit.KELVIN );
    }

    private void initEditor() {
        // Update the Temperature Unit and related resolutions and ranges.
        updateTemperatureUnit( _temperatureUnit );
    }

    // Convert maximum Temperature value from Kelvin to display units.
    public void setMaximumTemperatureK( final double maximumTemperatureK ) {
        setMaximumValue( UnitConversion.convertTemperature( maximumTemperatureK,
                                                            TemperatureUnit.KELVIN,
                                                            _temperatureUnit ) );
    }

    // Convert minimum Temperature value from Kelvin to display units.
    public void setMinimumTemperatureK( final double minimumTemperatureK ) {
        setMinimumValue( UnitConversion.convertTemperature( minimumTemperatureK,
                                                            TemperatureUnit.KELVIN,
                                                            _temperatureUnit ) );
    }

    // Convert new Temperature value from Kelvin to display units.
    // NOTE: This method is unused currently, but is provided in case we
    // change our mind about having the related slider be the data master.
    public void setTemperatureK( final double temperatureK ) {
        setValue( UnitConversion
                .convertTemperature( temperatureK, TemperatureUnit.KELVIN, _temperatureUnit ) );
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Store the new Temperature Unit to provide context for next change.
        _temperatureUnit = temperatureUnit;

        // Set the level of precision based on the granularity of the unit.
        switch ( _temperatureUnit ) {
        case KELVIN:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        case CELSIUS:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        case FAHRENHEIT:
            _numberFormat.setMaximumFractionDigits( 1 );
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
        setMinimumTemperatureK( PhysicsConstants.TEMPERATURE_MINIMUM_K );
        setMaximumTemperatureK( PhysicsConstants.TEMPERATURE_MAXIMUM_K );

        // Set the embedded unit label in the generic number editor.
        setMeasurementUnitString( _temperatureUnit.toPresentationString() );
    }

}
