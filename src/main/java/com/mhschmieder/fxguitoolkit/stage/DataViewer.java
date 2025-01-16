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
package com.mhschmieder.fxguitoolkit.stage;

import java.io.File;
import java.util.Collection;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.control.DataTableView;
import com.mhschmieder.fxguitoolkit.control.DataViewerToolBar;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

public class DataViewer extends XStage {

    // Declare the main tool bar.
    public DataViewerToolBar _toolBar;

    // Declare the table that will hold the dynamically loaded data.
    private DataTableView _tableView;

    public DataViewer( final String title,
                       final String windowKeyPrefix,
                       final String jarRelativeIconFilename,
                       final ProductBranding productBranding,
                       final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, true, true, productBranding, pClientProperties );

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

        // Disable Back and Forward until loading the first file.
        _toolBar._navigationButtons._backButton.setDisable( true );
        _toolBar._navigationButtons._forwardButton.setDisable( true );

        // Load the event handler for the Import Table Data Button.
        _toolBar._fileActionButtons._fileImportTableDataButton.setOnAction( 
                evt -> doImportTableData() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Load the event handler for the Navigate Back Button.
        _toolBar._navigationButtons._backButton.setOnAction( evt -> doNavigateBack() );

        // Load the event handler for the Navigate Forward Button.
        _toolBar._navigationButtons._forwardButton.setOnAction( evt -> doNavigateForward() );

        // Detect the ENTER key while the Import Table Data Button has focus, 
        // and use it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileImportTableDataButton.setOnKeyReleased( 
            keyEvent -> {
                final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
                if ( keyCombo.match( keyEvent ) ) {
                    // Trigger the Import Table Data action.
                    doImportTableData();
    
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

        // Detect the ENTER key while the Navigate Back Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._navigationButtons._backButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Navigate Back action.
                doNavigateBack();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Navigate Forward Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._navigationButtons._forwardButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Navigate Forward action.
                doNavigateForward();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    // TODO: Implement this.
    protected final void doNavigateBack() {}

    // TODO: Implement this.
    protected final void doNavigateForward() {}

    protected final void initStage( final String jarRelativeIconFilename ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, 840d, 500d, true );
    }

    @Override
    protected final Node loadContent() {
        // Instantiate and return the custom Content Node.
        _tableView = new DataTableView();

        // NOTE: We create an empty Border Pane to host the Table View, as it
        //  has to be generated and replaced dynamically and is initially
        //  blank/empty until the first file load.
        final BorderPane contentPane = new BorderPane();
        contentPane.setPadding( new Insets( 5.0d ) );
        contentPane.setCenter( _tableView );
        return contentPane;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new DataViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    @Override
    public final void processTableData( 
            final File file,
            final Collection< Collection< String > > rows ) {
        // Update the table view with the new data content.
        _tableView.updateTableView( rows );

        // Update the frame title with the input name.
        updateFrameTitle( file, false );

        // Enable Back and Disable Forward after loading a new file.
        _toolBar._navigationButtons._backButton.setDisable( false );
        _toolBar._navigationButtons._forwardButton.setDisable( true );

        // Enable Save As, Page Setup and Print, after loading a new file.
        _toolBar._fileActionButtons._fileSaveAsButton.setDisable( false );
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( false );
        _toolBar._fileActionButtons._filePrintButton.setDisable( false );
    }
}
