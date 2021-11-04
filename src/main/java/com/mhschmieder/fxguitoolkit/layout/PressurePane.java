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

import com.mhschmieder.commonstoolkit.physics.NaturalEnvironment;
import com.mhschmieder.commonstoolkit.physics.PhysicsConstants;
import com.mhschmieder.commonstoolkit.physics.PressureUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import com.mhschmieder.fxguitoolkit.control.PressureEditor;
import com.mhschmieder.fxguitoolkit.control.PressureSlider;

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

public final class PressurePane extends VBox {

    private Label          _pressureLabel;
    public PressureSlider  _pressureSlider;
    public PressureEditor  _pressureEditor;

    private DoubleProperty pressurePa;

    // Cache the number converter so its units and extrema can be changed later
    // when the Pressure Unit changes.
    // protected NumberConverter _numberConverter;

    public PressurePane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super( 6d );

        pressurePa = new SimpleDoubleProperty();

        initPane( clientProperties );
    }

    private void bindProperties() {
        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range.
        // NOTE: This is OK because we embed unit conversion in DoubleEditor.
        _pressureSlider.valueProperty().bindBidirectional( _pressureEditor.valueProperty() );

        // NOTE: Sliders sync to the exact value of JavaFX Bean Properties,
        // only passing through the unit conversion.
        pressurePaProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            _pressureSlider.setPressurePa( newValue.doubleValue() );
        } );

        // NOTE: Sliders might switch presentation units, whereas JavaFX Bean
        // Properties are specified with a single unchanging unit, so we have to
        // be careful to only sync the cached Pressure property to the slider
        // when a real magnitude change occurred vs. a Pressure Unit change.
        _pressureSlider.valueProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            final double storedValue = getPressurePa();
            final double sliderValue = _pressureSlider.getPressurePa();
            final double epsilon = 1e-7;

            // Make sure we don't set dirty flag because of round-off
            // errors in slider value when changing units, but wrap this
            // in a JavaFX runLater thread to ensure all FX event code
            // precedes the custom selection.
            if ( ( Math.abs( storedValue - sliderValue ) >= epsilon ) ) {
                Platform.runLater( () -> setPressurePa( sliderValue ) );
            }
        } );
    }

    public double getPressurePa() {
        return pressurePa.get();
    }

    private void initPane( final ClientProperties clientProperties ) {
        // Make a bolded label to clearly identify the functionality.
        _pressureLabel = GuiUtilities.getColumnHeader( "Atmospheric Pressure" ); //$NON-NLS-1$

        // Create a default Pressure Slider.
        _pressureSlider = new PressureSlider( clientProperties );

        // Conform the associated editor (text field) to the slider attributes.
        _pressureEditor = ControlFactory.getPressureEditor( clientProperties );
        _pressureEditor.setPrefWidth( 100d );
        _pressureEditor.setMaxWidth( 100d );

        // Cache a number converter so we can keep it up to date with the
        // Pressure Unit, which can change at any time.
        // _numberConverter = new NumberConverter(
        // PressureUnit.PASCALS.toPresentationString(),
        // _pressureEditor
        // .getNumberFormat(),
        // PhysicsConstants.PRESSURE_REFERENCE_PA,
        // PhysicsConstants.PRESSURE_MINIMUM_PA,
        // PhysicsConstants.PRESSURE_MAXIMUM_PA );

        getChildren().addAll( _pressureLabel, _pressureSlider, _pressureEditor );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 6d ) );

        // Make sure the Pressure Slider always gets vertical grow priority.
        VBox.setVgrow( _pressureSlider, Priority.ALWAYS );
    }

    public DoubleProperty pressurePaProperty() {
        return pressurePa;
    }

    public void reset() {
        _pressureSlider.setPressurePa( NaturalEnvironment.PRESSURE_PA_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _pressureLabel.setTextFill( foregroundColor );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        _pressureSlider.setGesturesEnabled( gesturesEnabled );
    }

    public void setPressurePa( final double pPressureKpa ) {
        pressurePa.set( pPressureKpa );
    }

    // Set and bind the Pressure property reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setPressurePaProperty( final DoubleProperty pPressurePa ) {
        // Cache the Pressure property reference.
        pressurePa = pPressurePa;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    /**
     * Set the new Scrolling Sensitivity for the Pressure Sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        _pressureSlider.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        _pressureSlider.toggleGestures();
    }

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        // Update the number converter's allowed value extrema etc.
        // _numberConverter.setMeasurementUnit( pressureUnit
        // .toPresentationString() );

        // Convert the Pressure default and range from default to new units.
        // final double pressureMinimum = UnitConversion
        // .convertPressure( PhysicsConstants.PRESSURE_MINIMUM_PA,
        // PressureUnit.KILOPASCALS,
        // pressureUnit );
        // final double pressureMaximum = UnitConversion
        // .convertPressure( PhysicsConstants.PRESSURE_MAXIMUM_PA,
        // PressureUnit.KILOPASCALS,
        // pressureUnit );
        // final double pressureDefault = UnitConversion
        // .convertPressure( PhysicsConstants.PRESSURE_REFERENCE_KPA,
        // PressureUnit.KILOPASCALS,
        // pressureUnit );

        // _numberConverter.setMinimumValue( pressureMinimum );
        // _numberConverter.setMaximumValue( pressureMaximum );
        // _numberConverter.setDefaultValue( pressureDefault );

        // Forward this method to the subsidiary controls.
        // NOTE: Make sure that there is enough room for the value expressed in
        // the new units, by setting the largest magnitude from all available
        // units. Otherwise, as the value is bounded, it changes before min
        // value and max values change, and therefore it becomes clamped as the
        // not-yet-converted old value may not be within the new range, and thus
        // it fires an event, setting the dirty flag.
        final double pressureMaximum = 10d * PhysicsConstants.PRESSURE_MAXIMUM_PA;
        final double pressureMinimum = -pressureMaximum;
        _pressureEditor.setMinimumValue( pressureMinimum );
        _pressureEditor.setMaximumValue( pressureMaximum );
        _pressureSlider.setMin( pressureMinimum );
        _pressureSlider.setMax( pressureMaximum );

        // Now that there is room, set the new units. Min and Max value are
        // calculated again inside setPressureUnit after setting the value.
        _pressureSlider.updatePressureUnit( pressureUnit );
        _pressureEditor.updatePressureUnit( pressureUnit );

        // In order to avoid order-dependency and initial condition Catch-22's,
        // always set the editor's value to match the paired slider's value.
        // NOTE: This doesn't fix the startup problem of the value being the
        // minimum allowed, when the user's cached unit is the default unit.
        Platform.runLater( () -> _pressureEditor.setValue( _pressureSlider.getValue() ) );
    }

}