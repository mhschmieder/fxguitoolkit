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
package com.mhschmieder.fxguitoolkit;

import javafx.scene.paint.Color;

/**
 * {@code ForegroundManager} is an interface that establishes the contract for
 * what methods an implementing class must provide in order to properly manage
 * the foreground of components such that the background does not mask text,
 * outlines, and other graphics that is drawn on the canvas.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public interface ForegroundManager {

    /**
     * Sets the appropriate foreground color for a {@code Component} based on
     * the specified background color.
     * <p>
     * Both the background and the foreground are applied to the entire layout
     * hierarchy, with the foreground color chosen to provide adequate contrast
     * against the background for text rendering as well as for line graphics.
     * <p>
     * This method should be overridden and called as the first line in the
     * method override, before adding support for GUI elements unique to the
     * derived class hierarchy.
     *
     * @param backColor
     *            The current background color to apply to this component
     *
     * @since 1.0
     */
    void setForegroundFromBackground( final Color backColor );

}
