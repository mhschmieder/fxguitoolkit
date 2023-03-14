/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.commonstoolkit.text.NumberFormatUtilities;
import com.mhschmieder.commonstoolkit.text.TextUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Converts between user-edited strings and {@link Double} values.
 * <p>
 * Accepts an optional {@link Runnable} that resets the editor on
 * {@link NumberFormatException}, or a {@link TextField} or {@link Spinner} that
 * is preemptively monitored for invalid input during typing, and restricts
 * valid input to a specified range when committed.
 */
public final class DoubleSpinnerStringConverter extends StringConverter< Double > {

    /**
     * Creates an {@link DoubleSpinnerStringConverter} for the specified
     * {@link Spinner}.
     * <p>
     * Uses the {@link TextField} and minimum and maximum values of the
     * specified {@link Spinner} for construction, and also sets the new
     * {@link DoubleSpinnerStringConverter} on its
     * {@link SpinnerValueFactory.DoubleSpinnerValueFactory}.
     *
     * @param doubleSpinner
     *            The {@link Spinner} to create a
     *            {@link DoubleSpinnerStringConverter} for
     * @param valueDescriptor
     *            The descriptor for the spinner's value category, used mostly
     *            in tool tips
     * @param defaultNumericValue
     *            The default {@link Double} value
     * @param maximumSpinnerWidth
     *            The maximum allowed width of the spinner
     * @param wrapAround
     *            The flag for whether to wrap from max to min (and vice-versa)
     * @param numericFormatterPattern
     *            The specific pattern to apply for the number representation
     * @param measurementUnitString
     *            The string representation of the Measurement Unit to tag onto
     *            the numbers after they've been edited and committed
     * @param locale
     *            The locale to use for number formatting
     * @return The new {@link DoubleSpinnerStringConverter}
     * @throws NullPointerException
     *             If {@code spinner} is {@code null}
     */
    public static DoubleSpinnerStringConverter createFor( final Spinner< Double > doubleSpinner,
                                                          final String valueDescriptor,
                                                          final double defaultNumericValue,
                                                          final double maximumSpinnerWidth,
                                                          final boolean wrapAround,
                                                          final String numericFormatterPattern,
                                                          final String measurementUnitString,
                                                          final Locale locale ) {
        // Use a decimal formatter that defaults to doubles when possible.
        final NumberFormat numberFormat = NumberFormatUtilities
                .getUnitDecoratedDecimalFormat( numericFormatterPattern,
                                                measurementUnitString,
                                                locale );

        final SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory =
                                                                                ( SpinnerValueFactory.DoubleSpinnerValueFactory ) doubleSpinner
                                                                                        .getValueFactory();

        final double minimumNumericValue = spinnerValueFactory.getMin();
        final double maximumNumericValue = spinnerValueFactory.getMax();
        final String tooltipText = TextUtilities.getValueRangeTooltipText( valueDescriptor,
                                                                           minimumNumericValue,
                                                                           maximumNumericValue,
                                                                           numberFormat );

        final TextField editor = doubleSpinner.getEditor();
        final DoubleSpinnerStringConverter converter =
                                                     new DoubleSpinnerStringConverter( spinnerValueFactory,
                                                                                       editor,
                                                                                       tooltipText,
                                                                                       minimumNumericValue,
                                                                                       maximumNumericValue,
                                                                                       defaultNumericValue,
                                                                                       measurementUnitString,
                                                                                       numberFormat );

        // Apply the string conversions to/from numbers (with units).
        spinnerValueFactory.setConverter( converter );

        // Honor the wrap-around setting from highest to lowest value.
        spinnerValueFactory.setWrapAround( wrapAround );

        // Set the attributes and callbacks common to all Number Spinners.
        GuiUtilities
                .applyNumberSpinnerAttributes( doubleSpinner, tooltipText, maximumSpinnerWidth );

        return converter;
    }

    private SpinnerValueFactory.DoubleSpinnerValueFactory _spinnerValueFactory;
    private TextField                                     _editor;

    private Runnable                                      _reset;

    private double                                        _minimumNumericValue;
    private double                                        _maximumNumericValue;
    private double                                        _defaultNumericValue;

    private String                                        _measurementUnitString;

    private NumberFormat                                  _numberFormat;

    /**
     * Creates an {@link DoubleSpinnerStringConverter}.
     * <p>
     * Swallows {@link NumberFormatException} but does nothing in response until
     * {@link #setReset} is defined.
     */
    public DoubleSpinnerStringConverter() {}

    /**
     * Creates an {@link DoubleSpinnerStringConverter} with an editor reset
     * callback.
     * <p>
     * Specifying {@code null} has the same effect as the default constructor.
     *
     * @param reset
     *            The {@link Runnable} to call upon
     *            {@link NumberFormatException}
     */
    public DoubleSpinnerStringConverter( final Runnable reset ) {
        _reset = reset;
    }

