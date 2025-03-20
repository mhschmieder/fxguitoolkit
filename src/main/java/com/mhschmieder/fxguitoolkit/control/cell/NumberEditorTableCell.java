/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.control.cell;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.geometry.Pos;

public abstract class NumberEditorTableCell< RT, Number > extends EditorTableCell< RT, Number > {

    // Maintain a reference to the Measurement Unit label (can be blank).
    @SuppressWarnings("nls") protected String _measurementUnit = "";

    // Cache a number formatter for displaying the numeric values.
    protected final NumberFormat              _numberFormat;

    public NumberEditorTableCell( final boolean pAllowedToBeBlank,
                                  final ClientProperties pClientProperties ) {
        this( null, pAllowedToBeBlank, pClientProperties );
    }

    public NumberEditorTableCell( final List< Integer > pUneditableRows,
                                  final boolean pAllowedToBeBlank,
                                  final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank, pClientProperties );

        // Generally, we prefer numeric fields to be centered.
        setAlignment( Pos.CENTER );

        // Make sure we show the numbers in the user's locale.
        _numberFormat = NumberFormat.getNumberInstance( pClientProperties.locale );
        _numberFormat.setMinimumFractionDigits( 0 );
        _numberFormat.setMinimumIntegerDigits( 1 );
    }

    @Override
    public void commitEdit( final Number newValue ) {
        // Reject empty fields and treat as cancellation of editing, as we do
        // not allow null numbers as it can complicate bindings and syncing.
        if ( blankTextAllowed || ( newValue != null ) ) {
            super.commitEdit( newValue );
        }
        else {
            super.cancelEdit();
        }
    }

    @Override
    public Number getAdjustedValue( final Number unadjustedValue ) {
        // If blank text is allowed, return the input unadjusted; otherwise trim
        // the edited value to make it legal, and to avoid confusing the user.
        // TODO: Think out all the edge cases and what to do, such as blank trim.
        final Number adjustedValue = blankTextAllowed 
                ? unadjustedValue 
                : adjustPrecision( unadjustedValue );

        return adjustedValue;
    }
    
    protected Number adjustPrecision( final Number unadjustedValue ) {
        // NOTE: There is nothing to do by default, but some derivations may
        //  need to supply type-specific overrides in a domain context.
        return unadjustedValue;
    }

    public final void setMeasurementUnit( final String measurementUnit ) {
        _measurementUnit = measurementUnit;
    }
}
