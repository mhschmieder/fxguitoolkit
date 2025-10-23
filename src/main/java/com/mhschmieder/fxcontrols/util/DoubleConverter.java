/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
package com.mhschmieder.fxcontrols.util;

import javafx.util.StringConverter;
import org.apache.commons.math3.util.FastMath;

import java.text.NumberFormat;

/**
 * For some reason, the Core JavaFX API doesn't include basic type-specific
 * implementations of string converters, so I provide a couple on this toolkit.
 * <p>
 * These can be especially useful with FX Charts when setting converters for
 * axis tick labels and the like -- especially if needing integers vs. doubles.
 * <p>
 * Other uses are more typical of JavaFX Controls contexts, for textField syncing.
 */
public class DoubleConverter extends StringConverter< Number > {

    // Maintain a reference to the Measurement Unit label (can be blank).
    protected String       _measurementUnit;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat _numberFormat;

    // Cache the default data value for when there is no valid input.
    protected double       _defaultValue;

    // Cache the minimum allowed data value (negative).
    protected double       _minimumValue;

    // Cache the maximum allowed data value (positive).
    protected double       _maximumValue;

    public DoubleConverter( final String measurementUnit,
                            final NumberFormat numberFormat,
                            final double defaultValue,
                            final double minimumValue,
                            final double maximumValue ) {
        // Always call the superclass constructor first!
        super();

        _measurementUnit = measurementUnit;
        _numberFormat = numberFormat;
        _defaultValue = defaultValue;
        _minimumValue = minimumValue;
        _maximumValue = maximumValue;
    }

    @Override
    public String toString( final Number number ) {
        String presentationValue = number.toString();

        try {
            presentationValue = _numberFormat.format( number ) + _measurementUnit;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return presentationValue;
    }

    // NOTE: make sure to strip the Measurement Unit label before converting to
    // a number.
    @Override
    public Number fromString( final String string ) {
        try {
            double editedValue = _numberFormat.parse( string ).doubleValue();

            // If limits were established, enforce them. Always check though, to
            // avoid overflow and underflow conditions.
            editedValue = FastMath.max( editedValue, _minimumValue );
            editedValue = FastMath.min( editedValue, _maximumValue );

            return editedValue;
        }
        catch ( final Exception e ) {
            // This error is a natural result of the user typing garbage, and
            // since they get immediate feedback by replacing with the old value
            // programmatically, we don't care to see the exception stack trace.
            return _defaultValue;
        }
    }

    public double getDefaultValue() {
        return _defaultValue;
    }

    public double getMinimumValue() {
        return _minimumValue;
    }

    public double getMaximumValue() {
        return _maximumValue;
    }

    public String getMeasurementUnit() {
        return _measurementUnit;
    }

    public NumberFormat getNumberFormat() {
        return _numberFormat;
    }

    public void setDefaultValue( final double defaultValue ) {
        _defaultValue = defaultValue;
    }

    public void setMinimumValue( final double minimumValue ) {
        _minimumValue = minimumValue;
    }

    public void setMaximumValue( final double maximumValue ) {
        _maximumValue = maximumValue;
    }

    public void setMeasurementUnit( final String measurementUnit ) {
        _measurementUnit = measurementUnit;
    }

    public void setNumberFormat( final NumberFormat numberFormat ) {
        _numberFormat = numberFormat;
    }
}
