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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxgraphicstoolkit.paint;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

// TODO: Move this to a new FxGraphicsToolkit library from FxGuiToolkit.
public final class ColorUtilities {

    // NOTE: The constructor is disabled, since this is a static class.
    private ColorUtilities() {}

    /**
     * This method conditionally adjusts the stroke of a given shape, to
     * guarantee contrast. As this may be called from within tight loops, the
     * new stroke is generated outside the method, but the algorithm is
     * generic enough to not care whether the new stroke is dark or light.
     * <p>
     * NOTE: There is an assumption that the new stroke will be either White or
     * Black. If that assumption proves too restrictive, we will need to modify
     * the algorithm for more elaborate comparisons of old and new strokes.
     * <p>
     * NOTE: This is not strictly true, but is for the most part, as the only
     * current exceptions are for marking special objects and also Layer Locked
     * status, none of which ever need to invert for contrast purposes.
     *
     * @param shape
     *            The Shape that is the target for the new Stroke
     * @param stroke
     *            The new Stroke to use if conditions are met for change
     * @param forceOverride
     *            Flag for whether to override the current foreground stroke
     *            color even if neither white nor black
     * @param exclusionColor
     *            Color to exclude from the forced override
     */
    public static void adjustStrokeForContrast( final Shape shape,
                                                final Paint stroke,
                                                final boolean forceOverride,
                                                final Color exclusionColor ) {
        // We (conditionally) only care about the extreme Black and White
        // settings that are potentially invisible against the new Foreground
        // Color, which we compare after that initial filtering.
        final Paint currentStroke = shape.getStroke();
        if ( ( Color.WHITE.equals( currentStroke ) || Color.BLACK.equals( currentStroke )
                || ( forceOverride && ( exclusionColor != null )
                        && !exclusionColor.equals( currentStroke ) ) )
                && !stroke.equals( currentStroke ) ) {
            shape.setStroke( stroke );
        }
    }

    // Get an AWT color converted from JavaFX, including alpha opacity.
    public static java.awt.Color getColor( final Color fxColor ) {
        if ( fxColor == null ) {
            return null;
        }

        final java.awt.Color awtColor = new java.awt.Color( ( float ) fxColor.getRed(),
                                                            ( float ) fxColor.getGreen(),
                                                            ( float ) fxColor.getBlue(),
                                                            ( float ) fxColor.getOpacity() );

        return awtColor;
    }

    // Get a JavaFX color converted from AWT, including alpha opacity.
    public static Color getColor( final java.awt.Color awtColor ) {
        if ( awtColor == null ) {
            return null;
        }

        final Color fxColor = Color.rgb( awtColor.getRed(),
                                         awtColor.getGreen(),
                                         awtColor.getBlue(),
                                         awtColor.getAlpha() / 255d );

        return fxColor;
    }

    /**
     * Return the appropriate foreground color based on whether the background
     * color is considered dark or light. If light, return black. If dark,
     * return white. This improves legibility for all contrast levels.
     *
     * @param backColor
     *            The graphics background {@link Color}.
     * @return The appropriate foreground {@link Color} (black or white)
     */
    public static Color getForegroundFromBackground( final Color backColor ) {
        return isColorDark( backColor ) ? Color.WHITE : Color.BLACK;
    }

    // TODO: Search for the JavaFX CSS source code that analyzes HSB vs. RGB.
    public static boolean isColorDark( final Color color ) {
        // Use HSB Analysis to find the perceived Brightness of the supplied
        // color. We introduce a small fudge factor for floating-point
        // imprecision and rounding, equating roughly to 51% as the cutoff for
        // Dark vs. Light, so that 50% Gray (aka Mid-Gray) will trigger a white
        // foreground. This makes for better contrast and is easier on the eyes
        // than a black foreground, in such cases.
        return color.getBrightness() <= 0.51d;
    }

}
