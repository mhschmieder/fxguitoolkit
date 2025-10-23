/**
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
package com.mhschmieder.fxcontrols.stage;

import com.mhschmieder.fxcontrols.control.SvgViewerToolBar;
import com.mhschmieder.fxcontrols.control.ZoomPane;
import com.mhschmieder.fxgraphics.shape.SvgUtilities;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.jsoup.nodes.Document;

import java.io.File;

/**
 * This is a generic SVG Viewer, used for testing concepts of SVG Import.
 */
public class SvgViewer extends XStage {

    // Declare the main tool bar.
    public SvgViewerToolBar _toolBar;

    // Declare a Group to host the SVG Paths that contain the content of an SVG
    // file.
    final Group             _svgGroup;

    public SvgViewer( final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        this( "SVG Viewer",
              "svgViewer",
              "/icons/oxygenIcons/SvgMimeType16.png", 
              productBranding, 
              pClientProperties );
    }

    public SvgViewer( final String title,
                      final String windowKeyPrefix,
                      final String jarRelativeIconFilename,
                      final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, true, true, productBranding, pClientProperties );

        _svgGroup = new Group();

        try {
            initStage( jarRelativeIconFilename );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    //  the toolbar buttons and their associated menu items, so that when one
    //  is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Disable Page Setup and Print, until loading the first file.
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( true );
        _toolBar._fileActionButtons._filePrintButton.setDisable( true );

        // Load the event handler for the File Import Graphics Button.
        _toolBar._fileActionButtons._fileImportVectorGraphicsButton.setOnAction( 
            evt -> doImportVectorGraphics() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Detect the ENTER key while the File Import Vector Graphics Button has 
        // focus, and use it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileImportVectorGraphicsButton.setOnKeyReleased( 
            keyEvent -> {
                final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
                if ( keyCombo.match( keyEvent ) ) {
                    // Trigger the File Open action.
                    doImportVectorGraphics();
    
                    // Consume the ENTER key so it doesn't get processed twice.
                    keyEvent.consume();
                }
        } );

        // Detect the ENTER key while the File Page Setup Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._filePageSetupButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Page Setup action.
                doPageSetup();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the File Print Button has focus, and use
        // it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._filePrintButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Print action.
                doPrint();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );
    }

    protected final void initStage( final String jarRelativeIconFilename ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, 840d, 500d, true );
    }

    @Override
    protected final Node loadContent() {
        // Instantiate and return the custom Content Node.
        final ZoomPane zoomPane = new ZoomPane( _svgGroup );
        final StackPane root = new StackPane();
        root.getChildren().add( zoomPane );

        final BorderPane contentPane = new BorderPane();
        contentPane.setPadding( new Insets( 5 ) );
        contentPane.setCenter( root );

        return contentPane;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new SvgViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    @Override
    public final void processSvgDocument( final File file, 
                                          final Document doc ) {
        // Update the scene graph with the new SVG content.
        SvgUtilities.makeSvgPathGroup( doc, _svgGroup );

        // Update the frame title with the input name.
        updateFrameTitle( file, false );

        // Enable Save As, Page Setup and Print, after loading a new file.
        _toolBar._fileActionButtons._fileSaveAsButton.setDisable( false );
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( false );
        _toolBar._fileActionButtons._filePrintButton.setDisable( false );
    }
}
