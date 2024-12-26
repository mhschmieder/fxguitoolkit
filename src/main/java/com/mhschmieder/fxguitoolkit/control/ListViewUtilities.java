/**
 * MIT License
 *
 * Copyright (c) 2023, 2025 Mark Schmieder
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

import com.mhschmieder.commonstoolkit.lang.LabelAssignable;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ListViewUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ListViewUtilities() {}

    /**
     * Returns a Combo Box that hosts labels from an enum that implements the
     * {@link LabelAssignable} interface. It doesn't have to be enum-based but
     * that is the most common and likely client, as the interface helps to
     * partially get around enums being final classes in Java.
     * <p>
     * The goal is to allow for a combo box that doesn't need overrides of the
     * control itself for string conversions of enum values. 
     * <p>
     * This approach allows for one factory method that covers all enums that
     * implement the interface, with no verbosity of repetitive boilerplate
     * code and copy paste of otherwise identical code between enum types.
     * 
     * @param <T> the object or enum that provides the Combo Box's choices
     * @param pClientProperties client properties for OS, Locale, etc.
     * @param supportedValues a list of supported values for the List View
     * @param tooltipText the tooltip to show when hovering on the Combo Box
     * @param defaultValue initial default value to set, to avoid nulls
     * @return a Combo Box that hosts a curated list of labels from an enum 
     */
    public static < T extends LabelAssignable< ? > > XComboBox< T > makeEnumSelector(
            final ClientProperties pClientProperties,
            final T[] supportedValues,
            final String tooltipText,
            final T defaultValue ) {
        // Make the callback for the ListCells to grab the custom enum label.
        final Callback< ListView< T >, ListCell< T > > cellFactory 
            = new Callback< ListView< T >, ListCell< T > >() {
            @Override
            public ListCell< T > call(final ListView< T > p ) {
                return new ListCell< T >() {
                    {
                        setContentDisplay( ContentDisplay.TEXT_ONLY );
                    }

                    @Override
                    protected void updateItem( final T item, boolean empty ) {
                        super.updateItem( item, empty );

                        final LabelAssignable currentLabelAssignable
                                = ( ( item == null ) || empty )
                                  ? getItem()
                                  : item;
                        if ( currentLabelAssignable != null ) {
                            setText( currentLabelAssignable.label() );
                        }
                    }
                };
            } };

        // Make the combo box using the supported enum values as objects.
        final XComboBox< T > selector = new XComboBox<>(
            pClientProperties,
            tooltipText,
            true,
            false,
            false,
            supportedValues );

        // Set the custom cell view on the displayed value field.
        selector.setButtonCell( cellFactory.call( null ) );

        // Set the custom cell view on the Combo Box's drop list.
        selector.setCellFactory( cellFactory );

        selector.getSelectionModel().select( defaultValue );

        return selector;
    }
}
