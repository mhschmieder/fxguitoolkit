/**
 * MIT License
 *
 * Copyright (c) 2023 Mark Schmieder
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

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/**
 * Defines a set of methods used for validating table cells, primarily as an
 * interceptor before edited values cache to the observable properties in the
 * table's backing store. In typical spreadsheets, one cell affects another,
 * so this often means other cells must be recalculated based on a cell edit.
 * <p>
 * NOTE: This acts mostly on the table's backing store list, but implementing
 *  classes may query the table cell's controller and replace the cached value.
 * <p>
 * TODO: Review the terminology and documentation to be clearer, as this was a
 *  somewhat refactoring of older in-line code that was copy/paste per class.
 * 
 * @param <RT> The object type that will be targeted for validation
 */
public interface CellValidator< RT > {

    /**
     * Validates the cell associated with the observable property that
     * the implementing class represents, using the selected row.
     * 
     * @param tableView The table that owns the selected row
     * @param tableRow The selected row that contains the cell
     */
    default void validateCellValue( final TableView< RT > tableView,
                                    final TableRow< RT > tableRow ) {
        // Save edits from the table cell's controller to the property bean.
        if ( tableRow != null ) {
            final int selectedIndex = tableRow.getIndex();
            final ObservableList< RT > items = tableView.getItems();
            if ( !items.isEmpty() && ( selectedIndex >= 0 ) 
                    && ( selectedIndex < items.size() ) ) {
                final RT selectedRecord = items.get( selectedIndex );
                setBeanProperty( selectedRecord );
            }
        }
    }

    /**
     * Sets the observable property associated with the table cell that
     * implements this method, for the selected row, usually validating the
     * edited value and/or using it to recalculate cells in other columns.
     * <p>
     * NOTE: This method requires knowledge of which bean properties are in
     *  use, so cannot have a default implementation and thus all implementing
     *  classes must override it in order to achieve proper data binding.
     * 
     * @param selectedRecord The record corresponding to the current row in
     *                       the table; more specifically, a list item
     */
    void setBeanProperty( final RT selectedRecord );
}
