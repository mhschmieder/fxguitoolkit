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

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import com.mhschmieder.commonstoolkit.lang.Labeled;
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
     * Returns a Combo Box that hosts labels from an object or enum that 
     * implements the {@link Labeled} interface. It doesn't have to be
     * enum-based but that is the most common and likely client, as the 
     * interface helps to partially get around enums being final classes.
     * <p>
     * The goal is to allow for a combo box that doesn't need overrides of the
     * control itself for string conversions of enum values. 
     * <p>
     * This approach allows for one factory method that covers all enums that
     * implement the interface without verbose, repetitive boilerplate code
     * and/or copy/paste of otherwise identical code between enum/object types.
     * 
     * @param <T> the object or enum that provides the Combo Box's choices
     * @param pClientProperties client properties for OS, Locale, etc.
     * @param supportedValues a list of supported values for the List View
     * @param tooltipText the tooltip to show when hovering on the Combo Box
     * @param defaultValue initial default value to set, to avoid nulls
     * @return a Combo Box that hosts a curated list of labels from an enum 
     */
    public static < T extends Labeled< ? > > XComboBox< T > makeLabeledSelector(
            final ClientProperties pClientProperties,
            final T[] supportedValues,
            final String tooltipText,
            final T defaultValue ) {
        // Make the callback for the ListCells to grab the custom label.
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

                        final Labeled currentLabelAssignable
                                = ( ( item == null ) || empty )
                                  ? getItem()
                                  : item;
                        if ( currentLabelAssignable != null ) {
                            // Remove leading whitespace when using in a 
                            // drop-list, as some labels may be designed to
                            // serve as measurement units that follow a
                            // numeric value, and many of those use a space.
                            setText( currentLabelAssignable.label().trim() );
                        }
                    }
                };
            } };

        // Make the Combo Box using the supported Labeled values as objects.
        final XComboBox< T > selector = new XComboBox<>(
            pClientProperties,
            tooltipText,
            true,
            false,
            false,
            supportedValues );
        
        // Initialize the Combo Box behavior using the custom Cell Factory.
        initSelectorBehavior( selector, cellFactory, defaultValue );

        return selector;
    }

    /**
     * Returns a Combo Box that hosts labels from the Month enum.
     * <p>
     * The goal is to allow for a combo box that doesn't need overrides of the
     * control itself for string conversions of Month values. 
     * <p>
     * This method is almost identical to the one above, but as Month doesn't
     * implement Labeled, it needs its own implementation method.
     * 
     * @param pClientProperties client properties for OS, Locale, etc.
     * @param supportedValues a list of supported values for the List View
     * @param tooltipText the tooltip to show when hovering on the Combo Box
     * @param defaultValue initial default value to set, to avoid nulls
     * @return a Combo Box that hosts a curated list of months from the enum 
     */
    public static XComboBox< Month > makeMonthSelector(
            final ClientProperties pClientProperties,
            final Month[] supportedValues,
            final String tooltipText,
            final Month defaultValue ) {
        // Make the callback for the ListCells to grab the custom enum label.
        final Callback< ListView< Month >, ListCell< Month > > cellFactory 
            = new Callback< ListView< Month >, ListCell< Month > >() {
            @Override
            public ListCell< Month > call(final ListView< Month > p ) {
                return new ListCell< Month >() {
                    {
                        setContentDisplay( ContentDisplay.TEXT_ONLY );
                    }

                    @Override
                    protected void updateItem( final Month item, boolean empty ) {
                        super.updateItem( item, empty );

                        final Month currentMonth
                                = ( ( item == null ) || empty )
                                  ? getItem()
                                  : item;
                        if ( currentMonth != null ) {
                            // TODO: Allow custom Locale and TextStyle?.
                            setText( currentMonth.getDisplayName( 
                                TextStyle.FULL, Locale.US ) );
                        }
                    }
                };
            } };

        // Make the Combo Box using the supported Month values as objects.
        final XComboBox< Month > selector = new XComboBox<>(
            pClientProperties,
            tooltipText,
            true,
            false,
            false,
            supportedValues );
        
        // Initialize the Combo Box behavior using the custom Cell Factory.
        initSelectorBehavior( selector, cellFactory, defaultValue );

        return selector;
    }

    /**
     * Initializes a Combo Box's behavior using a pre-made cell factory.
     * <p>
     * This method slightly diminishes the copy/paste repetition of the two
     * methods above, but it was a challenge to find correct syntax for also
     * having this method make the Combo Box, so that is not done yet.
     * 
     * @param selector the Combo Box that displays curated values
     * @param cellFactory the Cell Factory that handles the displayed values
     * @param defaultValue the initial default value to set, to avoid nulls
     */
    public static < T > void initSelectorBehavior(
            final XComboBox< T > selector,
            final Callback< ListView< T >, ListCell< T > > cellFactory,
            final T defaultValue ) {
        // Set the custom cell view on the displayed value field.
        selector.setButtonCell( cellFactory.call( null ) );

        // Set the custom cell view on the Combo Box's drop list.
        selector.setCellFactory( cellFactory );

        // Set the default value to avoid null pointers prior to user input.
        selector.getSelectionModel().select( defaultValue );
    }
}
