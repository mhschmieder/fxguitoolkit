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
import com.mhschmieder.fxguitoolkit.control.FloatEditor;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

public class FloatEditorTableCell< RT, VT > extends NumberEditorTableCell< RT, Float > {

    // Cache the raw Float representation of the data cachedValue.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final FloatProperty cachedValue;

    public FloatEditorTableCell( final boolean pAllowedToBeBlank,
                                 final ClientProperties pClientProperties ) {
        this( null, pAllowedToBeBlank, pClientProperties );
    }

    public FloatEditorTableCell( final List< Integer > pUneditableRows,
                                 final boolean pAllowedToBeBlank,
                                 final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank, pClientProperties );

        cachedValue = new SimpleFloatProperty( 0.0f );

        // Use two decimal places of precision for floats, in the default
        // locale.
        _numberFormat.setMaximumFractionDigits( 2 );
        _numberFormat.setParseIntegerOnly( false );
    }

   
    @Override
    protected TextField makeTextField() {
        return new FloatEditor(
            clientProperties, "0", "", blankTextAllowed, 0, 2, 0, 4);
    }

    @Override
    protected Float getEditorValue() {
        final String textValue = textField.getText();
        if ( textValue == null ) {
            return null;
        }
        
        final float floatValue = ( ( FloatEditor ) textField ).fromString( textValue );
        
        return Float.valueOf( floatValue );
    }

    @Override
    protected String getString() {
        final Float floatValue = getItem();
        if ( floatValue == null ) {
            return "";
        }
        
        // This text goes to the editor, so we don't want to clutter the user's
        // editing session with formatting and measurement units.
        // TODO: Determine whether we at least need to apply the Number
        //  Formatter for proper localization of commas, periods, etc.
        final String stringValue = Float.toString( floatValue );
        
        return stringValue;
    }

    @Override
    protected String getTextValue() {
        final Float floatValue = getItem();
        if ( floatValue == null ) {
            return "";
        }
        
        final String textValue = ( ( FloatEditor ) textField ).toString( floatValue );
        
        return textValue;
    }

    @Override
    public final void setValue( final Float pValue ) {
        // Locally cache the new cachedValue, separately from the textField.
        cachedValue.set( pValue );

        // Now do whatever we do for all data types in the base class.
        super.setValue( pValue );
    }

    public final FloatProperty cachedValueProperty() {
        return cachedValue;
    }

    public final float getCachedValue() {
        return cachedValue.get();
    }
    
    public final void setCachedValue( final float pCachedValue ) {
        // Locally cache the new cachedValue, separately from the textField.
        cachedValue.set( pCachedValue );
    }
}
