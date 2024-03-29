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
package com.mhschmieder.fxguitoolkit.stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.control.SvgViewerToolBar;
import com.mhschmieder.fxguitoolkit.control.ZoomPane;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This is a generic SVG Viewer, used for testing concepts of SVG Import.
 */
public class SvgViewer extends XStage {

    // Declare the main tool bar.
    public SvgViewerToolBar _toolBar;

    // Declare a Group to host the SVG Paths that contain the content of an SVG
    // file.
    final Group             _svgGroup;

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
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Disable Page Setup and Print, until loading the first file.
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( true );
        _toolBar._fileActionButtons._filePrintButton.setDisable( true );

        // Load the event handler for the File Open Button.
        _toolBar._fileActionButtons._fileOpenButton.setOnAction( evt -> doFileOpen() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Detect the ENTER key while the File Open Button has focus, and use it
        // to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileOpenButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Open action.
                doFileOpen();

                // Consume the ENTER key so it doesn't get processed
                // twice.
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

                // Consume the ENTER key so it doesn't get processed
                // twice.
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

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    protected final void doFileOpen() {
        // NOTE: Use the on-line example to take file load status into account.
        fileOpenSvgAsSceneGraph();
    }

    @Override
    public final boolean fileOpenErrorHandling( final File file,
                                                final FileStatus fileStatus ) {
        boolean sawErrors = false;
        
        switch ( fileStatus ) {
        case READ_ERROR:
            // Alert the user that a general file open error occurred.
            final String message = MessageFactory.getFileNotOpenedMessage( file );
            DialogUtilities.showFileOpenErrorAlert( message );
            break;
        case CREATED:
        case IMPORTED:
        case LOADED:
        case OPENED:
            sawErrors = true;
            break;
        // $CASES-OMITTED$
        default:
            break;
        }
        
        return sawErrors;
    }

    // This is a wrapper to ensure that all SVG open actions are treated
    // uniformly.
    private final void fileOpenSvgAsSceneGraph() {
        // Throw up a file chooser for the SVG filename.
        final String title = "Open SVG as Scene Graph"; //$NON-NLS-1$
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getSvgExtensionFilters();

        // Open a CSV file using the selected filename.
        fileOpen( this,
                  title,
                  _defaultDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.CSV_EXTENSION_FILTER,
                  false );
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

    // This file loader uses a specified file for the open, and is the
    // lowest-level shared call for all file open and import actions.
    @Override
    public final FileStatus loadFromFile( final File file ) {
        // Open the file.
        try {
            final String fileName = file.getName();
            final String fileNameCaseInsensitive = fileName.toLowerCase( Locale.ENGLISH );
            if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "svg" ) ) { //$NON-NLS-1$
                // Load the data from an SVG file.
                final Document doc = Jsoup.parse( file, "UTF-8", "" ); //$NON-NLS-1$ //$NON-NLS-2$

                // Update the full cache of displayed data and context, along
                // with tools enablement criteria.
                updateCache( file, doc );
            }
            else {
                // Do not attempt to open unsupported file types.
                return FileStatus.READ_ERROR;
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return FileStatus.READ_ERROR;
        }

        return FileStatus.OPENED;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new SvgViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    private final void updateCache( final File file, final Document doc ) {
        // Update the scene graph with the new SVG content.
        updateSceneGraph( doc );

        // Update the frame title with the input name.
        updateFrameTitle( file, false );

        // Enable Save As, Page Setup and Print, after loading a new file.
        _toolBar._fileActionButtons._fileSaveAsButton.setDisable( false );
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( false );
        _toolBar._fileActionButtons._filePrintButton.setDisable( false );
    }

    // Load the entire SVG file into a Group of JavaFX SVGPath nodes.
    @SuppressWarnings("nls")
    private final void updateSceneGraph( final Document doc ) {
        // Get the SVG Path Elements from the SVG Document.
        final Elements pathElements = doc.getElementsByTag( "path" );

        // Load each SVG Path from the Path Elements and add to the Group
        // Layout.
        final List< Node > svgPaths = new ArrayList<>();
        for ( final Element element : pathElements ) {
            // Load the actual SVG Path from the SVG Path Element.
            final String path = element.attr( "d" );

            // TODO: See if we need to use the SVG path Attribute.
            // final String usage = element.attr( "id" );

            // Create a JavaFX SVG Path Node and set its content from the SVG
            // Document, along with preferred attributes.
            final SVGPath svgPath = new SVGPath();
            svgPath.setContent( path );
            svgPath.setStrokeWidth( 1.0d );
            svgPath.setFillRule( FillRule.NON_ZERO );
            svgPath.setFill( Color.TRANSPARENT );
            svgPath.setStroke( Color.BLACK );

            // Add this SVG Path to the overall collection of SVG Paths.
            svgPaths.add( svgPath );
        }

        // Replace the SVG group Layout with the new SVG path Collection.
        _svgGroup.getChildren().setAll( svgPaths );
    }
}
