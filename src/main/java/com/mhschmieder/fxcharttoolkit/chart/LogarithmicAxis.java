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
package com.mhschmieder.fxcharttoolkit.chart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxcharttoolkit.IllegalLogarithmicRangeException;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.ValueAxis;

/**
 * A logarithmic axis implementation for JavaFX 2 charts<br>
 *
 * This class is modified from Kevin's dooApp, part of JRebirth. <br>
 */
public class LogarithmicAxis extends ValueAxis< Number > {

    /**
     * Declare the duration time of the animation in milliseconds.
     */
    // private static final Duration ANIMATION_TIME = Duration.millis( 2000d );

    /**
     * Validate the bounds by throwing an exception if the values are not
     * conform to the mathematics log interval: [0, Double.MAX_VALUE]
     *
     * @param lowerBound
     *            The lower bound of the axis
     * @param upperBound
     *            The upper bound of the axis
     * @throws IllegalLogarithmicRangeException
     *             If out of bounds, throw an exception
     */
    private static void validateBounds( final double lowerBound, final double upperBound )
            throws IllegalLogarithmicRangeException {
        if ( ( lowerBound < 0d ) || ( upperBound < 0d ) || ( lowerBound > upperBound ) ) {
            throw new IllegalLogarithmicRangeException( "The logarithmic range should be limited to [0,Double.MAX_VALUE] and the lower bound should be less than the upper bound" ); //$NON-NLS-1$
        }
    }

    // Declare the chart animator that wraps this chart, and an ID.
    // private Object currentAnimationID;

    // Define properties for the log lower and upper bounds of the axis.
    private DoubleProperty  logUpperBound;
    private DoubleProperty  logLowerBound;

