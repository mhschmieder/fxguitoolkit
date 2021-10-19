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
package com.mhschmieder.fxgraphicstoolkit.geometry;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 * This bounds subclass keeps an explicit reference to the min and max values
 * passed in, because Bounds itself is not in terms of width and height, and
 * keeping separate points is awkward. The top-to-bottom sense is flipped.
 */
public final class MinMaxBounds extends BoundingBox {

    public final Point2D _min;
    public final Point2D _max;

    public MinMaxBounds( final Bounds bounds ) {
        this( new Point2D( bounds.getMinX(), bounds.getMinY() ),
              new Point2D( bounds.getMaxX(), bounds.getMaxY() ) );
    }

    public MinMaxBounds( final Point2D min, final Point2D max ) {
        super( min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY() );

        _min = min;
        _max = max;
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( !super.equals( obj ) ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final MinMaxBounds other = ( MinMaxBounds ) obj;
        if ( _max == null ) {
            if ( other._max != null ) {
                return false;
            }
        }
        else if ( !_max.equals( other._max ) ) {
            return false;
        }
        if ( _min == null ) {
            if ( other._min != null ) {
                return false;
            }
        }
        else if ( !_min.equals( other._min ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ( prime * result ) + ( ( _max == null ) ? 0 : _max.hashCode() );
        result = ( prime * result ) + ( ( _min == null ) ? 0 : _min.hashCode() );
        return result;
    }

}