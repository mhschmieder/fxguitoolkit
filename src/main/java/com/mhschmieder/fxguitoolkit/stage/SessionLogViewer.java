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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.io.LogUtilities;
import com.mhschmieder.commonstoolkit.lang.StringConstants;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.SessionLogViewerToolBar;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;

public class SessionLogViewer extends XStage {

    public static final String     SESSION_LOG_VIEWER_TITLE_DEFAULT = " - Session Log Viewer";

    // Declare the main tool bar.
    public SessionLogViewerToolBar _toolBar;

    // Declare a text area for displaying the Session Log.
    protected TextArea             _sessionLogTextArea;

    // Declare a string to hold the filename for this Session Log File Cache.
    private final String           _sessionLogFilename;

    public SessionLogViewer( final String title,
                             final String windowKeyPrefix,
                             final String sessionLogFilename,
                             final ProductBranding productBranding,
                             final ClientProperties pClientProperties ) {
        this( title, 
              windowKeyPrefix, 
              sessionLogFilename, 
              productBranding, 
              pClientProperties, 
              true );
    }

    public SessionLogViewer( final String title,
                             final String windowKeyPrefix,
                             final String sessionLogFilename,
                             final ProductBranding productBranding,
                             final ClientProperties pClientProperties,
                             final boolean allowSessionLogRestart ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, productBranding, pClientProperties );

        _sessionLogFilename = sessionLogFilename;

        _defaultTitle = new StringBuilder( title );

        try {
            initStage( allowSessionLogRestart );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected final void initStage( final boolean allowSessionLogRestart ) {
       // First have the superclass initialize its content.
       initStage( "/icons/everaldo/EasyMobLog16.png", 840d, 480d, true );
       
       // NOTE: When using Log4J and some other logging mechanisms, there may
       //  be race conditions or security violations if the user resets the log.
       // TODO: Write forwarding methods down the hierarchy to avoid indirection.
       if ( !allowSessionLogRestart ) {
           _toolBar._sessionLogNewUpdateButtons._newSessionLogButton.setDisable( true );
           _toolBar._sessionLogNewUpdateButtons._newSessionLogButton.setVisible( false );
       }
   }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Load the event handler for the File Export Session Log Button.
        _toolBar._fileActionButtons._fileExportSessionLogButton
                .setOnAction( evt -> doFileExportSessionLog() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Load the event handler for the Wrap Text Toggle Button.
        _toolBar._textFormattingButtons._wrapTextButton.setOnAction( evt -> doWrapTextMode() );

        // Load the event handler for the New Session Log Button.
        _toolBar._sessionLogNewUpdateButtons._newSessionLogButton
                .setOnAction( evt -> doNewSessionLog() );

        // Load the event handler for the Update Session Log Button.
        _toolBar._sessionLogNewUpdateButtons._updateSessionLogButton
                .setOnAction( evt -> doUpdateSessionLog() );

        // Detect the ENTER key while the File Export Session Log Button has
        // focus, and use it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileExportSessionLogButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Export Session Log action.
                doFileExportSessionLog();

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

        // Detect the ENTER key while the Wrap Text Toggle Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._textFormattingButtons._wrapTextButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Wrap Text Toggle action.
                doWrapTextToggle();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the New Session Log Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._sessionLogNewUpdateButtons._newSessionLogButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the New Session Log action.
                doNewSessionLog();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Update Session Log Button has focus,
        // and use it to trigger its action (standard expected behavior).
        _toolBar._sessionLogNewUpdateButtons._updateSessionLogButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Update Session Log action.
                doUpdateSessionLog();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    protected final void doFileExportSessionLog() {
        fileExportSessionLog();
    }

    protected final void doNewSessionLog() {
        newSessionLogFileCache();
    }

    protected final void doUpdateSessionLog() {
        updateSessionLogFileCache();
    }

    protected final void doWrapTextMode() {
        setWrapTextMode( _toolBar._textFormattingButtons._wrapTextButton.isSelected() );
    }

    protected final void doWrapTextToggle() {
        // Toggle the state of the Wrap Text Toggle Button.
        _toolBar._textFormattingButtons._wrapTextButton
                .setSelected( !_toolBar._textFormattingButtons._wrapTextButton.isSelected() );

        // Now propagate that to the Wrap Text Mode setter.
        doWrapTextMode();
    }

    // Generic method to export a text dump of the Session Log.
    protected final FileStatus exportToTxt( final PrintWriter printWriter, 
                                            final String filename ) {
        // Avoid throwing unnecessary exceptions by filtering for bad print
        // writers.
        if ( printWriter == null ) {
            return FileStatus.WRITE_ERROR;
        }

        // Get the current Session Log File Cache.
        final String sessionLog = getSessionLogFileCache();

        // Print the entire Session Log out as one write-to-disc operation.
        printWriter.println( sessionLog );

        // Return the print writer's status, which subsumes exceptions.
        return printWriter.checkError() ? FileStatus.WRITE_ERROR : FileStatus.SAVED;
    }

    // This is a wrapper to ensure that all Session Log save actions are
    // treated uniformly.
    private final void fileExportSessionLog() {
        // Throw up a file chooser for the Session Log filename.
        final String title = "Export Session Log As"; //$NON-NLS-1$
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getTxtExtensionFilters();

        // Save a Session Log file using the selected filename.
        // TODO: To avoid writing atop the actual Session Log file and causing
        // potential deadlock or other such problems, we should provide a blank
        // initial file selection for the re-save.
        fileSaveAs( this,
                    title,
                    _defaultDirectory,
                    extensionFilterAdditions,
                    ExtensionFilters.TXT_EXTENSION_FILTER,
                    null );
    }

    // Take care of any extensions specific to this sub-class.
    @SuppressWarnings("nls")
    @Override
    public final FileStatus fileSaveExtensions( final File file,
                                                final File tempFile ) {
        // Pre-declare the File Save status in case of exceptions.
        FileStatus fileStatus = FileStatus.WRITE_ERROR;

        // TODO: Switch these and others to Apache Commons I/O library, which
        // has a SuffixFileFilter with accept() methods.
        final String fileName = file.getName();
        final String fileNameCaseInsensitive = fileName.toLowerCase( Locale.ENGLISH );
        try {
            if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "txt" ) ) {
                // Export the Session Log to a standard ASCII text log file,
                // overwriting it if it already exists.
                //
                // Chain a PrintWriter to a BufferedWriter to an OutputStreamWriter
                // (using UTF-8 encoding) to a FileOutputStream, for better 
                // performance and to guarantee platform-independence of newlines
                // and overall system-neutrality and locale-sensitivity of text data.
                try ( final FileOutputStream fileOutputStream 
                                = new FileOutputStream( tempFile );
                        final OutputStreamWriter outputStreamWriter 
                                = new OutputStreamWriter( fileOutputStream, "UTF8" );
                        final BufferedWriter bufferedWriter 
                                = new BufferedWriter( outputStreamWriter );
                        final PrintWriter printWriter 
                                = new PrintWriter( bufferedWriter ) ) {
                    fileStatus = exportToTxt( printWriter, file.getCanonicalPath() );
                }
            }
        }
        catch ( final NullPointerException | SecurityException | IOException e ) {
            e.printStackTrace();
        }

        return fileStatus;
    }