    // For performance reasons, we only want to make this once, at startup.
    protected NumberFormat  _numberFormat;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public LogarithmicAxis( final double lowerBound,
                            final double upperBound,
                            final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( lowerBound, upperBound );

        try {
            // Verify the given range according to the mathematical logarithmic
            // interval definition.
            validateBounds( lowerBound, upperBound );
        }
        catch ( final IllegalLogarithmicRangeException ilre ) {
            ilre.printStackTrace();
        }

        try {
            initAxis( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Default constructor.
    public LogarithmicAxis( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        // NOTE: We use the super-constructor without parameters, which makes
        // the boundaries auto-ranging.
        super();

        try {
            initAxis( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Override
    protected Object autoRange( final double minValue,
                                final double maxValue,
                                final double length,
                                final double labelSize ) {
        final Double[] range = new Double[] { minValue, maxValue };

        return range;
    }

    /**
     * Bind the logarithmic bounds to the super class bounds; considered to be
     * the base 10 logarithmic scale.
     */
    private void bindLogBoundsToDefaultBounds() {
        logLowerBound.bind( new DoubleBinding() {
            {
                super.bind( lowerBoundProperty() );
            }

            @Override
            protected double computeValue() {
                return Math.log10( lowerBoundProperty().get() );
            }
        } );

        logUpperBound.bind( new DoubleBinding() {
            {
                super.bind( upperBoundProperty() );
            }

            @Override
            protected double computeValue() {
                return Math.log10( upperBoundProperty().get() );
            }
        } );
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is used to get the list of minor tick mark positions to
     * display on the axis. This definition is based on the number of minor
     * ticks and the logarithmic formula.
     * <p>
     * NOTE: This calculates minor tick marks for every power of 10.
     */
    @Override
    protected List< Number > calculateMinorTickMarks() {
        final List< Number > minorTickMarksPositions = new ArrayList<>();

        final Number[] range = getRange();
        if ( range != null ) {
            final Number lowerBound = range[ 0 ];
            final Number upperBound = range[ 1 ];
            final double lowerBoundLog10 = Math.log10( lowerBound.doubleValue() );
            final double upperBoundLog10 = Math.log10( upperBound.doubleValue() );

            // NOTE: This refers to the major to minor tick ratio vs. the
            // actual number of minor ticks.
            // NOTE: As we don't want to redundantly display the major ticks on
            // either side of the range as minor ticks, we adjust the count.
            final int minorTickMarkCount = getMinorTickCount();
            final int minorTickMarkCountAdjusted = minorTickMarkCount - 1;

            for ( double i = lowerBoundLog10; i <= upperBoundLog10; i += 1d ) {
                for ( int j = 0; j <= 10; j++ ) {
                    final double tickValue = j * Math.pow( 10d, i );
                    for ( int k = 1; k < minorTickMarkCountAdjusted; k++ ) {
                        final double minorTickMarkPosition = ( k + 1 ) * tickValue;
                        minorTickMarksPositions.add( minorTickMarkPosition );
                    }
                }
            }
        }

        return minorTickMarksPositions;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is used to calculate a list of all the data values for each
     * tick mark in range, represented by the second parameter. The formula is
     * the same as the one for the minor tick marks, but here we want to display
     * one tick for each power of 10.
     * <p>
     * NOTE: This calculates one tick value for every power of 10.
     */
    @Override
    protected List< Number > calculateTickValues( final double length, final Object range ) {
        final List< Number > tickValues = new ArrayList<>();

        if ( range != null ) {
            final Number lowerBound = ( ( Number[] ) range )[ 0 ];
            final Number upperBound = ( ( Number[] ) range )[ 1 ];
            final double lowerBoundLog10 = Math.log10( lowerBound.doubleValue() );
            final double upperBoundLog10 = Math.log10( upperBound.doubleValue() );

            for ( double i = lowerBoundLog10; i <= upperBoundLog10; i += 1d ) {
                for ( int j = 1; j <= 10; j++ ) {
                    final double tickValue = j * Math.pow( 10d, i );
                    tickValues.add( tickValue );
                }
            }
        }

        return tickValues;
    }

    // Perform the matching between the data and the axis.
    @Override
    public double getDisplayPosition( final Number value ) {
        final double delta = logUpperBound.get() - logLowerBound.get();
        final double deltaV = Math.log10( value.doubleValue() ) - logLowerBound.get();
        final double deltaRatio = deltaV / delta;

        return getSide().isVertical() ? ( 1d - deltaRatio ) * getHeight() : deltaRatio * getWidth();
    }

    // This method provides the current range of the axis. A basic
    // implementation is to return an array of the lower bound and upper bound
    // properties defined into the ValueAxis superclass.
    @Override
    protected Number[] getRange() {
        return new Number[] { getLowerBound(), getUpperBound() };
    }

    // This method is only used to convert the number value to a string that
    // will be displayed under the tick mark. Here we use a number formatter, to
    // make sure the label is localized.
    @Override
    protected String getTickMarkLabel( final Number value ) {
        return _numberFormat.format( value );
    }

    // Perform the matching between the axis and the data.
    @Override
    public Number getValueForDisplay( final double displayPosition ) {
        final double delta = logUpperBound.get() - logLowerBound.get();
        return getSide().isVertical()
            ? Math.pow( 10d,
                        ( ( ( displayPosition - getHeight() ) / -getHeight() ) * delta )
                                + logLowerBound.get() )
            : Math.pow( 10d,
                        ( ( ( displayPosition / getWidth() ) * delta ) + logLowerBound.get() ) );
    }

    private final void initAxis( final ClientProperties pClientProperties ) {
        clientProperties = pClientProperties;

        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        _numberFormat.setMinimumIntegerDigits( 1 );
        _numberFormat.setMaximumIntegerDigits( 10 );

        // Do not give these initial values, as we will apply bindings next.
        logUpperBound = new SimpleDoubleProperty();
        logLowerBound = new SimpleDoubleProperty();

        // Bind the properties to the default bounds of the value axis.
        bindLogBoundsToDefaultBounds();
    }

    public void setLogarithmizedUpperBound( final double upperBound ) {
        final double nd = Math.pow( 10d, Math.ceil( Math.log10( upperBound ) ) );
        setUpperBound( nd == upperBound ? nd * 10d : nd );
    }

    /**
     * {@inheritDoc}
     *
     * This method is used to update the range when data are added into the
     * chart. There are two possibilities; the axis is animated or not. The
     * simplest case is to set the lower and upper bound properties directly
     * with the new values.
     *
     * @param range
     *            The number range to use for the axis
     * @param animate
     *            ignored. does not animate.
     */
    @Override
    protected void setRange( final Object range, final boolean animate ) {
        if ( range != null ) {
            final Number lowerBound = ( ( Number[] ) range )[ 0 ];
            final Number upperBound = ( ( Number[] ) range )[ 1 ];
            try {
                validateBounds( lowerBound.doubleValue(), upperBound.doubleValue() );
            }
            catch ( final IllegalLogarithmicRangeException ilre ) {
                ilre.printStackTrace();
            }

            setLowerBound( lowerBound.doubleValue() );
            setUpperBound( upperBound.doubleValue() );

            currentLowerBound.set( lowerBound.doubleValue() );
        }
    }

}