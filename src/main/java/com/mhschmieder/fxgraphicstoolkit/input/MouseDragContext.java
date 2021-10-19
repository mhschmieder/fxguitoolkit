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

/**
 * This class stores contextual information and settings for Mouse Drag and
 * related handling.
 * <p>
 * TODO: Add the ALT key detection etc., and change to more general name?
 * Possibly add the drag box and its handling as well?
 */
public class MouseDragContext {

    public double  _dragDestinationX;
    public double  _dragDestinationY;
    public double  _dragOriginX;
    public double  _dragOriginY;
    public boolean _valid;

    public MouseDragContext() {
        _dragDestinationX = 0d;
        _dragDestinationY = 0d;
        _dragOriginX = 0d;
        _dragOriginY = 0d;
        _valid = false;
    }

    // This is the initializer when interacting with Drag events.
    // NOTE: The first Drag Event is thrown out, so we zero the destination.
    // This ensures that the deltas stay scaled properly on each succession.
    public void initializeDrag( final double firstX, final double firstY ) {
        _dragDestinationX = 0d;
        _dragDestinationY = 0d;
        _dragOriginX = firstX;
        _dragOriginY = firstY;
        _valid = true;
    }

    // This is the initializer when interacting with Move events.
    // :NOTE: It is most likely the source will be set to the destination in
    // such cases, as the first Move Event is not thrown out as with Drag Events
    // and otherwise would result in an overly large initial delta computation.
    public void initializeMove( final double firstX,
                                final double firstY,
                                final double lastX,
                                final double lastY ) {
        _dragDestinationX = lastX;
        _dragDestinationY = lastY;
        _dragOriginX = firstX;
        _dragOriginY = firstY;
        _valid = true;
    }

    public void invalidate() {
        _valid = false;
    }

    @Override
    public String toString() {
        return "MouseDragContext[lastX:" + _dragDestinationX + ", lastY:" + _dragDestinationY //$NON-NLS-1$ //$NON-NLS-2$
                + ",\n\tfirstX:" + _dragOriginX + ", firstY:" + _dragOriginY //$NON-NLS-1$ //$NON-NLS-2$
                + ", valid:" + _valid + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
