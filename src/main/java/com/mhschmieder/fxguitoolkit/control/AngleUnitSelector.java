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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * The Angle Unit selector supports all Angle Units that are currently
 * implemented in our core Math Library.
 */
public final class AngleUnitSelector extends TextSelector {

    // Default Angle Unit, for best "out of box" experience.
    public static final String    ANGLE_UNIT_DEFAULT = AngleUnit.defaultValue().toCanonicalString();

    private static final String[] ANGLE_UNITS        =
                                              new String[] {
                                                             AngleUnit.DEGREES.toCanonicalString(),
                                                             AngleUnit.RADIANS
                                                                     .toCanonicalString() };

    public AngleUnitSelector( final ClientProperties clientProperties,
                              final boolean toolbarContext ) {
        // Always call the superclass constructor first!
        super( clientProperties,
               "Supported Angle Units", //$NON-NLS-1$
               toolbarContext,
               false,
               false,
               ANGLE_UNITS,
               ANGLE_UNIT_DEFAULT );
    }

    public AngleUnit getAngleUnit() {
        return AngleUnit.fromCanonicalString( getTextValue() );
    }

    public void setAngleUnit( final AngleUnit angleUnit ) {
        setTextValue( angleUnit.toCanonicalString() );
    }

}
