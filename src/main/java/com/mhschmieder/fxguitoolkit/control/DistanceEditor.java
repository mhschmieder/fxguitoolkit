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

import com.mhschmieder.commonstoolkit.math.DistanceUnit;
import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;

public class DistanceEditor extends NumberEditor {

    // Declare value increment/decrement amount for up and down arrow keys.
    // NOTE: We increment by 0.5 meters as this works well for most units.
    // TODO: Apply a different increment for each Distance Unit choice?
    public static final double VALUE_INCREMENT_M = 0.5d;

    // Store the Distance Unit so we'll know when we need to convert.
    private DistanceUnit       _distanceUnit;

    public DistanceEditor( final SessionContext sessionContext,
                           final String initialText,
                           final String tooltipText ) {
        // Always call the superclass constructor first!
        // :NOTE: We use up to two decimal place of precision for displaying
        // distance, and ten decimal places for parsing Distance.
        super( sessionContext, initialText, tooltipText, 0, 2, 0, 10 );

        _distanceUnit = DistanceUnit.METERS;

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Convert current distance value from display units to Meters.
    public final double getDistanceMeters() {
        return UnitConversion.convertDistance( getValue(), _distanceUnit, DistanceUnit.METERS );
    }

    private final void initEditor() {
        // Default the Distance Unit to Meters.
        _distanceUnit = DistanceUnit.METERS;

        // Now it is safe to set the value increment amount.
        setValueIncrement( VALUE_INCREMENT_M );
    }

    // Convert new Distance value from Meters to display units.
    public final void setDistanceMeters( final double distanceMeters ) {
        setValue( UnitConversion
                .convertDistance( distanceMeters, DistanceUnit.METERS, _distanceUnit ) );
    }

    // Convert maximum Distance value from Meters to display units.
    public final void setMaximumDistanceMeters( final double maximumDistanceMeters ) {
        setMaximumValue( UnitConversion
                .convertDistance( maximumDistanceMeters, DistanceUnit.METERS, _distanceUnit ) );
    }

    // Convert minimum Distance value from Meters to display units.
    public final void setMinimumDistanceMeters( final double minimumDistanceMeters ) {
        setMinimumValue( UnitConversion
                .convertDistance( minimumDistanceMeters, DistanceUnit.METERS, _distanceUnit ) );
    }

    public final void updateDistanceUnit( final DistanceUnit distanceUnitNew ) {
        // Convert Distance range from old units to new units.
        final double minimumDistance = UnitConversion
                .convertDistance( _minimumValue, _distanceUnit, distanceUnitNew );
        final double maximumDistance = UnitConversion
                .convertDistance( _maximumValue, _distanceUnit, distanceUnitNew );

        // Convert the current Distance from previous units to new units.
        final double distanceCurrent = UnitConversion
                .convertDistance( getValue(), _distanceUnit, distanceUnitNew );

        // Cache the new Distance Unit to provide context for next change.
        _distanceUnit = distanceUnitNew;

        // Modify the resolution to be appropriate for the new scale.
        switch ( _distanceUnit ) {
        case METERS:
            _numberFormat.setMaximumFractionDigits( 3 );
            break;
        case CENTIMETERS:
            _numberFormat.setMaximumFractionDigits( 1 );
            break;
        case MILLIMETERS:
            _numberFormat.setMaximumFractionDigits( 0 );
            break;
        case YARDS:
            _numberFormat.setMaximumFractionDigits( 3 );
            break;
        case FEET:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        case INCHES:
            _numberFormat.setMaximumFractionDigits( 1 );
            break;
        case UNITLESS:
            _numberFormat.setMaximumFractionDigits( 2 );
            break;
        default:
            break;
        }

        // NOTE: Text Editors must set their adjusted range before setting the
        // adjusted current value, as we manage value legality within callbacks
        // that check the locally cached minimum and maximum values.
        setMinimumValue( minimumDistance );
        setMaximumValue( maximumDistance );
        setValue( distanceCurrent );

        // Set the embedded unit string in the generic number editor.
        setMeasurementUnitString( _distanceUnit.toPresentationString() );
    }

}
