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

import com.mhschmieder.commonstoolkit.net.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import impl.org.controlsfx.skin.SearchableComboBoxSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;

/**
 * This is an abstract superclass to consolidate stuff that we want all Combo
 * Boxes to do, such as handle ESC and ENTER key events consistently.
 * <p>
 * TODO: Use the richer logic of Angle Selector's list updater method to
 * inform a consolidation of the two current approaches in this class, and note
 * that that class adds a third approach as well. All are mutually exclusive.
 */
public abstract class XComboBox extends ComboBox< String > {

    // We need to know at all times whether we are marked as Searchable.
    protected boolean       _searchable;

    // Cache a backup list to replace after auto-complete.
    // protected ObservableList< String > _backupList;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public XComboBox( final ClientProperties pClientProperties,
                      final String tooltipText,
                      final boolean toolbarContext,
                      final boolean editable,
                      final boolean searchable ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;

        _searchable = searchable;

        // _backupList = FXCollections.observableArrayList();

        try {
            initComboBox( tooltipText, toolbarContext, editable );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Override
    protected Skin< ? > createDefaultSkin() {
        return _searchable ? new SearchableComboBoxSkin<>( this ) : super.createDefaultSkin();
    }

    private final void initComboBox( final String tooltipText,
                                     final boolean toolbarContext,
                                     final boolean editable ) {
        // Provide Tool Tips in case this component is used in a sparse context
        // like a Tool Bar, where there may be no associated descriptive label.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            setTooltip( new Tooltip( tooltipText ) );
        }

        // It's best to set editable status before modifying CSS attributes.
        setEditable( editable );

        if ( toolbarContext ) {
            // Apply drop-shadow effects when the mouse enters this Combo Box.
            GuiUtilities.applyDropShadowEffect( this );
        }
        else {
            // Set the full list of shared Combo Box Properties (CSS etc.).
            GuiUtilities.setComboBoxProperties( this );
        }

        // Attempt to support auto-complete for all list data types.
        // NOTE: This code is not accomplishing its goals, and furthermore is
        // quite dangerous as it ends up triggering multiple action events and
        // blanks out the text, causing downstream null pointer exceptions and
        // erasing what the user types vs. auto-completing.
        /*
         * if ( editable ) {
         * final TextField textField = getEditor();
         * final AutoCompleteComparator< String > comparatorMethod =
         * new AutoCompleteStringComparator();
         * textField.focusedProperty().addListener( observable -> {
         * if ( getSelectionModel().getSelectedIndex() < 0 ) {
         * textField.setText( null );
         * }
         * } );
         * addEventHandler( KeyEvent.KEY_PRESSED, t -> hide() );
         * addEventHandler( KeyEvent.KEY_RELEASED, new EventHandler< KeyEvent
         * >() {
         * private boolean moveCaretToPos = false;
         * private int caretPos;
         * @SuppressWarnings("nls")
         * @Override
         * public void handle( final KeyEvent event ) {
         * final String textValue = textField.getText();
         * final KeyCode keyCode = event.getCode();
         * if ( keyCode == KeyCode.UP ) {
         * caretPos = -1;
         * if ( textValue != null ) {
         * moveCaret( textValue.length() );
         * }
         * return;
         * }
         * else if ( keyCode == KeyCode.DOWN ) {
         * if ( !isShowing() ) {
         * show();
         * }
         * caretPos = -1;
         * if ( textValue != null ) {
         * moveCaret( textValue.length() );
         * }
         * return;
         * }
         * else if ( keyCode == KeyCode.BACK_SPACE ) {
         * if ( textValue != null ) {
         * moveCaretToPos = true;
         * caretPos = textField.getCaretPosition();
         * }
         * }
         * else if ( keyCode == KeyCode.DELETE ) {
         * if ( textValue != null ) {
         * moveCaretToPos = true;
         * caretPos = textField.getCaretPosition();
         * }
         * }
         * else if ( keyCode == KeyCode.ENTER ) {
         * final int selectedIndex = getSelectionModel().getSelectedIndex();
         * setItems( _backupList );
         * if ( selectedIndex > 0 ) {
         * getSelectionModel().select( selectedIndex );
         * }
         * else {
         * getSelectionModel().selectFirst();
         * }
         * return;
         * }
         * if ( ( keyCode == KeyCode.RIGHT ) || ( keyCode == KeyCode.LEFT )
         * || keyCode.equals( KeyCode.SHIFT )
         * || keyCode.equals( KeyCode.CONTROL ) || event.isControlDown()
         * || ( keyCode == KeyCode.HOME )
         * || ( keyCode == KeyCode.END )
         * || ( keyCode == KeyCode.TAB ) ) {
         * return;
         * }
         * final ObservableList< String > list =
         * FXCollections.observableArrayList();
         * final ObservableList< String > data = _backupList; // getItems();
         * for ( final String aData : data ) {
         * if ( ( aData != null ) && ( textValue != null )
         * && comparatorMethod.matches( textValue, aData ) ) {
         * list.add( aData );
         * }
         * }
         * String t = "";
         * if ( textValue != null ) {
         * t = textValue;
         * }
         * setItems( list );
         * textField.setText( t );
         * if ( !moveCaretToPos ) {
         * caretPos = -1;
         * }
         * moveCaret( t.length() );
         * if ( !list.isEmpty() ) {
         * show();
         * }
         * }
         * private void moveCaret( final int textLength ) {
         * if ( caretPos == -1 ) {
         * getEditor().positionCaret( textLength );
         * }
         * else {
         * getEditor().positionCaret( caretPos );
         * }
         * moveCaretToPos = false;
         * }
         * } );
         * }
         */
    }

