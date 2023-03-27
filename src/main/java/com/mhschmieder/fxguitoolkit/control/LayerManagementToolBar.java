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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.action.LayerManagementActions;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class LayerManagementToolBar extends ToolBar {

    // Declare all of the Tool Bar Nodes.
    public PredictButtons     _predictButtons;
    public LayerActionButtons _layerActionButtons;

    // Default constructor
    public LayerManagementToolBar( final ClientProperties pClientProperties,
                                   final LayerManagementActions layerManagementActions ) {
        // Always call the superclass constructor first!
        super();

        try {
            initToolBar( pClientProperties, layerManagementActions );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initToolBar( final ClientProperties pClientProperties,
                              final LayerManagementActions layerManagementActions ) {
        // Make the Nodes for the Tool Bar.
        _predictButtons =
                        new PredictButtons( pClientProperties, layerManagementActions._toolsActions );
        _layerActionButtons = new LayerActionButtons();

        // Add some spacers to separate logical groupings.
        // NOTE: We force the Layer Control Buttons to right-justify, and to
        // stay right-justified if the window width changes.
        final Region spacer = new Region();
        HBox.setHgrow( spacer, Priority.ALWAYS );

        // Add all the Nodes to the Tool Bar.
        final ObservableList< Node > nodes = getItems();
        nodes.addAll( _predictButtons._predictButton,
                      _predictButtons._clearButton,
                      spacer,
                      _layerActionButtons._createButton,
                      _layerActionButtons._deleteButton );
    }

    public void setLayerDeleteEnabled( final boolean layerDeleteEnabled ) {
        // Forward this method to the Layer Control Button Group.
        _layerActionButtons.setLayerDeleteEnabled( layerDeleteEnabled );
    }

}