    /**
     * Creates an {@link DoubleSpinnerStringConverter} with the specified input
     * range.
     * <p>
     * Preemptively monitors {@code editor} to reject any invalid characters
     * during typing, restricts {@code editor} to [{@code minimumNumericValue},
     * {@code maximumNumericValue}] (inclusive) when valid text is committed,
     * and resets {@code editor} to the default value when invalid text is
     * committed.
     *
     * @param spinnerValueFactory
     *            The {@link SpinnerValueFactory} that regulates the supported
     *            value list for the associated {@link Spinner}
     * @param editor
     *            The {@link TextField} providing user-edited strings
     * @param tooltipText
     *            The pre-formatted text to display for the tool tip
     * @param minimumNumericValue
     *            The smallest valid {@link Double} value
     * @param maximumNumericValue
     *            The greatest valid {@link Double} value
     * @param defaultNumericValue
     *            The default {@link Double} value
     * @param measurementUnitString
     *            The string representation of the Measurement Unit to tag onto
     *            the numbers after they've been edited and committed
     * @param numberFormat
     *            The number formatter to apply for the number representation
     * @throws NullPointerException
     *             If {@code editor} is {@code null}
     */
    public DoubleSpinnerStringConverter( final SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory,
                                         final TextField editor,
                                         final String tooltipText,
                                         final double minimumNumericValue,
                                         final double maximumNumericValue,
                                         final double defaultNumericValue,
                                         final String measurementUnitString,
                                         final NumberFormat numberFormat ) {
        if ( spinnerValueFactory == null ) {
            throw new NullPointerException( "spinnerValueFactory" ); //$NON-NLS-1$
        }

        if ( editor == null ) {
            throw new NullPointerException( "editor" ); //$NON-NLS-1$
        }

        _spinnerValueFactory = spinnerValueFactory;
        _editor = editor;

        _minimumNumericValue = minimumNumericValue;
        _maximumNumericValue = maximumNumericValue;
        _defaultNumericValue = defaultNumericValue;

        _measurementUnitString = measurementUnitString;
        _numberFormat = numberFormat;

        _reset = () -> _editor.setText( Double.toString( _defaultNumericValue ) );

        _editor.setTooltip( new Tooltip( tooltipText ) );

        // Restrict keyboard input to numerals, sign, and delimiters.
        final String allowedCharacters = ( _minimumNumericValue < 0 ) ? "[0-9.,-]" : "[0-9.,]"; //$NON-NLS-1$ //$NON-NLS-2$
        _editor.addEventFilter( KeyEvent.KEY_TYPED, keyEvent -> {
            if ( !keyEvent.getCharacter().matches( allowedCharacters ) ) {
                keyEvent.consume();
            }
        } );

        // Restrict direct input to valid numerical characters.
        _editor.textProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            // Return with old value vs. penalizing user for internal errors.
            if ( ( newValue == null ) || newValue.trim().isEmpty() ) {
                Platform.runLater( () -> _editor.setText( oldValue ) );
                return;
            }

            // Handle differences in behavior for negative values allowed.
            if ( ( _minimumNumericValue < 0.0d ) ) {
                // Special case: minus sign if negative values allowed.
                if ( newValue.endsWith( "-" ) ) { //$NON-NLS-1$
                    if ( newValue.length() > 1 ) {
                        Platform.runLater( () -> _editor.setText( "-" ) ); //$NON-NLS-1$
                    }
                    return;
                }
            }

            // Revert to oldValue if newValue cannot be parsed.
            // TODO: Review whether this should be removed, as we do validity
            // checks at the time edits are committed and that may be enough.
            try {
                _numberFormat.parse( newValue );
            }
            catch ( final ParseException pe ) {
                final int measurementUnitIndex = newValue.indexOf( _measurementUnitString );
                try {
                    final String numericString = ( measurementUnitIndex < 0 )
                        ? newValue
                        : newValue.substring( 0, measurementUnitIndex + 1 );
                    Double.parseDouble( numericString );
                }
                catch ( final Exception e ) {
                    Platform.runLater( () -> _editor.setText( oldValue ) );
                }
            }
        } );

        // Validate committed input and restrict to legal range.
        final EventHandler< ActionEvent > defaultHandler = _editor.getOnAction();
        _editor.setOnAction( evt -> {
            // Clamp the committed value to the allowed range.
            clampValue();

            // Pass this event on to the default handler (releases focus).
            if ( defaultHandler != null ) {
                defaultHandler.handle( evt );
            }
        } );

        // When focus is lost, commit the changes; otherwise strip the unit.
        // NOTE: This callback is for manual edits, after they are committed.
        _editor.focusedProperty().addListener( ( observableValue, wasFocused, isNowFocused ) -> {
            if ( !isNowFocused ) {
                // Save edits from the Text Field to the property bean. Commit
                // the current selection as-is, or focus gets frozen.
                _editor.commitValue();

                // Clamp the committed value to the allowed range.
                clampValue();
            }
        } );

        // Unfortunately, it is up to us to commit edits from TAB focus.
        _editor.setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // Commit the current selection as-is, or focus gets frozen.
                _editor.commitValue();

                // Clamp the committed value to the allowed range.
                clampValue();

                break;
            case ESCAPE:
                // Revert to the previous selection.
                // NOTE: This code may never be reached; ESC must be caught at
                // a higher level and consumed. Need to debug to see if called.
                _editor.cancelEdit();

                // Try to force the previous value to reassert and display.
                Platform.runLater( () -> {
                    final Double canceledValue = getRestrictedValue();
                    spinnerValueFactory.setValue( canceledValue );
                } );

                break;
            case TAB:
                // Commit the current selection as-is, or focus gets frozen.
                _editor.commitValue();

                // Clamp the committed value to the allowed range.
                clampValue();

                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }

    public void clampValue() {
        final double restrictedValue = getRestrictedValue();

        try {
            final String restrictedString = _numberFormat.format( restrictedValue );
            Platform.runLater( () -> _editor.setText( restrictedString ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();

            final String doubleString = Double.toString( restrictedValue );
            Platform.runLater( () -> _editor.setText( doubleString ) );
        }

        // Update the spinner to match the clamped value.
        Platform.runLater( () -> {
            final String editedText = _editor.getText();
            final Double editedValue = fromString( editedText );
            _spinnerValueFactory.setValue( editedValue );
        } );
    }

    /**
     * Converts the specified {@link String} into its {@link Double} value.
     * <p>
     * A {@code null}, empty, or otherwise invalid argument returns zero and
     * also executes the editor reset callback, if any.
     *
     * @param stringValue
     *            The {@link String} to convert
     * @return The {@link Double} value of {@code stringValue}
     * @see #setReset
     */
    @Override
    public Double fromString( final String stringValue ) {
        // Return with default value vs. penalizing user for internal errors.
        final double defaultValue = _defaultNumericValue;
        if ( ( stringValue == null ) || stringValue.trim().isEmpty() ) {
            return defaultValue;
        }

        // If the user typed a formatted number with units, parse it exactly;
        // otherwise strip the units and try to directly convert the string to a
        // double.
        double newValue = defaultValue;
        try {
            final Number numericValue = _numberFormat.parse( stringValue );
            newValue = numericValue.doubleValue();
        }
        catch ( final ParseException pe ) {
            final int measurementUnitIndex = stringValue.indexOf( _measurementUnitString );
            try {
                final String numericString = ( measurementUnitIndex < 0 )
                    ? stringValue
                    : stringValue.substring( 0, measurementUnitIndex + 1 );
                newValue = Double.parseDouble( numericString );
            }
            catch ( final Exception e ) {
                if ( _reset != null ) {
                    _reset.run();
                }
            }
        }

        // If limits were established, enforce them by range-checking and
        // restricting the parsed or defaulted value. Always check though, to
        // avoid overflow and underflow conditions.
        final double clampedValue = getClampedValue( newValue );

        return clampedValue;
    }

    public double getClampedValue( final double unclampedValue ) {
        final double clampedValue = FastMath.min( FastMath.max( unclampedValue, _minimumNumericValue ),
                                              _maximumNumericValue );
        return clampedValue;
    }

    public double getRestrictedValue() {
        // The fromString method performs input validation.
        final String text = _editor.getText();
        final double restrictedValue = fromString( text );

        return restrictedValue;
    }

    /**
     * Sets the editor reset callback.
     * <p>
     * Specify {@code null} to clear a previously set {@link Runnable}. When
     * creating an {@link DoubleSpinnerStringConverter} for a {@link TextField}
     * or
     * {@link Spinner}, this callback is automatically defined to reset
     * committed invalid input to the closest value to zero within the legal
     * range. Setting a different callback will overwrite this functionality.
     *
     * @param reset
     *            The {@link Runnable} to call upon
     *            {@link NumberFormatException}
     * @see #fromString
     */
    public void setReset( final Runnable reset ) {
        _reset = reset;
    }

    /**
     * Converts the specified {@link Double} into its {@link String} form.
     * <p>
     * A {@code null} argument is converted into the default value.
     *
     * @param doubleValue
     *            The {@link Double} to convert
     * @return The {@link String} form of {@code doubleValue}
     */
    @Override
    public String toString( final Double doubleValue ) {
        String stringValue = Double.toString( _defaultNumericValue );

        if ( doubleValue == null ) {
            return stringValue;
        }

        try {
            stringValue = _numberFormat.format( doubleValue );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return stringValue;
    }

}
