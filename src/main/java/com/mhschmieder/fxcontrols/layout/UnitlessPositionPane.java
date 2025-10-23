/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
package com.mhschmieder.fxcontrols.layout;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.control.DoubleEditor;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class UnitlessPositionPane extends BorderPane {

    public DoubleEditor _xPositionEditor;
    public DoubleEditor _yPositionEditor;

    @SuppressWarnings("nls")
    public UnitlessPositionPane( final ClientProperties clientProperties ) {
        this( clientProperties, "X", "Y" );
    }

    public UnitlessPositionPane( final ClientProperties clientProperties,
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
        final Point2D cartesianPosition = new Point2D( _xPositionEditor.getValue(),
                                                       _yPositionEditor.getValue() );

        return cartesianPosition;
    }

    private final void initPane( final ClientProperties clientProperties,
                                 final String xPositionLabelText,
                                 final String yPositionLabelText ) {
        _xPositionEditor = new DoubleEditor( clientProperties,
                                             "0", //$NON-NLS-1$
                                             null,
                                             true,
                                             0,
                                             2,
                                             0,
                                             10 );
        _xPositionEditor.setValueIncrement( 0.5d );

        _yPositionEditor = new DoubleEditor( clientProperties,
                                             "0", //$NON-NLS-1$
                                             null,
                                             true,
                                             0,
                                             2,
                                             0,
                                             10 );
        _yPositionEditor.setValueIncrement( 0.5d );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 10.0d );
        gridPane.setVgap( 10.0d );

        final Label xPositionLabel = GuiUtilities.getControlLabel( xPositionLabelText );
        gridPane.add( xPositionLabel, 0, 0 );
        gridPane.add( _xPositionEditor, 1, 0 );

        final Label yPositionLabel = GuiUtilities.getControlLabel( yPositionLabelText );
        gridPane.add( yPositionLabel, 0, 1 );
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
        _xPositionEditor.setValue( cartesianPositionX );
        _yPositionEditor.setValue( cartesianPositionY );
    }

    public final void setCartesianPosition2D( final Point2D cartesianPosition2D ) {
        setCartesianPosition2D( cartesianPosition2D.getX(), cartesianPosition2D.getY() );
    }

    public final void setMaximumDistance( final double maximumDistance ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.setMaximumValue( maximumDistance );
        _yPositionEditor.setMaximumValue( maximumDistance );
    }

    public final void setMinimumDistance( final double minimumDistance ) {
        // Forward this method to the subsidiary components.
        _xPositionEditor.setMinimumValue( minimumDistance );
        _yPositionEditor.setMinimumValue( minimumDistance );
    }

}
