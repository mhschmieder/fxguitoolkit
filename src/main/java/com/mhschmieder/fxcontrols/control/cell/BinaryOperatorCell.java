/*
 * MIT License
 *
 * Copyright (c) 2025, Mark Schmieder. All rights reserved.
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
 * This file is part of the fxcontrols Library
 *
 * You should have received a copy of the MIT License along with the fxcontrols
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcontrols
 */
package com.mhschmieder.fxcontrols.control.cell;

import com.mhschmieder.fxcontrols.control.ListViewUtilities;
import com.mhschmieder.fxcontrols.control.XComboBox;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jmath.logic.BinaryConditionalOperator;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

public class BinaryOperatorCell< T >
        extends XTableCell< T, BinaryConditionalOperator> {

    // TODO: Move beyond Java 8 so that we can use the modern Logger API.
    /*
    private static final Logger LOGGER
            = System.getLogger( BinaryOperatorCell.class.getName() );
    */

    private XComboBox< BinaryConditionalOperator > comboBox;

    public BinaryOperatorCell(
            final TableColumn< T, BinaryConditionalOperator > column,
            final String tooltipText,
            final ClientProperties clientProperties ) {
        super();

        try {
            initTableCell(
                    column,
                    tooltipText,
                    clientProperties);
        }
        catch ( final Exception e ) {
            // TODO: Move beyond Java 8 so that we can use the modern Logger API.
            /*
            LOGGER.log( Level.ERROR, e.getMessage(), e );
            */
            e.printStackTrace();
        }
    }

    private void initTableCell(
            final TableColumn< T, BinaryConditionalOperator > column,
            final String tooltipText,
            final ClientProperties clientProperties ) {
        comboBox = ListViewUtilities.makeLabeledSelector(
                clientProperties,
                BinaryConditionalOperator.values(),
                tooltipText,
                BinaryConditionalOperator.defaultValue() );

        comboBox.setEditable( false );
        comboBox.setOnShowing( event -> {
            final TableView< T > tableView = getTableView();
            final TableView.TableViewSelectionModel< T >
                    selectionModel = tableView.getSelectionModel();
            final int selectedIndex = getTableRow().getIndex();
            selectionModel.select( selectedIndex );
            final int selectedIndexCorrected = selectionModel.getSelectedIndex();
            tableView.edit( selectedIndexCorrected, column );
        } );

        // NOTE: We are including a string converter due to errors occurring
        //  during runtime that are thrown when setting the new value during
        //  edit commits. This syntax must change once moving beyond Java 8.
        comboBox.setConverter(
                new StringConverter< BinaryConditionalOperator >() {
            @Override
            public String toString( final BinaryConditionalOperator operator ) {
                return operator == null ? "" : operator.label();
            }
            @Override
            public BinaryConditionalOperator fromString(String string) {
                // Find the enum whose label matches.
                for ( final BinaryConditionalOperator operator
                        : BinaryConditionalOperator.values() ) {
                    if ( operator.label().equals( string ) ) {
                        return operator;
                    }
                }
                return BinaryConditionalOperator.defaultValue();
            }
        } );

        // TODO: Review the saveEdits() code and when/why it may have been
        //  necessary, as it threw errors, and taking it out doesn't cause the
        //  enum to fail to find its label() representation due to toString()
        //  now being overridden to call label() on BinaryConditionalOperator.
        comboBox.setOnAction( event -> saveEdits() );
		/*
		comboBox.setButtonCell( new ListCell<>() {
			@Override
			protected void updateItem( final BinaryConditionalOperator item,
			                           final boolean empty ) {
				super.updateItem( item, empty );

				if ( !empty ) {
					setText( item.label() );
				}
			}
		} );
		*/

        comboBox.setMaxWidth( Double.MAX_VALUE );

        comboBox.minWidthProperty().bind( widthProperty().subtract(
                8.0d ) );
        comboBox.prefWidthProperty().bind( widthProperty() );

        comboBox.editableProperty().bind( column.editableProperty() );
        comboBox.disableProperty().bind( column.editableProperty().not() );

        setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
    }

    private void saveEdits() {
        setValue( comboBox.getValue() );
    }

    @Override
    public void updateItem( final BinaryConditionalOperator item,
                            final boolean empty ) {
        // Make sure the table cell knows the current state.
        super.updateItem( item, empty );

        // NOTE: We avoid displaying anything in empty rows.
        if ( empty ) {
            setText( null );
            setGraphic( null );
        }
        else {
            comboBox.setValue( item );
            setText( null );
            setGraphic( comboBox );
        }
    }
}
