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
package com.mhschmieder.fxcontrols.dialog;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.control.GraphicsExportOptionsToggleGroup;
import com.mhschmieder.fxcontrols.control.TextEditor;
import com.mhschmieder.fxgraphics.io.VectorGraphicsExportOptions;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This is the Export Option Dialog for Vector Graphics Export. By default,
 * everything is exported, and this also covers under-specified cases. All
 * options are mutually exclusive as otherwise an empty export could result.
 */
public class VectorGraphicsExportOptionsDialog extends ExportOptionsDialog {

    protected VectorGraphicsExportOptions _vectorGraphicsExportOptionsCandidate;

    public VectorGraphicsExportOptionsDialog( final String title,
                                              final String masthead,
                                              final ClientProperties clientProperties,
                                              final VectorGraphicsExportOptions vectorGraphicsExportOptionsCandidate,
                                              final boolean hasTitle,
                                              final boolean hasChart,
                                              final boolean hasAuxiliary,
                                              final String graphicsExportAllLabel,
                                              final String graphicsExportChartLabel,
                                              final String graphicsExportAuxiliaryLabel ) {
        // Always call the superclass constructor first!
        super( title, masthead, clientProperties );

        _vectorGraphicsExportOptionsCandidate = vectorGraphicsExportOptionsCandidate;

        try {
            initDialog( clientProperties,
                        hasTitle,
                        hasChart,
                        hasAuxiliary,
                        graphicsExportAllLabel,
                        graphicsExportChartLabel,
                        graphicsExportAuxiliaryLabel );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initDialog( final ClientProperties clientProperties,
                                   final boolean hasTitle,
                                   final boolean hasChart,
                                   final boolean hasAuxiliary,
                                   final String graphicsExportAllLabel,
                                   final String graphicsExportChartLabel,
                                   final String graphicsExportAuxiliaryLabel ) {
        final String title = _vectorGraphicsExportOptionsCandidate.getTitle();
        final TextEditor titleEditor = new TextEditor( title, true, true, clientProperties );
        final HBox titleBox = GuiUtilities.getLabeledTextFieldPane( "Document Title", //$NON-NLS-1$
                                                                    titleEditor );

        // Export Options are mutually exclusive so need a Toggle Group.
        final GraphicsExportOptionsToggleGroup exportOptionsToggleGroup =
                                                                        new GraphicsExportOptionsToggleGroup( _vectorGraphicsExportOptionsCandidate,
                                                                                                              graphicsExportAllLabel,
                                                                                                              graphicsExportChartLabel,
                                                                                                              graphicsExportAuxiliaryLabel );

        // NOTE: We only show Export Options where there is at least one choice
        // for refined scope. Otherwise the user has nothing to choose as we
        // will be exporting all data automatically in such cases.
        final ObservableList< Node > nodes = FXCollections.observableArrayList();
        if ( hasTitle ) {
            nodes.add( titleBox );
        }

        if ( hasChart || hasAuxiliary ) {
            nodes.add( exportOptionsToggleGroup._exportAllRadioButton );
        }
        if ( hasChart ) {
            nodes.add( exportOptionsToggleGroup._exportChartRadioButton );
        }
        if ( hasAuxiliary ) {
            nodes.add( exportOptionsToggleGroup._exportAuxiliaryRadioButton );
        }

        final VBox content = new VBox();
        content.getChildren().setAll( nodes );

        content.setPadding( new Insets( 10.0d, 10.0d, 10.0d, 10.0d ) );
        content.setSpacing( 10.0d );

        final DialogPane dialogPane = getDialogPane();
        dialogPane.setContent( content );

        // Bind the Title Editor to its associated property.
        titleEditor.textProperty()
                .bindBidirectional( _vectorGraphicsExportOptionsCandidate.titleProperty() );
    }

}
