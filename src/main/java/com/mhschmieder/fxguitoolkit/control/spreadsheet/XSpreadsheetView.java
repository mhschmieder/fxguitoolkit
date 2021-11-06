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
 * This file is part of the ChartToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * ChartToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/charttoolkit
 */
package com.mhschmieder.fxguitoolkit.control.spreadsheet;

import java.text.NumberFormat;
import java.util.Comparator;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetViewSelectionModel;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;

public abstract class XSpreadsheetView extends SpreadsheetView {

    protected static boolean canDeleteTableRowAt( final int deleteIndex,
                                                  final int minimumDeleteIndex,
                                                  final int maximumDeleteIndex,
                                                  final int minimumLastRowIndex ) {
        // If the index is out of bounds, or the table size has already reached
        // the minimum number of rows required by the table, ignore the deletion
        // request.
        return ( ( deleteIndex >= minimumDeleteIndex ) && ( deleteIndex <= maximumDeleteIndex )
                && ( maximumDeleteIndex >= minimumDeleteIndex )
                && ( maximumDeleteIndex > minimumLastRowIndex ) );
    }

    protected static final boolean canInsertTableRowAt( final int insertIndex,
                                                        final int minimumInsertIndex,
                                                        final int maximumInsertIndex,
                                                        final int maximumLastRowIndex ) {
        // If the index is out of bounds, or the table size has already reached
        // the maximum number of rows allowed by the table, ignore the insertion
        // request.
        return ( ( insertIndex >= minimumInsertIndex ) && ( insertIndex <= maximumInsertIndex )
                && ( maximumInsertIndex >= minimumInsertIndex )
                && ( maximumInsertIndex <= maximumLastRowIndex ) );
    }

    // Flag for whether auto-selection is enabled, when nothing is selected.
    protected boolean       _autoSelectionEnabled;

    // Flag for whether we are in the midst of deleting rows unconditionally.
    protected boolean       _deletingRowsUnconditionally;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    // Number format cache used for locale-specific number formatting of
    // uniquefier appendices.
    public NumberFormat     _uniquefierNumberFormat;

    public XSpreadsheetView( final ClientProperties pClientProperties,
                             final boolean autoSelectionEnabled,
                             final SelectionMode selectionMode ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;
        _autoSelectionEnabled = autoSelectionEnabled;
        _deletingRowsUnconditionally = false;

        try {
            initSpreadsheet( selectionMode );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean canDeleteTableRowAt( final int deleteIndex ) {
        final int minimumDeleteIndex = 0;
        final int maximumDeleteIndex = getLastRowIndex();
        final int minimumLastRowIndex = 1;
        return canDeleteTableRowAt( deleteIndex,
                                    minimumDeleteIndex,
                                    maximumDeleteIndex,
                                    minimumLastRowIndex );
    }

    // Determine whether row deletion is legal, regardless of context.
    public boolean canDeleteTableRows() {
        // Determine whether the table is populated or empty, whether any
        // delete-enabled rows are selected, and any additional criteria
        // supplied by overridden methods.
        boolean canDeleteRows = true;
        final SortedList< Integer > selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices != null ) && !selectedRowIndices.isEmpty() ) {
            for ( final Integer selectedRowIndex : selectedRowIndices ) {
                if ( !canDeleteTableRowAt( selectedRowIndex ) ) {
                    canDeleteRows = false;
                    break;
                }
            }
        }

        return canDeleteRows;
    }

    public final void clearSelection() {
        final SpreadsheetViewSelectionModel selectionModel = getSelectionModel();
        selectionModel.clearSelection();
    }

