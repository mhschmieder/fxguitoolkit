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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.css.converters.SizeConverter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
// import javafx.css.converter.SizeConverter;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.scene.chart.ValueAxis;
import javafx.util.StringConverter;

/**
 * This is a specialization of a standard Number Axis, in order to customize the
 * major tick generation to be based from zero in both directions vs. based on
 * the lowest value on the axis (which might be negative).
 */
public class CartesianAxis extends ValueAxis< Number > {

    /**
     * Default number formatter for NumberAxis, this stays in sync with
     * auto-ranging and formats values appropriately.
     * <p>
     * You can wrap this formatter to add prefixes or suffixes;
     *
     * @since JavaFX 2.0
     */
    public static class DefaultFormatter extends StringConverter< Number > {
        private final NumberFormat formatter;
        private String             prefix = null;
        private String             suffix = null;

        /**
         * Construct a DefaultFormatter for the tick marks.
         *
         * @param pFormatter
         *            The Number Formatter to use as the basis
         */
        public DefaultFormatter( final NumberFormat pFormatter ) {
            this( pFormatter, null, null );
        }

        /**
         * Construct a DefaultFormatter for the tick marks, with a prefix and/or
         * suffix.
         * <p>
         * TODO: Switch to a DecimalFormat for better pattern-based
         * localizations.
         *
         * @param pFormatter
         *            The Number Formatter to use as the basis
         * @param pPrefix
         *            The prefix to append to the start of formatted number, can
         *            be null if not needed
         * @param pSuffix
         *            The suffix to append to the end of formatted number, can
         *            be null if not needed
         */
        public DefaultFormatter( final NumberFormat pFormatter,
                                 final String pPrefix,
                                 final String pSuffix ) {
            formatter = pFormatter;
            prefix = pPrefix;
            suffix = pSuffix;
        }

        /**
         * Converts the string provided into a Number defined by the this
         * converter.
         * <p>
         * Format of the string and type of the resulting object is defined by
         * this converter.
         *
         * @return a Number representation of the string passed in.
         * @see StringConverter#toString
         */
        @Override
        public Number fromString( final String pString ) {
            try {
                final int prefixLength = ( prefix == null ) ? 0 : prefix.length();
                final int suffixLength = ( suffix == null ) ? 0 : suffix.length();
                return formatter.parse( pString.substring( prefixLength,
                                                           pString.length() - suffixLength ) );
            }
            catch ( final ParseException e ) {
                return null;
            }
        }

        /**
         * Converts the number provided into its string form.
         * <p>
         * Format of the returned string is defined by this converter.
         *
         * @return a string representation of the object passed in.
         * @see StringConverter#toString
         */
        @Override
        public String toString( final Number pNumber ) {
            return toString( pNumber, formatter );
        }

        protected String toString( final Number pNumber, final NumberFormat pFormatter ) {
            // Make sure the axis tick labels use the preferred number
            // resolution.
            if ( ( prefix != null ) && ( suffix != null ) ) {
                return prefix + pFormatter.format( pNumber ) + suffix;
            }
            else if ( prefix != null ) {
                return prefix + pFormatter.format( pNumber );
            }
            else if ( suffix != null ) {
                return pFormatter.format( pNumber ) + suffix;
            }
            else {
                return pFormatter.format( pNumber );
            }
        }

        protected String toString( final Number pNumber, final String pNumFormatter ) {
            if ( ( pNumFormatter == null ) || pNumFormatter.isEmpty() ) {
                return toString( pNumber, formatter );
            }
            return toString( pNumber, new DecimalFormat( pNumFormatter ) );
        }
    }

