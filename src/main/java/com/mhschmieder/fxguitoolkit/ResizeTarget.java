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
package com.mhschmieder.fxguitoolkit;

/**
 * {@code ResizeTarget} enumerates the full set of possible targets for resizing
 * a window or other component, including "none" for when there is no resizing
 * target (such as when moving instead). This terminology is more appropriate to
 * the context than the commonly used terms "Resize Mode" and "Resize
 * Direction".
 */
public enum ResizeTarget {
    NONE, TOP, TOP_LEFT, LEFT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT, RIGHT, TOP_RIGHT;

    public static ResizeTarget detectResizeTarget( final double yMin,
                                                   final double xMin,
                                                   final double yMax,
                                                   final double xMax,
                                                   final double resizeMarginTop,
                                                   final double resizeMarginRight,
                                                   final double resizeMarginBottom,
                                                   final double resizeMarginLeft ) {
        final boolean isTopSideResize = yMin <= resizeMarginTop;
        final boolean isLeftSideResize = xMin <= resizeMarginLeft;
        final boolean isBottomSideResize = yMax <= resizeMarginBottom;
        final boolean isRightSideResize = xMax <= resizeMarginRight;

        final boolean isTopLeftCornerResize = isTopSideResize && isLeftSideResize;
        final boolean isBottomLeftCornerResize = isBottomSideResize && isLeftSideResize;
        final boolean isBottomRightCornerResize = isBottomSideResize && isRightSideResize;
        final boolean isTopRightCornerResize = isTopSideResize && isRightSideResize;

        // Prioritize the four corner cases so we can then drop down
        // confidently to the simpler single-side cases.
        final ResizeTarget resizeTarget = isTopLeftCornerResize
            ? TOP_LEFT
            : isBottomLeftCornerResize
                ? BOTTOM_LEFT
                : isBottomRightCornerResize
                    ? BOTTOM_RIGHT
                    : isTopRightCornerResize
                        ? TOP_RIGHT
                        : isTopSideResize
                            ? TOP
                            : isLeftSideResize
                                ? LEFT
                                : isBottomSideResize ? BOTTOM : isRightSideResize ? RIGHT : NONE;

        return resizeTarget;
    }

}
