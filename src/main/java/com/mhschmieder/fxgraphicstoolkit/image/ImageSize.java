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
package com.mhschmieder.fxgraphicstoolkit.image;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Dimension2D;

public class ImageSize {

    // Declare default constants, where appropriate, for all fields.
    public static final boolean                         AUTO_SIZE_DEFAULT               = true;

    public static final double                          PIXEL_DIMENSIONS_WIDTH_DEFAULT  = 1440;
    public static final double                          PIXEL_DIMENSIONS_HEIGHT_DEFAULT = 900;

    public static final double                          PIXEL_DIMENSIONS_MINIMUM        = 64;
    public static final double                          PIXEL_DIMENSIONS_MAXIMUM        = 10000;

    public static final double[]                        PIXEL_WIDTH_PRESETS             =
                                                                            new double[] {
                                                                                           800,
                                                                                           1024,
                                                                                           1280,
                                                                                           1440 };

    public static final double[]                        PIXEL_HEIGHT_PRESETS            =
                                                                             new double[] {
                                                                                            600,
                                                                                            768,
                                                                                            900,
                                                                                            1024 };

    // Declare an abbreviated string representation of pixel units.
    @SuppressWarnings("nls") public static final String PIXEL_UNITS_ABBREVIATED         = " px";

    // As pixels are now floating-point, make sure to use User Locale.
    protected static final NumberFormat                 DOUBLE_FORMATTER                =
                                                                         NumberFormat
                                                                                 .getNumberInstance( Locale
                                                                                         .getDefault() );

    // Utility method to enforce size limits and return as modified.
    public static final double adjustDimensionForLimits( final double pixelDimensionCandidate ) {
        double pixelDimension = pixelDimensionCandidate;
        if ( pixelDimensionCandidate < PIXEL_DIMENSIONS_MINIMUM ) {
            pixelDimension = PIXEL_DIMENSIONS_MINIMUM;
        }
        else if ( pixelDimensionCandidate > PIXEL_DIMENSIONS_MAXIMUM ) {
            pixelDimension = PIXEL_DIMENSIONS_MAXIMUM;
        }

        return pixelDimension;
    }

    // Maintain the Aspect Ratio; update Pixel Height as necessary.
    public static final double adjustHeightForWidth( final double pixelHeightCandidate,
                                                     final double pixelWidth ) {
        // If one of the preset widths was selected, make sure its corresponding
        // preset height is automatically selected.
        double pixelHeight = pixelHeightCandidate;
        final int numberOfPixelWidthPresets = PIXEL_WIDTH_PRESETS.length;
        for ( int i = 0; i < numberOfPixelWidthPresets; i++ ) {
            if ( pixelWidth == PIXEL_WIDTH_PRESETS[ i ] ) {
                pixelHeight = PIXEL_HEIGHT_PRESETS[ i ];
                break;
            }
        }

        return pixelHeight;
    }

    // Maintain the Aspect Ratio; update Pixel Width as necessary.
    public static final double adjustWidthForHeight( final double pixelWidthCandidate,
                                                     final double pixelHeight ) {
        // If one of the preset heights was selected, make sure its
        // corresponding preset width is automatically selected.
        double pixelWidth = pixelWidthCandidate;
        final int numberOfPixelHeightPresets = PIXEL_HEIGHT_PRESETS.length;
        for ( int i = 0; i < numberOfPixelHeightPresets; i++ ) {
            if ( pixelHeight == PIXEL_HEIGHT_PRESETS[ i ] ) {
                pixelWidth = PIXEL_WIDTH_PRESETS[ i ];
                break;
            }
        }

        return pixelWidth;
    }

    // Utility method to convert a pixel dimension from a string
    // representation with abbreviated units to a double representation.
    // TODO: Move this to a utility class or a units class?
    // TODO: Use Apache Commons I/O library to strip the units instead?
    public static final double getPixelDimensionAsDouble( final String pixelDimensionString ) {
        try {
            final Number pixelDimension = DOUBLE_FORMATTER
                    .parse( pixelDimensionString.replaceAll( PIXEL_UNITS_ABBREVIATED, "" ) ); //$NON-NLS-1$
            return pixelDimension.doubleValue();
        }
        catch ( final ParseException pe ) {
            pe.printStackTrace();
        }

        // NOTE: This is a fail-safe in case of any uncaught exceptions.
        return Math.min( PIXEL_DIMENSIONS_HEIGHT_DEFAULT, PIXEL_DIMENSIONS_WIDTH_DEFAULT );
    }

