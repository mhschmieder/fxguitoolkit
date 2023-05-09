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

import javafx.geometry.Pos;

// TODO: Use our IntegerEditor class instead, and pass the measurement unit?
public class IntegerEditorTableCell< RT, VT > extends NumberEditorTableCell< RT, Integer > {

    public IntegerEditorTableCell( final boolean pAllowedToBeBlank ) {
        this( null, pAllowedToBeBlank );
    }

    public IntegerEditorTableCell( final List< Integer > pUneditableRows,
                                   final boolean pAllowedToBeBlank ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank );

        // Generally, we prefer numeric fields to be centered.
        setAlignment( Pos.CENTER );

        // Make sure we show the integers in the default locale.
        _numberFormat.setMaximumFractionDigits( 0 );
        _numberFormat.setParseIntegerOnly( true );
    }

    @Override
    protected Integer getEditorValue() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final String textValue = _textField.getText();
        final Integer integerValue = Integer.valueOf( textValue );
        final Integer editorValue = ( textValue == null ) || ( integerValue == null )
            ? null
            : integerValue;
        return editorValue;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getString() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final Integer integerValue = getItem();
        final String stringValue = ( integerValue == null )
            ? ""
            : _numberFormat.format( Integer.valueOf( integerValue.toString() ) )
                        + _measurementUnit;
        return stringValue;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTextValue() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final Integer integerValue = getItem();
        final String textValue = ( integerValue == null )
            ? ""
            : Integer.toString( integerValue.intValue() );
        return textValue;
    }
}