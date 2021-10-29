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

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.commonstoolkit.physics.MeasurementUnits;
import com.mhschmieder.commonstoolkit.physics.PressureUnit;
import com.mhschmieder.commonstoolkit.physics.TemperatureUnit;
import com.mhschmieder.commonstoolkit.physics.WeightUnit;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.DistanceUnitSelector;
import com.mhschmieder.fxguitoolkit.control.PressureUnitSelector;
import com.mhschmieder.fxguitoolkit.control.TemperatureUnitSelector;
import com.mhschmieder.fxguitoolkit.control.WeightUnitSelector;

import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class MeasurementUnitsPane extends VBox {

    protected DistanceUnitSelector    _distanceUnitSelector;
    // protected AngleUnitSelector _angleUnitSelector;
    protected WeightUnitSelector      _weightUnitSelector;
    protected TemperatureUnitSelector _temperatureUnitSelector;
    protected PressureUnitSelector    _pressureUnitSelector;

    // Cache a reference to the global Measurement Units.
    protected MeasurementUnits        measurementUnits;

    public MeasurementUnitsPane( final SessionContext sessionContext ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( sessionContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void bindProperties() {
        // A failed attempt to be less verbose. Perhaps could be improved.
        // Callable< String > distUnitStr = () -> measurementUnits
        // .getDistanceUnit().toCanonicalString();
        // ObjectProperty< DistanceUnit > distanceUnitProp = measurementUnits
        // .distanceUnitProperty();
        // StringBinding sb = Bindings.createStringBinding( distUnitStr,
        // distanceUnitProp );
        // _distanceUnitSelector.valueProperty().bind( sb );

        _distanceUnitSelector.getEditor().textProperty().bind( new StringBinding() {
            {
                bind( measurementUnits.distanceUnitProperty() );
            }

            @Override
            protected String computeValue() {
                return measurementUnits.getDistanceUnit().toCanonicalString();
            }
        } );

        // _angleUnitSelector..getEditor().textProperty().bind( new
        // StringBinding() {
        // {
        // bind( measurementUnits.angleUnitProperty() );
        // }
        //
        // @Override
        // protected String computeValue() {
        // return measurementUnits.getAngleUnit().toCanonicalString();
        // }
        // } );

        _weightUnitSelector.getEditor().textProperty().bind( new StringBinding() {
            {
                bind( measurementUnits.weightUnitProperty() );
            }

            @Override
            protected String computeValue() {
                return measurementUnits.getWeightUnit().toCanonicalString();
            }
        } );

        _temperatureUnitSelector.getEditor().textProperty().bind( new StringBinding() {
            {
                bind( measurementUnits.temperatureUnitProperty() );
            }

            @Override
            protected String computeValue() {
                return measurementUnits.getTemperatureUnit().toCanonicalString();
            }
        } );

        _pressureUnitSelector.getEditor().textProperty().bind( new StringBinding() {
            {
                bind( measurementUnits.pressureUnitProperty() );
            }

            @Override
            protected String computeValue() {
                return measurementUnits.getPressureUnit().toCanonicalString();
            }
        } );
    }

    private void initPane( final SessionContext sessionContext ) {
        final Label distanceUnitLabel = GuiUtilities.getControlLabel( "Distance Unit" ); //$NON-NLS-1$
        _distanceUnitSelector = new DistanceUnitSelector( sessionContext,
                                                          false,
                                                          false,
                                                          DistanceUnit.defaultValue() );

        // final Label angleUnitLabel = SceneGraphNodeUtilities.getControlLabel(
        // "Angle Unit" ); //$NON-NLS-1$
        // _angleUnitSelector = new AngleUnitSelector( sessionContext, false,
        // AngleUnit.defaultValue() );

        final Label weightUnitLabel = GuiUtilities.getControlLabel( "Weight Unit" ); //$NON-NLS-1$
        _weightUnitSelector = new WeightUnitSelector( sessionContext,
                                                      false,
                                                      WeightUnit.defaultValue() );

        final Label temperatureUnitLabel = GuiUtilities.getControlLabel( "Temperature Unit" ); //$NON-NLS-1$
        _temperatureUnitSelector = new TemperatureUnitSelector( sessionContext,
                                                                false,
                                                                TemperatureUnit.defaultValue() );

        final Label pressureUnitLabel = GuiUtilities.getControlLabel( "Pressure Unit" ); //$NON-NLS-1$
        _pressureUnitSelector = new PressureUnitSelector( sessionContext,
                                                          false,
                                                          PressureUnit.defaultValue() );

        // Create a grid to host the Measurement Units controls.
        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 16d );
        gridPane.setVgap( 16d );
        gridPane.setPadding( new Insets( 0d, 16d, 0d, 16d ) );

        int row = 0;
        gridPane.add( distanceUnitLabel, 0, row );
        gridPane.add( _distanceUnitSelector, 1, row++ );

        // TODO: Give some thoughts to Angle Units and implement.
        // gridPane.add( angleUnitLabel, 0, row );
        // gridPane.add( _angleUnitSelector, 1, row++ );

        gridPane.add( weightUnitLabel, 0, row );
        gridPane.add( _weightUnitSelector, 1, row++ );

        gridPane.add( temperatureUnitLabel, 0, row );
        gridPane.add( _temperatureUnitSelector, 1, row++ );

        gridPane.add( pressureUnitLabel, 0, row );
        gridPane.add( _pressureUnitSelector, 1, row++ );

        getChildren().addAll( gridPane );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 16 ) );

        // Ensure that stacked components are all the same width.
        _distanceUnitSelector.setMinWidth( 120d );
        _distanceUnitSelector.setMaxWidth( 120d );

        // _angleUnitSelector.minWidthProperty().bind(
        // _distanceUnitSelector.widthProperty() );
        // _angleUnitSelector.maxWidthProperty().bind(
        // _distanceUnitSelector.widthProperty() );

        _weightUnitSelector.minWidthProperty().bind( _distanceUnitSelector.widthProperty() );
        _weightUnitSelector.maxWidthProperty().bind( _distanceUnitSelector.widthProperty() );

        _temperatureUnitSelector.minWidthProperty().bind( _distanceUnitSelector.widthProperty() );
        _temperatureUnitSelector.maxWidthProperty().bind( _distanceUnitSelector.widthProperty() );

        _pressureUnitSelector.minWidthProperty().bind( _distanceUnitSelector.widthProperty() );
        _pressureUnitSelector.maxWidthProperty().bind( _distanceUnitSelector.widthProperty() );

        // Bind the data model to the respective GUI components.
        // TODO: Determine whether the listeners below are rendered redundant.
        // TODO: Re-enable this after figuring out why it wasn't part of the
        // previous release.
        // bindProperties();

        // Load the event handler for the Distance Unit Selector.
        _distanceUnitSelector.setOnAction( evt -> {
            final DistanceUnit distanceUnit = _distanceUnitSelector.getDistanceUnit();
            measurementUnits.setDistanceUnit( distanceUnit );
        } );

        // Load the event handler for the Angle Unit Selector.
        // _angleUnitSelector.setOnAction( evt -> {
        // final AngleUnit angleUnit = _angleUnitSelector.getAngleUnit();
        // measurementUnits.setAngleUnit( angleUnit );
        // } );

        // Load the event handler for the Weight Unit Selector.
        _weightUnitSelector.setOnAction( evt -> {
            final WeightUnit weightUnit = _weightUnitSelector.getWeightUnit();
            measurementUnits.setWeightUnit( weightUnit );
        } );

        // Load the event handler for the Temperature Unit Selector.
        _temperatureUnitSelector.setOnAction( evt -> {
            final TemperatureUnit temperatureUnit = _temperatureUnitSelector.getTemperatureUnit();
            measurementUnits.setTemperatureUnit( temperatureUnit );
        } );

        // Load the event handler for the Pressure Unit Selector.
        _pressureUnitSelector.setOnAction( evt -> {
            final PressureUnit pressureUnit = _pressureUnitSelector.getPressureUnit();
            measurementUnits.setPressureUnit( pressureUnit );
        } );
    }

    /**
     * Reset all fields to the default values, regardless of state.
     */
    public void reset() {
        measurementUnits.reset();

        // NOTE: We have to update the selected items as well, as the bindings
        // only work with respect to user actions vs. programmatic updates.
        updateMeasurementUnits( measurementUnits );
    }

    // Set and bind the Measurement Units reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setMeasurementUnits( final MeasurementUnits pMeasurementUnits ) {
        // Cache the Measurement Units reference.
        measurementUnits = pMeasurementUnits;

        // Set all of the initial selections, as the bindings don't do this due
        // to differences between object properties and computed string values
        // with regards to trigger points for bindings to kick in on initial
        // evaluation of an unchanged state.
        updateMeasurementUnits( pMeasurementUnits );

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    // public final void updateAngleUnit( final AngleUnit angleUnit ) {
    // _angleUnitSelector.setValue( angleUnit.toCanonicalString() );
    // }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        _distanceUnitSelector.setValue( distanceUnit.toCanonicalString() );
    }

    public void updateMeasurementUnits( final MeasurementUnits pMeasurementsUnits ) {
        updateDistanceUnit( pMeasurementsUnits.getDistanceUnit() );
        // updateAngleUnit( pMeasurementsUnits.getAngleUnit() );
        updateWeightUnit( pMeasurementsUnits.getWeightUnit() );
        updateTemperatureUnit( pMeasurementsUnits.getTemperatureUnit() );
        updatePressureUnit( pMeasurementsUnits.getPressureUnit() );
    }

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        _pressureUnitSelector.setValue( pressureUnit.toCanonicalString() );
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        _temperatureUnitSelector.setValue( temperatureUnit.toCanonicalString() );
    }

    public void updateWeightUnit( final WeightUnit weightUnit ) {
        _weightUnitSelector.setValue( weightUnit.toCanonicalString() );
    }

}
