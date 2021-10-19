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
package com.mhschmieder.fxgraphicstoolkit.graphics;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

/**
 * This is a utility class for highlighting utilities and methods.
 */
public class HighlightUtilities {

    public static final void applyHighlight( final Node node,
                                             final boolean highlightOn,
                                             final List< Double > highlightDashPattern ) {
        final Shape shape = ( Shape ) node;
        final ObservableList< Double > strokeDashArray = shape.getStrokeDashArray();
        if ( highlightOn ) {
            strokeDashArray.setAll( highlightDashPattern );
        }
        else {
            strokeDashArray.clear();
        }
    }

    public static final List< Double > getHighlightDashPattern( final double scaleFactor ) {
        // Define and return the dash pattern to use for highlighting.
        final List< Double > highlightDashPattern = new ArrayList<>();
        highlightDashPattern.add( Double.valueOf( 2d * scaleFactor ) );
        highlightDashPattern.add( Double.valueOf( 4d * scaleFactor ) );

        return highlightDashPattern;
    }

}
