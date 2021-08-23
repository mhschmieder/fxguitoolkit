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
package com.mhschmieder.fxguitoolkit;

import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

/**
 * {@code BorderFactory} is a static factory class for ensuring a reduction in
 * copy/paste code for border construction and styling that should be maintained
 * consistently across different GUI hierarchies and custom derived classes.
 */
public class BorderFactory {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private BorderFactory() {}

    public static Border makeThinSolidBorder( final Paint stroke,
                                              final CornerRadii cornerRadii ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.THIN );
    }

    public static Border makeMediumSolidBorder( final Paint stroke,
                                                final CornerRadii cornerRadii ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.MEDIUM );
    }

    public static Border makeThickSolidBorder( final Paint stroke,
                                               final CornerRadii cornerRadii ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.THICK );
    }

    public static Border makeSolidBorder( final Paint stroke,
                                          final CornerRadii cornerRadii,
                                          final BorderWidths borderWidths ) {
        return makeBorder(
                stroke,
                BorderStrokeStyle.SOLID,
                cornerRadii,
                borderWidths );
    }

    public static Border makeBorder( final Paint stroke,
                                     final BorderStrokeStyle borderStrokeStyle,
                                     final CornerRadii cornerRadii,
                                     final BorderWidths borderWidths ) {
        final BorderStroke borderStroke = new BorderStroke(
                stroke,
                borderStrokeStyle,
                cornerRadii,
                borderWidths );

        return new Border( borderStroke );
    }

    public static Border makeThinSolidBorder( final Paint stroke,
                                              final CornerRadii cornerRadii,
                                              final Insets buttonInsets ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.THIN,
                buttonInsets );
    }

    public static Border makeMediumSolidBorder( final Paint stroke,
                                                final CornerRadii cornerRadii,
                                                final Insets buttonInsets ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.MEDIUM,
                buttonInsets );
    }

    public static Border makeThickSolidBorder( final Paint stroke,
                                               final CornerRadii cornerRadii,
                                               final Insets buttonInsets ) {
        return makeSolidBorder(
                stroke,
                cornerRadii,
                BorderStroke.THICK,
                buttonInsets );
    }

    public static Border makeSolidBorder( final Paint stroke,
                                          final CornerRadii cornerRadii,
                                          final BorderWidths borderWidths,
                                          final Insets buttonInsets ) {
        return makeBorder(
                stroke,
                BorderStrokeStyle.SOLID,
                cornerRadii,
                borderWidths,
                buttonInsets );
    }

    public static Border makeBorder( final Paint stroke,
                                     final BorderStrokeStyle borderStrokeStyle,
                                     final CornerRadii cornerRadii,
                                     final BorderWidths borderWidths,
                                     final Insets buttonInsets ) {
        final BorderStroke borderStroke = new BorderStroke(
                stroke,
                borderStrokeStyle,
                cornerRadii,
                borderWidths,
                buttonInsets );

        return new Border( borderStroke );
    }

}
