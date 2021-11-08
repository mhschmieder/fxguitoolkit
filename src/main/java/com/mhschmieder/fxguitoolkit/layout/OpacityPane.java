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

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import com.mhschmieder.fxguitoolkit.control.DoubleEditor;
import com.mhschmieder.fxguitoolkit.control.OpacitySlider;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This layout manager couples an opacity editor with a slider.
 */
public final class OpacityPane extends VBox {

    private Label          _opacityLabel;
    public OpacitySlider   _opacitySlider;
    public DoubleEditor    _opacityEditor;

    private DoubleProperty opacityPercent;

    public OpacityPane( final ClientProperties clientProperties, final String labelText ) {
        // Always call the superclass constructor first!
        super();

        opacityPercent = new SimpleDoubleProperty();

        initPane( clientProperties, labelText );
    }

    private void bindProperties() {
        // Bidirectionally bind the Opacity property to its associated text
        // input control's value property (which reflects committed edits).
        // :NOTE: This is OK because we embed unit conversion in DoubleEditor.
        _opacityEditor.valueProperty().bindBidirectional( opacityPercentProperty() );

        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range.
        // NOTE: This is commented out because we have to deal with units.
        // _opacitySlider.valueProperty()
        // .bindBidirectional( _opacityEditor.valueProperty() );

        // We don't need a listener for the Opacity slider, as there is only one
        // Opacity Unit option, so just use data binding directly.
        _opacitySlider.valueProperty().bindBidirectional( opacityPercentProperty() );
    }

    public double getOpacityPercent() {
        return opacityPercent.get();
    }

    private void initPane( final ClientProperties clientProperties, final String labelText ) {
        // Create a default Opacity Slider.
        _opacitySlider = new OpacitySlider( clientProperties );

        // Conform the associated editor (text field) to the slider attributes.
        _opacityEditor = ControlFactory.makeOpacitySliderEditor( clientProperties, _opacitySlider );
        _opacityEditor.setPrefWidth( 70d );

        _opacityLabel = GuiUtilities.getControlLabel( labelText );

        final HBox hbox = new HBox();
        hbox.getChildren().addAll( _opacityLabel, _opacityEditor );
        hbox.setAlignment( Pos.BASELINE_LEFT );
        hbox.setSpacing( 10d );

        getChildren().addAll( hbox, _opacitySlider );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 0d, 16d, 0d, 16d ) );

        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range.
        // NOTE: We no longer need the more complicated text binding with the
        // new converter and formatter API calls, but rather than delete that
        // code, it is good to keep around as an example of how to use the newer
        // API calls from Oracle. Our Opacity Editor has a custom double
        // precision value property that allows for direct bindings with the
        // slider's double precision value property, bypassing this step. The
        // text property of the underlying Text Field stays in sync as a
        // formatted number via other methods and callbacks.
        // NOTE: We don't even need this binding at all anymore, as we instead
        // bind the editor and the slider independently to the data model. This
        // reduces confusion over any master/slave relationships between the
        // paired controls, and seems to retain the higher editor resolution.
        // _opacityEditor.valueProperty().bindBidirectional(
        // _opacitySlider.valueProperty() );
        // _opacityEditor.textProperty().bindBidirectional(
        // _opacitySlider.valueProperty(),
        // new OpacityConverter( _opacityEditor
        // .getMeasurementUnitString(),
        // _opacityEditor
        // .getNumberFormat(),
        // OpacitySlider.INITIAL_OPACITY_DEFAULT,
        // _opacitySlider.getMin(),
        // _opacitySlider
        // .getMax() ) );
    }

    public DoubleProperty opacityPercentProperty() {
        return opacityPercent;
    }

    public void saveEdits() {
        // Save and/or correct the current edited value.
        final double editedValue = _opacityEditor.getClampedValue();
        if ( editedValue != _opacityEditor.getValue() ) {
            _opacityEditor.setValue( editedValue );

            // Sync the slider and read-only label to the saved value in the
            // editor.
            setOpacityPercent( _opacityEditor.getValue() );
        }
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        _opacitySlider.setGesturesEnabled( gesturesEnabled );
    }

    public void setMaximum( final double maximumopacity ) {
        _opacitySlider.setMax( maximumopacity );
        _opacityEditor.setMaximumValue( maximumopacity );
    }

    public void setMinimum( final double minimumopacity ) {
        _opacitySlider.setMin( minimumopacity );
        _opacityEditor.setMinimumValue( minimumopacity );
    }

    public void setNumericRange( final double minimumopacity, final double maximumopacity ) {
        setMinimum( minimumopacity );
        setMaximum( maximumopacity );
    }

    public void setOpacityPercent( final double pOpacityPercent ) {
        // Forward this to the Opacity Slider to keep it in sync.
        opacityPercent.set( pOpacityPercent );
    }

    // Set and bind the Opacity Percent property reference.
    // :NOTE: This should be done only once, to avoid breaking bindings.
    public void setOpacityPercentProperty( final DoubleProperty pOpacityPercent ) {
        // Cache the Opacity Percent property reference.
        opacityPercent = pOpacityPercent;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    /**
     * Set the new Scrolling Sensitivity for the Opacity Sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        _opacitySlider.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        _opacitySlider.toggleGestures();
    }

}
