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

import java.util.Locale;

/**
 * This is an enumeration of Legend Alignments, which are relative terms that
 * refer to how the Legend relates to a Chart or Charts.
 */
public enum LegendAlignment {

    /**
     * Represents that the legend is to be placed above the charts.
     */
    ABOVE,

    /**
     * Represents that the legend is to be placed below the charts.
     */
    BELOW,

    /**
     * Represents that the legend is to be placed between the charts.
     */
    BETWEEN,

    /**
     * Represents that the legend is to be placed to the left of the charts.
     */
    LEFT,

    /**
     * Represents that the legend is to be placed to the right of the charts.
     */
    RIGHT;

    public static LegendAlignment canonicalValueOf( final String canonicalLegendAlignment ) {
        return ( canonicalLegendAlignment != null )
            ? valueOf( canonicalLegendAlignment.toUpperCase( Locale.ENGLISH ) )
            : defaultValue();
    }

    public static LegendAlignment defaultValue() {
        return RIGHT;
    }

    public final String toCanonicalString() {
        return toString().toLowerCase( Locale.ENGLISH );
    }

}
