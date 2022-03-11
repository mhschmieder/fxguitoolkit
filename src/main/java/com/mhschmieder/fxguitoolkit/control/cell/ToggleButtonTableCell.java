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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control.cell;

import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

// NOTE: This needs to be a CheckBoxTableCell derivative in order to inherit
// all the correct table cell height, width, gaps, and other rendering details.
public abstract class ToggleButtonTableCell< RT, VT > extends CheckBoxTableCell< RT, Boolean > {

    // This is a custom cell so we declare our own Toggle Button to handle it.
    // NOTE: Custom XToggleButton backed out, as CSS based implementations
    // don't give the opportunity to veto state changes and "lie" about state.
    // Unfortunately, we have to do this sometimes due to inadequacies of the
    // TableView API in terms of momentary blocking of table cell editing.
    protected ToggleButton _toggleButton;

    // Cache text and colors for selected and unselected states.
    private final String   _selectedText;
    private final String   _deselectedText;
    private final Color    _selectedBackgroundColor;
    private final Color    _deselectedBackgroundColor;
    private final Color    _selectedTextFillColor;
    private final Color    _deselectedTextFillColor;

    public ToggleButtonTableCell( final String selectedText,
                                  final String deselectedText,
                                  final Color selectedBackgroundColor,
                                  final Color deselectedBackgroundColor,
                                  final Color selectedTextFillColor,
                                  final Color deselectedTextFillColor,
                                  final String tooltipText ) {
        // Always call the superclass constructor first!
        super();

        _selectedText = selectedText;
        _deselectedText = deselectedText;
        _selectedBackgroundColor = selectedBackgroundColor;
        _deselectedBackgroundColor = deselectedBackgroundColor;
        _selectedTextFillColor = selectedTextFillColor;
        _deselectedTextFillColor = deselectedTextFillColor;

        try {
            initTableCell( tooltipText );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final Color getBackgroundColor() {
        final Boolean storedValue = getItem();
        if ( storedValue == null ) {
            return Color.BLACK;
        }

        final boolean evaluatedValue = storedValue.booleanValue();

        final Color backgroundColor = evaluatedValue
            ? _selectedBackgroundColor
            : _deselectedBackgroundColor;

        return backgroundColor;
    }

    @SuppressWarnings("nls")
    private final String getString() {
        final Boolean storedValue = getItem();
        if ( storedValue == null ) {
            return "";
        }

        final boolean evaluatedValue = storedValue.booleanValue();

        final String stringValue = evaluatedValue ? _selectedText : _deselectedText;

        return stringValue;
    }

    private final Color getTextFillColor() {
        final Boolean storedValue = getItem();
        if ( storedValue == null ) {
            return Color.WHITE;
        }

        final boolean evaluatedValue = storedValue.booleanValue();

        final Color textFillColor = evaluatedValue
            ? _selectedTextFillColor
            : _deselectedTextFillColor;

        return textFillColor;
    }

    private final void initTableCell( final String tooltipText ) {
        // Make the Toggle Button with initial specified selection state.
        _toggleButton = new ToggleButton();

        // Set the Tool Tip text for the Toggle Button in this Table Cell,
        // whether an image is used or it only has standard button text.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            _toggleButton.setTooltip( new Tooltip( tooltipText ) );
        }

        // Try to make the Toggle Button fill the entire Table Cell.
        // NOTE: Setting height causes incremental growth per click!
        _toggleButton.minWidthProperty().bind( widthProperty() );
        _toggleButton.prefWidthProperty().bind( widthProperty() );

        // Register a callback to handle button clicks/toggles.
        // NOTE: This callback is how we find out the Toggle Button state
        // changed, so we are responsible for syncing that with the underlying
        // data model, which must be done via bean properties vs. using the
        // Toggle Button's selected state, since we programmatically change
        // the values when we enforce rules or when we clone a table row.
        _toggleButton.setOnAction( evt -> {
            // Save edits from the Toggle Button to the property bean.
            saveEdits();
        } );

        // We always want to show the Toggle Button vs. a string value.
        setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
    }

    private final void saveEdits() {
        // Save edits from the Toggle Button to the property bean.
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

    // NOTE: This method requires knowledge of which bean properties are in
    // use so cannot have a default or base class implementation and is required
    // for all subclasses to override in order to achieve proper data binding.
    protected abstract void setBeanProperty( final RT selectedRecord );

    @Override
    public void updateItem( final Boolean item, final boolean empty ) {
        // Make sure the table cell knows the current toggle state.
        super.updateItem( item, empty );

        // Display the string representation of the current toggle state.
        // NOTE: We avoid displaying anything in empty rows.
        if ( empty ) {
            setText( null );
            setGraphic( null );
        }
        else {
            // Set the Text, Background Color, and Text Fill Color, based on the
            // selected vs. unselected status, just as with filters.
            final String stringValue = getString();
            _toggleButton.setText( stringValue );

            final Color backgroundColor = getBackgroundColor();
            final Background background = GuiUtilities.getButtonBackground( backgroundColor );
            _toggleButton.setBackground( background );

            final Color textFillColor = getTextFillColor();
            _toggleButton.setTextFill( textFillColor );

            setText( null );
            setGraphic( _toggleButton );
        }
    }

}// class ToggleButtonTableCell
