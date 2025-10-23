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
package com.mhschmieder.fxcontrols.control;

import com.mhschmieder.fxcontrols.action.ActionFactory;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.GlobalUtilities;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;

import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Common utilities for working with menus, such as setting accelerators.
 */
public final class MenuUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private MenuUtilities() {}

    // If an accelerator is assigned, set it by platform.
    public static void setMenuItemAccelerator( final ClientProperties clientProperties,
                                               final MenuItem menuItem,
                                               final String menuName,
                                               final String itemName,
                                               final String bundleName ) {
        // Fail-safe check to avoid unnecessary null pointer exceptions.
        if ( menuItem == null ) {
            return;
        }

        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, false );

        // If an accelerator is assigned, get it from a resource bundle.
        final KeyCombination acceleratorKeyCombination = ActionFactory
                .makeAcceleratorKeyCombination( clientProperties,
                                                menuName,
                                                itemName,
                                                resourceBundle );
        if ( acceleratorKeyCombination != null ) {
            menuItem.setAccelerator( acceleratorKeyCombination );
        }
    }

    public static void setToggleGroup( final Collection< MenuItem > menuItems ) {
        // Declare a Toggle Group to hold mutually exclusive menu choices, and
        // verify that they are modeled as Radio Menu Items.
        final ToggleGroup toggleGroup = new ToggleGroup();
        for ( final MenuItem menuItem : menuItems ) {
            if ( menuItem instanceof RadioMenuItem ) {
                ( ( RadioMenuItem ) menuItem ).setToggleGroup( toggleGroup );
            }
        }
    }
}
