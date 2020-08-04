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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;

/**
 * {@code DynamicXTableView} is a further abstraction of {@link XTableView}
 * that sets up the functionality that is likely to be shared by all multi-row
 * tables that support dynamically adding and deleting rows after creation.
 *
 * @param <TD>
 *            The data type for the table
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public abstract class DynamicXTableView< TD > extends XTableView< TD > {

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Constructs a {@code DynamicTableXPanel} with auto-selection disabled.
     * <p>
     * This is an abstract base class, so its purpose is to avoid copy/paste
     * code in the derived classes; it is unable to function on its own.
     *
     * @since 1.0
     */
    public DynamicXTableView() {
        this( false );
    }

    /**
     * Constructs a {@code DynamicTableXPanel} with the specified
     * auto-selection.
     * <p>
     * This is an abstract base class, so its purpose is to avoid copy/paste
     * code in the derived classes; it is unable to function on its own.
     *
     * @param autoSelectionIsEnabled
     *            {@code true} if auto-selection is enabled when nothing is
     *            manually or programmatically selected
     *
     * @since 1.0
     */
    public DynamicXTableView( final boolean autoSelectionIsEnabled ) {
        // Always call the superclass constructor first!
        super( autoSelectionIsEnabled );

        // Tables that support Add/Delete also support multi-row selection.
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
    }

    ////////////////////// Table manipulation methods ////////////////////////

    /**
     * Returns {@code true} if a row can be inserted at the specified index.
     *
     * @param insertIndex
     *            The selected index for inserting a new row
     * @param minimumInsertIndex
     *            The minimum allowed index for inserting a new row
     * @param maximumInsertIndex
     *            The maximum allowed index for inserting a new row
     * @param maximumLastRowIndex
     *            The maximum index that is ever allowed for this table
     * @return {@code true} if a row can be inserted at the specified index
     *
     * @since 1.0
     */
    @SuppressWarnings("static-method")
    protected boolean canInsertTableRowAt( final int insertIndex,
                                           final int minimumInsertIndex,
                                           final int maximumInsertIndex,
                                           final int maximumLastRowIndex ) {
        // If the index is out of bounds, or the table size has already reached
        // the maximum number of rows allowed, ignore the insertion request.
        return ( ( insertIndex >= minimumInsertIndex ) && ( insertIndex <= maximumInsertIndex )
                && ( maximumInsertIndex >= minimumInsertIndex )
                && ( maximumInsertIndex <= maximumLastRowIndex ) );
    }

    /**
     * Returns {@code true} if row deletion is legal, regardless of context.
     *
     * @return {@code true} if row deletion is legal, regardless of context
     *
     * @since 1.0
     */
    public boolean canDeleteTableRows() {
        // Determine whether the table is populated or empty, whether any
        // delete-enabled rows are selected, and any additional criteria
        // supplied by overridden methods. If no rows are selected, we seed
        // the logic auto-selected to the last row.
        //
        // Maybe provide or override the preferred auto-select row index?
        boolean canDeleteRows = true;
        final int[] selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices != null ) && ( selectedRowIndices.length > 0 ) ) {
            for ( final int selectedRowIndex : selectedRowIndices ) {
                if ( !canDeleteTableRowAt( selectedRowIndex ) ) {
                    canDeleteRows = false;
                    break;
                }
            }
        }
        else {
            // If no rows were selected, and auto-selection is enabled, correct
            // the default selection to be the last valid row index.
            if ( isAutoSelectionEnabled() ) {
                final int maximumRowIndex = getLastRowIndex();
                if ( !canDeleteTableRowAt( maximumRowIndex ) ) {
                    canDeleteRows = false;
                }
            }
            else {
                canDeleteRows = false;
            }
        }

        return canDeleteRows;
    }

    /**
     * Returns {@code true} if a row can be deleted at the specified index
     *
     * @param deleteIndex
     *            The selected index for deleting an existing row
     * @return {@code true} if a row can be deleted at the specified index
     *
     * @since 1.0
     */
    public boolean canDeleteTableRowAt( final int deleteIndex ) {
        final int minimumDeleteIndex = 0;
        final int maximumDeleteIndex = getLastRowIndex();
        final int minimumLastRowIndex = -1;
        return canDeleteTableRowAt( deleteIndex,
                                    minimumDeleteIndex,
                                    maximumDeleteIndex,
                                    minimumLastRowIndex );
    }

    /**
     * Returns {@code true} if a row can be deleted at the specified index
     *
     * @param deleteIndex
     *            The selected index for deleting an existing row
     * @param minimumDeleteIndex
     *            The minimum allowed index for deleting an existing row
     * @param maximumDeleteIndex
     *            The maximum allowed index for deleting an existing row
     * @param minimumLastRowIndex
     *            The minimum index that is ever allowed for this table
     * @return {@code true} if a row can be deleted at the specified index
     *
     * @since 1.0
     */
    @SuppressWarnings("static-method")
    protected boolean canDeleteTableRowAt( final int deleteIndex,
                                           final int minimumDeleteIndex,
                                           final int maximumDeleteIndex,
                                           final int minimumLastRowIndex ) {
        // If the index is out of bounds, or the table size has already reached
        // the minimum number of rows required, ignore the deletion request.
        return ( ( deleteIndex >= minimumDeleteIndex ) && ( deleteIndex <= maximumDeleteIndex )
                && ( maximumDeleteIndex >= minimumDeleteIndex )
                && ( maximumDeleteIndex > minimumLastRowIndex ) );
    }

    /**
     * Returns the row index for the newly inserted row (if valid), added to the
     * table at the selected row index.
     * <p>
     * This method finds the lower-most row selected (or the last row if none
     * were selected), and inserts an initially similar row right after it.
     *
     * @return The row index for the newly inserted row (if valid)
     *
     * @since 1.0
     */
    public int insertTableRow() {
        // Clone the selected or defaulted row when adding a new one.
        final int minimumInsertIndex = 0;
        final int referenceIndex = insertTableRow( minimumInsertIndex );

        return referenceIndex;
    }

    /**
     * Returns the row index for the newly inserted row (if valid), added to the
     * table at the selected row index.
     * <p>
     * This method finds the lower-most row selected (or the last row if none
     * were selected), and inserts an initially similar row right after it.
     *
     * @param minimumInsertIndex
     *            The minimum allowed index for inserting a new row
     * @return The row index for the newly inserted row (if valid)
     *
     * @since 1.0
     */
    public int insertTableRow( final int minimumInsertIndex ) {
        final int maximumRowCountIndex = Integer.MAX_VALUE;

        // Insert a new table row set to the currently selected row.
        final int referenceIndex = insertTableRow( minimumInsertIndex, maximumRowCountIndex );

        return referenceIndex;
    }

    /**
     * Returns the row index for the newly inserted row (if valid), added to the
     * table at the selected row index.
     * <p>
     * This method finds the lower-most row selected (or the last row if none
     * were selected), and inserts an initially similar row right after it.
     *
     * @param minimumInsertIndex
     *            The minimum allowed index for inserting a new row
     * @param maximumLastRowIndex
     *            The maximum index that is ever allowed for this table
     * @return The row index for the newly inserted row (if valid)
     *
     * @since 1.0
     */
    protected final int insertTableRow( final int minimumInsertIndex,
                                        final int maximumLastRowIndex ) {
        // Insert an initially similar or identical row after the selected row,
        // by adding an item to the associated collection (the table syncs up).
        final int selectionIndex = getSelectedRow( minimumInsertIndex );
        final int insertIndex = selectionIndex + 1;
        final int maximumInsertIndex = getLastRowIndex() + 1;
        final int referenceIndex = addItemAt( insertIndex,
                                              minimumInsertIndex,
                                              maximumInsertIndex,
                                              maximumLastRowIndex );

        return referenceIndex;
    }

    /**
     * Returns the row index for the newly inserted row (if valid), added to the
     * associated collection at the specified index (the table syncs up).
     * <p>
     * There is no default implementation in this abstract base class, as the
     * data model for each derived class will be needed for making a data object
     * associated with the row's contents.
     *
     * @param insertIndex
     *            The selected index for inserting a new row
     * @param minimumInsertIndex
     *            The minimum allowed index for inserting a new row
     * @param maximumInsertIndex
     *            The maximum allowed index for inserting a new row
     * @param maximumLastRowIndex
     *            The maximum index that is ever allowed for this table
     * @return The row index for the newly inserted row (if valid)
     *
     * @since 1.0
     */
    protected abstract int addItemAt( final int insertIndex,
                                      final int minimumInsertIndex,
                                      final int maximumInsertIndex,
                                      final int maximumLastRowIndex );

    /**
     * Returns the row index for the final deleted row (if valid), or the last
     * row if none were selected.
     * <p>
     * This method finds the lower-most row selected (or the last row if none
     * were selected), and inserts an initially similar row right after it.
     *
     * @return The row index for the final deleted row (if valid)
     *
     * @since 1.0
     */
    public int deleteTableRows() {
        // Default to no restrictions on the row range to be deleted.
        //
        // The minimum last row count index of -1 is required for tables that
        // can be empty (whether initially or after editing), as an index of
        // zero corresponds to a minimum row count of one.
        final int minimumDeleteIndex = 0;
        final int minimumLastRowIndex = -1;

        // Delete the selected table row(s).
        final int referenceIndex = deleteTableRows( minimumDeleteIndex, minimumLastRowIndex );

        return referenceIndex;
    }

    /**
     * Returns the row index for the final deleted row (if valid), or the last
     * row if none were selected.
     * <p>
     * This method finds the lower-most row selected (or the last row if none
     * were selected), and inserts an initially similar row right after it.
     *
     * @param minimumDeleteIndex
     *            The minimum allowed index for deleting an existing row
     * @param minimumLastRowIndex
     *            The minimum index that is ever allowed for this table
     * @return The row index for the final deleted row (if valid)
     *
     * @since 1.0
     */
    protected final int deleteTableRows( final int minimumDeleteIndex,
                                         final int minimumLastRowIndex ) {
        // Delete all of the selected table row(s), except the minimum row.
        //
        // A comparator is used, to get the selected rows into reverse order.
        int referenceIndex = -1;
        final int[] selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices != null ) && ( selectedRowIndices.length > 0 ) ) {
            int correctedIndex = -1;
            for ( final int deleteIndex : selectedRowIndices ) {
                // As the table changes size inside this loop, we have to
                // refresh the last row index on each iteration.
                final int maximumDeleteIndex = getLastRowIndex();
                correctedIndex = removeItemAt( deleteIndex,
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
            final int selectionLength = selectedRowIndices.length;
            referenceIndex -= ( selectionLength - 1 );
        }
        else {
            // If the user didn't select any rows, default to deleting the last
            // row in the table (by removing the last item in the list).
            final int maximumDeleteIndex = getLastRowIndex();
            referenceIndex = removeItemAt( maximumDeleteIndex,
                                           minimumDeleteIndex,
                                           maximumDeleteIndex,
                                           minimumLastRowIndex );
        }

        // Select the reference row from the Delete action if it is valid, or
        // the last row in the table otherwise.
        selectRow( referenceIndex );

        return referenceIndex;
    }

    /**
     * Returns the row index for the deleted row (if the row was deleted).
     * <p>
     * This method removes the item from its collection at the specified index.
     * The table then syncs up automatically.
     *
     * @param deleteIndex
     *            The selected index for deleting an existing row
     * @param minimumDeleteIndex
     *            The minimum allowed index for deleting an existing row
     * @param maximumDeleteIndex
     *            The maximum allowed index for deleting an existing row
     * @param minimumLastRowIndex
     *            The minimum index that is ever allowed for this table
     * @return The row index for the deleted row (if the row was deleted)
     *
     * @since 1.0
     */
    protected final int removeItemAt( final int deleteIndex,
                                      final int minimumDeleteIndex,
                                      final int maximumDeleteIndex,
                                      final int minimumLastRowIndex ) {
        // Determine whether the item at the specified index can be removed.
        if ( !canDeleteTableRowAt( deleteIndex,
                                   minimumDeleteIndex,
                                   maximumDeleteIndex,
                                   minimumLastRowIndex ) ) {
            return -1;
        }

        // Remove the item at the specified index from its collection.
        //
        // This automatically syncs the table to remove the row that displays
        // the specified item.
        final ObservableList< TD > items = getItems();
        items.remove( deleteIndex );

        // Return the requested item removal index so that it can be used to
        // re-highlight the most appropriate default row for the next action.
        return deleteIndex;
    }

}
