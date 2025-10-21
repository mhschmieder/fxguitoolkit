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
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.fxgraphicstoolkit.io.RenderedGraphicsExportOptions;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.TextEditor;
import com.mhschmieder.fxguitoolkit.swing.RenderedGraphicsPanel;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.awt.EventQueue;

/**
 * This is the main content pane for Rendered Graphics Export Preview
 * windows.
 */
public final class RenderedGraphicsExportPreviewPane extends BorderPane {

    private HBox                                 _titleBox;
    private TextEditor                           _titleEditor;

    // Cache the Rendered Graphics Export Options.
    private RenderedGraphicsExportOptions        _renderedGraphicsExportOptions;

    // Cache the Swing Node wrapper for the Graphics Export Source, for
    // background fills.
    private SwingNode                            _graphicsPreviewNode;

    // Maintain a Swing Component reference for Graphics Export actions.
    private RenderedGraphicsPanel                _renderedGraphicsExportSource;

    // Cache the Client Properties (System Type, Locale, Client Type, etc.).
    public ClientProperties                        _clientProperties;

    public RenderedGraphicsExportPreviewPane( final ClientProperties pClientProperties,
                                              final RenderedGraphicsExportOptions renderedGraphicsExportOptions ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        _renderedGraphicsExportOptions = renderedGraphicsExportOptions;

        _graphicsPreviewNode = new SwingNode();

        try {
            initPane();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public RenderedGraphicsExportOptions getRenderedGraphicsExportOptions() {
        return _renderedGraphicsExportOptions;
    }

    private void initPane() {
        final String title = _renderedGraphicsExportOptions.getTitle();
        _titleEditor = new TextEditor( title,
                                       "Title for EPS Document Header", //$NON-NLS-1$
                                       true,
                                       true,
                                       _clientProperties );
        _titleEditor.setPrefWidth( 480d );
        _titleEditor.setMinWidth( 480d );

        _titleBox = GuiUtilities.getLabeledTextFieldPane( "Title", _titleEditor ); //$NON-NLS-1$
        _titleBox.setAlignment( Pos.CENTER );

        // Set the Title Editor to the top of the layout.
        setTop( _titleBox );

        // NOTE: We defer the layout of the main content pane, as it is
        // dependent upon run-time content generation.
        setPadding( new Insets( 6.0d, 6.0d, 6.0d, 6.0d ) );

        // Bind the Title Editor to its associated property.
        _titleEditor.textProperty().bindBidirectional(
                _renderedGraphicsExportOptions.titleProperty() );

        // Load the change listener for the Export Auxiliary Panel property.
        _renderedGraphicsExportOptions.exportAuxiliaryPanelProperty()
                .addListener(
                        ( observable,
                          oldValue,
                          newValue ) -> {
                    // Update the visibility of the associated panel.
                    EventQueue.invokeLater( () -> _renderedGraphicsExportSource
                            .setAuxiliaryPanelVisible( newValue ) );
                } );

        // Load the change listener for the Export Information Tables property.
        _renderedGraphicsExportOptions.exportInformationTablesProperty()
                .addListener(
                        ( observable,
                          oldValue,
                          newValue ) -> {
                    // Update the visibility of the associated panel.
                    EventQueue.invokeLater( () -> _renderedGraphicsExportSource
                            .setInformationTablesVisible( newValue ) );
                } );

        // Load the change listener for the Export Optional Item property.
        _renderedGraphicsExportOptions.exportOptionalItemProperty().addListener(
                ( observable,
                  oldValue,
                  newValue ) -> {
                    // Update the visibility of the associated panel.
                    EventQueue.invokeLater( () -> _renderedGraphicsExportSource
                            .setOptionalItemVisible( newValue ) );
                } );
    }

    /**
     * This method encapsulates the centered Border Pane layout position of the
     * Exported Graphics Preview Node.
     *
     * @param exportedGraphicsPreviewNode
     *            Exported graphics preview node
     */
    private void setExportedGraphicsPreviewNode(
            final Node exportedGraphicsPreviewNode ) {
        // First, wrap the content in a scroll pane so the user has more
        // flexibility and to compensate for small laptop screens.
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent( exportedGraphicsPreviewNode );

        // Replace the main preview content in the center of the layout.
        setCenter( scrollPane );

        // Attempt to force a re-layout as layout sizes may have changed.
        setNeedsLayout( true );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        _titleBox.setBackground( background );
    }

    public void setRenderedGraphicsExportOptions(
            final RenderedGraphicsExportOptions renderedGraphicsExportOptions ) {
        // Update the current export options (usually from preferences).
        _renderedGraphicsExportOptions
                .setRenderedGraphicsExportOptions( renderedGraphicsExportOptions );
    }

    /**
     * This method sets the container reference for exported graphics.
     *
     * @param renderedGraphicsExportSource
     *            The Swing container for the layout group to be exported
     */
    public void setRenderedGraphicsExportSource(
            final RenderedGraphicsPanel renderedGraphicsExportSource ) {
        // Cache the Graphics Export Source locally, for reference by panel
        // visibility change listeners.
        _renderedGraphicsExportSource = renderedGraphicsExportSource;

        // Set the Swing Node wrapper for the provided Swing container.
        _graphicsPreviewNode.setContent( _renderedGraphicsExportSource );

        // Reset the Exported Graphics Preview Node to the Border Layout.
        setExportedGraphicsPreviewNode( _graphicsPreviewNode );
    }

    public void updateExportOptionsView() {
        // Make sure the previously selected options immediately take hold.
        EventQueue.invokeLater( () -> _renderedGraphicsExportSource
                .updateExportOptionsView( _renderedGraphicsExportOptions ) );
    }
}
