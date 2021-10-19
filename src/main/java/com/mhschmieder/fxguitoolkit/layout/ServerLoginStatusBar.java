/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.FxGuiUtilities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public final class ServerLoginStatusBar extends StackPane {

    // protected static final String LONG_LOAD_STATUS_HEADER = "Loading Stored
    // Traces... "; //$NON-NLS-1$
    protected static final String SERVER_LOGIN_STATUS_HEADER = "Server Login Status: "; //$NON-NLS-1$

    // private HBox _longLoadStatusBar;
    // private ImageView _longLoadIcon;
    // private Label _longLoadStatusLabel;

    private HBox                  _loggedInStatusBar;
    private ImageView             _loggedInIcon;
    private Label                 _loggedInStatusLabel;

    private HBox                  _loggedOutStatusBar;
    private ImageView             _loggedOutIcon;
    private Label                 _loggedOutStatusLabel;

    // TODO: Add long load status message to parameter list.
    public ServerLoginStatusBar( final String loggedInMessage, final String loggedOutMessage ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( loggedInMessage, loggedOutMessage );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initPane( final String loggedInMessage, final String loggedOutMessage ) {
        // final String longLoadMessage = "Projects with Stored IFFT Traces May
        // Take a While to Load... Please Wait...";
        // _longLoadStatusBar = new HBox();
        // _longLoadIcon = ImageConversionUtilities.createIcon(
        // "/com/sketchdock/icons/LoggedOutRed16.png" );
        // _longLoadStatusLabel = SceneGraphUtilities
        // .getStatusLabel( LONG_LOAD_STATUS_HEADER + longLoadMessage );
        //
        // _longLoadStatusBar.getChildren().addAll( _longLoadIcon,
        // _longLoadStatusLabel );
        //
        // _longLoadStatusBar.setPadding( new Insets( 3d, 12d, 3d, 12d ) );
        // _longLoadStatusBar.setSpacing( 12d );

        _loggedInStatusBar = new HBox();
        _loggedInIcon = ImageUtilities.createIcon( "/com/sketchdock/icons/LoggedInBlue16.png" );
        _loggedInStatusLabel = FxGuiUtilities
                .getStatusLabel( SERVER_LOGIN_STATUS_HEADER + loggedInMessage );

        _loggedInStatusBar.getChildren().addAll( _loggedInIcon, _loggedInStatusLabel );

        _loggedInStatusBar.setPadding( new Insets( 3d, 12d, 3d, 12d ) );
        _loggedInStatusBar.setSpacing( 16d );

        _loggedOutStatusBar = new HBox();
        _loggedOutIcon = ImageUtilities.createIcon( "/com/sketchdock/icons/LoggedOutRed16.png" );
        _loggedOutStatusLabel = FxGuiUtilities
                .getStatusLabel( SERVER_LOGIN_STATUS_HEADER + loggedOutMessage );

        _loggedOutStatusBar.getChildren().addAll( _loggedOutIcon, _loggedOutStatusLabel );

        _loggedOutStatusBar.setPadding( new Insets( 3d, 12d, 3d, 12d ) );
        _loggedOutStatusBar.setSpacing( 12d );

        // Build the Stack Pane and prepare it for view-switching.
        getChildren().addAll( _loggedInStatusBar, _loggedOutStatusBar );
        // getChildren().addAll( _longLoadStatusBar, _loggedInStatusBar,
        // _loggedOutStatusBar );
        // StackPane.setAlignment( _longLoadStatusBar, Pos.CENTER_LEFT );
        StackPane.setAlignment( _loggedInStatusBar, Pos.CENTER_LEFT );
        StackPane.setAlignment( _loggedOutStatusBar, Pos.CENTER_LEFT );
        setAlignment( Pos.CENTER_LEFT );

        // Try to keep the Server Login Status Bar from getting too tall.
        setPrefHeight( 24d );
        setMaxHeight( 24d );
    }

    public final void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // _longLoadStatusBar.setBackground( background );
        _loggedInStatusBar.setBackground( background );
        _loggedOutStatusBar.setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );

        // _longLoadStatusLabel.setTextFill( Color.RED );
        _loggedInStatusLabel.setTextFill( foregroundColor );
        _loggedOutStatusLabel.setTextFill( foregroundColor );
    }

    // public final void updateLongLoadStatus( final boolean longLoadInProgress
    // ) {
    // Update the status message and icon for long load actions.
    // TODO: Pass in the authorization status as well.
    // if ( longLoadInProgress ) {
    // _longLoadStatusBar.setVisible( true );
    // _loggedInStatusBar.setVisible( false );
    // _loggedOutStatusBar.setVisible( false );
    // }
    // else {
    // _longLoadStatusBar.setVisible( false );
    // _loggedInStatusBar.setVisible( true );
    // _loggedOutStatusBar.setVisible( false );
    // }
    // }

    public final void updateServerLoginStatus( final boolean authorizedOnServer ) {
        // Update the status message and icon for server login status.
        if ( authorizedOnServer ) {
            // _longLoadStatusBar.setVisible( false );
            _loggedInStatusBar.setVisible( true );
            _loggedOutStatusBar.setVisible( false );
        }
        else {
            // _longLoadStatusBar.setVisible( false );
            _loggedInStatusBar.setVisible( false );
            _loggedOutStatusBar.setVisible( true );
        }
    }

}
