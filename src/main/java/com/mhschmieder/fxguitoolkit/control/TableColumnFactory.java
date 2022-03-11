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

import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

/**
 * {@code TableColumnFactory} is a static factory class for making Table Columns
 * with specific parameterization, data types, and initialization behavior plus
 * visual layout characteristics.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class TableColumnFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private TableColumnFactory() {}

    /**
     * Returns a {@link TableColumn} for non-specified {@link Object} types; the
     * highest abstraction possible before type-specific behavior is required.
     *
     * @param <TD>
     *            The data type for the table
     * @param columnName
     *            The string to show when the TableColumn is placed within the
     *            TableView
     * @param columnWidth
     *            The minimum width the table column is permitted to be resized
     *            to
     * @param columnPropertyName
     *            The name of the table property that this column manages
     * @param sortable
     *            If {@code true}, this columns can be used to sort the rows
     * @return A {@link TableColumn} for non-specified {@link Object} types
     *
     * @version 1.0
     */
    public static < TD > TableColumn< TD, Object > makeTableColumnForObject( final String columnName,
                                                                             final double columnWidth,
                                                                             final String columnPropertyName,
                                                                             final boolean sortable ) {
        final TableColumn< TD, Object > tableColumn = new TableColumn<>( columnName );
        tableColumn.setMinWidth( columnWidth );
        TableUtilities.setTableColumnHeaderProperties( tableColumn );

        // Conditionally prevent row-sorting of this column as it may destroy
        // the ability to understand how things relate to one another.
        tableColumn.setSortable( sortable );

        TableUtilities.setCellValueFactory( tableColumn, columnPropertyName );

        return tableColumn;
    }

    /**
     * Returns a {@link TableColumn} for managing {@link String} values.
     *
     * @param <TD>
     *            The data type for the table
     * @param columnName
     *            The string to show when the TableColumn is placed within the
     *            TableView
     * @param columnWidth
     *            The minimum width the table column is permitted to be resized
     *            to
     * @param columnPropertyName
     *            The name of the table property that this column manages
     * @param sortable
     *            If {@code true}, this columns can be used to sort the rows
     * @return A {@link TableColumn} for managing {@link String} values
     *
     * @version 1.0
     */
    public static < TD > TableColumn< TD, String > makeTableColumnForString( final String columnName,
                                                                             final double columnWidth,
                                                                             final String columnPropertyName,
                                                                             final boolean sortable ) {
        final TableColumn< TD, String > tableColumn = new TableColumn<>( columnName );
        tableColumn.setMinWidth( columnWidth );
        TableUtilities.setTableColumnHeaderProperties( tableColumn );

        // Conditionally prevent row-sorting of this column as it may destroy
        // the ability to understand how things relate to one another.
        tableColumn.setSortable( sortable );

        TableUtilities.setCellValueFactory( tableColumn, columnPropertyName );

        return tableColumn;
    }

    /**
     * Returns a {@link TableColumn} for managing {@link Number} values.
     *
     * @param <TD>
     *            The data type for the table
     * @param columnName
     *            The string to show when the TableColumn is placed within the
     *            TableView
     * @param columnWidth
     *            The minimum width the table column is permitted to be resized
     *            to
     * @param columnPropertyName
     *            The name of the table property that this column manages
     * @param sortable
     *            If {@code true}, this columns can be used to sort the rows
     * @return A {@link TableColumn} for managing {@link Number} values
     *
     * @version 1.0
     */
    public static < TD > TableColumn< TD, Number > makeTableColumnForNumber( final String columnName,
                                                                             final double columnWidth,
                                                                             final String columnPropertyName,
                                                                             final boolean sortable ) {
        final TableColumn< TD, Number > tableColumn = new TableColumn<>( columnName );
        tableColumn.setMinWidth( columnWidth );
        TableUtilities.setTableColumnHeaderProperties( tableColumn );

        // Conditionally prevent row-sorting of this column as it may destroy
        // the ability to understand how things relate to one another.
        tableColumn.setSortable( sortable );

        TableUtilities.setCellValueFactory( tableColumn, columnPropertyName );

        return tableColumn;
    }

    /**
     * Returns a {@link TableColumn} for managing {@link Boolean} values.
     *
     * @param <TD>
     *            The data type for the table
     * @param columnName
     *            The string to show when the TableColumn is placed within the
     *            TableView
     * @param columnWidth
     *            The minimum width the table column is permitted to be resized
     *            to
     * @param columnPropertyName
     *            The name of the table property that this column manages
     * @param sortable
     *            If {@code true}, this columns can be used to sort the rows
     * @return A {@link TableColumn} for managing {@link Boolean} values
     *
     * @version 1.0
     */
    public static < TD > TableColumn< TD, Boolean > makeTableColumnForBoolean( final String columnName,
                                                                               final double columnWidth,
                                                                               final String columnPropertyName,
                                                                               final boolean sortable ) {
        final TableColumn< TD, Boolean > tableColumn = new TableColumn<>( columnName );
        tableColumn.setMinWidth( columnWidth );
        TableUtilities.setTableColumnHeaderProperties( tableColumn );

        // Conditionally prevent row-sorting of this column as it may destroy
        // the ability to understand how things relate to one another.
        tableColumn.setSortable( sortable );

        TableUtilities.setCellValueFactory( tableColumn, columnPropertyName );

        return tableColumn;
    }

    /**
     * Returns a {@link TableColumn} for managing {@link Color} values.
     *
     * @param <TD>
     *            The data type for the table
     * @param columnName
     *            The string to show when the TableColumn is placed within the
     *            TableView
     * @param columnWidth
     *            The minimum width the table column is permitted to be resized
     *            to
     * @param columnPropertyName
     *            The name of the table property that this column manages
     * @param sortable
     *            If {@code true}, this columns can be used to sort the rows
     * @return A {@link TableColumn} for managing {@link Color} values
     *
     * @version 1.0
     */
    public static < TD > TableColumn< TD, Color > makeTableColumnForColor( final String columnName,
                                                                           final double columnWidth,
                                                                           final String columnPropertyName,
                                                                           final boolean sortable ) {
        final TableColumn< TD, Color > tableColumn = new TableColumn<>( columnName );
        tableColumn.setMinWidth( columnWidth );
        TableUtilities.setTableColumnHeaderProperties( tableColumn );

        // Conditionally prevent row-sorting of this column as it may destroy
        // the ability to understand how things relate to one another.
        tableColumn.setSortable( sortable );

        TableUtilities.setCellValueFactory( tableColumn, columnPropertyName );

        return tableColumn;
    }

}
