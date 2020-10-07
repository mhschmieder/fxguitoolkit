/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public abstract class EditorTableCell< RT, VT > extends TableCell< RT, VT > {

    // This is a custom cell so we declare our own Text Field to handle it.
    protected TextField       _textField;

    protected List< Integer > _uneditableRows;

    // Flag to note whether blank text is allowed or not.
    protected boolean         _blankTextAllowed;

    public EditorTableCell( final List< Integer > pUneditableRows,
                            final boolean pBlankTextAllowed ) {
        // Always call the superclass constructor first!
        super();

        _uneditableRows = pUneditableRows;

        _blankTextAllowed = pBlankTextAllowed;

        // Create a Text Field control to be used for actual editing.
        createTextField();
    }

    public final void adjustValue() {
        // Potentially adjust the current edits from the Text Field.
        final VT adjustedValue = getAdjustedValue();

        // Update the cached property from the adjusted, edited value.
        setValue( adjustedValue );
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        // Return to a state of displaying values vs. editing text.
        endEdits();
    }

    // :NOTE: This should be overridden by derived classes, as only those can
    // know the context=specific business logic of when to allow empty fields.
    @Override
    public void commitEdit( final VT newValue ) {
        // Deal with the default behavior before our specialized handling.
        super.commitEdit( newValue );

        // Return to a state of displaying values vs. editing text.
        endEdits();
    }

    // :NOTE: This is primarily invoked dynamically from the startEdit()
    // method so that we can guarantee we capture focus events that should
    // commit edits (e.g. mouse movement, TAB key, and ENTER key).
    @SuppressWarnings("nls")
    protected void createTextField() {
        // Make sure the item property is clamped to allowed values, then
        // update the text field to be in sync with the adjusted value.
        itemProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            if ( newValue == null ) {
                setText( "" );
            }
            else {
                // Update the displayed text to match the cached value.
                updateText();
            }
        } );

        // :TODO: Switch to a Text Editor, to avoid empty strings etc.
        _textField = new TextField();
        _textField.setMinWidth( getWidth() - ( getGraphicTextGap() * 2d ) );

        // Validate committed input (via ENTER) and adjust to allowed values.
        _textField.setOnAction( evt -> {
            // Commit the current selection as-is, without giving up focus.
            _textField.commitValue();

            // Save edits from the Text Field to the property bean.
            saveEdits();

            // Post-process after caching the new value, due to order
            // dependency of the text adjustments in various callbacks.
            Platform.runLater( () -> {
                // Update the displayed text to match the last cached value.
                updateText();

                // Reselect the adjusted text, to mimic Focus Gained.
                _textField.selectAll();
            } );
        } );

        // When focus is lost, commit the changes; otherwise update the text.
        // :NOTE: Mouse focus events to other rows, cause the editing state to
        // be turned off before the commit is called, and thus the edits are
        // thrown out by the JavaFX core methods. There doesn't appear to be a
        // workaround, but strangely this doesn't happen if moving focus within
        // the same row.
        _textField.focusedProperty().addListener( ( observableValue, wasFocused, isNowFocused ) -> {
            if ( isNowFocused ) {
                // Update the displayed text to match the last cached value.
                updateText();
            }
            else {
                // Commit the current selection as-is, without giving up focus.
                _textField.commitValue();

                // Save edits from the Text Field to the property bean.
                saveEdits();

                // Post-process after caching the new value, due to order
                // dependency of the text adjustments in various callbacks.
                Platform.runLater( () -> {
                    // Update the displayed text to match the last cached value.
                    updateText();
                } );
            }
        } );

        // :NOTE: We must manually handle the ENTER key in order to save edits
        // and release editing focus, but the ESCAPE key seems to be handled
        // already as it cancels edits and releases editing focus.
        _textField.setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // :NOTE: Nothing to do, as ENTER is best handled via onAction.
                break;
            case ESCAPE:
                // Revert to the most recent committed value.
                _textField.cancelEdit();

                // Post-process after caching the reverted value, due to order
                // dependency of the text adjustments in various callbacks.
                Platform.runLater( () -> {
                    // Update the displayed text to match the reverted value.
                    updateText();

                    // Reselect the updated text, to mimic Focus Gained.
                    _textField.selectAll();
                } );

                break;
            case TAB:
                // :NOTE: Nothing to do, as Text Input Controls commit edits and
                // then release focus when the TAB key is pressed, so the Focus
                // Lost handler is where value restrictions should be applied.
                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }

    private final void endEdits() {
        // Get the last cached item value as text.
        final String textValue = getTextValue();

        // Make sure the cached text value matches the cached item value.
        setText( textValue );

        // Do not show the Text Field when committing or canceling edits, or
        // when updating cached items from edit actions, as it is only needed
        // during active editing vs. when displaying committed or reverted
        // item values.
        setGraphic( null );
    }

    public final VT getAdjustedValue() {
        // Get the current displayed value of the Text Editor.
        final VT editorValue = getEditorValue();

        // If the text was left blank, and blank text is allowed, return a null
        // type; otherwise return with the current cached value.
        if ( ( editorValue == null ) ) {
            return _blankTextAllowed ? null : getItem();
        }

        // Potentially adjust the current edits from the Text Field.
        final VT adjustedValue = getAdjustedValue( editorValue );

        return adjustedValue;
    }

    public VT getAdjustedValue( final VT editorValue ) {
        // By default, if no overrides, return the editor value as-is.
        return editorValue;
    }

    protected abstract VT getEditorValue();

    // :TODO: Replace this with a JavaFX StringConverter initialization.
    @SuppressWarnings("nls")
    protected String getString() {
        final VT storedValue = getItem();
        if ( storedValue == null ) {
            return "";
        }

        final String stringValue = storedValue.toString();

        return stringValue;
    }

    protected abstract String getTextValue();

    // :NOTE: The execution order below is the only one that works dependably
    // for all contexts (focus via mouse, TAB, ENTER).
    private final void saveEdits() {
        // Potentially adjust the current edits from the Text Field.
        adjustValue();
    }

    // :NOTE: This method requires knowledge of which bean properties are in
    // use so cannot have a default or base class implementation and is required
    // for all subclasses to override in order to achieve proper data binding.
    protected void setBeanProperty( final RT selectedRecord ) {}

    public void setValue( final VT editorValue ) {
        // Commit the edited text to the table cell itself.
        commitEdit( editorValue );

        // Save edits from the Text Field to the property bean.
        final TableView< RT > tableView = getTableView();
        final TableRow< RT > tableRow = getTableRow();
        if ( tableRow != null ) {
            final int selectedIndex = tableRow.getIndex();
            final ObservableList< RT > items = tableView.getItems();
            if ( !items.isEmpty() && ( selectedIndex >= 0 ) && ( selectedIndex < items.size() ) ) {
                final RT selectedRecord = items.get( selectedIndex );
                setBeanProperty( selectedRecord );
            }
        }
    }

    @Override
    public void startEdit() {
        if ( !isEmpty() ) {
            // Don't allow editing if this row is excluded from editability.
            if ( ( _uneditableRows != null ) && !_uneditableRows.isEmpty() ) {
                final int currentRow = getTableRow().getIndex();
                for ( final int uneditableRow : _uneditableRows ) {
                    if ( currentRow == uneditableRow ) {
                        return;
                    }
                }
            }

            // Deal with the default behavior before our specialized handling.
            super.startEdit();

            // When we start editing, we need the Text Field to match and
            // display the last valid cached value.
            updateEdits();

            // Select the updated text, to make it obvious we started editing.
            _textField.selectAll();
        }
    }

    private final void updateEdits() {
        // When we start or update editing, we need the Text Field to match and
        // display the last valid cached value.
        updateText();

        // During editing, we want to see the editor, not the displayed
        // text from the previous editing session.
        setText( null );
        setGraphic( _textField );
    }

    @Override
    public void updateItem( final VT item, final boolean empty ) {
        // Make sure the table cell knows the current selected item.
        // :NOTE: We have to override the value of "empty" to overcome flaws in
        // inaccessible private methods of the JavaFX base class that prevents
        // us from entering edit mode during initial invocation.
        // :NOTE: No longer the case since Java 8u40, or maybe Java 8u60?
        super.updateItem( item, empty ); // false );

        // Blank the displayed text and the editor, if empty or null updates.
        if ( empty || ( item == null ) ) {
            setText( null );
            setGraphic( null );
            return;
        }

        if ( isEditing() ) {
            // When we update editing, we need the Text Field to match and
            // display the last valid cached value.
            updateEdits();
        }
        else {
            // Return to a state of displaying values vs. editing text.
            endEdits();
        }
    }

    public final void updateText() {
        // Get the most recently committed value.
        final String currentValue = getString();

        // Always check for invalid, incomplete, null, or empty values.
        if ( ( currentValue != null ) && ( !currentValue.trim().isEmpty() || _blankTextAllowed ) ) {
            // Update the text editor to match the last valid cached value.
            _textField.setText( currentValue );
        }
    }

}// class EditorTableCell
