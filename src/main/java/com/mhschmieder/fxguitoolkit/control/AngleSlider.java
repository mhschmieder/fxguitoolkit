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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.physics.AngleUnit;

import javafx.geometry.Orientation;

public final class AngleSlider extends NumberSlider {

    // Declare default minimum, maximum, and initial angles for { -180, 180 }.
    public static final double MINIMUM_ANGLE_DEGREES_DEFAULT = -180d;
    public static final double MAXIMUM_ANGLE_DEGREES_DEFAULT = 180d;
    public static final double INITIAL_ANGLE_DEGREES_DEFAULT = 0d;

    // Default tick spacing in degrees.
    public static final double MAJOR_TICK_SPACING_DEGREES    = 60d;
    public static final double MINOR_TICK_SPACING_DEGREES    = 10d;

    // Declare block increment/decrement amount for left and right arrow keys.
    // NOTE: We block to 0.5 degrees as this is a common increment value,
    // though we could make the arrow key increments a user preference.
    public static final double BLOCK_INCREMENT_DEGREES       = 0.5d;

    public AngleSlider( final SessionContext sessionContext, final boolean useContextMenu ) {
        this( sessionContext,
              MINIMUM_ANGLE_DEGREES_DEFAULT,
              MAXIMUM_ANGLE_DEGREES_DEFAULT,
              INITIAL_ANGLE_DEGREES_DEFAULT,
              MAJOR_TICK_SPACING_DEGREES,
              MINOR_TICK_SPACING_DEGREES,
              BLOCK_INCREMENT_DEGREES,
              useContextMenu );
    }

    public AngleSlider( final SessionContext sessionContext,
                        final double minimumAngleDegrees,
                        final double maximumAngleDegrees,
                        final double initialAngleDegrees,
                        final double majorTickSpacingDegrees,
                        final double minorTickSpacingDegrees,
                        final double blockIncrementDegrees,
                        final boolean useContextMenu ) {
        // Always call the superclass constructor first!
        super( sessionContext,
               minimumAngleDegrees,
               maximumAngleDegrees,
               initialAngleDegrees,
               majorTickSpacingDegrees,
               minorTickSpacingDegrees,
               blockIncrementDegrees,
               useContextMenu );

        try {
            initSlider();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public double getClampedValue( final double unclampedValue ) {
        // If the allowed angle range is a full period or more (360+ degrees),
        // then unwrap the angle. Otherwise, apply standard min/max clamping.
        final double clampedValue = ( Math.abs( getMax() - getMin() ) >= 360d )
            ? getUnwrappedAngleDegrees( unclampedValue )
            : Math.min( Math.max( unclampedValue, getMin() ), getMax() );

        return clampedValue;
    }

    public double getUnwrappedAngleDegrees( final double unclampedValue ) {
        // Unwrap the angle based on period, using the established minimum and
        // maximum so that we don't accidentally clamp, but still clamp if the
        // allowed range itself is less than a full period.
        double unwrappedAngleDegrees = unclampedValue;

        while ( unwrappedAngleDegrees < getMin() ) {
            unwrappedAngleDegrees += 360d;
        }

        while ( unwrappedAngleDegrees > getMax() ) {
            unwrappedAngleDegrees -= 360d;
        }

        return unwrappedAngleDegrees;
    }

    private void initSlider() {
        // Angles are generally presented as horizontal bar sliders.
        setOrientation( Orientation.HORIZONTAL );

        // Make sure the tick marks aren't bunched together.
        // NOTE: We can't do this on the base class in case the slider's
        // orientation is vertical, which we only know per context.
        setMinWidth( 220d );

        // Cache the string representation of the angle unit.
        // NOTE: We set this directly as there currently is no unit conversion,
        // for angles, as radians aren't very useful and we haven't added
        // supports for minutes, seconds, etc.
        setMeasurementUnitString( AngleUnit.DEGREES.toPresentationString() );
    }

    public void setAngleDegrees( final double angleDegrees ) {
        // If the allowed angle range is a full period or more (360+ degrees),
        // then unwrap the angle. Otherwise, apply standard min/max clamping.
        final double clampedValue = getClampedValue( angleDegrees );

        setValue( clampedValue );
    }

}
