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

import com.mhschmieder.fxgraphicstoolkit.input.MouseToolManager;
import com.mhschmieder.fxgraphicstoolkit.input.MouseToolMode;
import com.mhschmieder.jcommons.text.TextUtilities;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.scene.chart.ValueAxis;

import java.text.NumberFormat;

/**
 * A specialized Tracker Label Group used to manage Cursor Coordinates display.
 */
public class CursorCoordinatesTracker extends TrackerLabelGroup {

    /** Flag for determining whether to show cursor coordinates. */
    protected boolean _showCursorCoordinates;

    /** Cache for restoring previous cursor coordinates setting. */
    protected boolean _cachedShowCursorCoordinates;

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

        _showCursorCoordinates = false;
        _cachedShowCursorCoordinates = false;
    }
    
    /**
     * Returns {@code true} if the cursor coordinates are to be drawn.
     *
     * @return {@code true} if the cursor coordinates are to be drawn
     */
    public boolean isShowCursorCoordinates() {
        return _showCursorCoordinates;
    }

    /**
     * Controls whether the cursor coordinates are drawn.
     *
     * @param showCursorCoordinates
     *            If {@code true}, the cursor coordinates are drawn.
     */
    public void setShowCursorCoordinates( final boolean showCursorCoordinates ) {
        _showCursorCoordinates = showCursorCoordinates;
    }

    /**
     * Toggles the cursor coordinates display to on if off, and vice-versa.
     */
    public void toggleShowCursorCoordinates() {
        // Toggle the "Show Cursor Coordinates" state.
        setShowCursorCoordinates( !isShowCursorCoordinates() );
    }

    public void cacheShowCursorCoordinates() {
        _cachedShowCursorCoordinates = _showCursorCoordinates;
    }
    
    public void cacheAndShowCursorCoordinates() {
        // Cache the Show Cursor Coordinates preference, to restore after
        // confirming a Paste action (for example).
        cacheShowCursorCoordinates();

        // Temporarily override "Show Cursor Coordinates" and show the mouse
        // location as the basis of the pasted object set (for example).
        setShowCursorCoordinates( true );
    }

    /**
     * Restores the cached status of whether to show the cursor coordinates.
     */
    public void restoreShowCursorCoordinates() {
        // Restore the global setting for Cursor Coordinates.
        setShowCursorCoordinates( _cachedShowCursorCoordinates );

        // Due to some quirks in mouse handling order, we may have to explicitly
        // hide the associated cursor coordinates node.
        // TODO: Review whether this is still necessary, after other fixes.
        if ( !_cachedShowCursorCoordinates ) {
            setVisible( false );
        }
    }
    
    @Override
    public void updateTrackerLabel( final double displayX,
                                    final double displayY,
                                    final double localX,
                                    final double localY ) {
        // Do not continue unless cursor coordinate tracking is enabled.
        if ( !isShowCursorCoordinates() ) {
            setVisible( false );
            return;
        }
        
        super.updateTrackerLabel( displayX, displayY, localX, localY );
    }
    
    @Override
    public String getTrackerLabelText( final double localX,
                                       final double localY ) {
        // Update the displayed cursor coordinates in the current Distance Unit.
        String sCursorCoordinates = TextUtilities
                .getFormattedQuantityPair( localX,
                                           localY,
                                           _numberFormat,
                                           _distanceUnit.abbreviation() );

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
