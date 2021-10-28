/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.stage;

import java.util.List;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

public final class AboutBox extends Popup {

    // Declare the Image View host as a class instance variable so that the
    // owner can register a mouse pick event to hide this window.
    protected ImageView _splashImageView;

    public AboutBox( final Image splashScreenImage,
                     final boolean backgroundLoading,
                     final SystemType systemType,
                     final ProductBranding productBranding,
                     final List< String > thirdPartyAttributions,
                     final boolean useAppInfo ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPopup( splashScreenImage,
                       backgroundLoading,
                       systemType,
                       productBranding,
                       thirdPartyAttributions,
                       useAppInfo );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initPopup( final Image splashScreenImage,
                              final boolean backgroundLoading,
                              final SystemType systemType,
                              final ProductBranding productBranding,
                              final List< String > thirdPartyAttributions,
                              final boolean useAppInfo ) {
        // Place the Splash Screen Image in a JavaFX ImageView container, so it
        // can be displayed and scaled.
        _splashImageView = ImageUtilities.getImageView( splashScreenImage, backgroundLoading );

        // Create a root pane to host the (optional) banner label, attribution
        // for third-party libraries, and the Splash Screen Image.
        // NOTE: Some applications may have Splash Screens that already have
        // the version and revision date built-in.
        // TODO: Add spacing vs. comma separation, with two text nodes.
        final VBox root = new VBox();
        final ObservableList< Node > nodes = root.getChildren();
        if ( useAppInfo ) {
            final String revisionNotice = "Release Version: " //$NON-NLS-1$
                    + productBranding.productVersion + ",  Last Updated: " //$NON-NLS-1$
                    + productBranding.revisionDate;

            final HBox banner = GuiUtilities.getBanner( revisionNotice );
            nodes.add( banner );
        }

        nodes.add( _splashImageView );

        if ( useAppInfo ) {
            if ( thirdPartyAttributions != null ) {
                for ( final String thirdPartyAttribution : thirdPartyAttributions ) {
                    final HBox attribution = GuiUtilities.getBanner( thirdPartyAttribution );
                    nodes.add( attribution );
                }
            }
        }

        if ( useAppInfo ) {
            root.getStyleClass().add( "status-box" ); //$NON-NLS-1$

            GuiUtilities.applyDropShadowEffect( root );
        }

        // Pop-ups add content vs. setting a scene (hidden in implementation).
        getContent().add( root );

        // Make the About Box just translucent enough to see what's behind it.
        // NOTE: Translucency throws exceptions on Linux and Windows 8.1, but
        // we don't want to penalize all Windows users and Windows 8.1 has other
        // issues such as incorrect image width, so we only disable for Linux.
        // NOTE: Apparently, some Macs also crash when transparency is used
        // along with mouse focus listeners, as we show against a Swing window.
        if ( !SystemType.LINUX.equals( systemType ) && useAppInfo ) {
            setOpacity( 0.92125d );
        }

        // Enforce that the About Box is 100% on-screen in all circumstances.
        setAutoFix( true );

        // Allow the ESC key to be used to hide this pop-up window.
        setHideOnEscape( true );

        // Hide this pop-up window if it loses focus.
        setAutoHide( true );

        // Do not prevent additional event handling by the pop-up owner.
        // TODO: Re-evaluate this after switching to a Window vs. Node owner,
        // as currently this behaves the same way, true or false.
        // NOTE: Switched back to the default, because it seems to be a cause
        // of crashes on retina displays and dual monitor systems (Mac OS X) if
        // the mouse event is allowed to be forwarded to the AWT/Swing window
        // underneath (e.g. Sound Field) or by the Tool Bar (probably triggering
        // the Predict Button and likely running on the wrong thread).
        setConsumeAutoHidingEvents( true );

        // Make sure the image itself can receive mouse pick events.
        _splashImageView.setPickOnBounds( true );

        // Hide the About Box if the user clicks the mouse anywhere on the
        // Splash Screen Image.
        // TODO: Also hide it if it is no longer in front -- although there
        // don't seem to be any callbacks to register for that change of status.
        _splashImageView.setOnMouseClicked( mouseEvent -> {
            // If the mouse exited, hide this pop-up window.
            hide();
        } );
    }

    public void show( final Node ownerNode ) {
        // Always center the Splash Screen on the screen.
        GuiUtilities.centerOnScreen( this );

        // Show the About Box, focused on the splash image, so that the user can
        // quickly click on it or trivially hide the pop-up window by moving
        // focus almost anywhere.
        // NOTE: The Node itself must have a valid non-null Window owner.
        show( ownerNode, getX(), getY() );
        _splashImageView.requestFocus();
    }

    @Override
    public void show( final Window ownerWindow ) {
        // Always center the Splash Screen on the screen.
        GuiUtilities.centerOnScreen( this );

        // Show the About Box, focused on the splash image, so that the user can
        // quickly click on it or trivially hide the pop-up window by moving
        // focus almost anywhere.
        show( ownerWindow, getX(), getY() );
        _splashImageView.requestFocus();
    }

}
