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

import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import org.apache.commons.math3.util.FastMath;

import java.text.ParseException;

/**
 * This class formalizes aspects of text editing that are specific to integers.
 */
public class IntegerEditor extends NumberEditor {

    // Cache the minimum allowed data value (negative).
    protected int                 _minimumValue;

    // Cache the maximum allowed data value (positive).
    protected int                 _maximumValue;

    // Cache the default data value.
    protected int                 _defaultValue;

    // The amount to increment or decrement by, using the arrow keys.
    protected int                 _valueIncrement;

    // Cache the raw numeric representation of the data value.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final IntegerProperty value;

    public IntegerEditor( final ClientProperties clientProperties,
                          final String initialText,
                          final String tooltipText,
                          final boolean applyToolkitCss ) {
        this( clientProperties, initialText, tooltipText, applyToolkitCss, -Integer.MAX_VALUE, Integer.MAX_VALUE );
    }

    public IntegerEditor( final ClientProperties clientProperties,
                          final String initialText,
                          final String tooltipText,
                          final boolean applyToolkitCss,
                          final int minimumValue,
                          final int maximumValue ) {
        this( clientProperties, initialText, tooltipText, applyToolkitCss, minimumValue, maximumValue, 0, 0 );
    }

    public IntegerEditor( final ClientProperties clientProperties,
                          final String initialText,
                          final String tooltipText,
                          final boolean applyToolkitCss,
                          final int minimumValue,
                          final int maximumValue,
                          final int defaultValue,
                          final int valueIncrement ) {
        // Always call the superclass constructor first!
        super( clientProperties, initialText, tooltipText, applyToolkitCss );

        _defaultValue = defaultValue;

        _minimumValue = minimumValue;
        _maximumValue = maximumValue;

        _valueIncrement = valueIncrement;

        value = new SimpleIntegerProperty( _defaultValue );

        _reset = () -> setText( Integer.toString( _defaultValue ) );

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initEditor() {
        // Now it is safe to restrict keyboard input while referencing class
        // variables and potentially local number formatting instances.
        restrictKeyboardInput();

        // Make sure the value property is clamped to the required range, then
        // update the text field to be in sync with the clamped value.
        valueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            if ( newValue == null ) {
                setText( "" );
            }
            else {
                // If limits were established, enforce them. Always check
                // though, to avoid overflow and underflow.
                final int clampedValue = getClampedValue( newValue.intValue() );

                // Format the number to match how we display committed values.
                updateText( clampedValue );
            }
        } );

        setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // NOTE: Nothing to do, as ENTER is best handled via onAction.
                break;
            case ESCAPE:
                // Revert to the most recent committed value.
                cancelEdit();

                Platform.runLater( () -> {
                    // Update the displayed text to include all of the
                    // decorations.
                    decorateText();

                    // Reselect the reformatted text, to mimic Focus Gained.
                    selectAll();
                } );

                break;
            case TAB:
                // NOTE: Nothing to do, as Text Input Controls commit edits and
                // then release focus when the TAB key is pressed, so the Focus
                // Lost handler is where value restrictions should be applied.
                break;
            case UP:
                // Increment the current value by the set amount.
                if ( _valueIncrement != 0 ) {
                    setValue( getValue() + _valueIncrement );
                }

