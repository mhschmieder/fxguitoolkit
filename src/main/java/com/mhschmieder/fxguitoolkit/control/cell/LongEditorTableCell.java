/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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

import java.util.List;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.control.LongEditor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.TextField;

public class LongEditorTableCell< RT, VT > extends NumberEditorTableCell< RT, Long > {

    // Cache the raw Long representation of the data cachedValue.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final LongProperty cachedValue;

    public LongEditorTableCell( final boolean pAllowedToBeBlank,
                                final ClientProperties pClientProperties ) {
        this( null, pAllowedToBeBlank, pClientProperties );
    }

    public LongEditorTableCell( final List< Integer > pUneditableRows,
                                final boolean pAllowedToBeBlank,
                                final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank, pClientProperties );

        cachedValue = new SimpleLongProperty( 0L );

        // Make sure we show the longs in the default locale.
        _numberFormat.setMaximumFractionDigits( 0 );
        _numberFormat.setParseIntegerOnly( true );
    }
    
    @Override
    protected TextField makeTextField() {
        return new LongEditor(
            clientProperties, "0", "", blankTextAllowed, 0, 2, 0, 4);
    }

    @Override
    protected Long getEditorValue() {
        final String textValue = textField.getText();
        if ( textValue == null ) {
            return null;
        }
        
        final long longValue = ( ( LongEditor ) textField ).fromString( textValue );
        
        return Long.valueOf( longValue );
    }

    @Override
    protected String getString() {
        final Long longValue = getItem();
        if ( longValue == null ) {
            return "";
        }
        
        // This text goes to the editor, so we don't want to clutter the user's
        // editing session with measurement units, but do need localization.
        final String stringValue 
            = ( ( LongEditor ) textField ).toFormattedString( longValue );
        
        return stringValue;
    }

    @Override
    protected String getTextValue() {
        final Long longValue = getItem();
        if ( longValue == null ) {
            return "";
        }
        
        final String textValue = ( ( LongEditor ) textField ).toString( longValue );
        
        return textValue;
    }

    @Override
    public final void setValue( final Long pValue ) {
        // Locally cache the new cachedValue, separately from the textField.
        cachedValue.set( pValue );

        // Now do whatever we do for all data types in the base class.
        super.setValue( pValue );
    }

    public final LongProperty cachedValueProperty() {
        return cachedValue;
    }

    public final long getCachedValue() {
        return cachedValue.get();
    }
    
    public final void setCachedValue( final long pCachedValue ) {
        // Locally cache the new cachedValue, separately from the textField.
        cachedValue.set( pCachedValue );
    }
}