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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.DistanceEditor;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class CartesianPositionPane extends BorderPane {

    private Label         _xPositionLabel;
    public DistanceEditor _xPositionEditor;

    private Label         _yPositionLabel;
    public DistanceEditor _yPositionEditor;

    @SuppressWarnings("nls")
    public CartesianPositionPane( final ClientProperties clientProperties ) {
        this( clientProperties, "X", "Y" );
    }

    public CartesianPositionPane( final ClientProperties clientProperties,
                                  final String xPositionLabelText,
                                  final String yPositionLabelText ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties, xPositionLabelText, yPositionLabelText );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final Point2D getCartesianPosition2D() {
        final Point2D cartesianPosition = new Point2D( _xPositionEditor.getDistanceMeters(),
                                                       _yPositionEditor.getDistanceMeters() );

        return cartesianPosition;
    }

    public final double getCartesianPositionX() {
        final double cartesianPositionX = _xPositionEditor.getDistanceMeters();
        return cartesianPositionX;
    }

    public final double getCartesianPositionY() {
        final double cartesianPositionY = _yPositionEditor.getDistanceMeters();
        return cartesianPositionY;
    }

    @SuppressWarnings("nls")
    private final void initPane( final ClientProperties clientProperties,
                                 final String xPositionLabelText,
                                 final String yPositionLabelText ) {
        _xPositionEditor = new DistanceEditor( clientProperties, "0", null );
        _yPositionEditor = new DistanceEditor( clientProperties, "0", null );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 10.0d );
        gridPane.setVgap( 10.0d );

        _xPositionLabel = GuiUtilities.getControlLabel( xPositionLabelText );
        gridPane.add( _xPositionLabel, 0, 0 );
        gridPane.add( _xPositionEditor, 1, 0 );

        _yPositionLabel = GuiUtilities.getControlLabel( yPositionLabelText );
        gridPane.add( _yPositionLabel, 0, 1 );
        gridPane.add( _yPositionEditor, 1, 1 );

        gridPane.setAlignment( Pos.CENTER );
        gridPane.setPadding( new Insets( 10.0d ) );

        setLeft( gridPane );
    }

    public final void saveEdits() {
        _xPositionEditor.saveEdits();
        _yPositionEditor.saveEdits();
    }

    public final void setCartesianPosition2D( final double cartesianPositionX,
                                              final double cartesianPositionY ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.setDistanceMeters( cartesianPositionX );
        _yPositionEditor.setDistanceMeters( cartesianPositionY );
    }

    public final void setCartesianPosition2D( final Point2D cartesianPosition2D ) {
        setCartesianPosition2D( cartesianPosition2D.getX(), cartesianPosition2D.getY() );
    }

    public final void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );

        _xPositionLabel.setTextFill( foregroundColor );
        _yPositionLabel.setTextFill( foregroundColor );
    }

    public final void setMaximumDistanceMeters( final double maximumDistanceMeters ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.setMaximumDistanceMeters( maximumDistanceMeters );
        _yPositionEditor.setMaximumDistanceMeters( maximumDistanceMeters );
    }

    public final void setMinimumDistanceMeters( final double minimumDistanceMeters ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.setMinimumDistanceMeters( minimumDistanceMeters );
        _yPositionEditor.setMinimumDistanceMeters( minimumDistanceMeters );
    }

    public final void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.updateDistanceUnit( distanceUnit );
        _yPositionEditor.updateDistanceUnit( distanceUnit );
    }

}
