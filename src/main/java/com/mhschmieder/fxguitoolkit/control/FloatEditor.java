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
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.input.KeyCode;
import org.apache.commons.math3.util.FastMath;

import java.text.ParseException;

/**
 * This class formalizes aspects of text editing that are specific to floats.
 */
public class FloatEditor extends NumberEditor {

    // Cache the minimum allowed data value (negative).
    protected float             _minimumValue;

    // Cache the maximum allowed data value (positive).
    protected float             _maximumValue;

    // Cache the default data value.
    protected float             _defaultValue;

    // The amount to increment or decrement by, using the arrow keys.
    protected float             _valueIncrement;

    // Cache the raw numeric representation of the data value.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final FloatProperty value;

    public FloatEditor( final ClientProperties clientProperties,
                        final String initialText,
                        final String tooltipText,
                        final boolean applyToolkitCss,
                        final int minFractionDigitsFormat,
                        final int maxFractionDigitsFormat,
                        final int minFractionDigitsParse,
                        final int maxFractionDigitsParse ) {
        this( clientProperties,
              initialText,
              tooltipText,
              applyToolkitCss,
              minFractionDigitsFormat,
              maxFractionDigitsFormat,
              minFractionDigitsParse,
              maxFractionDigitsParse,
              -Float.MAX_VALUE,
              Float.MAX_VALUE );
    }

    public FloatEditor( final ClientProperties clientProperties,
                        final String initialText,
                        final String tooltipText,
                        final boolean applyToolkitCss,
                        final int minFractionDigitsFormat,
                        final int maxFractionDigitsFormat,
                        final int minFractionDigitsParse,
                        final int maxFractionDigitsParse,
                        final float minimumValue,
                        final float maximumValue ) {
        this( clientProperties,
              initialText,
              tooltipText,
              applyToolkitCss,
              minFractionDigitsFormat,
              maxFractionDigitsFormat,
              minFractionDigitsParse,
              maxFractionDigitsParse,
              minimumValue,
              maximumValue,
              0.0f,
              0.0f );
    }

