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
import com.mhschmieder.commonstoolkit.physics.NaturalEnvironment;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import com.mhschmieder.fxguitoolkit.control.HumidityEditor;
import com.mhschmieder.fxguitoolkit.control.HumiditySlider;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class HumidityPane extends VBox {

    private Label          _humidityLabel;
    public HumiditySlider  _humiditySlider;
    public HumidityEditor  _humidityEditor;

    private DoubleProperty humidityRelative;

    public HumidityPane( final SessionContext sessionContext ) {
        // Always call the superclass constructor first!
        super( 6d );

        humidityRelative = new SimpleDoubleProperty();

        initPane( sessionContext );
    }

    private void bindProperties() {
        // Bidirectionally bind the Humidity property to its associated text
        // input control's value property (which reflects committed edits).
        // NOTE: This is OK because we embed unit conversion in DoubleEditor.
        _humidityEditor.valueProperty().bindBidirectional( humidityRelativeProperty() );

        // Bidirectionally bind the slider to an editable text field restricted
        // to the slider range.
        // NOTE: This is commented out because we have to deal with units.
        // _humiditySlider.valueProperty()
        // .bindBidirectional( _humidityEditor.valueProperty() );

        // We don't need a listener for the Relative Humidity slider, as there
        // is only one Humidity Unit option, so just use data binding directly.
        _humiditySlider.valueProperty().bindBidirectional( humidityRelativeProperty() );
    }

    public double getHumidityRelative() {
        return humidityRelative.get();
    }

    public DoubleProperty humidityRelativeProperty() {
        return humidityRelative;
    }

    private void initPane( final SessionContext sessionContext ) {
        // Make a bolded label to clearly identify the functionality.
        _humidityLabel = GuiUtilities.getColumnHeader( "Relative Humidity" ); //$NON-NLS-1$

        // Create a Relative Humidity slider.
        _humiditySlider = new HumiditySlider( sessionContext );

        // Conform the associated editor (text field) to the slider attributes.
        _humidityEditor = ControlFactory.getHumiditySliderEditor( sessionContext, _humiditySlider );
        _humidityEditor.setPrefWidth( 100d );
        _humidityEditor.setMaxWidth( 100d );

        getChildren().addAll( _humidityLabel, _humiditySlider, _humidityEditor );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 6d ) );

        // Make sure the Humidity Slider always gets vertical grow priority.
        VBox.setVgrow( _humiditySlider, Priority.ALWAYS );
    }

    public void reset() {
        _humiditySlider.setValue( NaturalEnvironment.HUMIDITY_RELATIVE_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _humidityLabel.setTextFill( foregroundColor );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        _humiditySlider.setGesturesEnabled( gesturesEnabled );
    }

    public void setHumidityRelative( final double pHumidityRelative ) {
        humidityRelative.set( pHumidityRelative );
    }

    // Set and bind the Relative Humidity property reference.
    // :NOTE: This should be done only once, to avoid breaking bindings.
    public void setHumidityRelativeProperty( final DoubleProperty pHumidityRelative ) {
        // Cache the Relative Humidity property reference.
        humidityRelative = pHumidityRelative;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    /**
     * Set the new Scrolling Sensitivity for the Humidity Sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        _humiditySlider.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        _humiditySlider.toggleGestures();
    }

}