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
package com.mhschmieder.fxguitoolkit.control.cell;

import com.mhschmieder.fxguitoolkit.control.XColorPicker;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.paint.Color;

/**
 * This is a wrapper for the logic that is specific to Color Pickers.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public abstract class ColorPickerTableCell< RT > extends XTableCell< RT, Color > {

    // This is a custom cell so we declare our own Color Picker to handle it.
    protected XColorPicker _colorPicker;

    // NOTE: It is better to pass in the Table Column than to query at
    // run-time, as the latter can result in null pointer exceptions during
    // initialization, due to order-dependency.
    public ColorPickerTableCell( final TableColumn< RT, Color > column, final String tooltipText ) {
        // Always call the superclass constructor first!
        super();

        try {
            initTableCell( column, tooltipText );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initTableCell( final TableColumn< RT, Color > column,
                                      final String tooltipText ) {
        // Make the Color Picker with initial specified selection state.
        _colorPicker = new XColorPicker( tooltipText );
        _colorPicker.setMaxWidth( Double.MAX_VALUE );

        // Try to make the Color Picker fill the entire Table Cell.
        // NOTE: We have to account for insets and margins though.
        // NOTE: Setting height causes incremental growth per click!
        _colorPicker.minWidthProperty().bind( widthProperty().subtract( 8.0d ) );
        _colorPicker.prefWidthProperty().bind( widthProperty() );

        _colorPicker.editableProperty().bind( column.editableProperty() );
        _colorPicker.disableProperty().bind( column.editableProperty().not() );

        // It is safer to manually show the Color Picker and put it into editing
        // mode, than to deal with the complexities of the base class
        // implementation, as the Color Picker has a very different workflow and
        // event model from a standard supported control like a Text Field.
        _colorPicker.setOnShowing( evt -> {
            // Bring up the Color Picker to edit the Color value.
            final TableView< RT > tableView = getTableView();
            final TableViewSelectionModel< RT > selectionModel = tableView.getSelectionModel();
            final int selectedIndex = getTableRow().getIndex();
            selectionModel.select( selectedIndex );
            final int selectedIndexCorrected = selectionModel.getSelectedIndex();
            tableView.edit( selectedIndexCorrected, column );
        } );

        // Register a callback to handle user actions that commit a choice.
        // NOTE: This covers direct clicks in the palette, and confirmation of
        // custom colors, but deliberately avoids cases where the user canceled
        // the custom color pop-up or the main palette (via mouse focus), so
        // that we do not unnecessarily sync or commit unchanged values (which
        // could possibly falsely trigger the project-level dirty flag).
        _colorPicker.setOnAction( evt -> {
            // Save the edits from the Color Picker to the property bean.
            saveEdits();
        } );

        // We always want to show the color value along with a color icon.
        setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
    }

    private final void saveEdits() {
        // Get the current displayed value of the Color Picker.
        final Color color = _colorPicker.getValue();

        // Commit the edited Color value, and sync to the property bean.
        setValue( color );
    }

    @Override
    public void updateItem( final Color item, final boolean empty ) {
        // Make sure the table cell knows the current state.
        super.updateItem( item, empty );

        // Display the current state for the Color Picker.
        // NOTE: We avoid displaying anything in empty rows.
        if ( empty ) {
            setText( null );
            setGraphic( null );
        }
        else {
            _colorPicker.setValue( item );

            setText( null );
            setGraphic( _colorPicker );
        }
    }
}
