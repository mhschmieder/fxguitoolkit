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
package com.mhschmieder.fxguitoolkit.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * {@code LayoutFactory} is a static factory class for ensuring a reduction in
 * copy/paste code for layout construction and styling that should be maintained
 * consistently across different GUI hierarchies and custom derived classes.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class LayoutFactory {

    /**
     * This method serves merely as a sanity check that the Maven integration
     * and builds work properly and also behave correctly inside Eclipse IDE. It
     * will likely get removed once I gain more confidence that I have solved
     * the well-known issues with Maven inside Eclipse as I move on to more
     * complex projects with dependencies (this project is quite simple and has
     * no dependencies at this time, until more functionality is added).
     *
     * @param args
     *            The command-line arguments for executing this class as the
     *            main entry point for an application
     *
     * @since 1.0
     */
    public static void main( final String[] args ) {
        System.out.println( "Hello Maven from FxGuiToolkit!" ); //$NON-NLS-1$
    }

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private LayoutFactory() {}

    /**
     * Returns a {@link Background} object designed to be passed to
     * {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent background style for use
     * on all {@code Region} derived nodes, using a supplied background fill
     * and no corner radii or insets (for a flush fill against the parent).
     *
     * @param fill
     *            The desired background {@link Paint} for the {@code Region}
     * @return A {@link Background} object designed to be passed to
     *         {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Paint fill ) {
        return makeRegionBackground( fill, Insets.EMPTY );
    }

    /**
     * Returns a {@link Background} object designed to be passed to
     * {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent background style for use
     * on all {@code Region} derived nodes, using a supplied background fill
     * and no corner radii or insets (for a flush fill against the parent).
     *
     * @param fill
     *            The desired background {@link Paint} for the {@code Region}
     * @param radii
     *            The corner radii for the {@code Region}
     *            The {@link Insets} to use for the background
     * @return A {@link Background} object designed to be passed to
     *         {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Paint fill, final CornerRadii radii ) {
        final BackgroundFill backgroundFill = new BackgroundFill( fill, radii, Insets.EMPTY );

        return new Background( backgroundFill );
    }

    /**
     * Returns a {@link Background} object designed to be passed to
     * {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent background style for use
     * on all {@code Region} derived nodes, using a supplied background fill
     * and no corner radii or insets (for a flush fill against the parent).
     *
     * @param fill
     *            The desired background {@link Paint} for the {@code Region}
     * @param insets
     *            The {@link Insets} to use for the background
     * @return A {@link Background} object designed to be passed to
     *         {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Paint fill, final Insets insets ) {
        return makeRegionBackground( fill, CornerRadii.EMPTY, insets );
    }

    /**
     * Returns a {@link Background} object designed to be passed to
     * {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent background style for use
     * on all {@code Region} derived nodes, using a supplied background fill
     * and no corner radii or insets (for a flush fill against the parent).
     *
     * @param fill
     *            The desired background {@link Paint} for the {@code Region}
     * @param radii
     *            The corner radii for the {@code Region}
     * @param insets
     *            The {@link Insets} to use for the background
     * @return A {@link Background} object designed to be passed to
     *         {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Paint fill,
                                                   final CornerRadii radii,
                                                   final Insets insets ) {
        final BackgroundFill backgroundFill = new BackgroundFill( fill, radii, insets );

        return new Background( backgroundFill );
    }

    /**
     * Returns a linear gradient {@link Background} object designed to be passed
     * to {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent linear gradient style for
     * use on all {@code Region} derived nodes, using a supplied background
     * color,
     * gradient color, and no corner radii or insets (for a flush fill against
     * the
     * parent).
     * <p>
     * NOTE: Even if this is applied after setting a CSS style using Java calls
     * vs. CSS stylesheet loading, it is overruled bny any background color set
     * in a Java setStyle() invocation.
     *
     * @param backColor
     *            The desired background {@link Color} for the {@code Region}
     * @param gradientColor
     *            The desired gradient {@link Color} for the {@code Region}
     * @return A linear gradient {@link Background} object designed to be passed
     *         to {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Color backColor,
                                                   final Color gradientColor ) {
        final Stop[] stops = new Stop[] {
                                          new Stop( 0.0d, backColor ),
                                          new Stop( 1.0d, gradientColor ) };
        final LinearGradient gradient = new LinearGradient( 0.0d,
                                                            0.0d,
                                                            0.0d,
                                                            1.0d,
                                                            true,
                                                            CycleMethod.NO_CYCLE,
                                                            stops );

        return makeRegionBackground( gradient );
    }

    /**
     * Returns a linear gradient {@link Background} object designed to be passed
     * to {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent linear gradient style for
     * use on all {@code Region} derived nodes, using a supplied background
     * color,
     * gradient color, and no corner radii or insets (for a flush fill against
     * the
     * parent).
     * <p>
     * NOTE: Even if this is applied after setting a CSS style using Java calls
     * vs. CSS stylesheet loading, it is overruled bny any background color set
     * in a Java setStyle() invocation.
     *
     * @param backColor
     *            The desired background {@link Color} for the {@code Region}
     * @param gradientColor
     *            The desired gradient {@link Color} for the {@code Region}
     * @param radii
     *            The corner radii for the {@code Region}
     * @param insets
     *            The {@link Insets} to use for the background
     * @return A linear gradient {@link Background} object designed to be passed
     *         to {@code Region.setBackground()}
     */
    public static Background makeRegionBackground( final Color backColor,
                                                   final Color gradientColor,
                                                   final CornerRadii radii,
                                                   final Insets insets ) {
        final Stop[] stops = new Stop[] {
                                          new Stop( 0.0d, backColor ),
                                          new Stop( 1.0d, gradientColor ) };
        final LinearGradient gradient = new LinearGradient( 0.0d,
                                                            0.0d,
                                                            0.0d,
                                                            1.0d,
                                                            true,
                                                            CycleMethod.NO_CYCLE,
                                                            stops );

        return makeRegionBackground( gradient, radii, insets );
    }

    /**
     * Returns a {@link Background} object designed to be passed to
     * {@code Region.setBackground()}
     * <p>
     * This method is designed to produce a consistent background style for use
     * on all {@code Region} derived nodes, using a supplied background color
     * and no corner radii or insets (for a flush fill against the parent).
     *
     * @param backColor
     *            The desired background {@link Color} for the {@code Region}
     * @return A {@link Background} object designed to be passed to
     *         {@code Region.setBackground()}
     *
     * @version 1.0
     */
    public static Background makeRegionBackground( final Color backColor ) {
        final BackgroundFill backgroundFill = new BackgroundFill( backColor,
                                                                  CornerRadii.EMPTY,
                                                                  Insets.EMPTY );
        final Background background = new Background( backgroundFill );

        return background;
    }

    /**
     * Returns a {@link GridPane} with no initial contents, but with most layout
     * styling initialized to match the passed parameters.
     *
     * @param alignment
     *            The alignment of the grid within its width and height
     * @param padding
     *            The top, left, bottom, and right padding around the region's
     *            content
     * @param hgap
     *            The width of the horizontal gaps between columns
     * @param vgap
     *            The height of the vertical gaps between rows
     * @return A {@link GridPane} with no initial contents, but with most layout
     *         styling initialized to match the passed parameters
     *
     * @version 1.0
     */
    public static GridPane makeGridPane( final Pos alignment,
                                         final Insets padding,
                                         final double hgap,
                                         final double vgap ) {
        final GridPane gridPane = new GridPane();

        gridPane.setAlignment( alignment );

        gridPane.setHgap( hgap );
        gridPane.setVgap( vgap );

        gridPane.setPadding( padding );

        return gridPane;
    }

    public static HBox makeCenteredLabeledHBox( final Labeled... labeled ) {
        // Host the Labeled in an HBox so we can center it properly.
        final HBox centeredLabeledHBox = new HBox( 24d );
        centeredLabeledHBox.getChildren().addAll( labeled );
        centeredLabeledHBox.setAlignment( Pos.CENTER );

        return centeredLabeledHBox;
    }

}
