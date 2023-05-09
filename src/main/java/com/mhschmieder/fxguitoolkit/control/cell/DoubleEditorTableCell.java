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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;

// TODO: Use our DoubleEditor class instead, and pass the measurement unit?
public class DoubleEditorTableCell< RT, VT > extends EditorTableCell< RT, Double > {

    // Maintain a reference to the Measurement Unit label (can be blank).
    @SuppressWarnings("nls") private String _measurementUnit = "";

    // Cache a number formatter for displaying the double values.
    private final NumberFormat              _numberFormat;

    public DoubleEditorTableCell( final boolean pAllowedToBeBlank ) {
        this( null, pAllowedToBeBlank );
    }

    public DoubleEditorTableCell( final List< Integer > pUneditableRows,
                                  final boolean pAllowedToBeBlank ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank );

        // Generally, we prefer numeric fields to be centered.
        setAlignment( Pos.CENTER );

        // Use two decimal places of precision for numbers, in the default
        // locale.
        _numberFormat = NumberFormat.getNumberInstance( Locale.getDefault() );
        _numberFormat.setMinimumFractionDigits( 0 );
        _numberFormat.setMaximumFractionDigits( 2 );
        _numberFormat.setMinimumIntegerDigits( 1 );
        _numberFormat.setParseIntegerOnly( false );
    }

    @Override
    protected Double getEditorValue() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final String textValue = _textField.getText();
        final Double doubleValue = Double.valueOf( textValue );
        final Double editorValue = ( textValue == null ) || ( doubleValue == null )
            ? null
            : ( doubleValue.doubleValue() == Double.POSITIVE_INFINITY ) ? null : doubleValue;
        return editorValue;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getString() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final Double doubleValue = getItem();
        final String stringValue = ( doubleValue == null )
            ? ""
            : ( doubleValue.doubleValue() == Double.POSITIVE_INFINITY )
                ? "-"
                : _numberFormat.format( Double.valueOf( doubleValue.toString() ) )
                        + _measurementUnit;
        return stringValue;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTextValue() {
        // NOTE: This is a bit of a hack to allow invalid and/or impertinent
        // cells and to represent them with a consistent and intuitive rendering
        // that is globally understood as "no data".
        final Double doubleValue = getItem();
        final String textValue = ( doubleValue == null )
            ? ""
            : ( doubleValue.doubleValue() == Double.POSITIVE_INFINITY )
                ? "-"
                : Double.toString( doubleValue.doubleValue() );
        return textValue;
    }

    public final void setMeasurementUnit( final String measurementUnit ) {
        _measurementUnit = measurementUnit;
    }

}
