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
package com.mhschmieder.fxcontrols.control;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

    public static < TD > void addDragDropSupport( final TableView< TD > tableView ) {
        // Define a custom row factory to allow drag and drop of table rows.
        final Callback< TableView< TD >, TableRow< TD > > callback 
                = makeDragDropRowFactory( tableView );

        tableView.setRowFactory( callback );
    }

    public static < TD > Callback< TableView< TD >, TableRow< TD > > makeDragDropRowFactory( 
            final TableView< TD > tableView ) {
        // Define a custom row factory to allow drag and drop of table rows.
        // TODO: Determine whether this supports multi-select, and, if so,
        //  whether the selected rows can be discontiguous. We want to avoid
        //  losing rows during a multi9-row drop, if dragging upwards or well
        //  past the end of the last table row. Lots of edge cases to test!
        return ( table ) -> { return makeDragDropTableRow( table ); };
    }

    public static < TD > TableRow makeDragDropTableRow( final TableView< TD > table ) {
        final TableRow< TD > row = new TableRow<>();

        // Enable row to be draggable.
        row.setOnDragDetected( event -> {
            // If the drag didn't start due to a blank row being selected, we
            // start the visual drag feedback but don't let the row be dropped.
            // This is achieved by not consuming this event, so the user takes
            // note that the row can't be dropped and chooses another row.
            if ( startDrag( row ) ) {
                event.consume();
            }
        } );

        // Enable rows to accept a dropped item.
        row.setOnDragOver( event -> {
            final Dragboard db = event.getDragboard();

            // If a drag row index was saved, accept dropped items on table,
            // rows. Otherwise, there is no way we can drag it to another row.
            // NOTE: Support all modes so we can add a moved row before deleting.
           if ( hasDragRowIndex( db ) ) {
                event.acceptTransferModes( TransferMode.ANY );
            }

            // Even if the row cannot be dragged, consume the event so that
            // the full drag/drop sequence can finish and the user can start
            // a new drag/drop request with a different row that is valid.
            event.consume();
        } );

        // Handle the row drop.
        row.setOnDragDropped( event -> {
            final Dragboard db = event.getDragboard();
            final boolean rowDropped = endDrag( table, row, db );

            // Flag the drop status to indicate whether drag/drop completed.
            event.setDropCompleted( rowDropped );

            // Even if the drop request was rejected, consume the event so
            // the user isn't stuck in a drag sequence that can never end.
            event.consume();
        } );

        return row;
    }

    public static < TD > void setDragRowIndex( final TableRow< TD > row, final Dragboard db ) {
        // Save the row index of the row to be dragged.
        final ClipboardContent cc = new ClipboardContent();
        final int index = row.getIndex();
        cc.putString( String.valueOf( index ) );
        db.setContent( cc );
    }

    public static < TD > boolean hasDragRowIndex( final Dragboard db ) {
        // If no drag row index was saved, there is no way we can drag it.
        return db.hasString();
    }

    public static < TD > boolean startDrag( final TableRow< TD > row ) {
        // Don't copy empty rows to the drag board as that confuses users.
        if ( row.isEmpty() ) {
            return false;
        }

        // Start the visual feedback of live row dragging.
        // TODO: Review whether we should cache the image reference.
        // NOTE: Support Copy or Move so we can add a moved row before deleting.
        final Dragboard db = row.startDragAndDrop( TransferMode.COPY_OR_MOVE );
        db.setDragView( row.snapshot( null, null ) );

        // Save the row index of the row to be dragged.
        setDragRowIndex( row, db );

        return true;
    }

    public static < TD > boolean endDrag( final TableView< TD > table,
                                          final TableRow< TD > row,
                                          final Dragboard db ) {
        // If no drag row index was saved, there is no way we can drop it.
        if ( !hasDragRowIndex( db ) ) {
            return false;
        }

        // Exit early for efficiency, if the drag index is invalid.
        final int dragIndex = Integer.parseInt( db.getString() );
        if ( dragIndex < 0 ) {
            return false;
        }

        // Calculate the drop index based on the mouse position, as that
        // triggers which row receives this callback. Default to the "end
        // of the table" to add as first row, if empty.
        final ObservableList< TD > rows = table.getItems();
        final int dropIndex = !row.isEmpty() ? row.getIndex() : rows.size();

        // If the drop index is invalid or the same as the drag index,
        // there is nothing to do, so we don't redundantly "move" the row.
        if ( ( dropIndex < 0 ) || ( dragIndex == dropIndex ) ) {
            return false;
        }
        
        // Move (add/remove) the row within the backing list data model.
        // NOTE: Dragboard doesn't support POJO's, so we must manually
        //  grab the row to "add" to the drop location by first removing it.
        // NOTE: We are experimenting with adding first, by grabbing the row
        //  and allowing Copy as well as Move, for temporary pre-delete state.
        final TD rowData = rows.get( dragIndex );
        rows.add( dropIndex, rowData );
        final int removeIndex = ( dragIndex < dropIndex ) 
                ? dragIndex 
                : dragIndex + 1;
        rows.remove( removeIndex );

        // Update the selection to the selected row at its new position.
        table.getSelectionModel().select( dropIndex );

        return true;
    }
}
