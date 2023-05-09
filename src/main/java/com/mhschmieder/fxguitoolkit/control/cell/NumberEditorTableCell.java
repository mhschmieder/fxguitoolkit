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

public abstract class NumberEditorTableCell< RT, Number > extends EditorTableCell< RT, Number > {

    // Maintain a reference to the Measurement Unit label (can be blank).
    @SuppressWarnings("nls") protected String _measurementUnit = "";

    // Cache a number formatter for displaying the float values.
    protected final NumberFormat              _numberFormat;

    public NumberEditorTableCell( final boolean pAllowedToBeBlank ) {
        this( null, pAllowedToBeBlank );
    }

    public NumberEditorTableCell( final List< Integer > pUneditableRows,
                                 final boolean pAllowedToBeBlank ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank );

        // Generally, we prefer numeric fields to be centered.
        setAlignment( Pos.CENTER );

        // Make sure we show the numbers in the default locale.
        _numberFormat = NumberFormat.getNumberInstance( Locale.getDefault() );
        _numberFormat.setMinimumFractionDigits( 0 );
        _numberFormat.setMinimumIntegerDigits( 1 );
    }

    public final void setMeasurementUnit( final String measurementUnit ) {
        _measurementUnit = measurementUnit;
    }
}
