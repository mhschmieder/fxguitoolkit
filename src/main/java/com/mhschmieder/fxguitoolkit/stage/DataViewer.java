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
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.CsvUtilities;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.control.DataViewerToolBar;
import com.mhschmieder.fxguitoolkit.control.TableUtilities;
import com.mhschmieder.fxguitoolkit.control.XTableView;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

public class DataViewer extends XStage {

    // Declare the main tool bar.
    public DataViewerToolBar                       _toolBar;

    // Declare the table that will hold the dynamically loaded data.
    private XTableView< ObservableList< String > > _tableView;

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
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Disable Page Setup and Print, until loading the first file.
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( true );
        _toolBar._fileActionButtons._filePrintButton.setDisable( true );

        // Disable Back and Forward until loading the first file.
        _toolBar._navigationButtons._backButton.setDisable( true );
        _toolBar._navigationButtons._forwardButton.setDisable( true );

        // Load the event handler for the File Open Button.
        _toolBar._fileActionButtons._fileOpenButton.setOnAction( evt -> doFileOpen() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Load the event handler for the Navigate Back Button.
        _toolBar._navigationButtons._backButton.setOnAction( evt -> doNavigateBack() );

        // Load the event handler for the Navigate Forward Button.
        _toolBar._navigationButtons._forwardButton.setOnAction( evt -> doNavigateForward() );

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

    protected final void doFileOpen() {
        // NOTE: Use the on-line example to take file load status into account.
        fileOpenCsvAsTable();
    }

    // TODO: Implement this.
    protected final void doNavigateBack() {}

    // TODO: Implement this.
    protected final void doNavigateForward() {}

    // This is a wrapper to ensure that all CSV open actions are treated
    // uniformly.
    private final void fileOpenCsvAsTable() {
        // Throw up a file chooser for the CSV filename.
        final String title = "Open CSV as Table"; //$NON-NLS-1$
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getCsvExtendedExtensionFilters();

        // Open a CSV file using the selected filename.
        fileOpen( this,
                  title,
                  _defaultDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.CSV_EXTENSION_FILTER,
                  false );
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

    protected final void initStage( final String jarRelativeIconFilename ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, 840d, 500d, true );
    }

    @Override
    protected final Node loadContent() {
        // Instantiate and return the custom Content Node.
        _tableView = new XTableView<>();

        // This is a display-only table.
        _tableView.setTableEditable( false );

        // Try to force the preferred size to the total column width and
        // multiple rows of data plus the header.
        // NOTE: Once we pass in a size, leave room for scroll bars.
        // _tableView.setPrefSize( 760, 340 );
        _tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        // NOTE: We create an empty Border Pane to host the Table View, as it
        // has to be generated and replaced dynamically and is initially
        // blank/empty until the first file load.
        final BorderPane contentPane = new BorderPane();
        contentPane.setPadding( new Insets( 5.0d ) );
        contentPane.setCenter( _tableView );
        return contentPane;
    }

    // This file loader uses a specified file for the open, and is the
    // lowest-level shared call for all file open and import actions.
    @SuppressWarnings("nls")
    @Override
    public final FileStatus loadFromFile( final File file ) {
        final Collection< Collection< String > > rows = new ArrayList<>();

        // Open the file.
        try {
            final String fileName = file.getName();
            final String fileNameCaseInsensitive = fileName.toLowerCase( Locale.ENGLISH );
            if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "csv" )
                    || FilenameUtils.isExtension( fileNameCaseInsensitive, "zip" ) ) {
                // Load the data into a String from a CSV or ZIP file.
                final boolean fileOpened = CsvUtilities.convertCsvToStringVector( file, rows );
                if ( !fileOpened ) {
                    return FileStatus.READ_ERROR;
                }
            }
            else {
                // Do not attempt to open unsupported file types.
                return FileStatus.READ_ERROR;
            }
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();
            return FileStatus.READ_ERROR;
        }

        // Update the full cache of displayed data and context, along with tools
        // enablement criteria.
        updateCache( file, rows );

        return FileStatus.OPENED;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new DataViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    private final void updateCache( 
            final File file, final Collection< Collection< String > > rows ) {
        // Update the table view with the new data content.
        updateTableView( rows );

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

    private final void updateTableView( 
            final Collection< Collection< String > > dataRows ) {
        // Pad the vector of string vectors to the maximum column count.
        final int maxColumn = GlobalUtilities.padStringsToMaxColumn( dataRows );

        // Create names for the default column headers.
        final List< String > headers = new ArrayList<>( maxColumn );
        for ( int i = 0; i < maxColumn; i++ ) {
            headers.add( Integer.toString( i + 1 ) );
        }

        // Clear any existing table column headers to prepare for new ones.
        final ObservableList< TableColumn< ObservableList< String >, ? > > columns = _tableView
                .getColumns();
        columns.clear();

        // Convert to a TableView that has no property names due to being
        // non-editable, and has default initial cell width as each column will
        // be an indeterminate size.
        final ArrayList< TableColumn< ObservableList< String >, String > > tableColumnCollection =
                                                                                                 new ArrayList<>( maxColumn );
        for ( int i = 0; i < maxColumn; i++ ) {
            // NOTE: We appear to have to use an approach more akin to an SQL
            // example that I saw due to the data being random and not having
            // property names.
            // final String columnPropertyName = null; // "";
            // final TableColumn< ObservableList< String >, String > tableColumn
            // = _csvTable
            // .getTableColumnForString( headers.get( i ),
            // 50,
            // columnPropertyName,
            // false,
            // clientProperties );

            final TableColumn< ObservableList< String >, String > tableColumn =
                                                                              new TableColumn<>( headers
                                                                                      .get( i ) );
            tableColumn.setMinWidth( 50 );
            TableUtilities.setTableColumnHeaderProperties( tableColumn );

            // NOTE: Do not allow column reordering or row-sorting as this
            // destroys the ability to understand how things relate to one
            // another (that is, the relationships of the data).
            tableColumn.setSortable( false );

            // We are using non property style for making a dynamic table.
            final int j = i;
            final Callback< CellDataFeatures< ObservableList< String >, String >, ObservableValue< String > > callback =
                                                                                                                       param -> new SimpleStringProperty( param
                                                                                                                               .getValue()
                                                                                                                               .get( j )
                                                                                                                               .toString() );
            tableColumn.setCellValueFactory( callback );

            TableUtilities.setCellAlignment( tableColumn );

            tableColumnCollection.add( tableColumn );
        }
        columns.addAll( 0, tableColumnCollection );

        // Clear any existing table rows to prepare for new data.
        final ObservableList< ObservableList< String > > data = _tableView.getItems();
        data.clear();

        // Replace the current Table View. This will cause automatic updates so
        // should refresh the view on the screen. Iterate by Row, then Column.
        dataRows.forEach( dataRow -> {
            final ObservableList< String > row = FXCollections.observableArrayList();
            dataRow.forEach( column -> row.add( column ) );
            data.add( row );
        } );

        // Look at the revised list in the debugger to see why the display is
        // blank currently even though the data is there and the column headers.
        // NOTE: This proves the data is there, so it must be an issue with
        // cell background or the details of the cell factory class used.
        // TODO: Compare to what we do for other read-only string-based cells.
        final ObservableList< ObservableList< String > > data2 = _tableView.getItems();
        final int numberOfRows = data2.size();
        if ( numberOfRows < 1 ) {
            return;
        }
    }
}
