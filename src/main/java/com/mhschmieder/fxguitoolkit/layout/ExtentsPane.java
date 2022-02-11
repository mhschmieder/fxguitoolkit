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
 * This file is part of the FxuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.geometry.Extents2D;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public final class ExtentsPane extends GridPane {

    static final double          EPSILON_TOLERANCE = 1e-6;

    private Label                _minimumPaneLabel;
    public CartesianPositionPane _minimumPane;
    private Label                _sizePaneLabel;
    public CartesianPositionPane _sizePane;

    // Maintain an observable reference to the global Extents.
    protected Extents2D          extents;

    public ExtentsPane( final ClientProperties clientProperties,
                        final double extentsSizeMinimumMeters,
                        final double extentsSizeMaximumMeters,
                        final String propertiesCategory ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties,
                      extentsSizeMinimumMeters,
                      extentsSizeMaximumMeters,
                      propertiesCategory );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void bindProperties() {
        // NOTE: Editors sync to the exact value of JavaFX Bean Properties,
        // only passing through the unit conversion.
        extents.xProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    _minimumPane._xPositionEditor.setDistanceMeters( newValue.doubleValue() );
                } );
        extents.yProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    _minimumPane._yPositionEditor.setDistanceMeters( newValue.doubleValue() );
                } );
        extents.widthProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    _sizePane._xPositionEditor.setDistanceMeters( newValue.doubleValue() );
                } );
        extents.heightProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    _sizePane._yPositionEditor.setDistanceMeters( newValue.doubleValue() );
                } );

        // NOTE: Editors might switch presentation units, whereas JavaFX Bean
        // Properties are specified with a single unchanging unit, so we have to
        // be careful to only sync the cached Distance property to the editor
        // when a real magnitude change occurred vs. a Distance Unit change.
        _minimumPane._xPositionEditor.valueProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    final double storedValue = getMinimumX();
                    final double editorValue = _minimumPane._xPositionEditor.getDistanceMeters();

                    // Make sure we don't set dirty flag because of round-off
                    // errors in editor value when changing units, but wrap this
                    // in a JavaFX runLater thread to ensure all FX event code
                    // precedes the custom selection.
                    if ( ( Math.abs( storedValue - editorValue ) >= EPSILON_TOLERANCE ) ) {
                        Platform.runLater( () -> setMinimumX( editorValue ) );
                    }
                } );

        _minimumPane._yPositionEditor.valueProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    final double storedValue = getMinimumY();
                    final double editorValue = _minimumPane._yPositionEditor.getDistanceMeters();

                    // Make sure we don't set dirty flag because of round-off
                    // errors in editor value when changing units, but wrap this
                    // in a JavaFX runLater thread to ensure all FX event code
                    // precedes the custom selection.
                    if ( ( Math.abs( storedValue - editorValue ) >= EPSILON_TOLERANCE ) ) {
                        Platform.runLater( () -> setMinimumY( editorValue ) );
                    }
                } );

        _sizePane._xPositionEditor.valueProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    final double storedValue = getSizeX();
                    final double editorValue = _sizePane._xPositionEditor.getDistanceMeters();

                    // Make sure we don't set dirty flag because of round-off
                    // errors in editor value when changing units, but wrap this
                    // in a JavaFX runLater thread to ensure all FX event code
                    // precedes the custom selection.
                    if ( ( Math.abs( storedValue - editorValue ) >= EPSILON_TOLERANCE ) ) {
                        Platform.runLater( () -> setSizeX( editorValue ) );
                    }
                } );

        _sizePane._yPositionEditor.valueProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    final double storedValue = getSizeY();
                    final double editorValue = _sizePane._yPositionEditor.getDistanceMeters();

                    // Make sure we don't set dirty flag because of round-off
                    // errors in editor value when changing units, but wrap this
                    // in a JavaFX runLater thread to ensure all FX event code
                    // precedes the custom selection.
                    if ( ( Math.abs( storedValue - editorValue ) >= EPSILON_TOLERANCE ) ) {
                        Platform.runLater( () -> setSizeY( editorValue ) );
                    }
                } );
    }

    public double getMinimumX() {
        return extents.getX();
    }

    public double getMinimumY() {
        return extents.getY();
    }

    public double getSizeX() {
        return extents.getWidth();
    }

    public double getSizeY() {
        return extents.getHeight();
    }

    private void initPane( final ClientProperties clientProperties,
                           final double extentsSizeMinimumMeters,
                           final double extentsSizeMaximumMeters,
                           final String propertiesCategory ) {
        _minimumPaneLabel = GuiUtilities.getColumnHeader( "Lower Left Corner" ); //$NON-NLS-1$
        _minimumPane = new CartesianPositionPane( clientProperties );
        _sizePaneLabel = GuiUtilities.getColumnHeader( propertiesCategory + " Size" ); //$NON-NLS-1$
        _sizePane = new CartesianPositionPane( clientProperties, "Width", "Height" ); //$NON-NLS-1$ //$NON-NLS-2$

        _sizePane.setMinimumDistanceMeters( extentsSizeMinimumMeters );
        _sizePane.setMaximumDistanceMeters( extentsSizeMaximumMeters );

        setHgap( 6d );
        setVgap( 6d );

        setPadding( new Insets( 6d ) );

        add( _minimumPaneLabel, 0, 0 );
        add( _minimumPane, 0, 1 );
        add( _sizePaneLabel, 1, 0 );
        add( _sizePane, 1, 1 );

        GridPane.setHalignment( _minimumPaneLabel, HPos.CENTER );
        GridPane.setHalignment( _sizePaneLabel, HPos.CENTER );

        setAlignment( Pos.CENTER );
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        _minimumPane._xPositionEditor.setValue( Extents2D.X_METERS_DEFAULT );
        _minimumPane._yPositionEditor.setValue( Extents2D.Y_METERS_DEFAULT );
        _sizePane._xPositionEditor.setValue( Extents2D.WIDTH_METERS_DEFAULT );
        _sizePane._yPositionEditor.setValue( Extents2D.HEIGHT_METERS_DEFAULT );
    }

    // Set and bind the Extents reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setExtents( final Extents2D pExtents ) {
        // Cache the new Extents.
        extents = pExtents;

        _minimumPane._xPositionEditor.setValue( extents.getX() );
        _minimumPane._yPositionEditor.setValue( extents.getY() );
        _sizePane._xPositionEditor.setValue( extents.getWidth() );
        _sizePane._yPositionEditor.setValue( extents.getHeight() );

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );

        _minimumPaneLabel.setTextFill( foregroundColor );
        _minimumPane.setForegroundFromBackground( backColor );

        _sizePaneLabel.setTextFill( foregroundColor );
        _sizePane.setForegroundFromBackground( backColor );
    }

    public void setMinimumX( final double minimumX ) {
        extents.setX( minimumX );
    }

    public void setMinimumY( final double minimumY ) {
        extents.setY( minimumY );
    }

    public void setSizeX( final double sizeX ) {
        extents.setWidth( sizeX );
    }

    public void setSizeY( final double sizeY ) {
        extents.setHeight( sizeY );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the subcomponents.
        _minimumPane.updateDistanceUnit( distanceUnit );
        _sizePane.updateDistanceUnit( distanceUnit );
    }

}
