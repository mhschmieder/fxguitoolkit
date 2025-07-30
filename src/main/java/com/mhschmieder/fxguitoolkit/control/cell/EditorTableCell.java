/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.control.cell;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public abstract class EditorTableCell< RT, VT > extends XTableCell< RT, VT > {

    // This is a custom cell so we declare our own Text Field to handle it.
    protected TextField textField;

    protected List< Integer > uneditableRows;

    // Flag to note whether blank text is allowed or not.
    protected boolean blankTextAllowed;
    
    // Cache the Client Properties as they may be needed after initialization.
    protected ClientProperties clientProperties;

    public EditorTableCell( final List< Integer > pUneditableRows,
                            final boolean pBlankTextAllowed,
                            final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        uneditableRows = pUneditableRows;
        blankTextAllowed = pBlankTextAllowed;
        clientProperties = pClientProperties;

        // Make a custom Text Field control to be used for actual editing.
        textField = makeTextField();
        
        // Initialize the Text Field for editing.
        initTextField();
    }

    // NOTE: This is primarily invoked dynamically from the startEdit()
    //  method so that we can guarantee we capture focus events that should
    //  commit edits (e.g. mouse movement, TAB key, and ENTER key).
    protected void initTextField() {
        textField.setMinWidth( getWidth() - ( getGraphicTextGap() * 2.0d ) );

        // Validate committed input (via ENTER) and adjust to allowed values.
        textField.setOnAction( evt -> {
            // Commit the current selection as-is, without giving up focus.
            textField.commitValue();

            // Save edits from the Text Field to the property bean.
            saveEdits();

            // Post-process after caching the new value, due to order
            // dependency of the text adjustments in various callbacks.
            Platform.runLater( () -> {
                // Update the displayed text to match the last cached value.
                updateText();

                // Reselect the adjusted text, to mimic Focus Gained.
                textField.selectAll();
            } );
        } );

        // When focus is lost, commit the changes; otherwise update the text.
        // NOTE: Mouse focus events to other rows, cause the editing state to
        //  be turned off before the commit is called, and thus the edits are
        //  thrown out by the JavaFX core methods. There doesn't appear to be a
        //  workaround, but strangely this doesn't happen if moving focus within
        //  the same row.
        textField.focusedProperty().addListener( 
                ( observableValue, wasFocused, isNowFocused ) -> {
            if ( isNowFocused ) {
                // Update the displayed text to match the last cached value.
                updateText();
            }
            else {
                // Commit the current selection as-is, without giving up focus.
                textField.commitValue();

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

        // NOTE: We must manually handle the ENTER key in order to save edits
        //  and release editing focus, but the ESCAPE key seems to be handled
        //  already as it cancels edits and releases editing focus.
        textField.setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // NOTE: Nothing to do, as ENTER is best handled via onAction.
                break;
            case ESCAPE:
                // Revert to the most recent committed value.
                textField.cancelEdit();

                // Post-process after caching the reverted value, due to order
                // dependency of the text adjustments in various callbacks.
                Platform.runLater( () -> {
                    // Update the displayed text to match the reverted value.
                    updateText();

                    // Reselect the updated text, to mimic Focus Gained.
                    textField.selectAll();
                } );

                break;
            case TAB:
                // NOTE: Nothing to do, as Text Input Controls commit edits and
                //  then release focus when the TAB key is pressed, so the Focus
                //  Lost handler is where value restrictions should be applied.
                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
        
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
    }
    
    protected TextField makeTextField() {
        // Default implementation in case of no downstream override.
        return new TextField();
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
        
        //setContentDisplay( ContentDisplay.TEXT_ONLY );
    }

    // NOTE: This should be overridden by derived classes, as only those can
    // know the context-specific business logic of when to allow empty fields.
    @Override
    public void commitEdit( final VT newValue ) {
        // Deal with the default behavior before our specialized handling.
        super.commitEdit( newValue );

        // Return to a state of displaying values vs. editing text.
        endEdits();
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
            return blankTextAllowed ? null : getItem();
        }

        // Potentially adjust the current edits from the Text Field.
        // NOTE: This may be redundant with adjustments made in the Text Field.
        final VT adjustedValue = getAdjustedValue( editorValue );

        return adjustedValue;
    }

    public VT getAdjustedValue( final VT editorValue ) {
        // By default, if no overrides, return the textField value as-is.
        return editorValue;
    }

    protected abstract VT getEditorValue();

    // TODO: Replace this with a JavaFX StringConverter initialization.
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

    // NOTE: The execution order below is the only one that works dependably
    //  for all contexts (focus via mouse, TAB, ENTER).
    private final void saveEdits() {
        // Potentially adjust the current edits from the Text Field.
        adjustValue();
    }

    @Override
    public void startEdit() {
        if ( !isEmpty() ) {
            // Don't allow editing if this row is excluded from editability.
            if ( ( uneditableRows != null ) && !uneditableRows.isEmpty() ) {
                final int currentRow = getTableRow().getIndex();
                for ( final int uneditableRow : uneditableRows ) {
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
            //Platform.runLater( () -> {
                //textField.requestFocus();
                textField.selectAll();
            //} );
        }
    }

    private final void updateEdits() {
        // When we start or update editing, we need the Text Field to match and
        // display the last valid cached value.
        updateText();

        // During editing, we want to see the textField, not the displayed
        // text from the previous editing session.
        setText( null );
        setGraphic( textField );
    }

    @Override
    public void updateItem( final VT item, final boolean empty ) {
        // Make sure the table cell knows the current selected item.
        // NOTE: We have to override the value of "empty" to overcome flaws in
        //  inaccessible private methods of the JavaFX base class that prevents
        //  us from entering edit mode during initial invocation.
        // NOTE: No longer the case since Java 8u40, or maybe Java 8u60?
        super.updateItem( item, empty ); // false );

        // Blank the displayed text and the textField, if empty or null updates.
        if ( empty || ( item == null ) ) {
            setText( null );
            setGraphic( null );
            return;
        }

        if ( isEditing() ) {
            // When we update editing, we need the Text Field to match and
            // display the last valid cached value.
            updateEdits();
            //setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
        }
        else {
            // Return to a state of displaying values vs. editing text.
            //TableView< RT > table = getTableView();
            //table.getColumns().get( 0 ).setVisible( false );
            //table.getColumns().get( 0 ).setVisible( true );
            endEdits();
            //setContentDisplay( ContentDisplay.TEXT_ONLY );
        }
    }

    public final void updateText() {
        // Get the most recently committed value.
        final String currentValue = getString();

        // Always check for invalid, incomplete, null, or empty values.
        if ( ( currentValue != null ) 
                && ( !currentValue.trim().isEmpty() 
                        || blankTextAllowed ) ) {
            // Update the text textField to match the last valid cached value.
            textField.setText( currentValue );
        }
    }

    private TableColumn< RT, ? > getNextColumn( final boolean forward ) {
        final List< TableColumn< RT, ? > > columns = new ArrayList<>();
        for ( TableColumn< RT, ? > column : getTableView().getColumns() ) {
            columns.addAll( getLeaves( column ) );
        }

        // There is no other column that supports editing, if only one column.
        if ( columns.size() < 2 ) {
            return null;
        }

        int currentIndex = columns.indexOf( getTableColumn() );
        int nextIndex = currentIndex;
        if ( forward ) {
            nextIndex++;
            if ( nextIndex > columns.size() - 1 ) {
                nextIndex = 0;
            }
        } else {
            nextIndex--;
            if ( nextIndex < 0 ) {
                nextIndex = columns.size() - 1;
            }
        }

        return columns.get( nextIndex );
    }

    private Point getNextCellPosition( boolean forward ) {
        int columnCount = getTableView().getColumns().size();
        int rowCount = getTableView().getItems().size();
        int currentRow = getTableRow().getIndex();
        int newRow = currentRow;
        int newColumn = 0;

        java.util.List< TableColumn< RT, ?>> columns = new ArrayList<>();
        for ( TableColumn< RT, ? > column : getTableView().getColumns() ) {
            columns.addAll( getLeaves( column ) );
        }
        
        int currentColumn = columns.indexOf( getTableColumn() );
        newColumn = currentColumn;

        if ( currentColumn == 0 && currentRow == 0 && !forward ) {
            System.out.println("not moving : cant go backward");
            return null;
        }

        if ( currentColumn == columnCount-1 && currentRow == rowCount-1 && forward ) {
            System.out.println("not moving : cant go forward");
            return null;
        }

        if ( forward ) {
            if ( currentColumn == columnCount-1 ) {
                newColumn = 0;
                newRow ++;
            }
            else {
                newColumn++;
            }
        }
        else {
            if  (currentColumn == 0 ) {
                newRow--;
                newColumn = columnCount - 1;
            }
            else {
                newColumn--;
            }
        }

        System.out.println("from "+currentColumn+","+currentRow+" to "+newColumn+","+newRow+"");

        return new Point( newColumn,newRow );
    }

    private List< TableColumn< RT, ? > > getLeaves(
            final TableColumn< RT, ? > root ) {
        final List< TableColumn< RT, ? > > columns = new ArrayList<>();
        if ( root.getColumns().isEmpty() ) {
            // We only want the leaves that are editable, for tab traversal.
            if ( root.isEditable() ) {
                columns.add( root );
            }
            return columns;
        }
        
        for ( TableColumn< RT, ? > column : root.getColumns() ) {
            columns.addAll( getLeaves( column ) );
        }
        
        return columns;
    }
}
