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

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * {@code TableUtilities} is a static utilities class for ensuring a reduction
 * in copy/paste code for similar functionality that should be maintained
 * consistently across data types and {@code TableView} subclasses.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class TableUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private TableUtilities() {}

    /**
     * Sets the column header properties on the supplied {@link TableColumn}.
     *
     * @param column
     *            The {@link TableColumn} whose header properties should be set
     *
     * @version 1.0
     */
    @SuppressWarnings("nls")
    public static void setTableColumnHeaderProperties( final TableColumn< ? extends Object, ? > column ) {
        // Apply custom style guidelines to table headers and make them wrap.
        //
        // Larger fonts are used on the Mac in general, by default.
        //
        // Consider increasing this size once we get multi-row headers properly
        // painting the full background of adjacent single-row headers.
        final Label tableColumnHeader = new Label( column.getText() );

        tableColumnHeader.getStyleClass().add( "table-column-header" );

        final StackPane pane = new StackPane();
        pane.getChildren().add( tableColumnHeader );
        pane.prefWidthProperty().bind( column.widthProperty() );
        tableColumnHeader.prefWidthProperty().bind( pane.prefWidthProperty() );

        column.setGraphic( pane );
    }

    /**
     * Sets the cell value factory on the supplied {@link TableColumn}.
     *
     * @param <TD>
     *            The data type for the table
     * @param <CT>
     *            The type of the content for all cells in this
     *            {@link TableColumn}
     * @param tableColumn
     *            The {@link TableColumn} whose cell value factory should be set
     * @param columnPropertyName
     *            The name of the table property that this column manages
     *
     * @version 1.0
     */
    public static < TD, CT > void setCellValueFactory( final TableColumn< TD, CT > tableColumn,
                                                       final String columnPropertyName ) {
        final Callback< CellDataFeatures< TD, CT >, ObservableValue< CT > > callback =
                                                                                     new PropertyValueFactory<>( columnPropertyName );
        tableColumn.setCellValueFactory( callback );
    }

    /**
     * Sets the cell alignment on the supplied {@link TableColumn}.
     * <p>
     * This method is designed as a common cell factory and cell alignment
     * utility for read-only non-specific object-based table columns.
     *
     * @param <TD>
     *            The data type for the table
     * @param <CT>
     *            The type of the content for all cells in this
     *            {@link TableColumn}
     * @param tableColumn
     *            The {@link TableColumn} whose cell alignment should be set
     *
     * @version 1.0
     */
    public static < TD, CT > void setCellAlignment( final TableColumn< TD, CT > tableColumn ) {
        tableColumn.setCellFactory( column -> {
            final TableCell< TD, CT > cell = new TableCell< TD, CT >() {
                @SuppressWarnings("nls")
                private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                }

                @Override
                public void updateItem( final CT item, final boolean empty ) {
                    super.updateItem( item, empty );
                    setText( empty ? null : getString() );
                    setGraphic( null );
                }
            };

            cell.setAlignment( Pos.CENTER );

            return cell;
        } );
    }

}
