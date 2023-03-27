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

import java.util.List;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;

import javafx.collections.ObservableList;

/**
 * Special editor to handle specifics of Layer Name editing restrictions.
 */
public final class LayerNameTableCell extends LabelEditorTableCell< LayerProperties, String > {

    public LayerNameTableCell( final boolean pBlankTextAllowed,
                               final ClientProperties pClientProperties ) {
        this( null, pBlankTextAllowed, pClientProperties );
    }

    public LayerNameTableCell( final List< Integer > pUneditableRows,
                               final boolean pBlankTextAllowed,
                               final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pBlankTextAllowed, pClientProperties );
    }

    @Override
    protected void setBeanProperty( final LayerProperties selectedRecord ) {
        // Get the current displayed value of the Text Editor.
        // NOTE: We now get the adjusted bean value instead, or it gets lost.
        final String newLayerName = getValue(); // getEditorValue();

        // Enforce the Unique Layer Name Policy.
        final ObservableList< LayerProperties > layerCollection = getTableView().getItems();
        final String oldLayerName = selectedRecord.getLayerName();
        final int currentLayerIndex = LayerUtilities.getLayerIndex( layerCollection, oldLayerName );
        final String activeLayerName = LayerUtilities.getActiveLayerName( layerCollection );
        LayerUtilities.uniquefyLayerName( newLayerName,
                                          _uniquefierNumberFormat,
                                          layerCollection,
                                          currentLayerIndex,
                                          activeLayerName );
    }

}