    // Get the current Session Log File Cache.
    public final String getSessionLogFileCache() {
        // For efficiency and downstream flexibility, use a string builder.
        final StringBuilder sessionLogStringBuilder = new StringBuilder();

        // Chain a BufferedReader to an InputStreamReader to a FileInputStream,
        // for better performance.
        //
        // NOTE: Using the Logger API causes deadlock on second use, so is
        // commented out until we adopt a full Logging Framework. For now, we
        // simply redirect to a file in the user's default temporary directory.
        // try ( final LogOutputStream sessionLogOutputStream = new
        // LogOutputStream() ) {
        try ( final FileInputStream fileInputStream 
                        = new FileInputStream( _sessionLogFilename );
                final InputStreamReader inputStreamReader
                        = new InputStreamReader( fileInputStream );
                final BufferedReader bufferedReader 
                        = new BufferedReader( inputStreamReader ) ) {
            String line;
            while ( ( line = bufferedReader.readLine() ) != null ) {
                // NOTE: Need the new line character as it gets discarded by
                // the buffered reader.
                sessionLogStringBuilder.append( line );
                sessionLogStringBuilder.append( StringConstants.LINE_SEPARATOR );
            }
        }
        catch ( final NullPointerException | IOException e ) {
            e.printStackTrace();
        }

        return sessionLogStringBuilder.toString();
    }

    @Override
    protected final Node loadContent() {
        // Instantiate and return the custom Content Node.
        _sessionLogTextArea = new TextArea();
        _sessionLogTextArea.setEditable( false );
        _sessionLogTextArea.setPrefWidth( 740d );
        _sessionLogTextArea.setPrefHeight( 320d );
        _sessionLogTextArea.autosize();

        GuiUtilities.setTextAreaProperties( _sessionLogTextArea, "log-viewer-text-area" );

        // Make the Session Log just translucent enough to see what's behind it.
        // NOTE: Translucency throws exceptions on Linux and Windows 8.1, but
        // we don't want to penalize all Windows users and Windows 8.1 has other
        // issues such as incorrect image width, so we only disable for Linux.
        if ( !SystemType.LINUX.equals( clientProperties.systemType ) ) {
            _sessionLogTextArea.setOpacity( 0.92125d );
        }

        final BorderPane contentPane = new BorderPane();
        contentPane.setPadding( new Insets( 5.0d ) );
        contentPane.setCenter( _sessionLogTextArea );
        return contentPane;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new SessionLogViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    // Start a new Session Log File Cache and related settings.
    public void newSessionLogFileCache() {
        // Redirect the logging from scratch, to start with a clean log.
        LogUtilities.redirectLogging( _sessionLogFilename );

        // Restore the Session Log Header (important for service calls).
        // NOTE: As we might change the main CSS stylesheet at run-time, it is
        //  best to query it anew rather than cache and forward it to this class.
        String cssStylesheet = Application.getUserAgentStylesheet();
        if ( cssStylesheet == null ) {
            cssStylesheet = "*** UNSPECIFIED ***";
        }
        LogUtilities.generateSessionLogHeader( clientProperties, _productBranding, cssStylesheet );

        // Update the Session Log File Cache to reflect the erasure.
        updateSessionLogFileCache();
    }

    protected final void setWrapTextMode( final boolean wrapText ) {
        // Match the Wrap Text Mode to the toggle button state.
        _sessionLogTextArea.setWrapText( wrapText );
    }

    // NOTE: This must be overridden so we can update the text area view of the
    // Session Log File Cache.
    @Override
    public final void toFront() {
        super.toFront();

        // Update the Session Log File Cache and related settings.
        updateSessionLogFileCache();
    }

    // Update the Session Log File Cache and related settings.
    public final void updateSessionLogFileCache() {
        // Get the current Session Log File Cache.
        final String sessionLog = getSessionLogFileCache();

        // Copy the Session Log to the Text Area.
        _sessionLogTextArea.setText( sessionLog );
    }

    @Override
    public void saveAllPreferences() {
        // NOTE Auto-generated method stub
        
    }
}