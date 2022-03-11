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
package com.mhschmieder.fxguitoolkit.stage;

import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This is a container class for collections of windows of various types and
 * classifications, used to enable macro-like operations on multiple windows so
 * as to avoid cut/paste errors and oversights. Primarily this will be used for
 * secondary windows (including pop-ups) that are owned by other windows.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class FxWindowManager {

    /**
     * Finds the screen where the Stage is and returns true if some parts of the
     * stage are out of the screen, and therefore not visible.
     *
     * @param stage
     *            The Stage to check for screen visibility
     * @return A flag for whether or not this Stage would be clipped or hidden
     *         due to appearing partially or fully on screens that are no longer
     *         available, plugged in, or turned on
     */
    public static boolean isStageOutOfBounds( final Stage stage ) {
        // Get the full list of screens that fully or partially contain the
        // provided Stage.
        final double width = stage.getWidth();
        final double height = stage.getHeight();
        final double minX = stage.getX();
        final double minY = stage.getY();
        final List< Screen > screenList =
                                        Screen.getScreensForRectangle( minX, minY, width, height );

        // If the Stage is not even partially visible on any of the screens, it
        // is considered completely out of bounds.
        if ( screenList.size() < 1 ) {
            return true;
        }

        final double maxX = minX + width;
        final double maxY = minY + height;

        boolean upperLeftOutOfBounds = true;
        boolean upperRightOutOfBounds = true;
        boolean lowerLeftOutOfBounds = true;
        boolean lowerRightOutOfBounds = true;

        // Cycle through all of the candidate screens to make sure each of the
        // four corners of the provided Stage are within the bounds of one of
        // them, as otherwise we have partial clipping of the Stage.
        for ( final Screen screen : screenList ) {
            final Rectangle2D bounds = screen.getVisualBounds();

            if ( bounds.contains( minX, minY ) ) {
                upperLeftOutOfBounds = false;
            }

            if ( bounds.contains( maxX, minY ) ) {
                upperRightOutOfBounds = false;
            }

            if ( bounds.contains( minX, maxY ) ) {
                lowerLeftOutOfBounds = false;
            }

            if ( bounds.contains( maxX, maxY ) ) {
                lowerRightOutOfBounds = false;
            }
        }

        final boolean stageOutOfBounds = upperLeftOutOfBounds || upperRightOutOfBounds
                || lowerLeftOutOfBounds || lowerRightOutOfBounds;

        return stageOutOfBounds;
    }

}
