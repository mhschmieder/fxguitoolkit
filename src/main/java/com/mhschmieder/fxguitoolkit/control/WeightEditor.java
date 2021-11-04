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

import com.mhschmieder.commonstoolkit.physics.UnitConversion;
import com.mhschmieder.commonstoolkit.physics.WeightUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

public class WeightEditor extends DoubleEditor {

    // Store the Weight Unit so we'll know when we need to convert.
    private WeightUnit _weightUnit;

    // //////////////////////////////////////////////////////////////////////////
    // Constructors and Initialization
    public WeightEditor( final ClientProperties clientProperties,
                         final String text,
                         final String tooltipText ) {
        // Always call the superclass constructor first!
        // NOTE: We use up to two decimal place of precision for displaying
        // weight, and ten decimal places for parsing weight.
        super( clientProperties, text, tooltipText, 0, 2, 0, 10 );

        _weightUnit = WeightUnit.defaultValue();

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current weight value from display units to kilograms.
    // NOTE: This method is unused currently, but is provided in case we
    // change our mind about having the related slider be the data master.
    public final double getWeightKg() {
        return UnitConversion.convertWeight( getValue(), _weightUnit, WeightUnit.KILOGRAMS );
    }

    private final void initEditor() {
        // Update the Weight Unit and related resolutions and ranges.
        updateWeightUnit( _weightUnit );
    }

    // Convert maximum Weight value from kilograms to display units.
    public final void setMaximumWeightKg( final double maximumWeightKg ) {
        setMaximumValue( UnitConversion
                .convertWeight( maximumWeightKg, WeightUnit.KILOGRAMS, _weightUnit ) );
    }

    // Convert minimum Weight value from kilograms to display units.
    public final void setMinimumWeightKg( final double minimumWeightKg ) {
        setMinimumValue( UnitConversion
                .convertWeight( minimumWeightKg, WeightUnit.KILOGRAMS, _weightUnit ) );
    }

    // Convert new wWight value from kilograms to display units.
    public final void setWeightKg( final double weightKg ) {
        setValue( UnitConversion.convertWeight( weightKg, WeightUnit.KILOGRAMS, _weightUnit ) );
    }

    public final void updateWeightUnit( final WeightUnit weightUnit ) {
        // Store the new Weight Unit to provide context for next change.
        _weightUnit = weightUnit;

        // Set the level of precision based on the granularity of the unit.
        switch ( _weightUnit ) {
        case KILOGRAMS:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        case GRAMS:
            _numberFormat.setMaximumFractionDigits( 0 );
            break;
        case METRIC_TONS:
            _numberFormat.setMaximumFractionDigits( 5 );
            break;
        case POUNDS:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        case OUNCES:
            _numberFormat.setMaximumFractionDigits( 1 );
            break;
        default:
            break;
        }

        // Convert the current weight from previous units to new units.
        final double weightCurrent = UnitConversion
                .convertWeight( getValue(), _weightUnit, weightUnit );

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
        setMinimumWeightKg( -Double.MAX_VALUE );
        setMaximumWeightKg( Double.MAX_VALUE );

        setValue( weightCurrent );

        // Set the embedded unit label in the generic number editor.
        setMeasurementUnitString( _weightUnit.toPresentationString() );
    }

}
