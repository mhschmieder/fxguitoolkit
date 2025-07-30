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
package com.mhschmieder.fxguitoolkit.control;

import java.util.Comparator;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * {@code XTableView} is a concrete base class that serves as a specialization
 * of {@link TableView}, primarily to augment the core API so that derived
 * classes don't have to write copy/paste code that might diverge over time.
 *
 * @param <TD>
 *            The data type for the table
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class XTableView< TD > extends TableView< TD > {

    /**
     * Flag for whether auto-selection is enabled, when nothing is selected.
     */
    private final boolean autoSelectionEnabled;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Constructs an {@link XTableView} with auto-selection disabled.
     */
    public XTableView() {
        this( false );
    }

    /**
     * Constructs an {@link XTableView} with the specified auto-selection.
     *
     * @param autoSelectionIsEnabled
     *            {@code true} if auto-selection is enabled when nothing is
     *            manually or programmatically selected
     *
     * @since 1.0
     */
    public XTableView( final boolean autoSelectionIsEnabled ) {
        // Always call the superclass constructor first!
        super();

        autoSelectionEnabled = autoSelectionIsEnabled;

        try {
            initTable();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * Initializes this Table View in an encapsulated way that protects all
     * constructors from run-time exceptions that might prevent instantiation.
     * <p>
     * This method is designed to be invoked by derived classes during their own
     * initialization, as it requires parameters that aren't available in this
     * abstract base class.
     *
     * @since 1.0
     */
    private final void initTable() {
        // Replace the default "No content..." placeholder label with a blank,
        // white pane. This is as close as we can get to the look of an empty
        // table that matches the table's color scheme, but without column or
        // cell dividers and/or borders until the first row is added. Still,
        // it is less jarring to the user than the default label against a
        // potentially dark background color, as the window's background is
        // used until the default placeholder label is replaced by a table.
        // There does not appear to be another way to override the default.
        final Pane placeholder = new Pane();
        final Background background = LayoutFactory.makeRegionBackground( 
                Color.WHITE );
        placeholder.setBackground( background );
        setPlaceholder( placeholder );

        // Prevent multi-column table sorting (when shift-clicking columns).
        setOnSort( sortEvent -> {
            while ( getSortOrder().size() > 1 ) {
                getSortOrder().remove( 1 );
            }
        } );
        
        // Final status of experiment in handling TAB and ENTER for traversing the
        // table cells. Although this code mostly works, it breaks other aspects of
        // those keys, and we have to use weird combinations of clearing selections
        // and consuming the key event, yet the end result is that ENTER goes up and
        // down rows but only goes into edit mode once on the last row. If in edit
        // mode, with or without committing changes, neither key traverses usefully.
        setOnKeyPressed( keyEvent -> {
            // Cache the previous selection, in case of multi-mode.
            final int selectedIndex = getSelectionModel().getSelectedIndex();
        
            switch ( keyEvent.getCode() ) {
                case ENTER:
                    if ( keyEvent.isShiftDown() ) {
                        getSelectionModel().selectAboveCell();
                    }
                    else {
                        getSelectionModel().selectBelowCell();
                    }
                    
                    getSelectionModel().clearSelection( selectedIndex );

                    keyEvent.consume();
                   
                    break;
                    
                case TAB:
                    if ( keyEvent.isShiftDown() ) {
                        getSelectionModel().selectLeftCell();
                    }
                    else {
                        getSelectionModel().selectRightCell();
                    }
                    
                    getSelectionModel().clearSelection( selectedIndex );

                    /*
                    // Cache the previous selection, in case of multi-mode.
                    final int selectedIndex = getSelectionModel().getSelectedIndex();
                    
                    if ( keyEvent.isShiftDown() ) {
                        getSelectionModel().selectPrevious();
                    } else {
                        getSelectionModel().selectNext();
                    }

                    // Remove the previous selection, in case of multi-mode.
                    getSelectionModel().clearSelection( selectedIndex );
                    */

                    //keyEvent.consume();
                    
                    break;

                default:
                    break;
            }
        } );
    }

    ////////////////// Accessor methods for private data /////////////////////

    /**
     * Returns {@code true} if auto-selection is enabled when nothing is
     * manually or programmatically selected.
     *
     * @return {@code true} if auto-selection is enabled when nothing is
     *         manually or programmatically selected
     *
     * @since 1.0
     */
    public final boolean isAutoSelectionEnabled() {
        return autoSelectionEnabled;
    }

    /**
     * Sets the {@code editable} property to {@code true} if this
     * {@link TableView} is editable, then also makes sure that the individual
     * table cells can be selected.
     *
     * @param tableEditable
     *            {@code true} if this {@link TableView} is editable
     *
     * @since 1.0
     */
    public final void setTableEditable( final boolean tableEditable ) {
        // Set the flag for whether the table is editable or not.
        setEditable( tableEditable );

        // Allow the individual cells to be selected.
        getSelectionModel().cellSelectionEnabledProperty().set( true );
    }

    /**
     * Returns a {@link TableColumn} that is offset from the provided
     * {@link TableColumn}. It is up to the caller to provide a valid offset.
     *
     * @param column
     *            The {@link TableColumn} to use as the reference for the column
     *            offset
     * @param offset
     *            The column offset to apply to the provided column
     * @return A {@link TableColumn} that is offset from the provided
     *         {@link TableColumn}, or {@code null} if the offset is invalid
     *
     * @since 1.0
     */
    public final TableColumn< TD, ? > getOffsetTableColumn( final TableColumn< TD, ? > column,
                                                            final int offset ) {
        final int columnIndex = getVisibleLeafIndex( column );
        final int newColumnIndex = columnIndex + offset;

        return getVisibleLeafColumn( newColumnIndex );
    }

    ////////////////////// Table manipulation methods ////////////////////////

    /**
     * Clears any active selection in the table.
     *
     * @since 1.0
     */
    public final void clearSelection() {
        // Due to some thread timing bugs on macOS, and just for general
        // performance, it is best to avoid known no-op conditions.
        final TableView.TableViewSelectionModel< TD > selectionModel = getSelectionModel();
        if ( !selectionModel.isEmpty() ) {
            selectionModel.clearSelection();
        }
    }

    /**
     * Returns the index of the last row in the table.
     *
     * @return The index of the last row in the table
     *
     * @since 1.0
     */
    public final int getLastRowIndex() {
        return getItems().size() - 1;
    }

    /**
     * Returns the number of selected rows, or zero if none selected.
     *
     * @return The number of selected rows, or zero if none selected
     */
    public final int getNumberOfSelectedRows() {
        final int[] selectedRowIndices = getSelectedRows();
        final int numberOfSelectedRows = ( selectedRowIndices != null )
            ? selectedRowIndices.length
            : 0;

        return numberOfSelectedRows;
    }

    /**
     * Returns the list of currently selected table row indices, in reverse
     * order so that deletions and other actions can be performed sequentially
     * without any of the indices "going bad" mid-stream.
     * <p>
     * A simple static array of integers is used vs. a Sorted List observable
     * wrapper, as the latter adds indices without even removing old ones, if
     * the associated table changes (such as via row deletion or insertion)
     * after the Sorted List is fetched. An unexpected property, not discussed
     * in the official JavaFX API documentation!
     *
     * @return A list of the selected table row indices, or {@code null} if none
     *         selected
     *
     * @since 1.0
     */
    public final int[] getSelectedRows() {
        // Get the selected row indices in reverse order, so that all indices
        // remain valid if we delete rows one at a time.
        final TableViewSelectionModel< TD > selectionModel = getSelectionModel();
        final SortedList< Integer > sortedRows = selectionModel.getSelectedIndices()
                .sorted( Comparator.reverseOrder() );
        if ( ( sortedRows == null ) || sortedRows.isEmpty() ) {
            return null;
        }

        // Convert to standard integers, as the SortedList can actually change
        // after-the-fact to add (not even replace) indices that reflect
        // real-time changes to the table.
        //
        // We should search for a common utility method that does this.
        final int numberOfSelectedRows = sortedRows.size();
        final int[] selectedRows = new int[ numberOfSelectedRows ];
        for ( int rowIndex = 0; rowIndex < numberOfSelectedRows; rowIndex++ ) {
            selectedRows[ rowIndex ] = sortedRows.get( rowIndex ).intValue();
        }

        return selectedRows;
    }

    /**
     * Returns the hierarchically-lower-most selected row, or the last row in
     * the table if none were selected.
     *
     * @param minimumRowIndex
     *            The lowest index that is considered valid for the selected row
     * @return The hierarchically-lower-most selected row, or the last row in
     *         the table if none were selected
     *
     * @since 1.0
     */
    public final int getSelectedRow( final int minimumRowIndex ) {
        // If the user didn't select any rows, default initially to the row
        // before the first valid row index, to make sure any selected row
        // overrides the initial default.
        int selectionIndex = minimumRowIndex - 1;

        final int[] selectedRowIndices = getSelectedRows();
        if ( ( selectedRowIndices != null ) && ( selectedRowIndices.length > 0 ) ) {
            final int maximumRowIndex = selectedRowIndices.length - 1;
            selectionIndex = selectedRowIndices[ maximumRowIndex ];
        }
        else {
            // If no rows were selected, and auto-selection is enabled, correct
            // the default selection to be the last valid row index.
            if ( autoSelectionEnabled ) {
                final int maximumRowIndex = getLastRowIndex();
                selectionIndex = FastMath.max( selectionIndex, maximumRowIndex );
            }
        }

        return selectionIndex;
    }

    /**
     * Auto-selects the default table row selection, which is the last row in
     * the table in this implementation.
     *
     * @since 1.0
     */
    public final void setDefaultSelection() {
        // Trigger the default auto-selection by mimicking an empty or invalid
        // selection.
        selectRow( -1 );
    }

    /**
     * Selects the specified row in the table.
     * <p>
     * This method clears any active selections at the same time as setting the
     * new row selection, using the safe combined {@link
     * TableViewSelectionModel#clearAndSelect} method, so that only one row is
     * selected at a time, avoiding confusion over focus and selection status.
     * <p>
     * This is safer than performing two separate actions, and also more
     * performant, as it avoids the interim state where the selected row index
     * is deliberately set to the invalid selection indicator of "-1".
     *
     * @param rowIndex
     *            The index of the table row to select
     *
     * @since 1.0
     */
    public final void selectRow( final int rowIndex ) {
        // Select the requested row, or auto-select the last row in the table if
        // the requested row is invalid. If the table is now empty, select
        // nothing as otherwise an index out of range exception is thrown.
        final int lastRowIndex = getLastRowIndex();
        final int adjustedRowIndex = ( ( rowIndex < 0 ) || ( rowIndex > lastRowIndex ) )
            ? lastRowIndex
            : FastMath.min( rowIndex, lastRowIndex );
        if ( adjustedRowIndex >= 0 ) {
            final TableViewSelectionModel< TD > selectionModel = getSelectionModel();
            selectionModel.clearAndSelect( adjustedRowIndex );
        }
    }

    /**
     * Selects the specified cell in the table, then scrolls to its table row.
     * <p>
     * This effectively places editing focus in the specified row and column.
     *
     * @param rowIndex
     *            The row index of the table cell to select
     * @param columnIndex
     *            The column index of the table cell to select
     *
     * @since 1.0
     */
    public final void setEditingFocus( final int rowIndex, final int columnIndex ) {
        // Set the specific cell that should get editing focus.
        selectCell( rowIndex, columnIndex );

        // Scroll to the row that this cell is on, to make sure it is visible.
        scrollTo( rowIndex );
    }

    /**
     * Selects the specified cell in the table.
     * <p>
     * This method clears any active selections at the same time as setting the
     * new cell selection, using the safe combined {@link
     * TableViewSelectionModel#clearAndSelect} method, so that only one cell is
     * selected at a time, avoiding confusion over focus and selection status.
     * <p>
     * This is safer than performing two separate actions, and also more
     * performant, as it avoids the interim state where the selected cell
     * indices are deliberately set to the invalid selection indicator of "-1".
     *
     * @param rowIndex
     *            The row index of the table cell to select
     * @param columnIndex
     *            The column index of the table cell to select
     *
     * @since 1.0
     */
    public final void selectCell( final int rowIndex, final int columnIndex ) {
        // Select the requested cell, or do nothing if the row is invalid.
        final int lastRowIndex = getLastRowIndex();
        if ( ( rowIndex >= 0 ) && ( rowIndex <= lastRowIndex ) ) {
            final TableViewSelectionModel< TD > selectionModel = getSelectionModel();
            final TableColumn< TD, ? > column = getColumns().get( columnIndex );
            selectionModel.clearAndSelect( rowIndex, column );
        }
    }

    /////////////// ForegroundManager implementation methods /////////////////

    /**
     * Sets the appropriate foreground color for this table based on the
     * specified background color.
     * <p>
     * Both the background and the foreground are applied to the entire layout
     * hierarchy, with the foreground color chosen to provide adequate contrast
     * against the background for text rendering as well as for line graphics.
     * <p>
     * This method should be overridden and called as the first line in the
     * method override, before adding support for GUI elements unique to the
     * derived class hierarchy.
     *
     * @param backColor
     *            The current background color to apply to this table
     *
     * @since 1.0
     */
    public void setForegroundFromBackground( final Color backColor ) {
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );
    }
}