    /**
     * TODO: Find a non-private way to do this, as it breaks in JavaFX 11.
     */
    protected static class StyleableProperties {
        @SuppressWarnings({
                            "nls",
                            "unchecked" }) protected static final CssMetaData< CartesianAxis, Number >        TICK_UNIT =
                                                                                                                        new CssMetaData< CartesianAxis, Number >( "-fx-tick-unit",
                                                                                                                                                                  SizeConverter
                                                                                                                                                                          .getInstance(),
                                                                                                                                                                  5d ) {

                                                                                                                                                                                                                                            @Override
                                                                                                                                                                                                                                            public StyleableProperty< Number > getStyleableProperty( final CartesianAxis n ) {
                                                                                                                                                                                                                                                return ( StyleableProperty< Number > ) n
                                                                                                                                                                                                                                                        .tickUnitProperty();
                                                                                                                                                                                                                                            }

                                                                                                                                                                                                                                            @Override
                                                                                                                                                                                                                                            public boolean isSettable( final CartesianAxis n ) {
                                                                                                                                                                                                                                                return ( n.tickUnit == null )
                                                                                                                                                                                                                                                        || !n.tickUnit
                                                                                                                                                                                                                                                                .isBound();
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        };

        protected static final List< CssMetaData< ? extends Styleable, ? > >                                  STYLEABLES;
        static {
            final List< CssMetaData< ? extends Styleable, ? > > styleables =
                                                                           new ArrayList<>( ValueAxis
                                                                                   .getClassCssMetaData() );
            styleables.add( TICK_UNIT );
            STYLEABLES = Collections.unmodifiableList( styleables );
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     *         CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
    public static List< CssMetaData< ? extends Styleable, ? > > getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    // Cache the unfiltered tick values, to use for Grid Lines.
    private List< Number >           _unfilteredTickValues;

    /**
     * The value between each major tick mark in data units. This is
     * automatically set if we are auto-ranging.
     * <p>
     * NOTE: We do not allow auto-ranging of Cartesian Axes.
     */
    protected final DoubleProperty   tickUnit;

    protected final StringProperty   currentFormatterProperty;

    protected final DefaultFormatter defaultFormatter;

    /**
     * Create a non-auto-ranging CartesianAxis with the given upper and lower
     * bound.
     *
     * @param pAxisLabel
     *            The name to display for this axis
     * @param pLowerBound
     *            The lower bound for this axis, i.e. min plottable value
     * @param pUpperBound
     *            The upper bound for this axis, i.e. max plottable value
     * @param pTickUnit
     *            The tick unit, i.e. space between tick marks
     * @param pMinorTickCount
     *            The number of minor tick divisions to display between each
     *            major tick mark
     * @param pAxisSide
     *            The side of the hose chart to show the axis on
     * @param pTickLabelFormat
     *            The number format to use for the tick mark labels
     */
    @SuppressWarnings("nls")
    public CartesianAxis( final String pAxisLabel,
                          final double pLowerBound,
                          final double pUpperBound,
                          final double pTickUnit,
                          final int pMinorTickCount,
                          final Side pAxisSide,
                          final NumberFormat pTickLabelFormat ) {
        // Always call the superclass constructor first!
        super( pLowerBound, pUpperBound );

        // Can't auto-range as there are no actual data points to plot.
        setAutoRanging( false );

        setLabel( pAxisLabel );

        final boolean minorTicksVisible = pMinorTickCount > 1;

        setTickMarkVisible( true );
        setTickLabelsVisible( true );
        setMinorTickCount( pMinorTickCount );
        setMinorTickVisible( minorTicksVisible );

        setSide( pAxisSide );

        tickUnit = new StyleableDoubleProperty( 6d ) {
            @Override
            public Object getBean() {
                return CartesianAxis.this;
            }

            @Override
            public CssMetaData< CartesianAxis, Number > getCssMetaData() {
                return StyleableProperties.TICK_UNIT;
            }

            @Override
            public String getName() {
                return "tickUnit";
            }

            @Override
            protected void invalidated() {
                invalidateRange();
                requestAxisLayout();
            }
        };

        currentFormatterProperty = new SimpleStringProperty( this, "currentFormatter", "" );

        defaultFormatter = new DefaultFormatter( pTickLabelFormat );
        setTickLabelFormatter( defaultFormatter );

        _unfilteredTickValues = new ArrayList<>( 0 );
    }

    /**
     * Calculate a list of the data values for every minor tick mark
     *
     * @return List of data values where to draw minor tick marks
     */
    @Override
    protected List< Number > calculateMinorTickMarks() {
        final List< Number > minorTickMarks = new ArrayList<>();
        final double lowerBound = getLowerBound();
        final double upperBound = getUpperBound();
        final double tickUnitValue = getTickUnit();
        final double minorUnit = tickUnitValue / Math.max( 1, getMinorTickCount() );
        if ( tickUnitValue > 0 ) {
            if ( ( ( upperBound - lowerBound ) / minorUnit ) > 30000 ) {
                // This is a ridiculous amount of minor tick marks; something
                // has probably gone wrong.
                System.err
                        .println( "Warning: we tried to create more than 30000 minor tick marks on a NumberAxis. " //$NON-NLS-1$
                                + "Lower Bound=" + getLowerBound() + ", Upper Bound=" //$NON-NLS-1$ //$NON-NLS-2$
                                + getUpperBound() + ", Tick Unit=" + tickUnitValue ); //$NON-NLS-1$
                return minorTickMarks;
            }
            final boolean tickUnitIsInteger = Math.rint( tickUnitValue ) == tickUnitValue;
            if ( tickUnitIsInteger ) {
                double minor = Math.floor( lowerBound ) + minorUnit;
                final int count = ( int ) Math
                        .ceil( ( Math.ceil( lowerBound ) - minor ) / minorUnit );
                for ( int i = 0; ( minor < Math.ceil( lowerBound ) )
                        && ( i < count ); minor += minorUnit, i++ ) {
                    if ( minor > lowerBound ) {
                        minorTickMarks.add( minor );
                    }
                }
            }
            double major = tickUnitIsInteger ? Math.ceil( lowerBound ) : lowerBound;
            final int count = ( int ) Math.ceil( ( upperBound - major ) / tickUnitValue );
            for ( int i = 0; ( major < upperBound ) && ( i < count ); major +=
                                                                            tickUnitValue, i++ ) {
                final double next = Math.min( major + tickUnitValue, upperBound );
                double minor = major + minorUnit;
                final int minorCount = ( int ) Math.ceil( ( next - minor ) / minorUnit );
                for ( int j = 0; ( minor < next ) && ( j < minorCount ); minor += minorUnit, j++ ) {
                    minorTickMarks.add( minor );
                }
            }
        }

        return minorTickMarks;
    }

    /**
     * Calculate a list of all the data values for each tick mark in range.
     *
     * @param length
     *            The length of the axis in display units
     * @param range
     *            A range object returned from autoRange()
     * @return A list of tick marks that fit along the axis if it was the given
     *         length
     */
    @Override
    protected List< Number > calculateTickValues( final double length, final Object range ) {
        final Object[] rangeProps = ( Object[] ) range;
        final double lowerBound = ( Double ) rangeProps[ 0 ];
        final double upperBound = ( Double ) rangeProps[ 1 ];
        final double tickUnitValue = ( Double ) rangeProps[ 2 ];
        final List< Number > tickValues = new ArrayList<>();

        // Clear the unfiltered tick values, to start over.
        _unfilteredTickValues.clear();

        if ( lowerBound == upperBound ) {
            tickValues.add( lowerBound );
            _unfilteredTickValues.add( lowerBound );
        }
        else if ( tickUnitValue <= 0 ) {
            tickValues.add( lowerBound );
            _unfilteredTickValues.add( lowerBound );

            tickValues.add( upperBound );
            _unfilteredTickValues.add( upperBound );
        }
        else if ( tickUnitValue > 0 ) {
            // Unconditionally add the lower bound of each axis.
            tickValues.add( lowerBound );
            _unfilteredTickValues.add( lowerBound );

            if ( ( ( upperBound - lowerBound ) / tickUnitValue ) > 5000 ) {
                // This is a ridiculous amount of major tick marks; something
                // has probably gone wrong.
                System.err
                        .println( "Warning: we tried to create more than 5000 major tick marks on a NumberAxis. " //$NON-NLS-1$
                                + "Lower Bound=" + lowerBound + ", Upper Bound=" + upperBound //$NON-NLS-1$ //$NON-NLS-2$
                                + ", Tick Unit=" + tickUnit ); //$NON-NLS-1$
            }
            else {
                if ( ( lowerBound + tickUnitValue ) < upperBound ) {
                    // If the origin is inside the range, start with it and work
                    // outwards. Otherwise, use Oracle's original algorithm.
                    if ( ( lowerBound < 0d ) && ( upperBound > 0d ) ) {
                        // Detect the x-axis, as it requires additional logic.
                        final Side axisSide = getSide();
                        final boolean isXAxis = axisSide.isHorizontal();

                        // Mark the lower and upper bounds if integers (and if
                        // for the x-axis), so that we can avoid unnecessarily
                        // eliminating the next-to-lowest and next-to-highest
                        // x-axis tick marks and labels (and their associated
                        // grid lines).
                        final boolean lowerBoundIsInteger = Math.rint( lowerBound ) == lowerBound;
                        final boolean upperBoundIsInteger = Math.rint( upperBound ) == upperBound;

                        // Add the negative tick marks below the origin first,
                        // in numerical order as Oracle throws some of them out
                        // if they are in reverse order.
                        // NOTE: We skip the next-to-final tick mark before the
                        // lower bound, as things get too crowded otherwise and
                        // the labels can overlap and become illegible. Oracle's
                        // layoutChildren() method in the parent Axis class
                        // forgets to check for edge conditions at the lower
                        // bounds, when it decides whether to skip a label.
                        // NOTE: We instead compute a fudge factor that amounts
                        // to the same thing but is sensitive to the actual
                        // available pixels, thus being less punitive overall.
                        // NOTE: This fudge factor is mostly necessary for the
                        // x-axis, which means the top or the bottom side, and
                        // needs to account for the lowest value label likely
                        // being wider due to being a non-even divisor and thus
                        // using more decimal places than the other tick labels.
                        // NOTE: We can trivially reject cases where there is
                        // plenty of room due to the pixel distances between
                        // major ticks being quite large (for the x-axis).
                        // NOTE: We try to figure out the best fudge factor
                        // based on the number of digits, and noting that a
                        // non-integer lower bound will add a negative sign, a
                        // decimal point, and one more digit.
                        final double axisLength = isXAxis ? getWidth() : getHeight();
                        final double scale =
                                           calculateNewScale( axisLength, lowerBound, upperBound );
                        final double majorTickDistancePixels = scale * tickUnitValue;
                        final double yAxisRatio = majorTickDistancePixels <= 20d ? 0.45d : 0.2d;

                        // NOTE: The x-axis needs to leave room for the minus
                        // sign, on the negative side of the origin.
                        double nominalBound = Math.abs( Math.rint( lowerBound ) );
                        double xAxisRatio = nominalBound >= 1000d
                            ? majorTickDistancePixels <= 48d ? 1.9d : 0.5d
                            : nominalBound >= 100d
                                ? majorTickDistancePixels <= 40d ? 1.5d : 0.5d
                                : nominalBound >= 10d
                                    ? majorTickDistancePixels <= 32d ? 1.2d : 0.5d
                                    : majorTickDistancePixels <= 24d ? 0.9d : 0.5d;
                        double fudgeFactor = isXAxis
                            ? xAxisRatio * tickUnitValue
                            : yAxisRatio * tickUnitValue;

                        // Add the ordered negative tick marks below the origin.
                        // NOTE: The lower bound has already been added.
                        int tickCount = ( int ) Math.ceil( Math.abs( lowerBound ) / tickUnitValue )
                                - 1;
                        double tickValue = -tickUnitValue * tickCount;
                        for ( int i = 0; i < tickCount; tickValue += tickUnitValue, i++ ) {
                            if ( ( isXAxis && lowerBoundIsInteger )
                                    || ( tickValue >= ( lowerBound + fudgeFactor ) ) ) {
                                if ( !tickValues.contains( tickValue ) ) {
                                    tickValues.add( tickValue );
                                }
                            }

                            if ( !_unfilteredTickValues.contains( tickValue ) ) {
                                _unfilteredTickValues.add( tickValue );
                            }
                        }

                        // Make sure the origin is unconditionally added, unless
                        // there is only one negative tick before it, this is
                        // the x-axis, and there's not enough room to show it.
                        // NOTE: The fudge factor is smaller here, as it only
                        // takes one character to represent zero as an integer.
                        tickValue = 0d;
                        if ( !isXAxis || ( tickValues.size() > 1 )
                                || ( tickValue >= ( lowerBound + ( 0.5d * fudgeFactor ) ) ) ) {
                            if ( !tickValues.contains( tickValue ) ) {
                                tickValues.add( tickValue );
                            }
                        }

                        if ( !_unfilteredTickValues.contains( tickValue ) ) {
                            _unfilteredTickValues.add( tickValue );
                        }

                        // NOTE: The x-axis doesn't need to leave room for the
                        // minus sign, on the positive side of the origin.
                        nominalBound = Math.abs( Math.rint( upperBound ) );
                        xAxisRatio = nominalBound >= 1000d
                            ? majorTickDistancePixels <= 40d ? 1.95d : 0.55d
                            : nominalBound >= 100d
                                ? majorTickDistancePixels <= 32d ? 1.55d : 0.55d
                                : nominalBound >= 10d
                                    ? majorTickDistancePixels <= 24d ? 1.25d : 0.55d
                                    : majorTickDistancePixels <= 16d ? 0.95d : 0.55d;
                        fudgeFactor = isXAxis
                            ? xAxisRatio * tickUnitValue
                            : yAxisRatio * tickUnitValue;

                        // Add the ordered positive tick marks above the origin.
                        // NOTE: The upper bound gets added at the end.
                        tickCount = ( int ) Math.ceil( Math.abs( upperBound ) / tickUnitValue ) - 1;
                        tickValue = tickUnitValue;
                        for ( int i = 0; i < tickCount; tickValue += tickUnitValue, i++ ) {
                            if ( ( isXAxis && upperBoundIsInteger )
                                    || ( tickValue <= ( upperBound - fudgeFactor ) ) ) {
                                if ( !tickValues.contains( tickValue ) ) {
                                    tickValues.add( tickValue );
                                }
                            }

                            if ( !_unfilteredTickValues.contains( tickValue ) ) {
                                _unfilteredTickValues.add( tickValue );
                            }
                        }
                    }
                    else {
                        // If tickUnitValue is integer, start with the nearest
                        // integer.
                        double tickValue = Math.rint( tickUnitValue ) == tickUnitValue
                            ? Math.ceil( lowerBound )
                            : lowerBound + tickUnitValue;
                        final int tickCount = ( int ) Math
                                .ceil( ( upperBound - tickValue ) / tickUnitValue );
                        for ( int i = 0; ( tickValue <= upperBound )
                                && ( i < tickCount ); tickValue += tickUnitValue, i++ ) {
                            if ( !tickValues.contains( tickValue ) ) {
                                tickValues.add( tickValue );
                            }

                            if ( !_unfilteredTickValues.contains( tickValue ) ) {
                                _unfilteredTickValues.add( tickValue );
                            }
                        }
                    }
                }
            }

            tickValues.add( upperBound );
            _unfilteredTickValues.add( upperBound );
        }

        // Unconditionally add the upper bound of each axis.
        return tickValues;
    }

    /**
     * {@inheritDoc}
     *
     * @since JavaFX 8.0
     */
    @Override
    public List< CssMetaData< ? extends Styleable, ? > > getCssMetaData() {
        return getClassCssMetaData();
    }

    /**
     * Called to get the current axis range.
     *
     * @return A range object that can be passed to setRange() and
     *         calculateTickValues()
     */
    @Override
    protected Object getRange() {
        return new Object[] {
                              getLowerBound(),
                              getUpperBound(),
                              getTickUnit(),
                              getScale(),
                              currentFormatterProperty.get() };
    }

    /**
     * Get the string label name for a tick mark with the given value
     *
     * @param value
     *            The value to format into a tick label string
     * @return A formatted string for the given value
     */
    @Override
    protected String getTickMarkLabel( final Number value ) {
        StringConverter< Number > formatter = getTickLabelFormatter();
        if ( formatter == null ) {
            formatter = defaultFormatter;
        }
        return formatter.toString( value );
    }

    public final double getTickUnit() {
        return tickUnit.get();
    }

    public List< Number > getUnfilteredTickValues() {
        return _unfilteredTickValues;
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the
     * font that is set for the tick marks
     *
     * @param value
     *            tick mark value
     * @param rotation
     *            The text rotation
     * @param numFormatter
     *            The number formatter
     * @return size of tick mark label for given value
     */
    private Dimension2D measureTickMarkSize( final Number value,
                                             final double rotation,
                                             final String numFormatter ) {
        String labelText;
        StringConverter< Number > formatter = getTickLabelFormatter();
        if ( formatter == null ) {
            formatter = defaultFormatter;
        }
        if ( formatter instanceof DefaultFormatter ) {
            labelText = ( ( DefaultFormatter ) formatter ).toString( value, numFormatter );
        }
        else {
            labelText = formatter.toString( value );
        }
        return measureTickMarkLabelSize( labelText, rotation );
    }

    /**
     * Measure the size of the label for given tick mark value. This uses the
     * font that is set for the tick marks
     *
     * @param value
     *            tick mark value
     * @param range
     *            range to use during calculations
     * @return size of tick mark label for given value
     */
    @Override
    protected Dimension2D measureTickMarkSize( final Number value, final Object range ) {
        final Object[] rangeProps = ( Object[] ) range;
        final String formatter = ( String ) rangeProps[ 4 ];
        return measureTickMarkSize( value, getTickLabelRotation(), formatter );
    }

    /**
     * Called to set the current axis range to the given range. If isAnimating()
     * is true then this method should animate the range to the new range.
     * <p>
     * NOTE: We do not allow animation in Cartesian Axes and related charts.
     *
     * @param range
     *            A range object returned from autoRange()
     * @param animate
     *            If true animate the change in range
     */
    @Override
    protected void setRange( final Object range, final boolean animate ) {
        final Object[] rangeProps = ( Object[] ) range;
        final double lowerBound = ( Double ) rangeProps[ 0 ];
        final double upperBound = ( Double ) rangeProps[ 1 ];
        final double tickUnitValue = ( Double ) rangeProps[ 2 ];
        final double scale = ( Double ) rangeProps[ 3 ];
        final String formatter = ( String ) rangeProps[ 4 ];
        currentFormatterProperty.set( formatter );
        setLowerBound( lowerBound );
        setUpperBound( upperBound );
        setTickUnit( tickUnitValue );
        currentLowerBound.set( lowerBound );
        setScale( scale );
    }

    public final void setTickUnit( final double value ) {
        tickUnit.set( value );
    }

    public final DoubleProperty tickUnitProperty() {
        return tickUnit;
    }

}
