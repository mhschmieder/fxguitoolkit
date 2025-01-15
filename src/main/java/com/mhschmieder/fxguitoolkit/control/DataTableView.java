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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mhschmieder.commonstoolkit.util.GlobalUtilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * The Data Table View is designed as as read-only table viewer for tabular data
 * that is pulled in from outside via CSV or related file formats that contain a
 * single table of text-based data, likely of mixed data types and hence modeled
 * as String data for each column/cell rather than attempting to intuit units etc.
 * <p>
 * NOTE: The generic type is an Observable List of Strings as that is what the
 *  Table View's underlying model expects so that it can sync the data to the view.
 */
public class DataTableView extends XTableView< ObservableList< String > > {

    public DataTableView() {
        // Always call the superclass constructor first!
        super();

        // Make an initially empty table that gets filled later by CSV file data.
        initTable();
    }

    private void initTable() {
        // This is a display-only table.
        setTableEditable( false );

        // Try to force the preferred size to the total column width and
        // multiple rows of data plus the header.
        // NOTE: Once we pass in or set a size, leave room for scroll bars.
        // setPrefSize( 760, 340 );
        setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );     
    }

    public void updateTableView( 
            final Collection< Collection< String > > dataRows ) {
        // Pad the vector of string vectors to the maximum column count.
        final int maxColumn = GlobalUtilities.padStringsToMaxColumn( dataRows );

        // Create names for the default column headers.
        final List< String > headers = new ArrayList<>( maxColumn );
        for ( int i = 0; i < maxColumn; i++ ) {
            headers.add( Integer.toString( i + 1 ) );
        }

        // Clear any existing table column headers to prepare for new ones.
        final ObservableList< TableColumn< ObservableList< String >, ? > > 
                columns = getColumns();
        columns.clear();

        // Convert to a TableView that has no property names due to being
        // non-editable, and has default initial cell width as each column will
        // be an indeterminate size.
        final ArrayList< TableColumn< ObservableList< String >, String > > 
                tableColumnCollection = new ArrayList<>( maxColumn );
        for ( int i = 0; i < maxColumn; i++ ) {
            // NOTE: We appear to have to use an approach more akin to an SQL
            //  example that I saw due to the data being random and not having
            //  property names.
            // final String columnPropertyName = null; // "";
            // final TableColumn< ObservableList< String >, String > tableColumn
            // = getTableColumnForString( headers.get( i ),
            // 50,
            // columnPropertyName,
            // false,
            // clientProperties );

            final TableColumn< ObservableList< String >, String > 
                    tableColumn = new TableColumn<>( headers.get( i ) );
            tableColumn.setMinWidth( 50 );
            TableUtilities.setTableColumnHeaderProperties( tableColumn );

            // NOTE: Do not allow column reordering or row-sorting as this
            //  destroys the ability to understand how things relate to one
            //  another (that is, the relationships of the data).
            tableColumn.setSortable( false );

            // We are using non property style for making a dynamic table.
            final int j = i;
            final Callback< CellDataFeatures< ObservableList< String >, String >, 
                    ObservableValue< String > > callback = param -> 
                            new SimpleStringProperty( 
                                    param.getValue().get( j ).toString() );
            tableColumn.setCellValueFactory( callback );

            TableUtilities.setCellAlignment( tableColumn );

            tableColumnCollection.add( tableColumn );
        }
        columns.addAll( 0, tableColumnCollection );

        // Clear any existing table rows to prepare for new data.
        final ObservableList< ObservableList< String > > data = getItems();
        data.clear();

        // Replace the current Table View. This will cause automatic updates so
        // should refresh the view on the screen. Iterate by Row, then Column.
        dataRows.forEach( dataRow -> {
            final ObservableList< String > row = FXCollections.observableArrayList();
            dataRow.forEach( column -> row.add( column ) );
            data.add( row );
        } );

        // Look at the revised list in the debugger to see why the display is
        // blank currently even though the data and column headers are there.
        // NOTE: This proves the data is there, so it must be an issue with
        //  cell background or the details of the cell factory class used.
        // TODO: Compare to what we do for other read-only string-based cells.
        final ObservableList< ObservableList< String > > data2 = getItems();
        final int numberOfRows = data2.size();
        if ( numberOfRows < 1 ) {
            return;
        }
    }
}
