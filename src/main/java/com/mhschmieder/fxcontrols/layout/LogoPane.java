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
package com.mhschmieder.fxcontrols.layout;

import com.mhschmieder.fxgraphics.image.LogoUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.fxpdfexport.FxPdfTools;
import com.pdfjet.Image;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * This is a container used to provide more control over Logo positioning,
 * sizing, rendering, etc.
 */
public final class LogoPane extends HBox {

    // Use Image Views to load the light and dark logos.
    private ImageView _logoLight;
    private ImageView _logoDark;

    // Use a Label to host the active Logo Image View.
    private Label     _logoLabel;

    public LogoPane( final String jarRelativeLightLogoUrl,
                     final String jarRelativeDarkLogoUrl ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( jarRelativeLightLogoUrl, jarRelativeDarkLogoUrl );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void exportToPdf( final PDF document,
                             final Page visualizationPage,
                             final double xOffset,
                             final double yOffset,
                             final double scaleFactor )
            throws Exception {
        // As PDF is a "white" paper-oriented export format, use a dark logo.
        final Image logoImage = FxPdfTools.getImageSnapshot( document, _logoDark );
        if ( logoImage != null ) {
            logoImage.setPosition( xOffset, yOffset );
            logoImage.scaleBy( scaleFactor );
            logoImage.drawOn( visualizationPage );
        }
    }

    public double getLogoLeftMargin() {
        final Insets logoMargin = HBox.getMargin( _logoLabel );
        return ( logoMargin != null ) ? logoMargin.getLeft() : 0.0d;
    }

    private void initPane( final String jarRelativeLightLogoUrl,
                           final String jarRelativeDarkLogoUrl ) {
        // Load the logos for dark and light backgrounds so that we can switch 
        // between them when the host's background changes.
        _logoLight = LogoUtilities.getLogoImageView( Color.BLACK,
                                                       jarRelativeLightLogoUrl, 
                                                       jarRelativeDarkLogoUrl );
        _logoDark = LogoUtilities.getLogoImageView( Color.WHITE,
                                                      jarRelativeLightLogoUrl, 
                                                      jarRelativeDarkLogoUrl );

        // Make a Label to host the Logo Image Icon, to control sizing etc.
        _logoLabel = new Label();

        // Make sure the Copyright Notice below the Logo in the Logo Image
        // doesn't get clipped, by aligning to the top of the Label host.
        _logoLabel.setAlignment( Pos.TOP_LEFT );

        // Add the single label component to this container.
        getChildren().add( _logoLabel );

        // Make sure the Logo Icon is always on the left, with minimal gaps.
        // TODO: Use ScenicView to compare setPadding() vs. setMargins().
        setMargin( _logoLabel, new Insets( 6.0d ) );

        // Try to prevent the Logo from getting clipped or hidden.
        // NOTE: We give the image a chance to load before binding to it.
        Platform.runLater( () -> {
            _logoLabel.minWidthProperty().bind( _logoLight.fitWidthProperty() );
            _logoLabel.minHeightProperty().bind( _logoLight.fitHeightProperty() );
            minWidthProperty().bind( _logoLight.fitWidthProperty() );
            minHeightProperty().bind( _logoLight.fitHeightProperty() );
        } );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Replace with light logo if switching to a dark background.
        // NOTE: We also set the label's background, for consistent insets.
        final ImageView logo = ColorUtilities.isColorDark( backColor ) 
                ? _logoLight 
                : _logoDark;
        _logoLabel.setBackground( background );
        _logoLabel.setGraphic( logo );
    }
}
