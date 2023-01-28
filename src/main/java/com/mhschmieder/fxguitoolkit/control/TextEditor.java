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

import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

/**
 * This class consolidates desired behavioral characteristics for basic text
 * editing, such as waiting until editing is committed before saving to the data
 * model (vs. live syncing with each character typed, even though interim and
 * incomplete values may be illegal, reverted, or otherwise undesirable).
 */
public class TextEditor extends XTextField {

    // Flag to note whether blank text is allowed or not.
    private final boolean        _blankTextAllowed;

    // Cache the raw string representation of the data value.
    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final StringProperty value;

    @SuppressWarnings("nls")
    public TextEditor( final boolean applyToolkitCss,
                       final ClientProperties pClientProperties ) {
        this( "", null, applyToolkitCss, true, pClientProperties );
    }

    public TextEditor( final String initialText,
                       final boolean applyToolkitCss, 
                       final ClientProperties pClientProperties ) {
        this( initialText, null, applyToolkitCss, pClientProperties );
    }

    public TextEditor( final String initialText,
                       final String tooltipText,
                       final boolean applyToolkitCss,
                       final ClientProperties pClientProperties ) {
        this( initialText, 
              tooltipText, 
              applyToolkitCss, 
              false, 
              pClientProperties );
    }

    public TextEditor( final String initialText,
                       final boolean applyToolkitCss,
                       final boolean pBlankTextAllowed,
                       final ClientProperties pClientProperties ) {
        this( initialText, 
              null, 
              applyToolkitCss, 
              pBlankTextAllowed, 
              pClientProperties );
    }

    public TextEditor( final String pInitialText,
                       final String pTooltipText,
                       final boolean applyToolkitCss,
                       final boolean pBlankTextAllowed,
                       final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pInitialText, pTooltipText, applyToolkitCss, pClientProperties );

        _blankTextAllowed = pBlankTextAllowed;

        value = new SimpleStringProperty( pInitialText );

        try {
            initEditor();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final void adjustValue() {
        // Potentially adjust the current edits from the Text Field.
        final String adjustedValue = getAdjustedValue();

        // Update the cached property from the adjusted, edited value.
        setValue( adjustedValue );
    }

    public final String getAdjustedValue() {
        // Get the current input in its most recent edited state.
        final String savedText = getText();

        // If the text was left blank, and blank text is allowed, return an
        // empty string; otherwise return with the current cached value.
        if ( ( savedText == null ) || savedText.trim().isEmpty() ) {
            return _blankTextAllowed ? "" : getValue(); //$NON-NLS-1$
        }

        // Potentially adjust the current edits from the Text Field.
        final String adjustedValue = getAdjustedValue( savedText );

        return adjustedValue;
    }

    public String getAdjustedValue( final String text ) {
        // If blank text is allowed, return the input unadjusted; otherwise trim
        // the edited value to make it legal, and to avoid confusing the user.
        final String adjustedValue = _blankTextAllowed ? text : text.trim();

        return adjustedValue;
    }

    public final String getValue() {
        return value.get();
    }

    private final void initEditor() {
        // Use a TextFormatter to wrap and bind the provided string format.
        // TODO: Complete this new approach and then remove setOnAction().
        // final TextFormatter< String > formatter = new TextFormatter<>( new
        // FormatStringConverter<>( numberFormat ) );
        // formatter.valueProperty().bindBidirectional( value );
        // setTextFormatter( formatter );

        // Validate committed input (via ENTER) and adjust to allowed values.
        setOnAction( evt -> {
            // Commit the current selection as-is, without giving up focus.
            commitValue();

            // Save edits from the Text Field to the property bean.
            saveEdits();

            // Post-process after caching the new value, due to order
            // dependency of the text adjustments in various callbacks.
            Platform.runLater( () -> {
                // Update the displayed text to match the cached value.
                updateText();

                // Reselect the adjusted text, to mimic Focus Gained.
                selectAll();
            } );
        } );

        // When focus is lost, commit the changes; otherwise update the text.
        focusedProperty().addListener( ( observableValue, wasFocused, isNowFocused ) -> {
            if ( isNowFocused ) {
                // Update the displayed text to match the cached value.
                updateText();
            }
            else {
                // Save edits from the Text Field to the property bean.
                saveEdits();

                // Update the displayed text to match the cached value.
                updateText();
            }
        } );

        // Make sure the value property is clamped to allowed values, then
        // update the text field to be in sync with the adjusted value.
        valueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            if ( newValue == null ) {
                setText( "" ); //$NON-NLS-1$
            }
            else {
                // Update the displayed text to match the cached value.
                updateText();
            }
        } );

        // NOTE: We must manually handle the ENTER key in order to save edits
        // and release editing focus, but the ESCAPE key seems to be handled
        // already as it cancels edits and releases editing focus.
        setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // NOTE: Nothing to do, as ENTER is best handled via onAction.
                break;
            case ESCAPE:
                // Revert to the most recent committed value.
                cancelEdit();

                // Post-process after caching the reverted value, due to order
                // dependency of the text adjustments in various callbacks.
                Platform.runLater( () -> {
                    // Update the displayed text to match the reverted value.
                    updateText();

                    // Reselect the reformatted text, to mimic Focus Gained.
                    selectAll();
                } );

                break;
            case TAB:
                // NOTE: Nothing to do, as Text Input Controls commit edits and
                // then release focus when the TAB key is pressed, so the Focus
                // Lost handler is where value restrictions should be applied.
                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }

    public final void saveEdits() {
        // Potentially adjust the current edits from the Text Field.
        adjustValue();
    }

    public final void setValue( final String pValue ) {
        value.set( pValue );
    }

    public final void updateText() {
        // Get the most recently committed value.
        final String savedValue = getValue();

        // Update the displayed text to match the cached value.
        setText( savedValue );
    }

    public final StringProperty valueProperty() {
        return value;
    }

}