                break;
            case DOWN:
                // Decrement the current value by the set amount.
                if ( _valueIncrement != 0 ) {
                    setValue( getValue() - _valueIncrement );
                }

                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }

    @Override
    public String getAllowedCharacters() {
        // Restrict keyboard input to numerals, sign, and delimiters.
        final String allowedCharacters = ( _minimumValue < 0 )
            ? ( _maximumValue > 0 ) ? "[0-9.,+-]" : "[0-9.,-]"
            : ( _maximumValue > 0 ) ? "[0-9.,+]" : "[0-9.,]";
        return allowedCharacters;
    }

    @Override
    public final String getDecoratedText() {
        // Get the most recently committed value.
        final int savedValue = getValue();

        // Show the number with units to indicate we committed edits.
        final String formattedText = toString( savedValue );

        // Decorate the text as that is the context of interest.
        final String decoratedText = getDecoratedText( savedValue, formattedText );

        return decoratedText;
    }

    // NOTE: This is an opportunity to pre-parse the typed text before
    // converting to a number, such as when we disallow positive numbers (e.g.).
    public String getDecoratedText( final int savedValue, final String savedText ) {
        final String decoratedText = savedText;

        return decoratedText;
    }

    @Override
    public final void updateText() {
        // Get the most recently committed value.
        final int savedValue = getValue();

        // Update the displayed text to match the cached value.
        updateText( savedValue );
    }

    public final void updateText( final int savedValue ) {
        // Show the number with units to indicate we committed edits.
        final String formattedValue = toString( savedValue );

        // Update the displayed text to match the cached value.
        setText( formattedValue );
    }

    @Override
    public final void clampValue() {
        // Get the clamped, edited value, stripped of decorations and
        // formatting.
        final int clampedValue = getClampedValue();

        // Update the cached property from the clamped, edited value.
        setValue( clampedValue );
    }

    public final int getClampedValue() {
        // The fromString method performs input validation.
        final String undecoratedText = getUndecoratedText();
        final int clampedValue = fromString( undecoratedText );

        return clampedValue;
    }

    public int getClampedValue( final int unclampedValue ) {
        final int clampedValue =
                FastMath.min( FastMath.max( unclampedValue, _minimumValue ), _maximumValue );
        return clampedValue;
    }

    public final int getMinimumValue() {
        return _minimumValue;
    }

    public final void setMinimumValue( final int minimumValue ) {
        _minimumValue = minimumValue;
    }

    public final int getMaximumValue() {
        return _maximumValue;
    }

    public final void setMaximumValue( final int maximumValue ) {
        _maximumValue = maximumValue;
    }

    public final void setValueIncrement( final int pValueIncrement ) {
        _valueIncrement = pValueIncrement;
    }

    public final int getValue() {
        return value.get();
    }

    public final void setValue( final int pValue ) {
        value.set( pValue );
    }

    public final IntegerProperty valueProperty() {
        return value;
    }

    /**
     * Converts the specified {@link String} into its integer value.
     * <p>
     * A {@code null}, empty, or otherwise invalid argument returns zero and
     * also executes the textField reset callback, if any.
     *
     * @param stringValue
     *            The {@link String} to convert
     * @return The integer value of {@code stringValue}
     * @see #setReset
     */
    public int fromString( final String stringValue ) {
        // Return with current value vs. penalizing user for internal errors.
        final int currentValue = getValue();
        if ( ( stringValue == null ) || stringValue.trim().isEmpty() ) {
            return currentValue;
        }

        // If the user typed a formatted number with units, parse it exactly;
        // otherwise strip the units and try to directly convert the string to
        // an integer.
        int intValue = currentValue;
        try {
            final Number numericValue = _numberParse.parse( stringValue );
            intValue = numericValue.intValue();
        }
        catch ( final ParseException pe ) {
            final int measurementUnitIndex = stringValue.indexOf( _measurementUnitString );
            try {
                final String numericString = ( measurementUnitIndex < 0 )
                    ? stringValue
                    : stringValue.substring( 0, measurementUnitIndex + 1 );
                intValue = Integer.parseInt( numericString );
            }
            catch ( IndexOutOfBoundsException | NumberFormatException | NullPointerException e ) {
                if ( _reset != null ) {
                    _reset.run();
                }
            }
        }

        // If limits were established, enforce them by range-checking and
        // restricting the parsed or defaulted value. Always check though, to
        // avoid overflow and underflow conditions.
        final int clampedValue = getClampedValue( intValue );

        return clampedValue;
    }

    /**
     * Converts the specified integer into its {@link String} form, with the
     * measurement unit string appended for a complete representation.
     * <p>
     * A {@code null} argument is converted into the default value.
     *
     * @param intValue
     *            The integer to convert
     * @return The {@link String} form of {@code intValue}
     */
    public final String toString( final int intValue ) {
        // Do a simple string conversion to a number, in case we get arithmetic
        // exceptions using the number formatter.
        String stringValue = toFormattedString( intValue );
        stringValue += _measurementUnitString;

        return stringValue;
    }

    /**
     * Converts the specified integer into its {@link String} form.
     * <p>
     * A {@code null} argument is converted into the default value.
     *
     * @param intValue
     *            The integer to convert
     * @return The {@link String} form of {@code intValue}
     */
    public final String toFormattedString( final int intValue ) {
        // Do a simple string conversion to a number, in case we get arithmetic
        // exceptions using the number formatter.
        String stringValue = Integer.toString( intValue );

        try {
            stringValue = _numberFormat.format( intValue );
        }
        catch ( final ArithmeticException ae ) {
            ae.printStackTrace();
        }

        return stringValue;
    }
}