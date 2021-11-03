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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.net.ClientProperties;

/**
 * {@code SecondsEditor} is a specialized editor for entering the seconds part
 * of a DMS formatted angle.
 * <p>
 * TODO: Replace class inheritance with a refinement on the factory method.
 */
public class SecondsEditor extends IntegerEditor {

    // Declare value increment/decrement amount for up and down arrow keys.
    // NOTE: We increment by 1 second as we are using DMS as separate values.
    public static final int VALUE_INCREMENT_SECONDS = 1;

    public SecondsEditor( final ClientProperties clientProperties, final String tooltipText ) {
        // Always call the superclass constructor first!
        super( clientProperties,
               "0", //$NON-NLS-1$
               tooltipText,
               0,
               59,
               0,
               VALUE_INCREMENT_SECONDS );

        setMeasurementUnitString( "\"" ); //$NON-NLS-1$
    }

}
