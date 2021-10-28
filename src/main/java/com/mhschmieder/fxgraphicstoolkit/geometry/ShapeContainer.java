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

import com.mhschmieder.commonstoolkit.physics.DistanceUnit;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

/**
 * This interface defines a contract for methods that are common to all Shape
 * Container class hierarchies. This includes forwarding methods that are
 * necessary just so that we can abstract third-party geometry libraries and
 * decouple them from our own.
 */
public interface ShapeContainer {

    ObservableList< Transform > getShapeTransforms();

    /**
     * Sets the scale transform on the overall {@link ShapeContainer shape
     * container} to a uniform scale in this Node's frame of reference.
     *
     * See javafx.scene.transform.Transform.scale() similar method on Transform.
     *
     * @param distanceUnitOld
     *            The old or reference {@link DistanceUnit} to scale from
     * @param distanceUnitNew
     *            The new {@link DistanceUnit} to scale to
     */
    void scale( final DistanceUnit distanceUnitOld, final DistanceUnit distanceUnitNew );

    /**
     * This methods conditionally sets a new foreground color for the
     * graphics.
     *
     * @param foreColor
     *            The desired new foreground stroke color
     * @param forceOverride
     *            Flag for whether to override the current foreground stroke
     *            color even if neither white nor black
     */
    void setForeground( final Color foreColor, final boolean forceOverride );

    /**
     * Sets the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform color in this node's frame of reference.
     *
     * See javafx.scene.shape.Shape.setStroke(paint) similar method on Shape.
     *
     * @param paint
     *            The color of the stroke
     */
    void setStroke( final Paint paint );

    /**
     * Update the Stroke Width on the overall {@link ShapeContainer shape
     * container} to a recalculated scale in this Node's frame of reference.
     *
     * @param distanceUnitReference
     *            The reference Distance Unit to re-scale to
     * @param distanceUnitCurrent
     *            The current Distance Unit to scale Stroke Width from
     * @param strokeWidthBasis
     *            The Stroke Width basis, in current Distance Unit
     * @param strokeWidthRatio
     *            The ratio to apply to the basic Stroke Width
     */
    void updateStrokeWidth( final DistanceUnit distanceUnitReference,
                            final DistanceUnit distanceUnitCurrent,
                            final double strokeWidthBasis,
                            final double strokeWidthRatio );

    /**
     * Updates the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform scale in this node's frame of reference.
     *
     * See javafx.scene.shape.Shape.setStrokeWidth(double) similar method on
     * Shape.
     *
     * @param pStrokeWidth
     *            The width of the stroke, roughly in pixels
     */
    void updateStrokeWidth( final double pStrokeWidth );

}
