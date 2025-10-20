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

import com.mhschmieder.fxgraphicstoolkit.geometry.GeometryUtilities;
import com.mhschmieder.fxgraphicstoolkit.input.ClickLocation;
import com.mhschmieder.fxgraphicstoolkit.input.MouseToolManager;
import com.mhschmieder.physicstoolkit.DistanceUnit;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.ValueAxis;

import java.text.NumberFormat;

/**
 * A further specialization of the Cursor Coordinates Tracker to deal with
 * Cartesian Space. As there are application contexts where Cartesian Space is
 * not in effect, such as geo applications using lat/lon with projections, this
 * additional functionality is modeled in a derived subclass when needed.
 */
public class CartesianCursorCoordinatesTracker extends CursorCoordinatesTracker {

    /**
     * Declare cursor coordinates for general use, in model units (meters).
     */
    public Point2D _cursorCoordinatesMeters;

    public CartesianCursorCoordinatesTracker( final MouseToolManager mouseToolManager,
                                              final NumberFormat numberFormat,
                                              final DistanceUnit distanceUnit,
                                              final ValueAxis< Number > xAxis,
                                              final ValueAxis< Number > yAxis ) {
        // Always call the superclass constructor first!
        super( mouseToolManager,
               numberFormat,
               distanceUnit,
               xAxis,
               yAxis );

        _cursorCoordinatesMeters = new Point2D( 0.0d, 0.0d );
    }

    /**
     * Update the cursor coordinates, presented using the current Distance Unit.
     *
     * @param cursorCoordinatesPixels
     *            Cursor location in pixels, from the top of the Cartesian Chart
     */
    public void updateCursorCoordinatesLabel( final ClickLocation cursorCoordinatesPixels ) {
        // Cache the cursor coordinates for reference by Cut/Copy/Paste.
        _cursorCoordinatesMeters = convertDisplayCoordinatesToMeters( 
                cursorCoordinatesPixels, true );

        // Convert the cursor coordinates from display units (pixels) to the
        // current User Preference for Distance Unit.
        final Point2D cursorCoordinatesLocal = convertDisplayCoordinatesToLocalCoordinates( 
                cursorCoordinatesPixels, true );

        updateTrackerLabel( 
                cursorCoordinatesPixels.x,
                cursorCoordinatesPixels.y,
                cursorCoordinatesLocal.getX(),
                cursorCoordinatesLocal.getY() );
    }

    /**
     * Convert a click location in display units (pixels) to a Point2D in model
     * space units (Meters), by first converting to whatever Distance Unit is in
     * use, and then from there to Meters.
     * <p>
     * Note that drag deltas should just flip the y coordinate as it isn't a
     * point in space but a delta to be added to another point, so we pass a
     * flag for whether to use the layout bounds or not.
     *
     * @param cursorCoordinatesPixels
     *            The supplied click location, in display units (pixels)
     * @param useLayoutBounds
     *            Flag for whether to use the layout bounds or not
     * @return The converted coordinates as a Point2D geometry object
     */
    public Point2D convertDisplayCoordinatesToMeters( final ClickLocation cursorCoordinatesPixels,
                                                      final boolean useLayoutBounds ) {
        return convertDisplayCoordinatesToMeters( cursorCoordinatesPixels.x,
                                                  cursorCoordinatesPixels.y,
                                                  cursorCoordinatesPixels.sourceLayoutBounds,
                                                  useLayoutBounds );
    }

    /**
     * Convert (x, y) coordinates in display units (pixels) to a Point2D in
     * model space units (Meters), by first converting to whatever Distance Unit
     * is in use, and then from there to Meters.
     * <p>
     * Note that drag deltas should just flip the y coordinate as it isn't a
     * point in space but a delta to be added to another point, so we pass a
     * flag for whether to use the layout bounds or not.
     *
     * @param cursorXPixels
     *            The x-coordinate to be converted from pixels to Meters
     * @param cursorYPixels
     *            The y-coordinate to be converted from pixels to Meters
     * @param layoutBounds
     *            The layout bounds for the source node
     * @param useLayoutBounds
     *            Flag for whether to use the layout bounds or not
     * @return The converted coordinates as a Point2D geometry object
     */
    public Point2D convertDisplayCoordinatesToMeters( final double cursorXPixels,
                                                      final double cursorYPixels,
                                                      final Bounds layoutBounds,
                                                      final boolean useLayoutBounds ) {
        // The display to venue transform is in user units vs. stored units, so
        // we convert to Meters from whatever Distance Unit is currently in use.
        final Point2D point = convertDisplayCoordinatesToLocalCoordinates( cursorXPixels,
                                                                           cursorYPixels,
                                                                           layoutBounds,
                                                                           useLayoutBounds );
        final Point2D pointMeters = GeometryUtilities
                .getPointInMeters( point.getX(), point.getY(), _distanceUnit );

        // Return the transformed coordinates as a new Point2D instance.
        return pointMeters;
    }

