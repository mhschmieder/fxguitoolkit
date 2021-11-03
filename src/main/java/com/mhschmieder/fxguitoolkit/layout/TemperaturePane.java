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

import com.mhschmieder.commonstoolkit.net.ClientProperties;
import com.mhschmieder.commonstoolkit.physics.NaturalEnvironment;
import com.mhschmieder.commonstoolkit.physics.PhysicsConstants;
import com.mhschmieder.commonstoolkit.physics.TemperatureUnit;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import com.mhschmieder.fxguitoolkit.control.TemperatureEditor;
import com.mhschmieder.fxguitoolkit.control.TemperatureSlider;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class TemperaturePane extends VBox {

    private Label            _temperatureLabel;
    public TemperatureSlider _temperatureSlider;
    public TemperatureEditor _temperatureEditor;

    private DoubleProperty   temperatureK;

    // Cache the number converter so its units and extrema can be changed later
    // when the Temperature Unit changes.
    // protected NumberConverter _numberConverter;

    public TemperaturePane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super( 6d );

        temperatureK = new SimpleDoubleProperty();

        initPane( clientProperties );
    }

    private void bindProperties() {
        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range.
        // NOTE: This is OK because we embed unit conversion in DoubleEditor.
        _temperatureSlider.valueProperty().bindBidirectional( _temperatureEditor.valueProperty() );

        // NOTE: Sliders sync to the exact value of JavaFX Bean Properties,
        // only passing through the unit conversion.
        temperatureKProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            _temperatureSlider.setTemperatureK( newValue.doubleValue() );
        } );

        // NOTE: Sliders might switch presentation units, whereas JavaFX Bean
        // Properties are specified with a single unchanging unit, so we have to
        // be careful to only sync the cached Temperature property to the slider
        // when a real magnitude change occurred vs. a Temperature Unit change.
        _temperatureSlider.valueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            final double storedValue = getTemperatureK();
            final double sliderValue = _temperatureSlider.getTemperatureK();
            final double epsilon = 1e-10;

            // Make sure we don't set dirty flag because of round-off
            // errors in slider value when changing units, but wrap this
            // in a JavaFX runLater thread to ensure all FX event code
            // precedes the custom selection.
            if ( ( Math.abs( storedValue - sliderValue ) >= epsilon ) ) {
                Platform.runLater( () -> setTemperatureK( sliderValue ) );
            }
        } );
    }

    public double getTemperatureK() {
        return temperatureK.get();
    }

    private void initPane( final ClientProperties clientProperties ) {
        // Make a bolded label to clearly identify the functionality.
        _temperatureLabel = GuiUtilities.getColumnHeader( "Temperature" ); //$NON-NLS-1$

        // Create a default Temperature Slider.
        _temperatureSlider = new TemperatureSlider( clientProperties );

        // Conform the associated editor (text field) to the slider attributes.
        _temperatureEditor = ControlFactory.getTemperatureEditor( clientProperties );
        _temperatureEditor.setPrefWidth( 100d );
        _temperatureEditor.setMaxWidth( 100d );

        // Cache a number converter so we can keep it up to date with the
        // Temperature Unit, which can change at any time.
        // _numberConverter = new NumberConverter(
        // TemperatureUnit.KELVIN.toPresentationString(),
        // _temperatureEditor
        // .getNumberFormat(),
        // PhysicsConstants.TEMPERATURE_REFERENCE_K,
        // PhysicsConstants.TEMPERATURE_MINIMUM_K,
        // PhysicsConstants.TEMPERATURE_MAXIMUM_K );

        getChildren().addAll( _temperatureLabel, _temperatureSlider, _temperatureEditor );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 6d ) );

        // Make sure the Temperature Slider always gets vertical grow priority.
        VBox.setVgrow( _temperatureSlider, Priority.ALWAYS );
    }

    public void reset() {
        _temperatureSlider.setTemperatureK( NaturalEnvironment.TEMPERATURE_K_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _temperatureLabel.setTextFill( foregroundColor );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        _temperatureSlider.setGesturesEnabled( gesturesEnabled );
    }

    protected void setMaximum( final double maximumTemperature ) {
        _temperatureSlider.setMax( maximumTemperature );
    }

    protected void setMinimum( final double minimumTemperature ) {
        _temperatureSlider.setMin( minimumTemperature );
    }

    /**
     * Set the new Scrolling Sensitivity for the Temperature Sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        _temperatureSlider.setScrollingSensitivity( scrollingSensitivity );
    }

    public void setTemperatureK( final double pTemperatureK ) {
        temperatureK.set( pTemperatureK );
    }

    // Set and bind the Temperature property reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setTemperatureKProperty( final DoubleProperty pTemperatureK ) {
        // Cache the Temperature property reference.
        temperatureK = pTemperatureK;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    public DoubleProperty temperatureKProperty() {
        return temperatureK;
    }

    public void toggleGestures() {
        _temperatureSlider.toggleGestures();
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        // Update the number converter's allowed value extrema etc.
        // _numberConverter.setMeasurementUnit( temperatureUnit
        // .toPresentationString() );

        // Convert the Temperature default and range from default to new units.
        // final double temperatureMinimum = UnitConversion
        // .convertTemperature( PhysicsConstants.TEMPERATURE_MINIMUM_K,
        // TemperatureUnit.KELVIN,
        // temperatureUnit );
        // final double temperatureMaximum = UnitConversion
        // .convertTemperature( PhysicsConstants.TEMPERATURE_MAXIMUM_K,
        // TemperatureUnit.KELVIN,
        // temperatureUnit );
        // final double temperatureDefault = UnitConversion
        // .convertTemperature( PhysicsConstants.ROOM_TEMPERATURE_K,
        // TemperatureUnit.KELVIN,
        // temperatureUnit );

        // _numberConverter.setMinimumValue( temperatureMinimum );
        // _numberConverter.setMaximumValue( temperatureMaximum );
        // _numberConverter.setDefaultValue( temperatureDefault );

        // Forward this method to the subsidiary controls.
        // NOTE: Make sure that there is enough room for the value expressed in
        // the new units, by setting the largest magnitude from all available
        // units. Otherwise, as the value is bounded, it changes before min
        // value and max values change, and therefore it becomes clamped as the
        // not-yet-converted old value may not be within the new range, and thus
        // it fires an event, setting the dirty flag.
        final double temperatureMaximum = 10d * PhysicsConstants.TEMPERATURE_MAXIMUM_K;
        final double temperatureMinimum = -temperatureMaximum;
        _temperatureEditor.setMinimumValue( temperatureMinimum );
        _temperatureEditor.setMaximumValue( temperatureMaximum );
        _temperatureSlider.setMin( temperatureMinimum );
        _temperatureSlider.setMax( temperatureMaximum );

        // Now that there is room, set the new units. Min and Max value are
        // calculated again inside setTemperatureUnit after setting the value.
        _temperatureSlider.updateTemperatureUnit( temperatureUnit );
        _temperatureEditor.updateTemperatureUnit( temperatureUnit );

        // In order to avoid order-dependency and initial condition Catch-22's,
        // always set the editor's value to match the paired slider's value.
        // NOTE: This doesn't fix the startup problem of the value being the
        // minimum allowed, when the user's cached unit is the default unit.
        Platform.runLater( () -> _temperatureEditor.setValue( _temperatureSlider.getValue() ) );
    }

}