/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.control;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

/**
 * Immutable class holding data for where a user clicked.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class ClickLocation {

    public final double x;
    public final double y;
    public final double sceneX;
    public final double sceneY;
    public final double screenX;
    public final double screenY;
    public final Bounds sourceLayoutBounds;
    public final Bounds sourceBoundsInLocal;
    public final Bounds sourceBoundsInParent;

    /**
     * This is the copy constructor, for when the source click location might be
     * an ongoing reference whose values could change but don't
     * necessarily desire to be propagated to all usage contexts.
     *
     * @param clickLocation
     *            The source Click Location to be copied
     */
    public ClickLocation( final ClickLocation clickLocation ) {
        this( clickLocation.x,
              clickLocation.y,
              clickLocation.sceneX,
              clickLocation.sceneY,
              clickLocation.screenX,
              clickLocation.screenY,
              clickLocation.sourceLayoutBounds,
              clickLocation.sourceBoundsInLocal,
              clickLocation.sourceBoundsInParent );
    }

    /**
     * Requires a JavaFX {@link Node}.
     *
     * @param contextMenuEvent
     *            whose source is a Node
     */
    public ClickLocation( final ContextMenuEvent contextMenuEvent ) {
        this( contextMenuEvent.getX(),
              contextMenuEvent.getY(),
              contextMenuEvent.getSceneX(),
              contextMenuEvent.getSceneY(),
              contextMenuEvent.getScreenX(),
              contextMenuEvent.getScreenY(),
              ( ( Node ) contextMenuEvent.getSource() ).getLayoutBounds(),
              ( ( Node ) contextMenuEvent.getSource() ).getBoundsInLocal(),
              ( ( Node ) contextMenuEvent.getSource() ).getBoundsInParent() );
    }

    @SuppressWarnings("hiding")
    public ClickLocation( final double x,
                          final double y,
                          final double sceneX,
                          final double sceneY,
                          final double screenX,
                          final double screenY,
                          final Bounds sourceLayoutBounds,
                          final Bounds sourceBoundsInLocal,
                          final Bounds sourceBoundsInParent ) {
        this.x = x;
        this.y = y;
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.screenX = screenX;
        this.screenY = screenY;
        this.sourceLayoutBounds = sourceLayoutBounds;
        this.sourceBoundsInLocal = sourceBoundsInLocal;
        this.sourceBoundsInParent = sourceBoundsInParent;
    }

    /**
     * Requires a JavaFX {@link Node}.
     *
     * @param mouseEvent
     *            whose source is a Node
     */
    public ClickLocation( final MouseEvent mouseEvent ) {
        this( mouseEvent.getX(),
              mouseEvent.getY(),
              mouseEvent.getSceneX(),
              mouseEvent.getSceneY(),
              mouseEvent.getScreenX(),
              mouseEvent.getScreenY(),
              ( ( Node ) mouseEvent.getSource() ).getLayoutBounds(),
              ( ( Node ) mouseEvent.getSource() ).getBoundsInLocal(),
              ( ( Node ) mouseEvent.getSource() ).getBoundsInParent() );
    }

}
