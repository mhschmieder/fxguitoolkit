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
package com.mhschmieder.fxguitoolkit.control.cell;

import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;

import javafx.collections.ObservableList;

public class LayerStatusTableCell extends ToggleButtonTableCell< LayerProperties, Boolean > {

    public LayerStatusTableCell() {
        // Always call the superclass constructor first!
        super( "Active", //$NON-NLS-1$
               "Inactive", //$NON-NLS-1$
               ColorConstants.ACTIVE_BACKGROUND_COLOR,
               ColorConstants.INACTIVE_BACKGROUND_COLOR,
               ColorConstants.ACTIVE_FOREGROUND_COLOR,
               ColorConstants.INACTIVE_FOREGROUND_COLOR,
               "Click to Toggle Layer Status Between Active and Inactive" ); //$NON-NLS-1$
    }

    @Override
    protected void setBeanProperty( final LayerProperties selectedRecord ) {
        // NOTE: This is only modeled as a toggle so we can show two distinct
        // states differently, but there must be better ways to do that. As it
        // is, we need to at least avoid possible recursion caused by
        // redundantly setting an already-set value, as well as unsetting a
        // value, as clicking this button should never toggle the state and
        // should only set it if currently unset.
        // NOTE: Due to programmatic changes, the toggle button's selected
        // state can't be trusted, so we instead use the bean property as the
        // reference for the toggle action.
        final boolean layerActive = selectedRecord.isLayerActive();
        if ( layerActive ) {
            return;
        }

        // Enforce the Active Layer Policy.
        final ObservableList< LayerProperties > layerCollection = getTableView().getItems();
        final String layerName = selectedRecord.getLayerName();
        LayerUtilities.enforceActiveLayerPolicy( layerCollection, layerName, false );
    }

}// class LayerStatusTableCell
