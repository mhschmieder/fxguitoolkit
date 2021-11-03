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

import com.mhschmieder.commonstoolkit.net.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.image.ImageSize;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.TextSelector;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class ImageSizePane extends VBox {

    public CheckBox      _autoSizeCheckBox;
    private TextSelector _pixelWidthSelector;
    private TextSelector _pixelHeightSelector;

    // Maintain a reference to the global Image Size.
    private ImageSize    imageSize;

    // //////////////////////////////////////////////////////////////////////////
    // Constructors and Initialization
    public ImageSizePane( final ClientProperties clientProperties, final boolean initialAutoSize ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties, initialAutoSize );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void bindProperties() {
        if ( imageSize != null ) {
            _autoSizeCheckBox.selectedProperty().bindBidirectional( imageSize.autoSizeProperty() );
        }
    }

    private void initPane( final ClientProperties clientProperties,
                           final boolean initialAutoSize ) {
        _autoSizeCheckBox = GuiUtilities.getCheckBox( "Use On-Screen Image Size", //$NON-NLS-1$
                                                      initialAutoSize );

        final Label pixelWidthLabel = GuiUtilities.getControlLabel( "Width" ); //$NON-NLS-1$

        // Gather the list of pixel width presets.
        // TODO: Derive a special class from DoubleSelector, and add unit?
        final String[] pixelWidths = ImageSize.getPixelWidthPresetsAsStrings();
        final String pixelWidthDefault = ImageSize
                .getPixelDimensionAsString( ImageSize.PIXEL_DIMENSIONS_WIDTH_DEFAULT );
        _pixelWidthSelector = new TextSelector( clientProperties,
                                                "Image Width in Pixels", //$NON-NLS-1$
                                                false,
                                                true,
                                                false,
                                                pixelWidths,
                                                pixelWidthDefault );

        final Label pixelHeightLabel = GuiUtilities.getControlLabel( "Height" ); //$NON-NLS-1$

        // Gather the list of pixel height presets.
        // TODO: Derive a special class from DoubleSelector, and add unit?
        final String[] pixelHeights = ImageSize.getPixelHeightPresetsAsStrings();
        final String pixelHeightDefault = ImageSize
                .getPixelDimensionAsString( ImageSize.PIXEL_DIMENSIONS_HEIGHT_DEFAULT );
        _pixelHeightSelector = new TextSelector( clientProperties,
                                                 "Image Height in Pixels", //$NON-NLS-1$
                                                 false,
                                                 true,
                                                 false,
                                                 pixelHeights,
                                                 pixelHeightDefault );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 6d );
        gridPane.setVgap( 6d );
        gridPane.setPadding( new Insets( 0d, 6d, 0d, 6d ) );

        gridPane.add( pixelWidthLabel, 0, 0 );
        gridPane.add( _pixelWidthSelector, 1, 0 );
        gridPane.add( pixelHeightLabel, 0, 1 );
        gridPane.add( _pixelHeightSelector, 1, 1 );

        gridPane.setAlignment( Pos.CENTER );
        gridPane.setPadding( new Insets( 12d ) );

        final Node imageSizeBorderNode = GuiUtilities
                .getTitledBorderWrappedNode( gridPane, "Target Image Size" ); //$NON-NLS-1$
        imageSizeBorderNode.setDisable( initialAutoSize );

        getChildren().setAll( _autoSizeCheckBox, imageSizeBorderNode );

        setPadding( new Insets( 10d ) );
        setSpacing( 10d );

        setAlignment( Pos.CENTER );

        // Bind Image Size Pane enablement to the associated Auto-Size Check
        // Box.
        imageSizeBorderNode.disableProperty().bind( _autoSizeCheckBox.selectedProperty() );

        // Load the event handler for the Pixel Width Selector.
        _pixelWidthSelector.setOnAction( evt -> {
            // Set the new Pixel Width if the user didn't cancel the
            // Combo Box. Maintain the Aspect Ratio, if possible.
            final String pixelWidthString = _pixelWidthSelector.getValue();
            if ( ( pixelWidthString != null ) && !pixelWidthString.trim().isEmpty() ) {
                final double pixelWidthCandidate = ImageSize
                        .getPixelDimensionAsDouble( pixelWidthString );
                final double pixelWidth = ImageSize.adjustDimensionForLimits( pixelWidthCandidate );
                final boolean pixelWidthAdjusted = ( pixelWidth != pixelWidthCandidate );

                // Enforce the size limits; update as necessary, or if
                // the user typed a custom number and left off the
                // units.
                if ( pixelWidthAdjusted
                        || !pixelWidthString.endsWith( ImageSize.PIXEL_UNITS_ABBREVIATED ) ) {
                    Platform.runLater( () -> _pixelWidthSelector
                            .setValue( ImageSize.getPixelDimensionAsString( pixelWidth ) ) );
                }

                // Maintain the Aspect Ratio; update as necessary.
                final double pixelHeightCandidate = imageSize.getPixelWidth();
                final double pixelHeight = ImageSize.adjustHeightForWidth( pixelHeightCandidate,
                                                                           pixelWidth );
                final boolean pixelHeightAdjusted = ( pixelHeight != pixelHeightCandidate );
                if ( pixelHeightAdjusted ) {
                    Platform.runLater( () -> _pixelHeightSelector
                            .setValue( ImageSize.getPixelDimensionAsString( pixelHeight ) ) );
                }

                // Update the model, as we haven't yet written bindings.
                imageSize.setPixelDimensions( pixelWidth, pixelHeight );
            }
        } );

        // Load the event handler for the Pixel Height Selector.
        _pixelHeightSelector.setOnAction( evt -> {
            // Set the new Pixel Height if the user didn't cancel the
            // Combo Box. Maintain the Aspect Ratio, if possible.
            final String pixelHeightString = _pixelHeightSelector.getValue();
            if ( ( pixelHeightString != null ) && !pixelHeightString.trim().isEmpty() ) {
                final double pixelHeightCandidate = ImageSize
                        .getPixelDimensionAsDouble( pixelHeightString );
                final double pixelHeight =
                                         ImageSize.adjustDimensionForLimits( pixelHeightCandidate );
                final boolean pixelHeightAdjusted = ( pixelHeight != pixelHeightCandidate );

                // Enforce the size limits; update as necessary, or if
                // the user typed a custom number and left off the
                // units.
                if ( pixelHeightAdjusted
                        || !pixelHeightString.endsWith( ImageSize.PIXEL_UNITS_ABBREVIATED ) ) {
                    Platform.runLater( () -> _pixelHeightSelector
                            .setValue( ImageSize.getPixelDimensionAsString( pixelHeight ) ) );
                }

                // Maintain the Aspect Ratio; update as necessary.
                final double pixelWidthCandidate = imageSize.getPixelWidth();
                final double pixelWidth = ImageSize.adjustWidthForHeight( pixelWidthCandidate,
                                                                          pixelHeight );
                final boolean pixelWidthAdjusted = ( pixelWidth != pixelWidthCandidate );
                if ( pixelWidthAdjusted ) {
                    Platform.runLater( () -> _pixelWidthSelector
                            .setValue( ImageSize.getPixelDimensionAsString( pixelWidth ) ) );
                }

                // Update the model, as we haven't yet written bindings.
                imageSize.setPixelDimensions( pixelWidth, pixelHeight );
            }
        } );
    }

    public void setImageSize( final ImageSize pImageSize ) {
        // Temporarily unbind the properties so we can set new Image Size.
        unbindProperties();

        // Cache the new Image Size.
        imageSize = pImageSize;

        // Update the GUI with the new values, since we have too many
        // complexities to be able to use data binding here.
        _autoSizeCheckBox.setSelected( imageSize.isAutoSize() );

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    private void unbindProperties() {
        if ( imageSize != null ) {
            _autoSizeCheckBox.selectedProperty()
                    .unbindBidirectional( imageSize.autoSizeProperty() );
        }
    }

}
