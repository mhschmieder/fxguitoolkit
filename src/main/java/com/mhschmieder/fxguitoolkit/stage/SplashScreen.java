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

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class SplashScreen extends Stage {

    // Declare the Image View host as a class instance variable so that the
    // owner can register a mouse pick event to hide this window.
    protected ImageView _splashImageView;

    public SplashScreen( final Modality modality,
                         final Image splashScreenImage,
                         final boolean backgroundLoading,
                         final SystemType systemType,
                         final ProductBranding productBranding ) {
        // Always call the superclass constructor first!
        super( StageStyle.TRANSPARENT );

        // Initialize the Modality as soon as possible (API contract).
        initModality( modality );

        try {
            initStage( splashScreenImage, backgroundLoading, systemType, productBranding );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initStage( final Image splashScreenImage,
                              final boolean backgroundLoading,
                              final SystemType systemType,
                              final ProductBranding productBranding ) {
        // Place the Splash Screen Image in an ImageView container, so it can be
        // displayed and scaled.
        _splashImageView = ImageUtilities.getImageView( splashScreenImage, backgroundLoading );

        // Create a root pane to host the Splash Screen Image.
        final BorderPane root = new BorderPane();
        root.setCenter( _splashImageView );

        // Try for transparency, but default to White if this fails on macOS.
        final Scene scene = new Scene( root, Color.TRANSPARENT );

        // Always center the Splash Screen on the screen.
        GuiUtilities.centerOnScreen( this );

        // Set the Splash Screen to top, so it isn't blocked by other
        // applications. It is easy to dismiss, so shouldn't prove annoying.
        setAlwaysOnTop( true );

        // It is now safe to set the Scene, now that the Window is initialized.
        setScene( scene );

        // Hide the Splash Screen if the user clicks the mouse anywhere on the
        // Splash Screen Image.
        // TODO: Also hide it if it is no longer in front -- although there
        // don't seem to be any callbacks to register for that change of status.
        _splashImageView.setOnMouseClicked( mouseEvent -> {
            // If the mouse exited, hide this window.
            hide();
        } );
    }

}
