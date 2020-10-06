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
 * This file is part of the FxuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * This is a utility class for generic common image functionality.
 * <p>
 * This class needs to move to an FxGraphicsToolkit library, but is here for now
 * due to some time pressure on dependent projects.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class ImageUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ImageUtilities() {}

    /**
     * Create an Icon as an Image View, using a JAR-resident resource.
     * 
     * @param jarRelativeIconFilename
     * @return
     */
    public static ImageView createIcon( final String jarRelativeIconFilename ) {
        // Direct-load the Icon into an Image View container.
        return getImageView( jarRelativeIconFilename, false );
    }

    /**
     * Create a Legend as an Image View, using a JAR-resident resource.
     * 
     * @param jarRelativeLegendFilename
     * @param preserveSourceImageRatio
     * @param derivedImageAspectRatio
     * @param fitWidth
     * @param fitHeight
     * @return
     */
    public static ImageView createLegend( final String jarRelativeLegendFilename,
                                          final boolean preserveSourceImageRatio,
                                          final double derivedImageAspectRatio,
                                          final double fitWidth,
                                          final double fitHeight ) {
        // Background-load the Legend into an Image View container.
        return getImageView( jarRelativeLegendFilename,
                             true,
                             preserveSourceImageRatio,
                             derivedImageAspectRatio,
                             fitWidth,
                             fitHeight );
    }

    /**
     * Create a Logo as an Image View, using a JAR-resident resource.
     * 
     * @param jarRelativeLogoFilename
     * @return
     */
    public static ImageView createLogo( final String jarRelativeLogoFilename ) {
        // Background-load the Logo into an Image View container.
        return getImageView( jarRelativeLogoFilename, true );
    }

    /**
     * @param node
     * @return
     */
    public static BufferedImage getBufferedImageSnapshot( final Node node ) {
        // Get the JavaFX WritableImage as the snapshot of the source Node.
        final WritableImage snapshot = getWritableImageSnapshot( node );

        // Convert to an AWT-based BufferedImage until something equivalent
        // exists in JavaFX.
        // :TODO: Use a PixelReader and PixelWriter to write to file? Or is this
        // utility method doing that anyway?
        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage( snapshot, null );

        return bufferedImage;
    }

    /**
     * Put an Image into an Image View container for use in Layouts.
     * 
     * @param image
     * @param backgroundLoading
     * @return
     */
    public static ImageView getImageView( final Image image, final boolean backgroundLoading ) {
        // :NOTE: We no longer use the dimensions; we know the size as we
        // chose the JAR resources to match layout constraints of Tool Bars
        // etc. Might need to re-enable "preserve aspect ratio" though, but
        // overall we see no ill effect from this change and are hoping it
        // will fix the DPI-related issues on Windows 8.1 with image width.
        final ImageView imageView = getImageView( image, backgroundLoading, false, -1d, -1d, -1d );

        return imageView;
    }

    /**
     * Put an Image into an Image View container for use in Layouts.
     * 
     * @param image
     * @param backgroundLoading
     * @param preserveRatio
     * @param aspectRatio
     * @param fitWidth
     * @param fitHeight
     * @return
     */
    public static ImageView getImageView( final Image image,
                                          final boolean backgroundLoading,
                                          final boolean preserveRatio,
                                          final double aspectRatio,
                                          final double fitWidth,
                                          final double fitHeight ) {
        final ImageView imageView = new ImageView();

        // Update the Image View container with the pre-loaded Image.
        updateImageView( imageView,
                         image,
                         backgroundLoading,
                         preserveRatio,
                         aspectRatio,
                         fitWidth,
                         fitHeight );

        return imageView;
    }

    /**
     * Direct-load or background-load an Image as a JAR-resident resource.
     * 
     * @param jarRelativeImageFilename
     * @param backgroundLoading
     * @return
     */
    public static ImageView getImageView( final String jarRelativeImageFilename,
                                          final boolean backgroundLoading ) {
        // Load the referenced Image as a JAR-resident resource.
        final Image image = loadImageAsJarResource( jarRelativeImageFilename, backgroundLoading );

        // Put the Image into an Image View container for use in Layouts.
        final ImageView imageView = getImageView( image, backgroundLoading );

        return imageView;
    }

    /**
     * Direct-load or background-load an Image as a JAR-resident resource.
     * 
     * @param jarRelativeImageFilename
     * @param backgroundLoading
     * @param preserveSourceImageRatio
     * @param derivedImageAspectRatio
     * @param fitWidth
     * @param fitHeight
     * @return
     */
    public static ImageView getImageView( final String jarRelativeImageFilename,
                                          final boolean backgroundLoading,
                                          final boolean preserveSourceImageRatio,
                                          final double derivedImageAspectRatio,
                                          final double fitWidth,
                                          final double fitHeight ) {
        // Load the referenced Image as a JAR-resident resource.
        final Image image = loadImageAsJarResource( jarRelativeImageFilename, backgroundLoading );

        // Put the Image into an Image View container for use in Layouts.
        final ImageView imageView = getImageView( image,
                                                  true,
                                                  preserveSourceImageRatio,
                                                  derivedImageAspectRatio,
                                                  fitWidth,
                                                  fitHeight );

        return imageView;
    }

    /**
     * @param node
     * @return
     */
    public static WritableImage getWritableImageSnapshot( final Node node ) {
        // Render the snapshot source with no compression.
        // :NOTE: This version of the snapshot method is a blocking function,
        // but there is another version that allows a callback to be passed in.
        // :TODO: If we pre-construct a WritableImage, we can give it a size
        // different from the screen size, such as the one entered in the Image
        // Export Options Dialog.
        final WritableImage snapshot = node.snapshot( new SnapshotParameters(), null );

        return snapshot;
    }

    /**
     * Load an Image as an Application JAR-resident resource.
     * <p>
     * :TODO: Algorithmically tag the Image size to a partially specified name?
     * 
     * @param jarRelativeImageFilename
     * @param backgroundLoading
     * @return
     */
    public static Image loadImageAsJarResource( final String jarRelativeImageFilename,
                                                final boolean backgroundLoading ) {
        // If no valid file (with extension) provided, return a null Image.
        if ( ( jarRelativeImageFilename == null ) || ( jarRelativeImageFilename.length() < 5 ) ) {
            return null;
        }

        // Return the referenced Image from the Application's JAR file.
        try {
            // Load the JAR-resident Image file into an Image container.
            final Image image = new Image( jarRelativeImageFilename, backgroundLoading );

            // Track the Image's error property, to report any errors.
            image.errorProperty().addListener( ( observable, oldValue, imageError ) -> {
                if ( imageError ) {
                    image.getException().printStackTrace();
                }
            } );

            return image;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load and upsample the {@link Image image} without smoothing or
     * anti-aliasing the pixels.
     *
     * @param inputStream
     * @param preserveSourceImageRatio
     * @param derivedImageAspectRatio
     * @param fitWidth
     * @param fitHeight
     * @return
     */
    public static Image loadImageFromStream( final InputStream inputStream,
                                             final boolean preserveSourceImageRatio,
                                             final double derivedImageAspectRatio,
                                             final double fitWidth,
                                             final double fitHeight ) {
        // Determine whether the source image Aspect Ratio should be preserved.
        // If not, use the supplied fit dimensions (if valid) and apply the
        // supplied Aspect Ratio to whichever fit dimension wasn't provided.
        // :NOTE: Provide -1.0 (e.g.) to invalidate any of the numeric
        // arguments.
        final double fitWidthAdjusted = preserveSourceImageRatio
            ? ( fitWidth > 0d ) ? fitWidth : -1d
            : ( derivedImageAspectRatio > 0d )
                ? ( fitWidth > 0d )
                    ? fitWidth
                    : ( fitHeight > 0d ) ? fitHeight * derivedImageAspectRatio : -1d
                : -1d;
        final double fitHeightAdjusted = preserveSourceImageRatio
            ? ( fitHeight > 0d ) ? fitHeight : -1d
            : ( derivedImageAspectRatio > 0d )
                ? ( fitHeight > 0d )
                    ? fitHeight
                    : ( fitWidth > 0d ) ? fitWidth / derivedImageAspectRatio : -1d
                : -1d;

        final Image image = new Image( inputStream,
                                       fitWidthAdjusted,
                                       fitHeightAdjusted,
                                       preserveSourceImageRatio,
                                       false );

        return image;
    }

    /**
     * @param imageView
     * @param image
     * @param preserveSourceImageRatio
     * @param derivedImageAspectRatio
     * @param fitWidth
     * @param fitHeight
     */
    public static void setImageView( final ImageView imageView,
                                     final Image image,
                                     final boolean preserveSourceImageRatio,
                                     final double derivedImageAspectRatio,
                                     final double fitWidth,
                                     final double fitHeight ) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        // Determine whether the source image Aspect Ratio should be preserved.
        // If not, use the supplied fit dimensions (if valid) and apply the
        // supplied Aspect Ration to whichever fit dimension wasn't provided.
        // :NOTE: Provide -1.0 (e.g.) to invalidate any of the numeric
        // arguments.
        final double fitWidthAdjusted = preserveSourceImageRatio
            ? ( fitWidth > 0d ) ? fitWidth : -1d
            : ( derivedImageAspectRatio > 0d )
                ? ( fitWidth > 0d )
                    ? fitWidth
                    : ( fitHeight > 0d )
                        ? fitHeight * derivedImageAspectRatio
                        : imageHeight * derivedImageAspectRatio
                : imageWidth;
        final double fitHeightAdjusted = preserveSourceImageRatio
            ? ( fitHeight > 0d ) ? fitHeight : -1d
            : ( derivedImageAspectRatio > 0d )
                ? ( fitHeight > 0d )
                    ? fitHeight
                    : ( fitWidth > 0d )
                        ? fitWidth / derivedImageAspectRatio
                        : imageWidth / derivedImageAspectRatio
                : imageHeight;

        imageView.setImage( image );
        imageView.setPreserveRatio( preserveSourceImageRatio );
        imageView.setFitWidth( fitWidthAdjusted );
        imageView.setFitHeight( fitHeightAdjusted );
        imageView.setSmooth( false );
        imageView.setCache( true );
    }

    /**
     * Put an Image into an Image View container for use in Layouts.
     * 
     * @param imageView
     * @param image
     * @param backgroundLoading
     */
    public static void updateImageView( final ImageView imageView,
                                        final Image image,
                                        final boolean backgroundLoading ) {
        // Update the Image View container with the pre-loaded Image.
        // :NOTE: We no longer use the dimensions; we know the size as we
        // chose the JAR resources to match layout constraints of Tool Bars
        // etc. Might need to re-enable "preserve aspect ratio" though, but
        // overall we see no ill effect from this change and are hoping it
        // will fix the DPI-related issues on Windows 8.1 with image width.
        updateImageView( imageView, image, backgroundLoading, false, -1d, -1d, -1d );
    }

    /**
     * Update an Image View container with a pre-loaded Image.
     * 
     * @param imageView
     * @param image
     * @param backgroundLoading
     * @param preserveRatio
     * @param aspectRatio
     * @param fitWidth
     * @param fitHeight
     */
    public static void updateImageView( final ImageView imageView,
                                        final Image image,
                                        final boolean backgroundLoading,
                                        final boolean preserveRatio,
                                        final double aspectRatio,
                                        final double fitWidth,
                                        final double fitHeight ) {
        // Track the Image's loading progress, so we can safely set it on the
        // ImageView host after the background Image load completes.
        if ( image != null ) {
            if ( backgroundLoading ) {
                image.progressProperty().addListener( ( observable, oldValue, progress ) -> {
                    // Progress is a percentage from 0.0% to 1.0%.
                    if ( progress.doubleValue() >= 0.999999d ) {
                        if ( !image.isError() ) {
                            setImageView( imageView,
                                          image,
                                          preserveRatio,
                                          aspectRatio,
                                          fitWidth,
                                          fitHeight );
                        }
                    }
                } );
            }
            else {
                setImageView( imageView, image, preserveRatio, aspectRatio, fitWidth, fitHeight );
            }
        }
    }

    /**
     * Update an Image View container, using a JAR-resident resource.
     * 
     * @param imageView
     * @param jarRelativeImageFilename
     * @param backgroundLoading
     */
    public static void updateImageView( final ImageView imageView,
                                        final String jarRelativeImageFilename,
                                        final boolean backgroundLoading ) {
        // Load the referenced Image as a JAR-resident resource.
        final Image image = loadImageAsJarResource( jarRelativeImageFilename, backgroundLoading );

        // Update the Image View container with the pre-loaded Image.
        updateImageView( imageView, image, backgroundLoading );
    }

}