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
package com.mhschmieder.fxguitoolkit.control.cell;

import java.text.NumberFormat;
import java.util.List;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.text.NumberFormatUtilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LabelEditorTableCell< RT, VT > extends EditorTableCell< RT, String > {

    // Cache the raw string representation of the data value.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final StringProperty value;

    // Number format cache used for locale-specific number formatting of
    // uniquefier appendices.
    public NumberFormat          _uniquefierNumberFormat;

    public LabelEditorTableCell( final boolean pBlankTextAllowed,
                                 final SessionContext pSessionContext ) {
        this( null, pBlankTextAllowed, pSessionContext );
    }

    @SuppressWarnings("nls")
    public LabelEditorTableCell( final List< Integer > pUneditableRows,
                                 final boolean pBlankTextAllowed,
                                 final SessionContext pSessionContext ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pBlankTextAllowed );

        value = new SimpleStringProperty( "" );

        try {
            initTableCell( pSessionContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Override
    public void commitEdit( final String newValue ) {
        // Reject empty strings and treat as cancellation of editing, as we do
        // not allow blank labels since it can break mappings of lookups.
        if ( _blankTextAllowed || ( ( newValue != null ) && !newValue.trim().isEmpty() ) ) {
            super.commitEdit( newValue );
        }
        else {
            super.cancelEdit();
        }
    }

    @Override
    public String getAdjustedValue( final String text ) {
        // If blank text is allowed, return the input unadjusted; otherwise trim
        // the edited value to make it legal, and to avoid confusing the user.
        final String adjustedValue = _blankTextAllowed ? text : text.trim();

        return adjustedValue;
    }

    @Override
    protected String getEditorValue() {
        return _textField.getText();
    }

    @Override
    protected String getString() {
        final String stringValue = getItem();
        return stringValue;
    }

    @Override
    protected String getTextValue() {
        return getItem();
    }

    public final String getValue() {
        return value.get();
    }

    private final void initTableCell( final SessionContext pSessionContext ) {
        _uniquefierNumberFormat = NumberFormatUtilities
                .getUniquefierNumberFormat( pSessionContext.locale );
    }

    @Override
    public final void setValue( final String pValue ) {
        // Locally cache the new value, separately from the editor.
        value.set( pValue );

        // Now do whatever we do for all data types in the base class.
        super.setValue( pValue );
    }

    public final StringProperty valueProperty() {
        return value;
    }

}
