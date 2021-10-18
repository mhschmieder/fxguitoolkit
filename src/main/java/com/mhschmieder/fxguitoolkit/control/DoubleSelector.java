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

import com.mhschmieder.commonstoolkit.math.MathUtilities;
import com.mhschmieder.commonstoolkit.net.SessionContext;

import javafx.collections.FXCollections;
import javafx.scene.input.KeyEvent;

/**
 * This class formalizes aspects of list selection that are specific to
 * double precision floating point value sets.
 */
public class DoubleSelector extends NumberSelector {

    public DoubleSelector( final SessionContext sessionContext,
                           final int minFractionDigitsFormat,
                           final int maxFractionDigitsFormat,
                           final int minFractionDigitsParse,
                           final int maxFractionDigitsParse,
                           final boolean useLocale,
                           final String tooltipText,
                           final boolean toolbarContext,
                           final boolean editable,
                           final boolean searchable ) {
        // Always call the superclass constructor first!
        super( sessionContext,
               minFractionDigitsFormat,
               maxFractionDigitsFormat,
               minFractionDigitsParse,
               maxFractionDigitsParse,
               useLocale,
               tooltipText,
               toolbarContext,
               editable,
               searchable );
    }

    public DoubleSelector( final SessionContext sessionContext,
                           final int minFractionDigitsFormat,
                           final int maxFractionDigitsFormat,
                           final int minFractionDigitsParse,
                           final int maxFractionDigitsParse,
                           final boolean useLocale,
                           final String tooltipText,
                           final boolean toolbarContext,
                           final boolean editable,
                           final boolean searchable,
                           final double minimumValue,
                           final double maximumValue,
                           final double increment ) {
        this( sessionContext,
              minFractionDigitsFormat,
              maxFractionDigitsFormat,
              minFractionDigitsParse,
              maxFractionDigitsParse,
              useLocale,
              tooltipText,
              toolbarContext,
              editable,
              searchable );

        try {
            initComboBox( minimumValue, maximumValue, increment );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final double getDoubleValue() {
        final String formattedValue = getValue();
        final double doubleValue = MathUtilities.parseDouble( formattedValue, _numberFormat );
        return doubleValue;
    }

    @SuppressWarnings("nls")
    private final void initComboBox( final double minimumValue,
                                     final double maximumValue,
                                     final double increment ) {
        // Put together the monotonically increasing list of choices.
        final int numberOfChoices = ( int ) Math
                .floor( ( ( maximumValue - minimumValue ) + 1 ) / increment );
        final String[] doubleValues = new String[ numberOfChoices ];

        double doubleValue = minimumValue;
        for ( int i = 0; i < numberOfChoices; i++ ) {
            doubleValues[ i ] = MathUtilities.formatDouble( doubleValue, _numberFormat );
            doubleValue += increment;
        }

        // Ensure that most items are visible before scrolling, but also make
        // sure the overall list doesn't get unwieldy.
        setVisibleRowCount( Math.min( numberOfChoices, 25 ) );

        // Set the non-editable list of supported double values.
        setItems( FXCollections.observableArrayList( doubleValues ) );

        // Restrict keyboard input to numerals, sign, and delimiters.
        final String allowedCharacters = ( minimumValue < 0d )
            ? ( maximumValue > 0d ) ? "[0-9.,+-]" : "[0-9.,-]"
            : ( maximumValue > 0d ) ? "[0-9.,+]" : "[0-9.,]";
        addEventFilter( KeyEvent.KEY_TYPED, keyEvent -> {
            if ( !keyEvent.getCharacter().matches( allowedCharacters ) ) {
                keyEvent.consume();
            }
        } );
    }

    public final void setDoubleValue( final double doubleValue ) {
        final String formattedValue = MathUtilities.formatDouble( doubleValue, _numberFormat );
        setValue( formattedValue );
    }

}
