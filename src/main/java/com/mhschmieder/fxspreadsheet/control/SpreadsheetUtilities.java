/*
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
 * This file is part of the FxSpreadsheet Library
 *
 * You should have received a copy of the MIT License along with the
 * FxSpreadsheet Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxspreadsheet
 */
package com.mhschmieder.fxspreadsheet.control;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public final class SpreadsheetUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SpreadsheetUtilities() {}

    // This is a utility method to safely retrieve displayed text from a
    // spreadsheet cell, defaulting to an empty string to avoid interruption of
    // control flow if there is an issue with the index or the reference.
    public static String getDisplayedText(
            final ObservableList< SpreadsheetCell > referenceRow,
            final int columnIndex ) {
        String displayedText = "";

        try {
            final SpreadsheetCell cell = referenceRow.get( columnIndex );
            if ( cell != null ) {
                displayedText = cell.getText();
            }
        }
        catch ( final IndexOutOfBoundsException ioobe ) {
            ioobe.printStackTrace();
        }

        return displayedText;
    }

    // This is a utility method to query a spreadsheet cell's boolean value
    // when a Toggle Button is being used to represent, edit, and render the
    // value (via the auxiliary Graphic Node).
    // TODO: Find a way to query boolean toggle values without casting to a
    //  ToggleButton (a bit hacky), and make it into a standard method like
    //  other property getters.
    public static boolean isToggleButtonSelected(
            final ObservableList< SpreadsheetCell > referenceRow,
            final int columnIndex,
            final boolean defaultSelected ) {
        // Fetch the auxiliary Graphic Node for this Spreadsheet Cell.
        final Node auxiliaryNode = referenceRow.get( columnIndex ).getGraphic();

        // Avoid problems by defaulting selected status if not a Toggle Button.
        if ( !( auxiliaryNode instanceof ToggleButton ) ) {
            return defaultSelected;
        }

        // Cast the current cell's auxiliary graphic to a ToggleButton.
        final ToggleButton toggleButton = ( ToggleButton ) auxiliaryNode;

        // At this point, we are dealing with simple selected/unselected
        // criteria to represent the boolean flag's consumable value.
        final boolean toggleButtonSelected = toggleButton.isSelected();

        return toggleButtonSelected;
    }
}
