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

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.LogUtilities;
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

public class SessionLogViewer extends XStage {

    public static final String     SESSION_LOG_VIEWER_TITLE_DEFAULT = " - Session Log Viewer";

    // Declare the main tool bar.
    public SessionLogViewerToolBar _toolBar;

    // Declare a text area for displaying the Session Log.
    protected TextArea             _sessionLogTextArea;

    // Declare a string to hold the filename for this Session Log File Cache.
    private final String           _sessionLogFilename;

    public SessionLogViewer( final String sessionLogFilename,
                             final ProductBranding productBranding,
                             final ClientProperties pClientProperties ) {
        this( productBranding.productName + SESSION_LOG_VIEWER_TITLE_DEFAULT, 
              "sessionLogViewer", 
              sessionLogFilename, 
              productBranding, 
              pClientProperties, 
              true );
    }

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
       initStage( "/icons/everaldo/EasyMobLog16.png", 840.0d, 480.0d, true );
       
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
    //  the toolbar buttons and their associated menu items, so that when one
    //  is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Load the event handler for the File Export Session Log Button.
        _toolBar._fileActionButtons._fileExportSessionLogButton
                .setOnAction( evt -> doExportSessionLog() );

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
                doExportSessionLog();

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

    @Override
    public final String getSessionLogFilename() {
        return _sessionLogFilename;
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
        final String sessionLog = LogUtilities.loadSessionLogFromCache( _sessionLogFilename );

        // Copy the Session Log to the Text Area.
        _sessionLogTextArea.setText( sessionLog );
    }
}