    // Update the drop-list of available values.
    // TODO: Find a way to restore the previous value when it was displayed but
    // not actively selected, and to distinguish the two cases, as currently we
    // pass in "-1" to indicate that we don't want to preserve the selection.
    public final void updateValues( final ObservableList< String > values,
                                    final int defaultSelectedIndex ) {
        // Cache the backup list for auto-complete follow-up list replacement.
        // _backupList.setAll( values );

        // Save the selection to reinstate after replacing the drop-list.
        final SingleSelectionModel< String > selectionModel = getSelectionModel();
        final int currentSelectedIndex = ( defaultSelectedIndex >= 0 )
            ? defaultSelectedIndex
            : selectionModel.getSelectedIndex();

        // Replace the entire list all at once, to reduce callbacks and interim
        // states. Avoid side effects of replacing an identical list as that can
        // cause a callback on the current selection being reselected as though
        // it is a brand new (different) selection (due to being a legitimate
        // member of a "new" list), thus generating unwanted callbacks.
        final ObservableList< String > items = getItems();
        if ( !items.equals( values ) ) {
            setItems( values );
        }

        // If the desired selection index is still within bounds, reselect it.
        if ( ( currentSelectedIndex >= 0 ) && ( currentSelectedIndex < values.size() ) ) {
            selectionModel.select( currentSelectedIndex );
        }
        else {
            // If out of bounds, default to the first item in the display list.
            selectionModel.selectFirst();
        }
    }

    // Update the drop-list of available values.
    public final void updateValues( final ObservableList< String > values,
                                    final String defaultValue,
                                    final boolean preserveSelection ) {
        // Cache the backup list for auto-complete follow-up list replacement.
        // _backupList.setAll( values );

        // Save the selection to reinstate after factoring the drop-list.
        final String currentValue = getValue();

        // Replace the entire list all at once, to reduce callbacks and interim
        // states. Avoid side effects of replacing an identical list as that can
        // cause a callback on the current selection being reselected as though
        // it is a brand new (different) selection (due to being a legitimate
        // member of a "new" list), thus generating unwanted callbacks.
        final ObservableList< String > items = getItems();
        if ( !items.equals( values ) ) {
            setItems( values );
        }

        // Conditionally attempt to restore the previous selection.
        // NOTE: When switching back and forth between two lists, we usually
        // want to avoid auto-selection so we don't lose earlier values when
        // switching back. It is important to check for null selections.
        if ( preserveSelection ) {
            // If the previous selection is also in the new list, reselect it.
            if ( ( currentValue != null ) && values.contains( currentValue ) ) {
                setValue( currentValue );
                return;
            }
        }

        // If we aren't trying to preserve the previous selection, or no match
        // found, use the provided default text value if present; otherwise set
        // to the first item in the list.
        if ( ( defaultValue != null ) && values.contains( defaultValue ) ) {
            setValue( defaultValue );
        }
        else if ( !values.isEmpty() ) {
            setValue( values.get( 0 ) );
        }
    }

    // Update the drop-list of available values.
    public final void updateValues( final String[] values, final int defaultSelectedIndex ) {
        updateValues( FXCollections.observableArrayList( values ), defaultSelectedIndex );
    }

    // Update the drop-list of available values.
    public final void updateValues( final String[] values,
                                    final String defaultValue,
                                    final boolean preserveSelection ) {
        updateValues( FXCollections.observableArrayList( values ),
                      defaultValue,
                      preserveSelection );
    }

}
