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

public final class LayerDisplayTableCell extends ToggleButtonTableCell< LayerProperties, Boolean > {

    public LayerDisplayTableCell() {
        // Always call the superclass constructor first!
        super( "Visible", //$NON-NLS-1$
               "Hidden", //$NON-NLS-1$
               ColorConstants.VISIBLE_BACKGROUND_COLOR,
               ColorConstants.HIDDEN_BACKGROUND_COLOR,
               ColorConstants.VISIBLE_FOREGROUND_COLOR,
               ColorConstants.HIDDEN_FOREGROUND_COLOR,
               "Click to Toggle Layer Display Between Visible and Hidden" ); //$NON-NLS-1$
    }

    @Override
    protected void setBeanProperty( final LayerProperties selectedRecord ) {
        // Enforce the Hidden Layer Policy.
        final ObservableList< LayerProperties > layerCollection = getTableView().getItems();
        final String layerName = selectedRecord.getLayerName();
        LayerUtilities.enforceHiddenLayerPolicy( layerCollection,
                                                 layerName,
                                                 !selectedRecord.isLayerVisible() );
    }

}// class LayerDisplayTableCell
