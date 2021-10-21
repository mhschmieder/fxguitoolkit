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
package com.mhschmieder.fxgraphicstoolkit.geometry;

import java.util.List;

import com.mhschmieder.commonstoolkit.math.DistanceUnit;
import com.mhschmieder.commonstoolkit.physics.UnitConversion;
import com.mhschmieder.fxgraphicstoolkit.graphics.HighlightUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

/**
 * This container allows for encapsulation of multiple JavaFX Shapes for a
 * {@link Group}, for nested grouping and to avoid passing multiple JavaFX
 * Shapes to the {@link Group} base class.
 */
public class ShapeGroup extends Group implements ShapeContainer {

    /** Stroke width, to be applied to the entire JavaFX Shapes group. */
    protected final DoubleProperty strokeWidth;

    public ShapeGroup() {
        // Always call the super-constructor first!
        super();

        strokeWidth = new SimpleDoubleProperty( 1d );
    }

    public final void addShape( final double strokeScale, final Shape newChild ) {
        addShape( newChild );

        // NOTE: Centered stroke is default, but better safe than sorry, as
        // outside stroke can crash the application if shape is non-manifold.
        newChild.setStrokeType( StrokeType.CENTERED );
    }

    public final void addShape( final Shape newChild ) {
        getChildren().add( newChild );

        // Make sure that any changes to stroke width are inherited.
        newChild.strokeWidthProperty().bind( strokeWidthProperty() );
    }

    public final void addShapeGroup( final ShapeGroup newChild ) {
        getChildren().add( newChild );

        // Make sure that any changes to stroke width are inherited.
        newChild.strokeWidthProperty().bind( strokeWidthProperty() );
    }

    public final void addShapes( final double strokeScale, final List< Shape > newChildren ) {
        getChildren().addAll( newChildren );

        newChildren.forEach( newChild -> {
            // Make sure that any changes to stroke width are inherited.
            newChild.strokeWidthProperty().bind( strokeWidthProperty().multiply( strokeScale ) );

            // NOTE: Centered stroke is default, but better safe than sorry, as
            // outside stroke can crash the application.
            newChild.setStrokeType( StrokeType.CENTERED );
        } );
    }

    public final void addShapes( final List< Shape > newChildren ) {
        getChildren().addAll( newChildren );

        newChildren.forEach( newChild -> newChild.strokeWidthProperty()
                .bind( strokeWidthProperty() ) );
    }

    public final void clearShapes() {
        getChildren().clear();
    }

    @Override
    public final ObservableList< Transform > getShapeTransforms() {
        return getTransforms();
    }

    public final double getStrokeWidth() {
        return strokeWidth.get();
    }

    public void highlight( final boolean highlightOn ) {
        // Grab the dash pattern to use for highlighting.
        final List< Double > highlightDashPattern = HighlightUtilities
                .getHighlightDashPattern( getStrokeWidth() );

        getChildren().forEach( child -> {
            if ( child instanceof Shape ) {
                HighlightUtilities.applyHighlight( child, highlightOn, highlightDashPattern );
            }
            else if ( child instanceof ShapeGroup ) {
                ( ( ShapeGroup ) child ).highlight( highlightOn );
            }
        } );
    }

    public final void reset() {
        // Clear the added shapes to ensure they can be garbage collected.
        clearShapes();
    }

    /**
     * Sets the scale transform on the overall {@link ShapeContainer shape
     * container} to a uniform scale in this Node's frame of reference.
     * <p>
     * This is similar to the scale() method on the Transform object.
     *
     * @param distanceUnitOld
     *            The old or reference {@link DistanceUnit} to scale from
     * @param distanceUnitNew
     *            The new {@link DistanceUnit} to scale to
     */
    @Override
    public final void scale( final DistanceUnit distanceUnitOld,
                             final DistanceUnit distanceUnitNew ) {
        // Apply the Distance Unit scale transform, as it is generally
        // compensated for by the Transform on the overall wrapper group.
        // TODO: Verify it is safe to remove existing Transforms, but if we
        // don't, then we get no results if converting old to new and get
        // cumulative scaling if using Meters as the Distance Unit basis.
        final double distanceScaleFactor = UnitConversion
                .convertDistance( 1d, distanceUnitOld, distanceUnitNew );
        final ObservableList< Transform > transforms = getShapeTransforms();
        final Scale scaleTransform = Transform.scale( distanceScaleFactor, distanceScaleFactor );
        transforms.setAll( scaleTransform );
    }

    /**
     * Sets the scale transform on child {@link javafx.scene.shape.Shape shapes}
     * to a uniform scale in this Node's frame of reference.
     * <p>
     * This is similar to the scale() method on the Transform object.
     *
     * @param distanceUnitOld
     *            The old or reference {@link DistanceUnit} to scale from
     * @param distanceUnitNew
     *            The new {@link DistanceUnit} to scale to
     */
    public final void scaleShapes( final DistanceUnit distanceUnitOld,
                                   final DistanceUnit distanceUnitNew ) {
        final double distanceScaleFactor = UnitConversion
                .convertDistance( 1d, distanceUnitOld, distanceUnitNew );
        scaleShapes( distanceScaleFactor );
    }

