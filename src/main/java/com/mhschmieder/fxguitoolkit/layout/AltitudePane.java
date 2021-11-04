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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.physics.Altitude;
import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.commonstoolkit.physics.PhysicsConstants;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public final class AltitudePane extends BorderPane {

    private Label      _altitudeLabel;

    public RadioButton _lowAltitudeRadioButton;
    public RadioButton _mediumAltitudeRadioButton;
    public RadioButton _highAltitudeRadioButton;
    public RadioButton _customAltitudeRadioButton;

    public ToggleGroup _altitudeToggleGroup;

    public AltitudePane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // NOTE: This method is used to clear all visible selections when a
    // manually set pressure is in effect, as we do not currently match those
    // values to the altitude-based ranges.
    public void clearAltitude() {
        _customAltitudeRadioButton.setSelected( true );
    }

    public Altitude getAltitude() {
        return _lowAltitudeRadioButton.isSelected()
            ? Altitude.LOW
            : _mediumAltitudeRadioButton.isSelected()
                ? Altitude.MEDIUM
                : _highAltitudeRadioButton.isSelected() ? Altitude.HIGH : Altitude.defaultValue();
    }

    private void initPane( final ClientProperties clientProperties ) {
        // Make a bolded label to clearly identify the functionality.
        _altitudeLabel = GuiUtilities.getColumnHeader( "Altitude" ); //$NON-NLS-1$

        _altitudeToggleGroup = new ToggleGroup();
        final DistanceUnit defaultDistanceUnit = DistanceUnit.defaultValue();
        _lowAltitudeRadioButton = GuiUtilities.getRadioButton( Altitude.LOW
                .toPresentationString( defaultDistanceUnit ), _altitudeToggleGroup, true );
        _mediumAltitudeRadioButton = GuiUtilities.getRadioButton( Altitude.MEDIUM
                .toPresentationString( defaultDistanceUnit ), _altitudeToggleGroup, false );
        _highAltitudeRadioButton = GuiUtilities.getRadioButton( Altitude.HIGH
                .toPresentationString( defaultDistanceUnit ), _altitudeToggleGroup, false );

        _customAltitudeRadioButton = new RadioButton();
        _customAltitudeRadioButton.setToggleGroup( _altitudeToggleGroup );

        // NOTE: Radio Buttons on their own are semantically meaningless, so
        // the way to associate them with values that are useful in multiple
        // contexts is to set custom user data that can be queried externally.
        _lowAltitudeRadioButton.setUserData( PhysicsConstants.PRESSURE_LOW_ALTITUDE_PA );
        _mediumAltitudeRadioButton.setUserData( PhysicsConstants.PRESSURE_MEDIUM_ALTITUDE_PA );
        _highAltitudeRadioButton.setUserData( PhysicsConstants.PRESSURE_HIGH_ALTITUDE_PA );
        _customAltitudeRadioButton.setUserData( null );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 8d );
        gridPane.setVgap( 8d );

        gridPane.add( _altitudeLabel, 0, 0 );
        gridPane.add( _lowAltitudeRadioButton, 0, 1 );
        gridPane.add( _mediumAltitudeRadioButton, 0, 2 );
        gridPane.add( _highAltitudeRadioButton, 0, 3 );

        gridPane.setAlignment( Pos.TOP_LEFT );
        gridPane.setPadding( new Insets( 6d ) );

        setLeft( gridPane );

        // The radio button selection indicator doesn't show unless we actively
        // select it. It isn't enough to set which member of the Toggle Group is
        // selected at construction time.
        // NOTE: This makes no difference, for some reason.
        Platform.runLater( this::reset );
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        // Set the radio button to match the default as a separate later task so
        // that we don't get race conditions in syncing external components to
        // the basic values of the sliders.
        // NOTE: Due to floating-point precision, it would be difficult to
        // automatically default the correct altitude radio button anyway, so we
        // do so manually instead.
        setAltitude( Altitude.defaultValue() );
    }

    public void setAltitude( final Altitude altitude ) {
        switch ( altitude ) {
        case LOW:
            _lowAltitudeRadioButton.setSelected( true );
            break;
        case MEDIUM:
            _mediumAltitudeRadioButton.setSelected( true );
            break;
        case HIGH:
            _highAltitudeRadioButton.setSelected( true );
            break;
        default:
            break;
        }
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _altitudeLabel.setTextFill( foregroundColor );

        _lowAltitudeRadioButton.setTextFill( foregroundColor );
        _mediumAltitudeRadioButton.setTextFill( foregroundColor );
        _highAltitudeRadioButton.setTextFill( foregroundColor );
        // _customAltitudeRadioButton.setTextFill( foregroundColor );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Reset the altitude buttons to match the new units.
        _lowAltitudeRadioButton.setText( Altitude.LOW.toPresentationString( distanceUnit ) );
        _mediumAltitudeRadioButton.setText( Altitude.MEDIUM.toPresentationString( distanceUnit ) );
        _highAltitudeRadioButton.setText( Altitude.HIGH.toPresentationString( distanceUnit ) );
    }

}