    // Delete the table row at the specified index.
    public int deleteTableRowAt( final int deleteIndex,
                                 final int minimumDeleteIndex,
                                 final int maximumDeleteIndex,
                                 final int minimumLastRowIndex ) {
        // First determine whether the indicated row can be deleted.
        if ( !canDeleteTableRowAt( deleteIndex,
                                   minimumDeleteIndex,
                                   maximumDeleteIndex,
                                   minimumLastRowIndex ) ) {
            return -1;
        }

        // Get the current grid so we can delete from the appropriate row.
        final Grid grid = getGrid();

        // Fetch the rows directly from the grid. These should be up-to-date.
        final ObservableList< ObservableList< SpreadsheetCell > > rows = grid.getRows();

        // Check for basic data integrity of the grid rows first.
        if ( ( rows == null ) || rows.isEmpty() ) {
            return -1;
        }

        // Check for out of range index errors next.
        final int referenceIndex = deleteIndex;
        if ( ( referenceIndex < 0 ) || ( referenceIndex >= rows.size() ) ) {
            return -1;
        }

        // Delete the requested spreadsheet row from the grid.
        rows.remove( deleteIndex );

        // NOTE: We have to update the grid in order for its view to be
        // refreshed, along with maintaining the correct indices for fixed rows
        // and columns, and the current multi-cell selection list.
        setGrid( grid );

        // Return the actual delete index so that it can be used to
        // re-highlight the most appropriate default row for the next action.
        return deleteIndex;
    }

    public int deleteTableRows() {
        // Default to no restrictions on the deletable row range.
        // NOTE: The minimum last row count index of -1 is required for tables
        // that can be empty (whether initially or after editing), as an index
        // of zero corresponds to a minimum row count of one.
        final int minimumDeleteIndex = 0;
        final int minimumLastRowIndex = -1;

        // Delete the selected row(s).
        final int referenceIndex = deleteTableRows( minimumDeleteIndex, minimumLastRowIndex );

        return referenceIndex;
    }

