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

import com.mhschmieder.commonstoolkit.math.DistanceUnit;
import com.mhschmieder.commonstoolkit.net.SessionContext;

/**
 * The Distance Units selector supports all Distance Units that are currently
 * implemented in our core Math Library.
 */
public class DistanceUnitSelector extends TextSelector {

    public static final String    CHOOSE_ONE               = "Choose One";                               //$NON-NLS-1$

    private static final String[] DISTANCE_UNITS_KNOWN     =
                                                       new String[] {
                                                                      DistanceUnit.METERS
                                                                              .toCanonicalString(),
                                                                      DistanceUnit.CENTIMETERS
                                                                              .toCanonicalString(),
                                                                      DistanceUnit.MILLIMETERS
                                                                              .toCanonicalString(),
                                                                      DistanceUnit.YARDS
                                                                              .toCanonicalString(),
                                                                      DistanceUnit.FEET
                                                                              .toCanonicalString(),
                                                                      DistanceUnit.INCHES
                                                                              .toCanonicalString() };
    private static final String[] DISTANCE_UNITS_AMBIGUOUS =
                                                           new String[] {
                                                                          CHOOSE_ONE,
                                                                          DistanceUnit.METERS
                                                                                  .toCanonicalString(),
                                                                          DistanceUnit.CENTIMETERS
                                                                                  .toCanonicalString(),
                                                                          DistanceUnit.MILLIMETERS
                                                                                  .toCanonicalString(),
                                                                          DistanceUnit.YARDS
                                                                                  .toCanonicalString(),
                                                                          DistanceUnit.FEET
                                                                                  .toCanonicalString(),
                                                                          DistanceUnit.INCHES
                                                                                  .toCanonicalString() };

    public DistanceUnitSelector( final SessionContext sessionContext,
                                 final boolean toolbarContext,
                                 final boolean includeChooseOne,
                                 final DistanceUnit distanceUnit ) {
        // Always call the superclass constructor first!
        super( sessionContext,
               "Supported Distance Units", //$NON-NLS-1$
               toolbarContext,
               false,
               false,
               includeChooseOne ? DISTANCE_UNITS_AMBIGUOUS : DISTANCE_UNITS_KNOWN,
               includeChooseOne ? CHOOSE_ONE : distanceUnit.toCanonicalString() );
    }

    public final DistanceUnit getDistanceUnit() {
        final String distanceUnitString = getTextValue();
        final DistanceUnit distanceUnit = CHOOSE_ONE.equals( distanceUnitString )
            ? DistanceUnit.UNITLESS
            : DistanceUnit.canonicalValueOf( distanceUnitString );
        return distanceUnit;
    }

    public final void setDistanceUnit( final DistanceUnit distanceUnit ) {
        final String distanceUnitString = DistanceUnit.UNITLESS.equals( distanceUnit )
            ? CHOOSE_ONE
            : distanceUnit.toCanonicalString();
        setTextValue( distanceUnitString );
    }

}