    /**
     * Sets the scale transform on child {@link javafx.scene.shape.Shape shapes}
     * to a uniform scale in this Node's frame of reference.
     * <p>
     * This is similar to the scale() method on the Transform object.
     *
     * @param scaleFactor
     *            The scale factor to be applied uniformly along all axes
     */
    public final void scaleShapes( final double scaleFactor ) {
        getChildren().forEach( child -> {
            if ( child instanceof Shape ) {
                final Shape shape = ( Shape ) child;
                final ObservableList< Transform > transforms = shape.getTransforms();
                final Scale scaleTransform = Transform.scale( scaleFactor, scaleFactor );
                transforms.setAll( scaleTransform );
            }
            else if ( child instanceof ShapeGroup ) {
                ( ( ShapeGroup ) child ).scaleShapes( scaleFactor );
            }
        } );
    }

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
    @Override
    public final void setForeground( final Color foreColor, final boolean forceOverride ) {
        // Make sure the contained Shapes are visible against the Background
        // using the supplied Foreground Color, but only change Black and White
        // vs. other Colors so that we don't mess up custom cues.
        // NOTE: The implementation below is brittle as it assumes too much
        // knowledge of various derived class structures and also that there is
        // stability to those classes. So this is really a placeholder for now.
        getChildren().forEach( childNode -> {
            if ( childNode instanceof Shape ) {
                // Only Shapes can set Stroke (used as Foreground Color).
                final Shape shape = ( Shape ) childNode;
                ColorUtilities.adjustStrokeForContrast( shape,
                                                        foreColor,
                                                        forceOverride,
                                                        ColorConstants.TARGET_HIGHLIGHTED_COLOR );
            }
            else if ( childNode instanceof Group ) {
                final Group group = ( Group ) childNode;
                group.getChildren().forEach( grandchildNode -> {
                    if ( grandchildNode instanceof Shape ) {
                        // Only Shapes can set Stroke (used as Foreground
                        // Color).
                        final Shape shape = ( Shape ) grandchildNode;
                        ColorUtilities
                                .adjustStrokeForContrast( shape,
                                                          foreColor,
                                                          forceOverride,
                                                          ColorConstants.TARGET_HIGHLIGHTED_COLOR );
                    }
                    else if ( grandchildNode instanceof ShapeGroup ) {
                        final ShapeGroup shapeContainer = ( ShapeGroup ) grandchildNode;
                        shapeContainer.setForeground( foreColor, forceOverride );
                    }
                } );
            }
        } );
    }

    /**
     * Sets the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform color in this node's frame of reference.
     * <p>
     * This is similar to the setStroke() method on the Shape object.
     *
     * @param paint
     *            The color of the stroke
     */
    @Override
    public final void setStroke( final Paint paint ) {
        getChildren().forEach( child -> {
            if ( child instanceof Shape ) {
                ( ( Shape ) child ).setStroke( paint );
            }
            else if ( child instanceof ShapeGroup ) {
                ( ( ShapeGroup ) child ).setStroke( paint );
            }
        } );
    }

    /**
     * Sets the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform scale in this node's frame of reference.
     * <p>
     * This is similar to the setStrokeWidth() method on the Shape object.
     *
     * @param pStrokeWidth
     *            The width of the stroke, roughly in pixels
     */
    public final void setStrokeWidth( final double pStrokeWidth ) {
        strokeWidth.set( pStrokeWidth );
    }

    /**
     * It is sometimes necessary to bind one stroke width with another.
     * 
     * @return The Stroke Width Property
     */
    public final DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

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
    @Override
    public final void updateStrokeWidth( final DistanceUnit distanceUnitReference,
                                         final DistanceUnit distanceUnitCurrent,
                                         final double strokeWidthBasis,
                                         final double strokeWidthRatio ) {
        // Modify Stroke Width resolution to be appropriate for the new scale.
        // NOTE: If default basis is used, no need to scale to Distance Unit,
        // but the zoom factor can then cause overly thick strokes.
        final double strokeWidthReference = UnitConversion
                .convertDistance( strokeWidthBasis, distanceUnitCurrent, distanceUnitReference );
        final double strokeWidthAdjusted = strokeWidthRatio * strokeWidthReference;

        // Globally set a uniform Stroke Width on the entire shape container.
        updateStrokeWidth( strokeWidthAdjusted );
    }

    /**
     * Updates the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform scale in this node's frame of reference.
     * <p>
     * This is similar to the setStroke() method on the Shape object.
     *
     * @param pStrokeWidth
     *            The width of the stroke, roughly in pixels
     */
    @Override
    public final void updateStrokeWidth( final double pStrokeWidth ) {
        setStrokeWidth( pStrokeWidth );
    }

}