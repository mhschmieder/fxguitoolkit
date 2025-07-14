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

import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;

import javafx.scene.Group;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * A simple Group used to host a Label associated with Cursor-based Tracking.
 * <p>
 * The data to display is not assumed here; this is a parent class for common
 * shared behavior. There are specific subclasses for Cursor Coordinates, etc.
 */
public abstract class TrackerLabelGroup extends Group {
    
    /** A Label to display coordinates or other data of interest at the cursor. */
    public Label _trackerLabel;

    /** Cache a local copy of the x axis so we can perform bounds containment. */
    protected ValueAxis< Number > _xAxis;

    /** Cache a local copy of the y axis so we can perform bounds containment. */
    protected ValueAxis< Number > _yAxis;

    public TrackerLabelGroup( final ValueAxis< Number > xAxis,
                              final ValueAxis< Number > yAxis ) {
        _xAxis = xAxis;
        _yAxis = yAxis;
        
        // Make the volatile tracker label, which is usually hidden.
        _trackerLabel = new Label();

        // In order to improve placement control, we add the tracker label
        // to this Group, and hide it initially so that no cycles are wasted
        // computing a container for non-displayed labels.
        getChildren().add( _trackerLabel );
        setVisible( false );
    }
    
    public void setForegroundFromBackground( final Color backColor ) {
        // Make sure the tracker label is visible against the new Background 
        // Color, but only change Black and White vs. other Colors.
        final Color foreColor = ColorUtilities.getForegroundFromBackground( backColor );

        // Even though transparent, the tracker must ensure that its displayed
        // annotation label is visible against any background.
        _trackerLabel.setTextFill( foreColor );
    }

    /**
     * Update the cursor coordinates, presented using the current Distance Unit.
     *
     * @param displayX
     *            The x-coordinate of the mouse location in display units
     *            (pixels), from the top of the Cartesian Chart host
     * @param displayY
     *            The y-coordinate of the mouse location in display units
     *            (pixels), from the top of the Cartesian Chart host
     * @param localX
     *            The x-coordinate of the mouse location in the local units
     *            (User Preference for Distance Unit)
     * @param localY
     *            The y-coordinate of the mouse location in the local units
     *            (User Preference for Distance Unit)
     */
    public void updateTrackerLabel( final double displayX,
                                    final double displayY,
                                    final double localX,
                                    final double localY ) {
        // Do not update the Data Tracker if we are outside the axis bounds.
        if ( ( localX < _xAxis.getLowerBound() ) 
                || ( localX > _xAxis.getUpperBound() )
                || ( localY < _yAxis.getLowerBound() )
                || ( localY > _yAxis.getUpperBound() ) ) {
            setVisible( false );
            return;
        }
        
        _trackerLabel.setText( getTrackerLabelText( localX, localY ) );

        // If necessary (to avoid clipping), relocate the tracker label.
        relocateTrackerLabel( displayX, displayY );
        
        // If the tracker label is not currently showing, make it visible.
        if ( !_trackerLabel.isVisible() ) {
            _trackerLabel.setVisible( true );
        }
    }
    
    public abstract String getTrackerLabelText( final double localX,
                                                final double localY );
    
    public void relocateTrackerLabel( final double displayX,
                                      final double displayY ) {
        // Keep the labels from clipping and from preventing the annotation
        // display from reaching the right edge or bottom edge of the chart, by
        // placing them to the left and/or above the cursor if they would clip,
        // and to the right and/or below the cursor otherwise.
        final double rightEdgePx = _xAxis.getDisplayPosition( _xAxis.getUpperBound() );
        final boolean beyondRightEdge = ( displayX + 12.0d
                + _trackerLabel.getWidth() ) > rightEdgePx;
        final double labelX = beyondRightEdge
            ? displayX - 12.0d - _trackerLabel.getWidth()
            : displayX + 12.0d;
        final double bottomEdgePx = _yAxis.getDisplayPosition( _yAxis.getLowerBound() );
        final boolean beyondBottomEdge = ( displayY + 12.0d
                + _trackerLabel.getHeight() ) > bottomEdgePx;
        final double labelY = beyondBottomEdge
            ? displayY - 12.0d - _trackerLabel.getHeight()
            : displayY + 12.0d;
        
        _trackerLabel.relocate( labelX, labelY );
    }
}
