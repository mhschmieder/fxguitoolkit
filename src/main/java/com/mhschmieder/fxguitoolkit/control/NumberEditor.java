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

import com.mhschmieder.commonstoolkit.lang.StringUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.text.NumberFormat;

/**
 * This class formalizes aspects of text editing that are specific to numbers.
 */
public abstract class NumberEditor extends XTextField {
    
    // Cache the error text value, to display when illegal values are present.
    protected String             _errorText;

    // Maintain a reference to the Measurement Unit string (can be blank).
    protected String       _measurementUnitString;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat _numberFormat;

    // Number format cache used for locale-specific number parsing.
    protected NumberFormat _numberParse;

    // This is a functional interface for resetting the control.
    protected Runnable     _reset;

    public NumberEditor( final ClientProperties clientProperties,
                         final String initialText,
                         final String tooltipText,
                         final boolean applyToolkitCss ) {
        // Always call the superclass constructor first!
        super( initialText, tooltipText, applyToolkitCss, clientProperties );
        
        // As this is a new feature, it is better to set later and ignore if empty.
        _errorText = "";

        // Set the measurement unit string after construction time, once known.
        _measurementUnitString = "";

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initEditor() {
        _numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        _numberParse = ( NumberFormat ) _numberFormat.clone();

        _numberFormat.setGroupingUsed( true );
        _numberParse.setGroupingUsed( true );

        // Use a TextFormatter to wrap and bind the provided number format.
        // NOTE: This stops measurement unit changes from falsely triggering
        // the dirty flag, but also means the formatting is applied but not the
        // units, so it's a bit tricky to see how to make this work with our
        // more elaborate needs (which also include clamping to avoid numbers
        // outside an approved range). It does prevent illegal characters.
        // TODO: Complete this new approach and then remove setOnAction().
        // final TextFormatter< Number > formatter = new TextFormatter<>( new
        // FormatStringConverter<>( numberFormat ) );
        // formatter.valueProperty().bindBidirectional( value );
        // setTextFormatter( formatter );

        // Validate committed input (via ENTER) and restrict to the legal range.
        setOnAction( evt -> {
            // Commit the current selection as-is, without giving up focus.
            commitValue();

            // Save edits from the Text Field to the property bean.
            saveEdits();

            // Post-process after caching the new value, due to order
            // dependency of the text adjustments in various callbacks.
            Platform.runLater( () -> {
                // Update the displayed text to include all of the decorations.
                decorateText();

                // Reselect the reformatted text, to mimic Focus Gained.
                selectAll();
            } );
        } );

        // When focus is lost, commit the changes; otherwise decorate the text.
        focusedProperty().addListener( ( observableValue, wasFocused, isNowFocused ) -> {
            if ( isNowFocused ) {
                // Update the displayed text to include all of the decorations.
                decorateText();
            }
            else {
                // Save edits from the Text Field to the property bean.
                saveEdits();

                // Update the displayed text to match the cached value.
                updateText();
            }
        } );
    }
    
    /**
     * Restricts keyboard input to a custom set of allowed characters.
     * <p>
     * As the allowed characters will be specific to any implementing class, and 
     * likely will depend on class-local variables not present on this abstract
     * base class, this method is not called during this class's own initialization
     * and should instead be invoked by implementing classes at a safe time.
     */
    public void restrictKeyboardInput() {
        // Restrict keyboard input to numerals, sign, and delimiters.
        final String allowedCharacters = getAllowedCharacters();
        addEventFilter( KeyEvent.KEY_TYPED, keyEvent -> {
            if ( !keyEvent.getCharacter().matches( allowedCharacters ) ) {
                keyEvent.consume();
            }
        } );
    }
    
    public String getErrorText() {
        return _errorText;
    }
    
    public void setErrorText( final String pErrorText ) {
        _errorText = pErrorText;
    }

    public final String getMeasurementUnitString() {
        return _measurementUnitString;
    }

    public final void setMeasurementUnitString( final String measurementUnitString ) {
        _measurementUnitString = measurementUnitString;

        // Make sure to redisplay with the new Measurement Unit suffix.
        decorateText();
    }

    /**
     * Sets the textField reset callback.
     * <p>
     * Specify {@code null} to clear a previously set {@link Runnable}. When
     * creating a {@link TextField}, this callback is automatically defined to
     * reset invalid input to the supplied default value (zero if not provided).
     * Setting a different callback will overwrite this functionality.
     *
     * @param reset
     *            The {@link Runnable} to call upon
     *            {@link NumberFormatException}
     */
    public final void setReset( final Runnable reset ) {
        _reset = reset;
    }

    public abstract String getAllowedCharacters();

    public final void decorateText() {
        // Get the most recently committed value, restoring decorations etc.
        final String decoratedText = getDecoratedText();

        // Update the displayed text to include all of the decorations.
        setText( decoratedText );
    }

    public abstract String getDecoratedText();

    public final String getUndecoratedText() {
        final String savedText = getText();
        final String undecoratedText = getUndecoratedText( savedText );

        return undecoratedText;
    }

    // NOTE: This is an opportunity to pre-parse the typed text before
    // converting to a number, such as when we disallow positive numbers (e.g.).
    public String getUndecoratedText( final String savedText ) {
        // By default, Java does not allow the positive sign to be typed, but
        // many users may be in the habit of typing it in, so we allow it in our
        // key filter and then must strip it here so that numbers get parsed
        // correctly vs. throwing exceptions and defaulting to previous values.
        final String undecoratedText = StringUtilities.stripPositiveSign( savedText );

        return undecoratedText;
    }

    public final void saveEdits() {
        // Clamp the committed value to the allowed range.
        clampValue();
    }

    public abstract void updateText();

    public abstract void clampValue();

}