   /**
     * Convert a click location in display units (pixels) to a Point2D in local
     * units (current user preference for Distance Unit).
     * <p>
     * Note that drag deltas should just flip the y coordinate as it isn't a
     * point in space but a delta to be added to another point, so we pass a
     * flag for whether to use the layout bounds or not.
     *
     * @param cursorCoordinatesPixels
     *            The supplied click location, in display units (pixels)
     * @param useLayoutBounds
     *            Flag for whether to use the layout bounds or not
     * @return The converted coordinates as a Point2D geometry object
     */
    public Point2D convertDisplayCoordinatesToLocalCoordinates( final ClickLocation cursorCoordinatesPixels,
                                                                final boolean useLayoutBounds ) {
        return convertDisplayCoordinatesToLocalCoordinates( 
                cursorCoordinatesPixels.x,
                cursorCoordinatesPixels.y,
                cursorCoordinatesPixels.sourceLayoutBounds,
                useLayoutBounds );
    }

    /**
     * Convert (x, y) coordinates in display units (pixels) to a Point2D in
     * local units (current user preference for Distance Unit).
     * <p>
     * Note that drag deltas should just flip the y coordinate as it isn't a
     * point in space but a delta to be added to another point, so we pass a
     * flag for whether to use the layout bounds or not.
     *
     * @param cursorXPixels
     *            The x-coordinate to be converted from pixels to venue units
     * @param cursorYPixels
     *            The y-coordinate to be converted from pixels to venue units
     * @param layoutBounds
     *            The layout bounds for the source node
     * @param useLayoutBounds
     *            Flag for whether to use the layout bounds or not
     * @return The converted coordinates as a Point2D geometry object
     */
    public Point2D convertDisplayCoordinatesToLocalCoordinates( final double cursorXPixels,
                                                                final double cursorYPixels,
                                                                final Bounds layoutBounds,
                                                                final boolean useLayoutBounds ) {
        // Use the layout bounds as the width and height for scale ratios.
        final double boundsHeight = layoutBounds.getHeight();
        final double boundsWidth = layoutBounds.getWidth();

        // Measure the offsets from the bottom.
        final double xOffsetPixels = cursorXPixels;
        final double yOffsetPixels = useLayoutBounds
            ? ( boundsHeight - cursorYPixels )
            : -cursorYPixels;

        // Pre-cache all of the chart range values, in venue coordinates.
        final double xLowerBound = _xAxis.getLowerBound();
        final double xUpperBound = _xAxis.getUpperBound();
        final double yLowerBound = _yAxis.getLowerBound();
        final double yUpperBound = _yAxis.getUpperBound();

        // Determine the best scale factor based on relative Aspect ratios.
        double scale = 1.0d;
        if ( boundsWidth > boundsHeight ) {
            // Width is greater.
            final double xRange = xUpperBound - xLowerBound;
            scale = xRange / boundsWidth;
        }
        else {
            // Height is greater.
            final double yRange = yUpperBound - yLowerBound;
            scale = yRange / boundsHeight;
        }

        // Convert the point from pixels to venue coordinates, relative to the
        // origin (lower left corner, or bottom).
        final double pointX = useLayoutBounds
            ? ( xLowerBound + ( xOffsetPixels * scale ) )
            : ( xOffsetPixels * scale );
        final double pointY = useLayoutBounds
            ? ( yLowerBound + ( yOffsetPixels * scale ) )
            : ( yOffsetPixels * scale );
        final Point2D point = new Point2D( pointX, pointY );

        // Convert the given coordinates from display units (pixels) to current
        // Distance Units, accounting also for the different y-axis direction.
        // NOTE: This is the wrong transformation, which we used until
        //  discovering it wasn't sensitive to non-zero origins. It is kept for
        //  reference as the logical order may be important towards refactoring
        //  the new code into a simple single transform such as what we do here.
        // final Transform displayToLocalTransform =
        // _chartOverlayGroup.getDisplayToLocalTransform();
        // final double fromBottom = _cartesianContent.getHeight() - yPx;
        // final Point2D point = displayToLocalTransform.transform( xPx,
        // -fromBottom );

        // TODO: Review whether containment by zoomCurrent is the exclusion
        //  criteria we wish to use.
        // 2016Jan22: containment test disabled. currently this method is only
        // called due to mouse events on the Cartesian Content Pane, which
        // encompasses the entire Cartesian grid in view. zoomCurrent should
        // also encompass the entire visible grid, so this containment test
        // should be superfluous.
        // if ( contains( zoomCurrent, point, true ) ) {
        return point;
        // }
    }
}
