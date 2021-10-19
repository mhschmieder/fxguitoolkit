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
package com.mhschmieder.fxcharttoolkit;

import com.mhschmieder.commonstoolkit.math.DistanceUnit;
import com.mhschmieder.fxgraphicstoolkit.geometry.ShapeContainer;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;

/**
 * This is a Group container for related Notes that are part of a Chart Content
 * context, so that we can consolidate common behavior and avoid copy/paste code
 * that accidentally diverges over time.
 */
public class ChartContentGroup extends Group {

    /**
     * Maintain a local Clipping Rectangle that is kept in sync with chart.
     */
    protected final Rectangle _clippingRectangle;

    /**
     * Keep track of what Distance Unit we're using to display, for later
     * conversion.
     */
    public DistanceUnit       _distanceUnit;

    /**
     * This is the full constructor, when all parameters are known.
     */
    public ChartContentGroup() {
        // Always call the superclass constructor first!
        super();

        _clippingRectangle = new Rectangle();

        _distanceUnit = DistanceUnit.defaultValue();

        try {
            initialize();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns the current Clipping Rectangle in use, allowing
     * other elements to stay in sync with this more complex clipping criteria
     * in this Group container, which updates frequently when Grid Lines have to
     * be recalculated.
     *
     * @return The current clipping rectangle for this Group
     */
    public final Rectangle getClippingRectangle() {
        return _clippingRectangle;
    }

    /**
     * @return The transform on this Group, which should be a single transform
     *         from venue (user Distance Units) to display (pixels) coordinates.
     */
    public final Transform getVenueToDisplayTransform() {
        return getTransforms().get( 0 );
    }

    /**
     * Set up the configuration of the common Chart Contents parameters.
     */
    private final void initialize() {
        // Setup clipping on the contents, using a dedicated clipping node that
        // will be updated as needed. Obviously, no anti-aliasing is used here.
        _clippingRectangle.setSmooth( false );
        setClip( _clippingRectangle );
    }

    /**
     * There may be a need to suppress layout requests. if this method calls
     * super.requestLayout(), then it does not suppress layout requests.
     * <p>
     * TODO: Review whether this specific override should be suppressed.
     */
    @Override
    public void requestLayout() {
        // Do nothing: suppress layout requests.
        // super.requestLayout();
    }

    /**
     * Scale the supplied {@link ShapeContainer}, for Distance Unit and Stroke
     * Width.
     *
     * @param shapeContainer
     *            The {@link ShapeContainer} to scale in the Cartesian Chart
     * @param distanceUnitReference
     *            The reference Distance Unit to scale from
     * @param displayToVenueScaleFactor
     *            The display-to-venue scale factor
     * @param strokeWidthRatio
     *            The ratio to apply to the Basic Stroke Width
     */
    public final void scaleGraphicalNode( final ShapeContainer shapeContainer,
                                          final DistanceUnit distanceUnitReference,
                                          final double displayToVenueScaleFactor,
                                          final double strokeWidthRatio ) {
        // Apply the Distance Unit scale transform, as it is generally
        // compensated for by the transform on the overall wrapper group.
        shapeContainer.scale( distanceUnitReference, _distanceUnit );

        // Modify Stroke Width resolution to be appropriate for the new scale.
        final double strokeWidthBasis = displayToVenueScaleFactor;
        shapeContainer.updateStrokeWidth( distanceUnitReference,
                                          _distanceUnit,
                                          strokeWidthBasis,
                                          strokeWidthRatio );
    }

    public final void setDistanceUnit( final DistanceUnit distanceUnit ) {
        // Cache the new Distance Unit to provide context for graphics.
        _distanceUnit = distanceUnit;
    }

    /**
     * Set the venue-to-display scale factor transform on this Group.
     *
     * @param venueToDisplayTransform
     *            The new venue-to-display scale factor as a {@link Transform}
     */
    public final void setVenueToDisplayTransform( final Transform venueToDisplayTransform ) {
        // Replace the graphics transforms with the new Scale transform.
        getTransforms().setAll( venueToDisplayTransform );
    }

    /**
     * This method updates the clipping rectangle to match a candidate from
     * some other source, which in this context is generally the main Chart
     * Overlay that must recalculate bounds every time the Grid Lines are
     * regenerated due to changes of Grid Resolution or Distance Unit changes.
     *
     * @param clippingRectangleCandidate
     *            The Clipping Rectangle to use for updating this one's bounds
     */
    public final void updateClippingRectangle( final Rectangle clippingRectangleCandidate ) {
        _clippingRectangle.setX( clippingRectangleCandidate.getX() );
        _clippingRectangle.setY( clippingRectangleCandidate.getY() );
        _clippingRectangle.setWidth( clippingRectangleCandidate.getWidth() );
        _clippingRectangle.setHeight( clippingRectangleCandidate.getHeight() );

        // Although this only affects auto-sized Groups (which ours are not), it
        // is safer to go ahead and request the content of each Group to
        // re-layout anyway, in case of future changes to the layout scheme.
        // :NOTE: Removed for now as it seems to cause lag during screen resize.
        // layoutChildren();
    }

    public final void updateLayout( final Rectangle layoutBounds ) {
        // Position the Chart Contents to the top left corner of the Chart.
        setLayoutX( layoutBounds.getX() );
        setLayoutY( layoutBounds.getY() );

        // Make sure the Chart Contents Group recomputes its layout.
        requestLayout();
    }

    /**
     * Update the translations on this Group.
     *
     * @param xLow
     *            The x-axis offset to use for the clipping rectangle
     * @param yLow
     *            The y-axis offset to use for the clipping rectangle
     * @param translateX
     *            The x-axis translation to use for the Chart Contents Group
     * @param translateY
     *            The y-axis translation to use for the Chart Contents Group
     */
    public final void updateTranslate( final double xLow,
                                       final double yLow,
                                       final double translateX,
                                       final double translateY ) {
        // Translate the clipping rectangle for the Chart Contents Group.
        _clippingRectangle.setTranslateX( _clippingRectangle.getX() + xLow );
        _clippingRectangle.setTranslateY( _clippingRectangle.getY() + yLow );

        // Translate the Chart Contents Group from the axis origin.
        setTranslateX( translateX );
        setTranslateY( translateY );
    }

}
