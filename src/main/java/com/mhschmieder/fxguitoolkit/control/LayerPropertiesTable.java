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
package com.mhschmieder.fxguitoolkit.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;
import com.mhschmieder.fxguitoolkit.control.cell.LayerColorTableCell;
import com.mhschmieder.fxguitoolkit.control.cell.LayerDisplayTableCell;
import com.mhschmieder.fxguitoolkit.control.cell.LayerLockTableCell;
import com.mhschmieder.fxguitoolkit.control.cell.LayerNameTableCell;
import com.mhschmieder.fxguitoolkit.control.cell.LayerStatusTableCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class LayerPropertiesTable extends DynamicXTableView< LayerProperties > {

    // Do not allow the user to sort by other than Layer Name.
    // NOTE: This has been reverted to disallowed due to our check for
    // delete-enabled being by row number vs. by Layer Name, so sorting the
    // Layers by name can allow the Default Layer 0 to be deleted. We should
    // either exclude that Layer from sorting, or write special logic to detect
    // the Default Layer by name vs. by row number.
    private static final boolean              SORTABLE_DEFAULT         = true;

    // Declare the table column header names.
    private static final String               COLUMN_HEADER_LAYER_NAME = "LAYER NAME";                                    //$NON-NLS-1$
    private static final String               COLUMN_HEADER_COLOR      = "COLOR";                                         //$NON-NLS-1$
    private static final String               COLUMN_HEADER_STATUS     = "STATUS";                                        //$NON-NLS-1$
    private static final String               COLUMN_HEADER_DISPLAY    = "DISPLAY";                                       //$NON-NLS-1$
    private static final String               COLUMN_HEADER_LOCK       = "LOCK";                                          //$NON-NLS-1$

    // Declare static constants to use for symbolically referencing table column
    // indices, to ensure no errors, and ease of extensibility.
    private static final int                  COLUMN_FIRST             = 0;
    public static final int                   COLUMN_LAYER_NAME        = COLUMN_FIRST;
    private static final int                  COLUMN_COLOR             = COLUMN_LAYER_NAME + 1;
    private static final int                  COLUMN_STATUS            = COLUMN_COLOR + 1;
    private static final int                  COLUMN_DISPLAY           = COLUMN_STATUS + 1;
    private static final int                  COLUMN_LOCK              = COLUMN_DISPLAY + 1;
    private static final int                  COLUMN_LAST              = COLUMN_LOCK;
    protected static final int                NUMBER_OF_COLUMNS        =
                                                                ( COLUMN_LAST - COLUMN_FIRST ) + 1;

    // Declare the enforced default row/index of 0 for the Default Layer.
    public static final int                   ROW_DEFAULT_LAYER        =
                                                                LayerUtilities.DEFAULT_LAYER_INDEX;

    // Declare the array of column names to be displayed in the table header.
    public static final String[]              _columnName              =
                                                          new String[] {
                                                                         COLUMN_HEADER_LAYER_NAME,
                                                                         COLUMN_HEADER_COLOR,
                                                                         COLUMN_HEADER_STATUS,
                                                                         COLUMN_HEADER_DISPLAY,
                                                                         COLUMN_HEADER_LOCK };

    // Declare preferred widths for each column.
    // NOTE: These numbers must add up to the assumed table width of 440.
    protected static final int[]              _columnWidth             = new int[] {
                                                                                     240,                                 // COLUMN_LAYER_NAME
                                                                                     120,                                 // COLUMN_COLOR
                                                                                     60,                                  // COLUMN_STATUS
                                                                                     60,                                  // COLUMN_DISPLAY
                                                                                     80                                   // COLUMN_LOCK
    };

    // Declare the array of column property names to be used for data binding.
    protected static final String[]           _columnPropertyName      = {
                                                                           "layerName",                                   // COLUMN_LAYER_NAME //$NON-NLS-1$
                                                                           "layerColor",                                  // COLUMN_COLOR //$NON-NLS-1$
                                                                           "layerActive",                                 // COLUMN_STATUS //$NON-NLS-1$
                                                                           "layerVisible",                                // COLUMN_DISPLAY //$NON-NLS-1$
                                                                           "layerLocked"                                  // COLUMN_LOCK //$NON-NLS-1$
    };

    // Cache the Layer Collection reference.
    private ObservableList< LayerProperties > _layerCollection;

    public LayerPropertiesTable( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( false );

        try {
            initTable( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Insert a Layer at the insert index, initially cloned from the reference
    // index (or defaulted if the reference index is too low).
    @Override
    public final int addItemAt( final int insertIndex,
                                final int minimumInsertIndex,
                                final int maximumInsertIndex,
                                final int maximumLastRowIndex ) {
        // First determine whether the indicated row can be inserted.
        if ( !canInsertTableRowAt( insertIndex,
                                   minimumInsertIndex,
                                   maximumInsertIndex,
                                   maximumLastRowIndex ) ) {
            return -1;
        }

        // Insert a new cloned Layer into the Layer Collection.
        final LayerProperties layer = LayerUtilities.addLayerClone( _layerCollection, insertIndex );

        // Make sure we catch failed inserts, to avoid null pointers later on.
        if ( layer == null ) {
            return -1;
        }

        // Find the new reference index for the newly added item, post-sort.
        final int referenceIndex = _layerCollection.indexOf( layer );

        // Return the newly added item as the reference index for the next
        // action, adjusted for the post-insert sort order.
        return referenceIndex;
    }

    @Override
    public final boolean canDeleteTableRowAt( final int deleteIndex ) {
        final int minimumDeleteIndex = ROW_DEFAULT_LAYER + 1;
        final int maximumDeleteIndex = getLastRowIndex();
        final int minimumLastRowIndex = ROW_DEFAULT_LAYER;
        return canDeleteTableRowAt( deleteIndex,
                                    minimumDeleteIndex,
                                    maximumDeleteIndex,
                                    minimumLastRowIndex );
    }

    // TODO: Think about a different strategy that delegates this logic to the
    // business model, unless it is clearly tied to view parameters such as row
    // count. For instance, the business model should control the Default Layer.
    @Override
    public final boolean canDeleteTableRowAt( final int deleteIndex,
                                              final int minimumDeleteIndex,
                                              final int maximumDeleteIndex,
                                              final int minimumLastRowIndex ) {
        // If the index is out of bounds, or the table size has already reached
        // the minimum number of rows required by the table, ignore the deletion
        // request.
        if ( !super.canDeleteTableRowAt( deleteIndex,
                                         minimumDeleteIndex,
                                         maximumDeleteIndex,
                                         minimumLastRowIndex ) ) {
            return false;
        }

        // Now, make sure the Layer at the selected row isn't locked.
        final LayerProperties layerToDelete = LayerUtilities.getLayer( _layerCollection,
                                                                       deleteIndex );
        return ( layerToDelete == null ) ? false : !layerToDelete.isLayerLocked();
    }

    // TODO: Think about a different strategy that delegates this logic to the
    // business model, unless it is clearly tied to view parameters such as row
    // count. For instance, the business model should control the Default Layer.
    @Override
    public final int deleteTableRows() {
        // The deletable row range starts after the default "Layer 0".
        // NOTE: The minimum last row index of 0 is required for tables that
        // cannot be empty (whether initially or after editing), as an index of
        // -1 corresponds to a minimum row count of zero.
        final int minimumDeleteIndex = ROW_DEFAULT_LAYER + 1;
        final int minimumLastRowIndex = ROW_DEFAULT_LAYER;

        // Delete the selected table row(s).
        final int referenceIndex = deleteTableRows( minimumDeleteIndex, minimumLastRowIndex );

        return referenceIndex;
    }

    private final void initTable( final ClientProperties pClientProperties ) {
        // This is an editable table, in terms of selectivity but not typing.
        setTableEditable( true );

        // Try to force the preferred size to the total column width and eight
        // initial rows of data plus the header.
        setPrefSize( 600d, 240d );
        setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        final ObservableList< TableColumn< LayerProperties, ? > > columns = getColumns();
        final ArrayList< TableColumn< LayerProperties, ? > > tableColumnCollection =
                                                                                   new ArrayList<>( NUMBER_OF_COLUMNS );

        int columnIndex = COLUMN_LAYER_NAME;
        final TableColumn< LayerProperties, String > layerNameColumn = TableColumnFactory
                .makeTableColumnForString( _columnName[ columnIndex ],
                                           _columnWidth[ columnIndex ],
                                           _columnPropertyName[ columnIndex ],
                                           SORTABLE_DEFAULT );
        tableColumnCollection.add( layerNameColumn );

        layerNameColumn.setCellFactory( column -> {
            final List< Integer > uneditableRows = new ArrayList<>( 1 );
            uneditableRows.add( Integer.valueOf( ROW_DEFAULT_LAYER ) );

            return new LayerNameTableCell( uneditableRows, false, pClientProperties );
        } );

        columnIndex = COLUMN_COLOR;
        final TableColumn< LayerProperties, Color > layerColorColumn = TableColumnFactory
                .makeTableColumnForColor( _columnName[ columnIndex ],
                                          _columnWidth[ columnIndex ],
                                          _columnPropertyName[ columnIndex ],
                                          SORTABLE_DEFAULT );
        tableColumnCollection.add( layerColorColumn );

        // TODO: Make a factory method, or at least a constant for the tool
        // tip.
        layerColorColumn.setCellFactory( LayerColorTableCell::new );

        columnIndex = COLUMN_STATUS;
        final TableColumn< LayerProperties, Boolean > layerStatusColumn = TableColumnFactory
                .makeTableColumnForBoolean( _columnName[ columnIndex ],
                                            _columnWidth[ columnIndex ],
                                            _columnPropertyName[ columnIndex ],
                                            SORTABLE_DEFAULT );
        tableColumnCollection.add( layerStatusColumn );

        layerStatusColumn.setCellFactory( column -> new LayerStatusTableCell() );

        columnIndex = COLUMN_DISPLAY;
        final TableColumn< LayerProperties, Boolean > layerDisplayColumn = TableColumnFactory
                .makeTableColumnForBoolean( _columnName[ columnIndex ],
                                            _columnWidth[ columnIndex ],
                                            _columnPropertyName[ columnIndex ],
                                            SORTABLE_DEFAULT );
        tableColumnCollection.add( layerDisplayColumn );

        layerDisplayColumn.setCellFactory( column -> new LayerDisplayTableCell() );

        columnIndex = COLUMN_LOCK;
        final TableColumn< LayerProperties, Boolean > layerLockColumn = TableColumnFactory
                .makeTableColumnForBoolean( _columnName[ columnIndex ],
                                            _columnWidth[ columnIndex ],
                                            _columnPropertyName[ columnIndex ],
                                            SORTABLE_DEFAULT );
        tableColumnCollection.add( layerLockColumn );

        layerLockColumn.setCellFactory( column -> new LayerLockTableCell() );

        columns.addAll( 0, tableColumnCollection );

        // Make sure whichever row contains the Default Layer 0 in the Layer
        // Name column, keeps that row as the first row in the table, regardless
        // of which column header was clicked as the main sorting criteria.
        sortPolicyProperty().set( tableView -> {
            final Comparator< LayerProperties > comparator = ( layer1, layer2 ) -> {
                final String layerName1 = layer1.getLayerName();
                if ( LayerUtilities.DEFAULT_LAYER_NAME.equals( layerName1 ) ) {
                    return -1;
                }

                final String layerName2 = layer2.getLayerName();
                if ( LayerUtilities.DEFAULT_LAYER_NAME.equals( layerName2 ) ) {
                    return 1;
                }

                final Comparator< LayerProperties > tableComparator = tableView.getComparator();

                // No column sorted: don't change order.
                if ( tableComparator == null ) {
                    return 0;
                }

                // Columns are sorted: sort accordingly.
                return tableComparator.compare( layer1, layer2 );
            };

            FXCollections.sort( getItems(), comparator );

            return true;
        } );
    }

    // TODO: Think about a different strategy that delegates this logic to the
    // business model, unless it is clearly tied to view parameters such as row
    // count. For instance, the business model should control the Default Layer.
    @Override
    public final int insertTableRow() {
        // Clone the selected or defaulted row when adding a new one, but do not
        // allow the Default Layer to be bumped via an insert action.
        final int minimumInsertIndex = ROW_DEFAULT_LAYER + 1;
        final int referenceIndex = insertTableRow( minimumInsertIndex );

        return referenceIndex;
    }

    // Reset all fields to the default values.
    public final void reset() {
        // TODO: Reset the Layer Properties as well.
        // TODO: Set back to empty list with just Default Layer present, and
        // forward this to the main application to sync it and reassign layers.
    }

    public final void setLayerCollection( final ObservableList< LayerProperties > pLayerCollection ) {
        // Cache a local copy of the Layer Collection, to act on directly.
        _layerCollection = pLayerCollection;

        // Replace the table's entire item list with the new collection.
        updateLayerCollection();
    }

    public final void updateLayerCollection() {
        // Update the table's entire item list from the cached collection.
        setItems( _layerCollection );
    }

}
