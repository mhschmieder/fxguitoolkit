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

import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.AngleEditor;
import com.mhschmieder.fxguitoolkit.control.AngleSlider;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AnglePane extends VBox {

    // Cache the main GUI controls so they can be set and queried externally.
    public AngleSlider _angleSlider;
    public AngleEditor _angleEditor;

    public AnglePane( final ClientProperties clientProperties,
                      final String labelText,
                      final boolean useContextMenu ) {
        // Always call the superclass constructor first!
        super();

        initPane( clientProperties, labelText, useContextMenu );
    }

    public final double getAngleDegrees() {
        return _angleSlider.getValue();
    }

    private final void initPane( final ClientProperties clientProperties,
                                 final String labelText,
                                 final boolean useContextMenu ) {
        // Create a default Angle Slider.
        _angleSlider = new AngleSlider( clientProperties, useContextMenu );

        // Conform the associated editor (text field) to the slider attributes.
        _angleEditor = ControlFactory.getAngleSliderEditor( clientProperties, _angleSlider );
        _angleEditor.setPrefWidth( 70d );

        final Label angleLabel = GuiUtilities.getControlLabel( labelText );

        final HBox hbox = new HBox();
        hbox.getChildren().addAll( angleLabel, _angleEditor );
        hbox.setAlignment( Pos.BASELINE_LEFT );
        hbox.setSpacing( 10d );

        getChildren().addAll( hbox, _angleSlider );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 6d ) );

        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range, also accounting for angle period adjustments.
        // NOTE: We no longer need the more complicated text binding with the
        // new converter and formatter API calls, but rather than delete that
        // code, it is good to keep around as an example of how to use the newer
        // API calls from Oracle. Our Angle Editor has a custom double precision
        // value property that allows for direct bindings with the slider's
        // double precision value property, bypassing this step. The text
        // property of the underlying Text Field stays in sync as a formatted
        // number via other methods and callbacks.
        _angleEditor.valueProperty().bindBidirectional( _angleSlider.valueProperty() );
        // _angleEditor.textProperty().bindBidirectional(
        // _angleSlider.valueProperty(),
        // new AngleConverter( _angleEditor
        // .getMeasurementUnitString(),
        // _angleEditor
        // .getNumberFormat(),
        // AngleSlider.INITIAL_ANGLE_DEGREES_DEFAULT,
        // _angleSlider.getMin(),
        // _angleSlider
        // .getMax() ) );
    }

    public final void saveEdits() {
        // Save and/or correct the current edited value.
        final double editedValue = _angleEditor.getClampedValue();
        if ( editedValue != _angleEditor.getValue() ) {
            _angleEditor.setValue( editedValue );

            // Sync the slider and read-only label to the saved value in the
            // editor.
            setAngleDegrees( _angleEditor.getValue() );
        }
    }

    public final void setAngleDegrees( final double angleDegrees ) {
        // Forward this to the Angle Slider so it can unwrap the angle.
        _angleSlider.setAngleDegrees( angleDegrees );
    }

    public final void setGesturesEnabled( final boolean gesturesEnabled ) {
        _angleSlider.setGesturesEnabled( gesturesEnabled );
    }

    public final void setMaximum( final double maximumAngle ) {
        _angleSlider.setMax( maximumAngle );
        _angleEditor.setMaximumValue( maximumAngle );
    }

    public final void setMinimum( final double minimumAngle ) {
        _angleSlider.setMin( minimumAngle );
        _angleEditor.setMinimumValue( minimumAngle );
    }

    public final void setNumericRange( final double minimumAngle, final double maximumAngle ) {
        setMinimum( minimumAngle );
        setMaximum( maximumAngle );
    }

    /**
     * Set the new Scrolling Sensitivity for the Angle Sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public final void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        _angleSlider.setScrollingSensitivity( scrollingSensitivity );
    }

    public final void toggleGestures() {
        _angleSlider.toggleGestures();
    }

    public final void updateAngleUnit( final AngleUnit angleUnit ) {
        // TODO: Implement this, and also for the associated slider?
        // _angleEditor.updateAngleUnit( angleUnit );
    }

}