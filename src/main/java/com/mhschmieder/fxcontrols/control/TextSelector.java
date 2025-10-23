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
package com.mhschmieder.fxcontrols.control;

import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class formalizes aspects of list selection that are specific to
 * text value sets.
 */
public class TextSelector extends XComboBox< String > {

    // This is the constructor to use when we don't know the initial drop-list
    // right away, or when it is too awkward to construct or reference inside a
    // constructor hierarchy (super() or this() has to be the first call).
    public TextSelector( final ClientProperties pClientProperties,
                         final String tooltipText,
                         final boolean applyToolkitCss,
                         final boolean editable,
                         final boolean searchable,
                         final int visibleRowCount ) {
        this( pClientProperties, 
               tooltipText, 
               applyToolkitCss, 
               editable, 
               searchable,
               visibleRowCount,
               null,
               FXCollections.observableArrayList() );
    }

    // This is the constructor to call when we know the initial drop-list.
    // For this version, we expect a static array of list values.
    public TextSelector( final ClientProperties pClientProperties,
                         final String tooltipText,
                         final boolean applyToolkitCss,
                         final boolean editable,
                         final boolean searchable,
                         final int visibleRowCount,
                         final String defaultValue,
                         final String[] textValues ) {
        // By default, make sure the list displays all items without scrolling.
        this( pClientProperties,
              tooltipText,
              applyToolkitCss,
              editable,
              searchable,
              visibleRowCount,
              defaultValue,
              ( textValues != null ) && ( textValues.length > 0 )
                  ? FXCollections.observableArrayList( textValues )
                  : FXCollections.observableArrayList() );
    }

    // This is the constructor to call when we know the initial drop-list.
    // For this version, we expect an already-constructed observable list.
    public TextSelector( final ClientProperties pClientProperties,
                         final String tooltipText,
                         final boolean applyToolkitCss,
                         final boolean editable,
                         final boolean searchable,
                         final int visibleRowCount,
                         final String defaultValue,
                         final ObservableList< String > valuesList ) {
        // Always call the superclass constructor first!
        super( pClientProperties, 
               tooltipText, 
               applyToolkitCss, 
               editable, 
               searchable,
               visibleRowCount,
               defaultValue,
               valuesList );
    }

    public final String getTextValue() {
        return getValue();
    }

    public final boolean isValueAllowed( final String textValue ) {
        final ObservableList< String > textValues = getItems();
        final boolean valueAllowed = ( textValue != null )
                && ( isEditable() || textValues.contains( textValue ) );
        return valueAllowed;
    }

    public final void setTextValue( final String textValue ) {
        // Try to avoid exceptions by having "item not found" be a no-op.
        final boolean valueAllowed = isValueAllowed( textValue );
        if ( valueAllowed ) {
            setValue( textValue );
        }
    }

}
