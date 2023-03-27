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
package com.mhschmieder.fxguitoolkit.layout;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;
import com.mhschmieder.fxguitoolkit.SceneGraphUtilities;
import com.mhschmieder.fxguitoolkit.control.LayerPropertiesTable;
import com.mhschmieder.fxguitoolkit.stage.XStage;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public final class LayerManagementPane extends BorderPane {

    // Declare the table and controls used for the Layer Properties.
    public LayerPropertiesTable    _layerPropertiesTable;

    // Maintain a reference to the owning stage, for enablement updates.
    protected XStage _layerManagementStage;

    // Declare change listeners for various observable properties.
    protected InvalidationListener layerSelectionChangeListener;

    public LayerManagementPane( final XStage layerManagementStage,
                                final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        _layerManagementStage = layerManagementStage;

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void addCallbackListeners() {
        // Detect changes in the table row selection, for enablement.
        addLayerSelectionListener();

        // If the user clicks outside the table, deselect all rows.
        addEventFilter( MouseEvent.MOUSE_CLICKED, evt -> {
            final Node sourceNode = evt.getPickResult().getIntersectedNode();
            if ( !SceneGraphUtilities.isNodeInHierarchy( sourceNode, _layerPropertiesTable ) ) {
                // NOTE: We must remove the selection listeners while clearing
                // the selection, or we get run-time array out of bounds index
                // exceptions due to interim states where the selection index is
                // set to -1 inside Oracle's implementation code.
                removeLayerSelectionListener();
                clearSelection();
                addLayerSelectionListener();
                _layerManagementStage.updateContextualSettings();
            }
        } );
    }

    public void addLayerSelectionListener() {
        // Register an invalidation listener to update the contextual Layer
        // Management options when the selection changes.
        if ( layerSelectionChangeListener == null ) {
            layerSelectionChangeListener = listener -> _layerManagementStage
                    .updateContextualSettings();
        }
        _layerPropertiesTable.getSelectionModel().selectedIndexProperty()
                .addListener( layerSelectionChangeListener );
    }

    public boolean canDeleteTableRows() {
        // Forward this method to the Layer Properties Table.
        return _layerPropertiesTable.canDeleteTableRows();
    }

    public void clearSelection() {
        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.clearSelection();
    }

    public int createLayer() {
        // Forward this method to the Layer Properties Table.
        return _layerPropertiesTable.insertTableRow();
    }

    public int deleteLayers() {
        // Forward this method to the Layer Properties Table.
        final int referenceIndex = _layerPropertiesTable.deleteTableRows();

        return referenceIndex;
    }

    // TODO: Determine the need for this method as compared to similar
    // methods for graphical objects, and restore the forwarding while
    // implementing the method on the table class itself.
    @SuppressWarnings("static-method")
    public String getNewLayerNameDefault() {
        // Forward this method to the Loudspeaker Properties Table.
        // return _layerPropertiesTable.getNewLayerNameDefault();
        return LayerUtilities.LAYER_NAME_DEFAULT + FastMath.random();
    }

    // TODO: Determine the need for this method as compared to similar
    // methods for graphical objects, and restore the forwarding while
    // implementing the method on the table class itself.
    @SuppressWarnings("static-method")
    public String getUniqueLayerName( final String layerNameCandidate ) {
        // Forward this method to the Layer Properties Table.
        // return _layerPropertiesTable.getUniqueLayerName( layerNameCandidate
        // );
        return LayerUtilities.LAYER_NAME_DEFAULT + FastMath.random();
    }

    private void initPane( final ClientProperties pClientProperties ) {
        _layerPropertiesTable = new LayerPropertiesTable( pClientProperties );

        setCenter( _layerPropertiesTable );

        setPadding( new Insets( 12d ) );

        // Add the callback listeners for Layer Selection, etc.
        addCallbackListeners();
    }

    // TODO: Determine the need for this method as compared to similar
    // methods for graphical objects, and restore the forwarding while
    // implementing the method on the table class itself.
    @SuppressWarnings("static-method")
    public boolean isLayerNameUnique( final String layerNameCandidate ) {
        // Forward this method to the Layer Properties Table.
        // return _layerPropertiesTable.isLayerNameUnique( layerNameCandidate );
        return true;
    }

    public void removeLayerSelectionListener() {
        // Unregister the invalidation listener to avoid invalid selection index
        // exceptions during interim states where the table is empty before a
        // collection replacement.
        if ( layerSelectionChangeListener != null ) {
            _layerPropertiesTable.getSelectionModel().selectedIndexProperty()
                    .removeListener( layerSelectionChangeListener );
        }
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        _layerPropertiesTable.reset();
    }

    // Place editing focus in the specified row and column.
    public void setEditingFocus( final int rowIndex, final int columnIndex ) {
        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.setEditingFocus( rowIndex, columnIndex );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.setForegroundFromBackground( backColor );
    }

    public void setLayerCollection( final ObservableList< LayerProperties > layerCollection ) {
        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.setLayerCollection( layerCollection );
    }

    public void setSelectedRow( final int selectedRowIndex ) {
        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.selectRow( selectedRowIndex );
    }

    public void updateLayerCollection() {
        // Forward this method to the Layer Properties Table.
        _layerPropertiesTable.updateLayerCollection();
    }

}// class LayerManagementPane
