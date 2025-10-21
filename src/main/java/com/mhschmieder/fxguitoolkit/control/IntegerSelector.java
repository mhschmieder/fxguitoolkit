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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.jcommons.text.NumberFormatUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyEvent;
import org.apache.commons.math3.util.FastMath;

/**
 * This class formalizes aspects of list selection that are specific to
 * integer value sets.
 * <p>
 * TODO: Make a long version, but there isn't library support in a few
 *  critical places in terms of using this implementation as a template.
 *  Do we need to use Double factory methods and classes and cast to Long?
 */
public class IntegerSelector extends NumberSelector {

    public IntegerSelector( final ClientProperties clientProperties,
                            final boolean useLocale,
                            final String tooltipText,
                            final boolean applyToolkitCss,
                            final boolean editable,
                            final boolean searchable ) {
        // Always call the superclass constructor first!
        super( clientProperties,
               0,
               0,
               0,
               0,
               useLocale,
               tooltipText,
               applyToolkitCss,
               editable,
               searchable );
    }

    public IntegerSelector( final ClientProperties clientProperties,
                            final boolean useLocale,
                            final String tooltipText,
                            final boolean applyToolkitCss,
                            final boolean editable,
                            final boolean searchable,
                            final int minimumValue,
                            final int maximumValue,
                            final int increment ) {
        this( clientProperties, 
              useLocale, 
              tooltipText, 
              applyToolkitCss, 
              editable, 
              searchable );

        try {
            initComboBox( minimumValue, maximumValue, increment );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initComboBox( final int minimumValue,
                                     final int maximumValue,
                                     final int increment ) {
        // Put together the monotonically increasing list of choices.
        final int numberOfChoices = ( int ) FastMath
                .floor( ( ( maximumValue - minimumValue ) + 1 ) / increment );
        final String[] integerValues = new String[ numberOfChoices ];

        int integerValue = minimumValue;
        for ( int i = 0; i < numberOfChoices; i++ ) {
            integerValues[ i ] = NumberFormatUtilities.formatInteger( integerValue, _numberFormat );
            integerValue += increment;
        }

        // Ensure that most items are visible before scrolling, but also make
        // sure the overall list doesn't get unwieldy.
        setVisibleRowCount( FastMath.min( numberOfChoices, 25 ) );

        // Set the non-editable list of supported integer values.
        setItems( FXCollections.observableArrayList( integerValues ) );

        // Restrict keyboard input to numerals, sign, and delimiters.
        final String allowedCharacters = ( minimumValue < 0 )
            ? ( maximumValue > 0 ) ? "[0-9.,+-]" : "[0-9.,-]"
            : ( maximumValue > 0 ) ? "[0-9.,+]" : "[0-9.,]";
        addEventFilter( KeyEvent.KEY_TYPED, keyEvent -> {
            if ( !keyEvent.getCharacter().matches( allowedCharacters ) ) {
                keyEvent.consume();
            }
        } );
    }

    public final int getIntegerValue() {
        final String formattedValue = getValue();
        final int integerValue = NumberFormatUtilities.parseInteger( formattedValue, _numberFormat );
        return integerValue;
    }

    public final void setIntegerValue( final int integerValue ) {
        final String formattedValue = NumberFormatUtilities.formatInteger( integerValue,
                                                                           _numberFormat );
        setValue( formattedValue );
    }

}
