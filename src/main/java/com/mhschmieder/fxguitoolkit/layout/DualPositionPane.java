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

import com.mhschmieder.commonstoolkit.math.DistanceUnit;
import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class DualPositionPane extends GridPane {

    public RadioButton           _mainPositionRadioButton;
    public CartesianPositionPane _mainPositionPane;
    public RadioButton           _alternatePositionRadioButton;
    public CartesianPositionPane _alternatePositionPane;

    public DualPositionPane( final SessionContext sessionContext,
                             final String mainPositionLabel,
                             final String alternatePositionLabel ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( sessionContext, mainPositionLabel, alternatePositionLabel );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final Point2D getAlternatePosition2D() {
        // Forward this method to the appropriate components.
        return _alternatePositionPane.getCartesianPosition2D();
    }

    public final Point2D getMainPosition2D() {
        // Forward this method to the appropriate components.
        return _mainPositionPane.getCartesianPosition2D();
    }

    private final void initPane( final SessionContext sessionContext,
                                 final String mainPositionLabel,
                                 final String alternatePositionLabel ) {
        final ToggleGroup dualPositionToggleGroup = new ToggleGroup();
        _mainPositionRadioButton = GuiUtilities
                .getRadioButton( mainPositionLabel, dualPositionToggleGroup, false );
        _alternatePositionRadioButton = GuiUtilities
                .getRadioButton( alternatePositionLabel, dualPositionToggleGroup, true );

        _mainPositionPane = new CartesianPositionPane( sessionContext );

        _alternatePositionPane = new CartesianPositionPane( sessionContext );

        setHgap( 10d );
        setVgap( 10d );

        setPadding( new Insets( 10d ) );

        add( _mainPositionRadioButton, 0, 0 );
        add( _mainPositionPane, 0, 1 );
        add( _alternatePositionRadioButton, 1, 0 );
        add( _alternatePositionPane, 1, 1 );

        setAlignment( Pos.CENTER );

        // Bind Position Pane enablement to the associated radio buttons.
        _mainPositionPane.disableProperty()
                .bind( _mainPositionRadioButton.selectedProperty().not() );
        _alternatePositionPane.disableProperty()
                .bind( _alternatePositionRadioButton.selectedProperty().not() );
    }

    public final boolean isAlternatePositionActive() {
        // Forward this method to the appropriate components.
        return _alternatePositionRadioButton.isSelected();
    }

    public final boolean isMainPositionActive() {
        // Forward this method to the appropriate components.
        return _mainPositionRadioButton.isSelected();
    }

    public final void saveEdits() {
        // Forward this method to the appropriate components.
        _mainPositionPane.saveEdits();
        _alternatePositionPane.saveEdits();
    }

    public final void setAlternatePosition2D( final double cartesianPositionX,
                                              final double cartesianPositionY ) {
        // Forward this method to the appropriate components.
        _alternatePositionPane.setCartesianPosition2D( cartesianPositionX, cartesianPositionY );
    }

    public final void setAlternatePosition2D( final Point2D cartesianPosition ) {
        // Forward this method to the appropriate components.
        _alternatePositionPane.setCartesianPosition2D( cartesianPosition );
    }

    public final void setMainPosition2D( final double cartesianPositionX,
                                         final double cartesianPositionY ) {
        // Forward this method to the appropriate components.
        _mainPositionPane.setCartesianPosition2D( cartesianPositionX, cartesianPositionY );
    }

    public final void setMainPosition2D( final Point2D cartesianPosition2D ) {
        // Forward this method to the appropriate components.
        _mainPositionPane.setCartesianPosition2D( cartesianPosition2D );
    }

    public final void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the appropriate components.
        _mainPositionPane.updateDistanceUnit( distanceUnit );
        _alternatePositionPane.updateDistanceUnit( distanceUnit );
    }

}
