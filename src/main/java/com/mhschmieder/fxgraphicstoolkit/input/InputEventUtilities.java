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
package com.mhschmieder.fxgraphicstoolkit.input;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

/**
 * This is a utility class for handling events from input devices, such as
 * zooming.
 */
public class InputEventUtilities {

    /**
     * Allow to zoom/scale any node with pivot at scene (x,y) coordinates.
     *
     * @param node
     *            The node to zoom/scale
     * @param zoomFactor
     *            The new zoom factor to apply to the old
     * @param x
     *            The new pre-scaled x-origin of the node
     * @param y
     *            The new pre-scaled y-origin of the node
     */
    public static final void zoom( final Node node,
                                   final double zoomFactor,
                                   final double x,
                                   final double y ) {
        final double oldScale = node.getScaleX();
        double scale = oldScale * zoomFactor;
        if ( scale < 0.05d ) {
            scale = 0.05d;
        }
        if ( scale > 50d ) {
            scale = 50d;
        }
        node.setScaleX( scale );
        node.setScaleY( scale );

        final double scaleRatio = ( scale / oldScale ) - 1d;
        final Bounds bounds = node.localToScene( node.getBoundsInLocal() );
        final double dx = ( x - ( ( 0.5d * bounds.getWidth() ) + bounds.getMinX() ) );
        final double dy = ( y - ( ( 0.5d * bounds.getHeight() ) + bounds.getMinY() ) );

        node.setTranslateX( node.getTranslateX() - ( scaleRatio * dx ) );
        node.setTranslateY( node.getTranslateY() - ( scaleRatio * dy ) );
    }

    public static final void zoom( final Node node, final ScrollEvent event ) {
        final double zoomFactor = Math.pow( 1.003d, event.getDeltaY() );
        final Point2D zoomPosition = new Point2D( event.getSceneX(), event.getSceneY() );
        zoom( node, zoomFactor, zoomPosition.getX(), zoomPosition.getY() );
    }

    public static final void zoom( final Node node, final ZoomEvent event ) {
        final double zoomFactor = event.getZoomFactor();
        final Point2D zoomPosition = new Point2D( event.getSceneX(), event.getSceneY() );
        zoom( node, zoomFactor, zoomPosition.getX(), zoomPosition.getY() );
    }

}