    // Delete the selected table row(s) (or the last row if none were selected).
    public final int deleteTableRows( final int minimumDeleteIndex,
                                      final int minimumLastRowIndex ) {
        // Delete all of the selected table row(s), except the minimum row.
        // NOTE: The row selection method takes care of auto-select defaults.
        int referenceIndex = -1;
        final SortedList< Integer > selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices != null ) && !selectedRowIndices.isEmpty() ) {
            int correctedIndex = -1;
            for ( final Integer deleteIndex : selectedRowIndices ) {
                // :NOTE: As the table changes size inside this loop, we have to
                // refresh the last row index on each iteration.
                final int maximumDeleteIndex = getLastRowIndex();
                correctedIndex = deleteTableRowAt( deleteIndex,
                                                   minimumDeleteIndex,
                                                   maximumDeleteIndex,
                                                   minimumLastRowIndex );

                // Make sure we only use the first valid corrected index, as we
                // handle delete in reverse order and want to use the last
                // selected row as the reference row.
                if ( referenceIndex < 0 ) {
                    referenceIndex = correctedIndex;
                }
            }

            // Now adjust the last selected row index for the number of rows
            // deleted (minus one, as we always try to select the row directly
            // after the one deleted).
            final int selectionLength = selectedRowIndices.size();
            referenceIndex -= ( selectionLength - 1 );
        }
        else {
            // If the user didn't select any rows, default to deleting the last
            // row in the table.
            final int maximumDeleteIndex = getLastRowIndex();
            referenceIndex = deleteTableRowAt( maximumDeleteIndex,
                                               minimumDeleteIndex,
                                               maximumDeleteIndex,
                                               minimumLastRowIndex );
        }

        return referenceIndex;
    }

    // Unconditionally delete the specified table row to the end of table.
    public final boolean deleteTableRowsUnconditionally( final int firstDeleteIndex ) {
        // Get the current grid so we can delete from the appropriate row.
        final Grid grid = getGrid();

        // Fetch the rows directly from the grid. These should be up-to-date.
        final ObservableList< ObservableList< SpreadsheetCell > > rows = grid.getRows();

        // Check for basic data integrity of the grid rows first.
        if ( ( rows == null ) || rows.isEmpty() ) {
            return false;
        }

        final int numberOfRows = rows.size();
        final int lastDeleteIndex = numberOfRows - 1;

        final boolean tableRowsDeleted = deleteTableRowsUnconditionally( firstDeleteIndex,
                                                                         lastDeleteIndex );

        return tableRowsDeleted;
    }

    // Delete the specified table row range unconditionally.
    public final boolean deleteTableRowsUnconditionally( final int firstDeleteIndex,
                                                         final int lastDeleteIndex ) {
        if ( firstDeleteIndex > lastDeleteIndex ) {
            return false;
        }

        // Get the current grid so we can delete from the appropriate row.
        final Grid grid = getGrid();

        // Fetch the rows directly from the grid. These should be up-to-date.
        final ObservableList< ObservableList< SpreadsheetCell > > rows = grid.getRows();

        // Check for basic data integrity of the grid rows first.
        if ( ( rows == null ) || rows.isEmpty() ) {
            return false;
        }

        // Avoid side effects by letting subscribers know we are deleting.
        _deletingRowsUnconditionally = true;

        // Delete the requested spreadsheet row from the grid.
        // :NOTE: We have to bump the end index as it is exclusive.
        rows.remove( firstDeleteIndex, lastDeleteIndex + 1 );

        // :NOTE: We have to update the grid in order for its view to be
        // refreshed, along with maintaining the correct indices for fixed rows
        // and columns, and the current multi-cell selection list.
        setGrid( grid );

        // It is now safe to let subscribers handle change events.
        _deletingRowsUnconditionally = false;

        return true;
    }

    public final int getLastRowIndex() {
        final Grid grid = getGrid();
        final int numberOfRows = grid.getRowCount();
        final int lastRowIndex = numberOfRows - 1;
        return lastRowIndex;
    }

    // TODO: Make this more flexible by passing in the Comparator criteria?
    // TODO: Provide or override the preferred auto-select row index.
    public final SortedList< Integer > getSelectedRows() {
        // Default to a null selection, to simplify and decouple handling logic.
        final ObservableList< Integer > selectedRowIndices = FXCollections.observableArrayList();

        final SpreadsheetViewSelectionModel selectionModel = getSelectionModel();
        final ObservableList< TablePosition > selectedCells = selectionModel.getSelectedCells();
        if ( ( selectedCells == null ) || selectedCells.isEmpty() ) {
            // If auto-selection enabled, select the last valid row.
            if ( isAutoSelectionEnabled() ) {
                final int maximumRowIndex = getLastRowIndex();

                // For uniformity of handling, create a one-element array.
                selectedRowIndices.addAll( maximumRowIndex );
            }
        }
        else {
            for ( final TablePosition selectedCell : selectedCells ) {
                final int row = selectedCell.getRow();
                if ( !selectedRowIndices.contains( row ) ) {
                    selectedRowIndices.addAll( row );
                }
            }
        }

        // Sort the selection in reverse, as lowest row always takes precedence.
        // This avoids indexing errors if rows are removed in forward order.
        if ( ( selectedRowIndices != null ) && !selectedRowIndices.isEmpty() ) {
            final SortedList< Integer > sortedRowIndices = new SortedList<>( selectedRowIndices
                    .sorted( Comparator.reverseOrder() ) );
            return sortedRowIndices;
        }

        return null;
    }

    @SuppressWarnings("nls")
    private void initSpreadsheet( final SelectionMode selectionMode ) {
        // This is an editable table, in terms of both selectivity and typing.
        setEditable( true );

        // Set the preferred selection mode, which is cell-based vs. row-based.
        getSelectionModel().setSelectionMode( selectionMode );

        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _uniquefierNumberFormat = NumberFormat.getNumberInstance( clientProperties.locale );

        // Try to use our custom CSS to style the table headers the way we do in
        // TableView (that is, with bold italic font and an off-blue background
        // for the table column header cells).
        GuiUtilities.addStylesheetAsJarResource( this, "/css/spreadsheet.css" );
    }

    public int insertTableRow() {
        // Clone the selected or defaulted row when adding a new one.
        final int minimumInsertIndex = 0;
        final int referenceIndex = insertTableRow( minimumInsertIndex );

        return referenceIndex;
    }

    public final int insertTableRow( final int minimumInsertIndex ) {
        final int maximumRowCountIndex = Integer.MAX_VALUE;

        // Insert a new table row set to the currently selected row.
        final int referenceIndex = insertTableRow( minimumInsertIndex, maximumRowCountIndex );

        return referenceIndex;
    }

    // Find the lower-most row selected (or the last row if none were selected),
    // and insert an initially similar or identical row after it.
    public final int insertTableRow( final int minimumInsertIndex, final int maximumLastRowIndex ) {
        // Get all of the currently selected rows, regardless of selection mode
        // or auto-select mode.
        final SortedList< Integer > selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices == null ) || selectedRowIndices.isEmpty() ) {
            return -1;
        }

        // Insert an initially similar or identical row after the selected row.
        // :NOTE: We only support single insertion, so we use the first of the
        // reverse-sorted rows, but it might be better to use the last instead.
        final int selectionIndex = selectedRowIndices.get( 0 );
        final int insertIndex = selectionIndex + 1;
        final int maximumInsertIndex = getLastRowIndex() + 1;
        final int referenceIndex = insertTableRowAt( insertIndex,
                                                     minimumInsertIndex,
                                                     maximumInsertIndex,
                                                     maximumLastRowIndex );

        return referenceIndex;
    }

    // Insert a table row at the insert index, initially cloned from the
    // reference index (or defaulted if the reference index is too low).
    public abstract int insertTableRowAt( int insertIndex,
                                          int minimumInsertIndex,
                                          int maximumInsertIndex,
                                          int maximumLastRowIndex );

    public final boolean isAutoSelectionEnabled() {
        return _autoSelectionEnabled;
    }

    public final boolean isDeletingRowsUnconditionally() {
        return _deletingRowsUnconditionally;
    }

    // Auto-select the last row in the spreadsheet.
    public final void setDefaultSelection() {
        // Trigger the default auto-selection by mimicking an empty or invalid
        // selection.
        setSelectedRow( -1 );
    }

    // Select the specified row in the table.
    // :NOTE: There are new methods that do things like select the next row.
    public final void setSelectedRow( final int selectedRowIndex ) {
        // Clear any active row selections, in case the table is empty.
        // :NOTE: Commented out due to the third-party code treating the visual
        // highlighting differently from the selection mechanism itself, so we
        // lose our highlighting if we clear the selection, but we basically
        // need to retain the same selection index to keep in sync.
        // :NOTE: Re-enabled so it doesn't break in contexts other than insert.
        clearSelection();

        // Select the requested row, or auto-select the last row in the
        // spreadsheet if the requested row is invalid. If the spreadsheet is
        // now empty, select nothing as otherwise an index out of range
        // exception will be thrown.
        // :NOTE: Disabled, because manual selection uses single selection mode
        // for some reason, whereas programmatic selection uses multiple
        // selection mode, so we stay stuck at the initial manual selection if
        // we clear and reset the selected rows. This is clearly a bug in the
        // third-party software but it isn't worth fixing as it represents a
        // flaw in how they piggy-back atop JavaFX Table/Cell components.
        // :NOTE: Re-enabled so it doesn't break in contexts other than insert.
        final int lastRowIndex = getLastRowIndex();
        final int autoselectRowIndex = ( selectedRowIndex < 0 )
            ? lastRowIndex
            : Math.min( selectedRowIndex, lastRowIndex );
        if ( autoselectRowIndex >= 0 ) {
            final SpreadsheetViewSelectionModel selectionModel = getSelectionModel();

            final ObservableList< SpreadsheetColumn > currentColumns = getColumns();
            // for ( int columnIndex = 0; columnIndex < currentColumns.size();
            // columnIndex++ ) {
            // final SpreadsheetColumn column = currentColumns.get( columnIndex
            // );
            // selectionModel.select( autoselectRowIndex - 1, column );
            // }

            final SpreadsheetColumn column = currentColumns.get( 0 );
            selectionModel.select( autoselectRowIndex, column );
        }
    }

}// class MsliSpreadsheetView