    // Utility method to convert a pixel dimension from a double
    // representation to a string representation with abbreviated units.
    // TODO: Move this to a utility class or a units class?
    public static final String getPixelDimensionAsString( final double pPixelDimension ) {
        final String pixelDimensionString = Double.toString( pPixelDimension )
                + PIXEL_UNITS_ABBREVIATED;
        return pixelDimensionString;
    }

    public static final String[] getPixelDimensionsAsStrings( final double[] pPixelDimensions ) {
        final int numberOfPixelDimensions = pPixelDimensions.length;
        final String[] pixelDimensionsStrings = new String[ numberOfPixelDimensions ];
        for ( int i = 0; i < numberOfPixelDimensions; i++ ) {
            pixelDimensionsStrings[ i ] = getPixelDimensionAsString( pPixelDimensions[ i ] );
        }
        return pixelDimensionsStrings;
    }

    public static final String getPixelHeightDefaultAsString() {
        return getPixelDimensionAsString( PIXEL_DIMENSIONS_HEIGHT_DEFAULT );
    }

    public static final String[] getPixelHeightPresetsAsStrings() {
        final String[] pixelHeightPresetsStrings =
                                                 getPixelDimensionsAsStrings( PIXEL_HEIGHT_PRESETS );
        return pixelHeightPresetsStrings;
    }

    public static final String getPixelWidthDefaultAsString() {
        return getPixelDimensionAsString( PIXEL_DIMENSIONS_WIDTH_DEFAULT );
    }

    public static final String[] getPixelWidthPresetsAsStrings() {
        final String[] pixelWidthPresetsStrings =
                                                getPixelDimensionsAsStrings( PIXEL_WIDTH_PRESETS );
        return pixelWidthPresetsStrings;
    }

    // Cached observable copy of most recent auto-size setting.
    private final BooleanProperty autoSize;

    // Declare an observable dimension that forms our Pixel Dimensions.
    private Dimension2D           pixelDimensions;

    // Default constructor when nothing is known.
    public ImageSize() {
        this( AUTO_SIZE_DEFAULT, PIXEL_DIMENSIONS_WIDTH_DEFAULT, PIXEL_DIMENSIONS_HEIGHT_DEFAULT );
    }

    // Fully specified constructor when everything is known.
    public ImageSize( final boolean pAutoSize, final Dimension2D pPixelDimensions ) {
        this( pAutoSize, pPixelDimensions.getWidth(), pPixelDimensions.getHeight() );
    }

    // Fully specified constructor when everything is known.
    public ImageSize( final boolean pAutoSize,
                      final double pPixelWidth,
                      final double pPixelHeight ) {
        autoSize = new SimpleBooleanProperty( pAutoSize );

        pixelDimensions = new Dimension2D( pPixelWidth, pPixelHeight );
    }

    // Copy constructor.
    public ImageSize( final ImageSize pImageSize ) {
        this( pImageSize.isAutoSize(), pImageSize.getPixelDimensions() );
    }

    public final BooleanProperty autoSizeProperty() {
        return autoSize;
    }

    public final Dimension2D getPixelDimensions() {
        return pixelDimensions;
    }

    public final double getPixelHeight() {
        return pixelDimensions.getHeight();
    }

    public final String getPixelHeightAsString() {
        return getPixelDimensionAsString( getPixelHeight() );
    }

    public final double getPixelWidth() {
        return pixelDimensions.getWidth();
    }

    public final String getPixelWidthAsString() {
        return getPixelDimensionAsString( getPixelWidth() );
    }

    public final boolean isAutoSize() {
        return autoSize.get();
    }

    // Default pseudo-constructor.
    public final void reset() {
        setImageSize( AUTO_SIZE_DEFAULT,
                      PIXEL_DIMENSIONS_WIDTH_DEFAULT,
                      PIXEL_DIMENSIONS_HEIGHT_DEFAULT );
    }

    public final void setAutoSize( final boolean pAutoSize ) {
        autoSize.set( pAutoSize );
    }

    // Fully specified pseudo-constructor.
    public final void setImageSize( final boolean pAutoSize,
                                    final double pPixelWidth,
                                    final double pPixelHeight ) {
        setAutoSize( pAutoSize );

        setPixelDimensions( pPixelWidth, pPixelHeight );
    }

    // Pseudo-copy constructors
    public final void setImageSize( final ImageSize pImageSize ) {
        setImageSize( pImageSize.isAutoSize(),
                      pImageSize.getPixelWidth(),
                      pImageSize.getPixelHeight() );
    }

    public final void setPixelDimensions( final double pPixelWidth, final double pPixelHeight ) {
        pixelDimensions = new Dimension2D( pPixelWidth, pPixelHeight );
    }

}
