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

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This specialized editor is for opacity, in any context.
 */
public final class OpacityEditor extends DoubleEditor {

    // Declare value increment/decrement amount for up and down arrow keys.
    public static final double VALUE_INCREMENT_PERCENT = 0.5d;

    public OpacityEditor( final ClientProperties clientProperties,
                          final String initialText,
                          final String tooltipText,
                          final double opacityMinimum,
                          final double opacityMaximum,
                          final double opacityInitial ) {
        // Always call the superclass constructor first!
        // :NOTE: We use up to one decimal place of precision for displaying
        // opacity, and one decimal place for parsing opacity.
        super( clientProperties,
               initialText,
               tooltipText,
               0,
               1,
               0,
               1,
               opacityMinimum,
               opacityMaximum,
               opacityInitial,
               VALUE_INCREMENT_PERCENT );
    }

}