    public FloatEditor( final ClientProperties clientProperties,
                        final String initialText,
                        final String tooltipText,
                        final boolean applyToolkitCss,
                        final int minFractionDigitsFormat,
                        final int maxFractionDigitsFormat,
                        final int minFractionDigitsParse,
                        final int maxFractionDigitsParse,
                        final float minimumValue,
                        final float maximumValue,
                        final float defaultValue,
                        final float valueIncrement ) {
        // Always call the superclass constructor first!
        super( clientProperties, initialText, tooltipText, applyToolkitCss );

        _defaultValue = defaultValue;

        _minimumValue = minimumValue;
        _maximumValue = maximumValue;

        _valueIncrement = valueIncrement;

        value = new SimpleFloatProperty( _defaultValue );

        _reset = () -> setText( Double.toString( _defaultValue ) );

        try {
            initEditor( minFractionDigitsFormat,
                        maxFractionDigitsFormat,
                        minFractionDigitsParse,
                        maxFractionDigitsParse );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initEditor( final int minFractionDigitsFormat,
                                   final int maxFractionDigitsFormat,
                                   final int minFractionDigitsParse,
                                   final int maxFractionDigitsParse ) {
        // Set the precision for floating-point text formatting.
        _numberFormat.setMinimumFractionDigits( minFractionDigitsFormat );
        _numberFormat.setMaximumFractionDigits( maxFractionDigitsFormat );

        // Set the precision for floating-point text formatting.
        _numberParse.setMinimumFractionDigits( minFractionDigitsParse );
        _numberParse.setMaximumFractionDigits( maxFractionDigitsParse );

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
                final float clampedValue = getClampedValue( newValue.floatValue() );

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
                if ( _valueIncrement != 0.0f ) {
                    setValue( getValue() + _valueIncrement );
                }

                break;
            case DOWN:
                // Decrement the current value by the set amount.
                if ( _valueIncrement != 0.0f ) {
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
        final float savedValue = getValue();

        // Show the number with units to indicate we committed edits.
        final String formattedText = toString( savedValue );

        // Decorate the text as that is the context of interest.
        final String decoratedText = getDecoratedText( savedValue, formattedText );

        return decoratedText;
    }

    // NOTE: This is an opportunity to pre-parse the typed text before
    // converting to a number, such as when we disallow positive numbers (e.g.).
    public String getDecoratedText( final float savedValue, final String savedText ) {
        final String decoratedText = savedText;

        return decoratedText;
    }

    @Override
    public final void updateText() {
        // Get the most recently committed value.
        final float savedValue = getValue();

        // Update the displayed text to match the cached value.
        updateText( savedValue );
    }

    public final void updateText( final float savedValue ) {
        // Show the number with units to indicate we committed edits.
        final String formattedValue = toString( savedValue );

        // Update the displayed text to match the cached value.
        setText( formattedValue );
    }

    @Override
    public final void clampValue() {
        // Get the clamped, edited value, stripped of decorations and
        // formatting.
        final float clampedValue = getClampedValue();

        // Update the cached property from the clamped, edited value.
        setValue( clampedValue );
    }

    public final float getClampedValue() {
        // The fromString method performs input validation.
        final String undecoratedText = getUndecoratedText();
        final float clampedValue = fromString( undecoratedText );

        return clampedValue;
    }

    public float getClampedValue( final float unclampedValue ) {
        final float clampedValue = FastMath.min( 
            FastMath.max( unclampedValue, _minimumValue ), _maximumValue );
        return clampedValue;
    }

    public final float getMinimumValue() {
        return _minimumValue;
    }

    public final void setMinimumValue( final float minimumValue ) {
        _minimumValue = minimumValue;
    }

    public final float getMaximumValue() {
        return _maximumValue;
    }

    public final void setMaximumValue( final float maximumValue ) {
        _maximumValue = maximumValue;
    }

    public final void setValueIncrement( final float pValueIncrement ) {
        _valueIncrement = pValueIncrement;
    }

    public final float getValue() {
        return value.get();
    }

    public final void setValue( final float pValue ) {
        value.set( pValue );
    }

    public final FloatProperty valueProperty() {
        return value;
    }

    /**
     * Converts the specified {@link String} into its float value.
     * <p>
     * A {@code null}, empty, or otherwise invalid argument returns zero and
     * also executes the textField reset callback, if any.
     *
     * @param stringValue
     *            The {@link String} to convert
     * @return The float value of {@code stringValue}
     * @see #setReset
     */
    public float fromString( final String stringValue ) {
        // Return with current value vs. penalizing user for internal errors.
        final float currentValue = getValue();
        if ( ( stringValue == null ) || stringValue.trim().isEmpty() ) {
            return currentValue;
        }

        // If the user typed a formatted number with units, parse it exactly;
        // otherwise strip the units and try to directly convert the string to a
        // double precision floating-point number.
        float floatValue = currentValue;
        try {
            final Number numericValue = _numberParse.parse( stringValue );
            floatValue = numericValue.floatValue();
        }
        catch ( final ParseException pe ) {
            final int measurementUnitIndex = stringValue.indexOf( _measurementUnitString );
            try {
                final String numericString = ( measurementUnitIndex < 0 )
                    ? stringValue
                    : stringValue.substring( 0, measurementUnitIndex + 1 );
                floatValue = Float.parseFloat( numericString );
            }
            catch ( IndexOutOfBoundsException | NumberFormatException | NullPointerException e ) {
                if ( _reset != null ) {
                    _reset.run();
                }
            }
        }

        // If necessary, adjust the precision level based on magnitude ranges.
        final float precisionAdjustedValue = adjustPrecision( floatValue );

        // If limits were established, enforce them by range-checking and
        // restricting the parsed or defaulted value. Always check though, to
        // avoid overflow and underflow conditions.
        final float clampedValue = getClampedValue( precisionAdjustedValue );

        return clampedValue;
    }

    /**
     * Converts the specified float into its {@link String} form, with the
     * measurement unit string appended for a complete representation.
     * <p>
     * A {@code null} argument is converted into the default value.
     *
     * @param floatValue
     *            The float to convert
     * @return The {@link String} form of {@code floatValue}
     */
    public final String toString( final float floatValue ) {
        // If the new error text feature has been set, use it for illegal values.
        if ( !Float.isFinite( floatValue ) && !_errorText.isEmpty() ) {
            return _errorText;
        }
        
       // Do a simple string conversion to a number, in case we get arithmetic
        // exceptions using the number formatter.
        String stringValue = toFormattedString( floatValue );
        stringValue += _measurementUnitString;

        return stringValue;
    }

    /**
     * Converts the specified float into its {@link String} form.
     * <p>
     * A {@code null} argument is converted into the default value.
     *
     * @param floatValue
     *            The float to convert
     * @return The {@link String} form of {@code floatValue}
     */
    public final String toFormattedString( final float floatValue ) {
        // Do a simple string conversion to a number, in case we get arithmetic
        // exceptions using the number formatter.
        String stringValue = Float.toString( floatValue );

        try {
            stringValue = _numberFormat.format( floatValue );
        }
        catch ( final ArithmeticException ae ) {
            ae.printStackTrace();
        }

        return stringValue;
    }

    public float adjustPrecision( final float floatValue ) {
        // By default, unless overridden, there is no further adjustment beyond
        // what is already set in the Number Parser.
        return floatValue;
    }
}
