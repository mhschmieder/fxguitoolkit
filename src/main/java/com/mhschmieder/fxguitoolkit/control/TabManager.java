/**
 * MIT License
 *
 * Copyright (c) 2024, 2025 Mark Schmieder
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

import java.util.List;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabManager {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private TabManager() {}

    public static void showTab( final TabPane tabPane,
                                final Tab tab,
                                final boolean toggleVisibility ) {
        final List< Tab > tabs = tabPane.getTabs();
        if ( tabs == null ) {
            return;
        }

        if ( toggleVisibility ) {
            toggleTabVisibility( tabs, tab );
        }
        else {
            if ( !tabs.contains( tab ) ) {
                tabs.add( tab );
            }
            tabPane.getSelectionModel().select( tab );
        }
    }

    public static void toggleTabVisibility( final List< Tab > tabs,
                                            final Tab tab ) {
        if ( tabs.contains( tab ) ) {
            tabs.remove( tab );
        }
        else {
            tabs.add( tab );
        }
    }

    public static void clearTabs( final TabPane tabPane ) {
        final List< Tab > tabs = tabPane.getTabs();
        if ( tabs != null ) {
            tabs.clear();
        }
    }
}
