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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import java.text.NumberFormat;

public class AngleConverter extends NumberConverter {

    public AngleConverter( final String measurementUnit,
                           final NumberFormat numberFormat,
                           final double defaultValue,
                           final double minimumValue,
                           final double maximumValue ) {
        // Always call the superclass constructor first!
        super( measurementUnit, numberFormat, defaultValue, minimumValue, maximumValue );
    }

    @Override
    public Number fromString( final String string ) {
        try {
            // Make sure to strip the Measurement Unit label before converting
            // to a number.
            final double editedValue = _numberFormat.parse( string ).doubleValue();

            // Unwrap the angle based on period, using the established minimum
            // and maximum so that we don't accidentally clamp, but still clamp
            // if the allowed range itself is less than a full period.
            double unwrappedAngleDegrees = editedValue;

            while ( unwrappedAngleDegrees < getMinimumValue() ) {
                unwrappedAngleDegrees += 360.0d;
            }

            while ( unwrappedAngleDegrees > getMaximumValue() ) {
                unwrappedAngleDegrees -= 360.0d;
            }

            return unwrappedAngleDegrees;
        }
        catch ( final Exception e ) {
            // This error is a natural result of the user typing garbage, and
            // since they get immediate feedback by replacing with the old value
            // programmatically, we don't care to see the exception stack trace.
            return _defaultValue;
        }
    }

}
