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

import org.apache.commons.math3.util.MathUtils;

import com.mhschmieder.fxgraphicstoolkit.input.RotationManager;
import com.mhschmieder.physicstoolkit.AngleUnit;
import com.mhschmieder.physicstoolkit.UnitConversion;

import javafx.scene.chart.ValueAxis;

/**
 * A specialized Tracker Label Group used to manage Mouse Rotation Angle display.
 */
public class MouseRotationTracker extends TrackerLabelGroup {

    /** Cache a local copy of the Rotation Manager to check mouse context. */
    public RotationManager _rotationManager;

    /** Number format cache used for locale-specific angle formatting. */
    public NumberFormat _angleFormat;

    /**
     * Keep track of what Angle Unit we're using to display rotation.
     */
    public AngleUnit _angleUnit;
    
    /** Cache the most recent object angle for single object selection. */
    public double _objectAngle;

    public MouseRotationTracker( final RotationManager rotationManager,
                                 final NumberFormat angleFormat,
                                 final AngleUnit angleUnit,
                                 final ValueAxis< Number > xAxis,
                                 final ValueAxis< Number > yAxis ) {
        // Always call the superclass constructor first!
        super( xAxis, yAxis );
        
        _rotationManager = rotationManager;
        _angleFormat = angleFormat;
        _angleUnit = angleUnit;
        
        _objectAngle = Double.NaN;
    }
    
    @Override
    public String getTrackerLabelText( final double localX,
                                       final double localY ) {
        // Don't assume we are set to degrees as our Angle Unit.
        final String angleUnitSymbol = _angleUnit.toAbbreviatedString();

        // Determine the Total Angle of the current mouse cursor position, and
        // normalized to the ( -180, +180 ) range.
        final double totalAngleRadians = _rotationManager._rotateReference 
                - _rotationManager._rotateTheta;
        final double normalizedAngleRadians = MathUtils
                .normalizeAngle( totalAngleRadians, 0.0d );
        final double normalizedAngle = UnitConversion
                .convertAngle( normalizedAngleRadians, AngleUnit.RADIANS, _angleUnit );
        final String sTotalAngle = _angleFormat.format( normalizedAngle ) + angleUnitSymbol;

        // Determine whether we have a single object or multiple objects 
        // selected, based on whether the downstream client set the object
        // angle to a valid angle before invoking this method. If we have a
        // single object selected, also list the Absolute Angle of that object
        // (modified by the Relative Angle of Rotation).
        String sObjectAngle = "";
        if ( !Double.isNaN(  _objectAngle ) ) {
            sObjectAngle = " (" + _angleFormat.format( _objectAngle )
                    + angleUnitSymbol + " absolute)";
        }

        // Combine the angles into one formatted angle report.
        final String sAngleReport = sTotalAngle + sObjectAngle;
        
        return sAngleReport;
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Cache the new Angle Unit to use for displaying rotation.
        _angleUnit = angleUnit;
    }
    
    /**
     * Sets the object angle to use for modifying the mouse rotation angle.
     * <p>
     * If set to NaN, the object angle is not relevant for the next update
     * to the tracker label, such as when multiple objects are selected.
     * <p>
     * It is critical that this method be called before updating the label!
     * 
     * @param objectAngle The object angle (degrees) to modify relative angle
     */
    public void setObjectAngle( final double objectAngle ) {
        _objectAngle = objectAngle;
    }
}
