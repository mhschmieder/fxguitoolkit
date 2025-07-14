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

import java.text.NumberFormat;

import com.mhschmieder.commonstoolkit.text.TextUtilities;
import com.mhschmieder.fxgraphicstoolkit.input.MouseToolManager;
import com.mhschmieder.fxgraphicstoolkit.input.MouseToolMode;
import com.mhschmieder.physicstoolkit.DistanceUnit;

import javafx.scene.chart.ValueAxis;

/**
 * A specialized Tracker Label Group used to manage Cursor Coordinates display.
 */
public class CursorCoordinatesTracker extends TrackerLabelGroup {

    /** Cache a local copy of the Mouse Tool Manager to check mouse context. */
    public MouseToolManager _mouseToolManager;

    /** Number format cache used for locale-specific number formatting. */
    public NumberFormat _numberFormat;

    /**
     * Keep track of what Distance Unit we're using to display coordinates.
     */
    public DistanceUnit _distanceUnit;

    public CursorCoordinatesTracker( final MouseToolManager mouseToolManager,
                                     final NumberFormat numberFormat,
                                     final DistanceUnit distanceUnit,
                                     final ValueAxis< Number > xAxis,
                                     final ValueAxis< Number > yAxis ) {
        // Always call the superclass constructor first!
        super( xAxis, yAxis );
        
        _mouseToolManager = mouseToolManager;
        _numberFormat = numberFormat;
        _distanceUnit = distanceUnit;
    }
    
    @Override
    public String getTrackerLabelText( final double localX,
                                       final double localY ) {
        // Update the displayed cursor coordinates in the current Distance Unit.
        String sCursorCoordinates = TextUtilities
                .getFormattedQuantityPair( localX,
                                           localY,
                                           _numberFormat,
                                           _distanceUnit.toAbbreviatedString() );

        // If in Paste Confirmation Mode, prepend a special context message.
        if ( MouseToolMode.PASTE.equals( _mouseToolManager._mouseMode ) ) {
            sCursorCoordinates = "Click to confirm paste coordinates:\n"
                    + sCursorCoordinates;
        }
        
        return sCursorCoordinates;
    }
    
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Cache the new Distance Unit to use for displaying coordinates.
        _distanceUnit = distanceUnit;
    }
}
