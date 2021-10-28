/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class PositioningPane extends GridPane {

    public RadioButton           _cartesianPositionRadioButton;
    public CartesianPositionPane _cartesianPositionPane;
    public RadioButton           _polarPositionRadioButton;
    public PolarPositionPane     _polarPositionPane;

    public PositioningPane( final SessionContext sessionContext ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( sessionContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final Point2D getCartesianPosition2D() {
        // Forward this method to the appropriate components.
        return _cartesianPositionPane.getCartesianPosition2D();
    }

    public final double getDistance() {
        // Forward this method to the appropriate components.
        return _polarPositionPane.getDistance();
    }

    public final double getRotationAngle() {
        // Forward this method to the appropriate components.
        return _polarPositionPane.getRotationAngle();
    }

    private final void initPane( final SessionContext sessionContext ) {
        final ToggleGroup dualPositionToggleGroup = new ToggleGroup();
        _cartesianPositionRadioButton = GuiUtilities.getRadioButton( "Cartesian Coordinates", //$NON-NLS-1$
                                                                     dualPositionToggleGroup,
                                                                     true );
        _polarPositionRadioButton = GuiUtilities.getRadioButton( "Polar Coordinates", //$NON-NLS-1$
                                                                 dualPositionToggleGroup,
                                                                 false );

        _cartesianPositionPane = new CartesianPositionPane( sessionContext );

        _polarPositionPane = new PolarPositionPane( sessionContext );

        setHgap( 10d );
        setVgap( 10d );

        setPadding( new Insets( 10d ) );

        add( _cartesianPositionRadioButton, 0, 0 );
        add( _cartesianPositionPane, 0, 1 );
        add( _polarPositionRadioButton, 1, 0 );
        add( _polarPositionPane, 1, 1 );

        setAlignment( Pos.CENTER );

        // Bind position pane enablement to the associated radio buttons.
        _cartesianPositionPane.disableProperty()
                .bind( _cartesianPositionRadioButton.selectedProperty().not() );
        _polarPositionPane.disableProperty()
                .bind( _polarPositionRadioButton.selectedProperty().not() );
    }

    public final boolean isCartesianPositionActive() {
        // Forward this method to the appropriate components.
        return _cartesianPositionRadioButton.isSelected();
    }

    public final boolean isPolarPositionActive() {
        // Forward this method to the appropriate components.
        return _polarPositionRadioButton.isSelected();
    }

    public final void saveEdits() {
        // Forward this method to the appropriate components.
        _cartesianPositionPane.saveEdits();
        _polarPositionPane.saveEdits();
    }

    public final void setCartesianPosition2D( final double cartesianPositionX,
                                              final double cartesianPositionY ) {
        // Forward this method to the appropriate components.
        _cartesianPositionPane.setCartesianPosition2D( cartesianPositionX, cartesianPositionY );
    }

    public final void setCartesianPosition2D( final Point2D cartesianPosition ) {
        // Forward this method to the appropriate components.
        _cartesianPositionPane.setCartesianPosition2D( cartesianPosition );
    }

    public final void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Polar Position Pane.
        _polarPositionPane.setGesturesEnabled( gesturesEnabled );
    }

    public final void setPolarPosition( final double rotationAngle, final double distance ) {
        // Forward this method to the appropriate components.
        _polarPositionPane.setPolarPosition( rotationAngle, distance );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public final void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Polar Position Pane.
        _polarPositionPane.setScrollingSensitivity( scrollingSensitivity );
    }

    public final void toggleGestures() {
        // Forward this method to the Polar Position Pane.
        _polarPositionPane.toggleGestures();
    }

    public final void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the appropriate components.
        _polarPositionPane.updateAngleUnit( angleUnit );
    }

    public final void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the appropriate components.
        _cartesianPositionPane.updateDistanceUnit( distanceUnit );
        _polarPositionPane.updateDistanceUnit( distanceUnit );
    }

}
