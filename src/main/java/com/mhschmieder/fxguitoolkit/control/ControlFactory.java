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
package com.mhschmieder.fxguitoolkit.control;

import java.net.URL;

import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * This is a factory for generating customized controls. It is a way of avoiding
 * class derivation where that is not necessary for altering the default
 * behavior of the core JavaFX controls -- especially for stuff like spinners.
 * <p>
 * One of its roles is to enforce preferred style guidelines and common
 * behavior.
 *
 * @version 0.1
 *
 * @author Mark Schmieder
 */
public class ControlFactory {

    // Predefine the notes/notices colors.
    public static final Color     NOTES_BACKGROUND_COLOR        = Color.FLORALWHITE;
    public static final Color     NOTES_FOREGROUND_COLOR        = Color.BLACK;

    // Helper method to get an Angle Editor, standalone or paired.
    public static final AngleEditor makeAngleEditor( final ClientProperties clientProperties,
                                                     final String tooltipText,
                                                     final double minimumValue,
                                                     final double maximumValue,
                                                     final double initialValue ) {
        final AngleEditor angleEditor = makeAngleEditor( clientProperties,
                                                         tooltipText,
                                                         AngleUnit.DEGREES.toPresentationString(),
                                                         minimumValue,
                                                         maximumValue,
                                                         initialValue );

        return angleEditor;
    }

    // Helper method to get an Angle Editor, standalone or paired.
    public static final AngleEditor makeAngleEditor( final ClientProperties clientProperties,
                                                     final String tooltipText,
                                                     final int minFractionDigitsFormat,
                                                     final int maxFractionDigitsFormat,
                                                     final int minFractionDigitsParse,
                                                     final int maxFractionDigitsParse,
                                                     final String measurementUnit,
                                                     final double minimumValue,
                                                     final double maximumValue,
                                                     final double initialValue ) {
        // Get the current value and format it as initial text.
        final String initialText = Double.toString( initialValue );

        final AngleEditor angleEditor = new AngleEditor( clientProperties,
                                                         initialText,
                                                         tooltipText,
                                                         minFractionDigitsFormat,
                                                         maxFractionDigitsFormat,
                                                         minFractionDigitsParse,
                                                         maxFractionDigitsParse,
                                                         minimumValue,
                                                         maximumValue,
                                                         initialValue );

        angleEditor.setMeasurementUnitString( measurementUnit );

        return angleEditor;
    }

    // Helper method to get an Angle Editor, standalone or paired.
    public static final AngleEditor makeAngleEditor( final ClientProperties clientProperties,
                                                     final String tooltipText,
                                                     final String measurementUnit,
                                                     final double minimumValue,
                                                     final double maximumValue,
                                                     final double initialValue ) {
        final AngleEditor angleEditor = makeAngleEditor( clientProperties,
                                                         tooltipText,
                                                         0,
                                                         2,
                                                         0,
                                                         10,
                                                         measurementUnit,
                                                         minimumValue,
                                                         maximumValue,
                                                         initialValue );

        return angleEditor;
    }

    // Helper method to get an Angle Editor to pair with a slider.
    public static final AngleEditor makeAngleSliderEditor( final ClientProperties clientProperties,
                                                           final AngleSlider angleSlider ) {
        final AngleEditor angleEditor = makeAngleSliderEditor( clientProperties,
                                                               angleSlider,
                                                               0,
                                                               2,
                                                               0,
                                                               10 );

        return angleEditor;
    }

    // Helper method to get an Angle Editor to pair with a slider.
    public static final AngleEditor makeAngleSliderEditor( final ClientProperties clientProperties,
                                                           final AngleSlider angleSlider,
                                                           final int minFractionDigitsFormat,
                                                           final int maxFractionDigitsFormat,
                                                           final int minFractionDigitsParse,
                                                           final int maxFractionDigitsParse ) {
        // Use the current slider value and limits to set the number editor.
        final AngleEditor angleEditor = makeAngleEditor( clientProperties,
                                                         null,
                                                         minFractionDigitsFormat,
                                                         maxFractionDigitsFormat,
                                                         minFractionDigitsParse,
                                                         maxFractionDigitsParse,
                                                         angleSlider.getMeasurementUnitString(),
                                                         angleSlider.getMin(),
                                                         angleSlider.getMax(),
                                                         angleSlider.getValue() );

        return angleEditor;
    }

    // Helper method to get an Opacity Editor, standalone or paired.
    public static final DoubleEditor makeOpacityEditor( final ClientProperties clientProperties,
                                                        final String tooltipText,
                                                        final String measurementUnit,
                                                        final double minimumValue,
                                                        final double maximumValue,
                                                        final double initialValue ) {
        // Get the current value and format it as initial text.
        final String initialText = Double.toString( initialValue );

        // Declare value increment/decrement amount for up and down arrow keys,
        // set to 0.5% as a mid-way value of general use for auto-increment.
        final double valueIncrementPercent = 0.5d;

        // NOTE: We use up to one decimal place of precision for displaying
        // opacity, and one decimal place for parsing opacity.
        final DoubleEditor opacityEditor = new DoubleEditor( clientProperties,
                                                             initialText,
                                                             tooltipText,
                                                             0,
                                                             1,
                                                             0,
                                                             1,
                                                             initialValue,
                                                             valueIncrementPercent );

        opacityEditor.setMeasurementUnitString( measurementUnit );

        return opacityEditor;
    }

    // Helper method to get an Opacity Editor to pair with a slider.
    public static final DoubleEditor makeOpacitySliderEditor( final ClientProperties clientProperties,
                                                              final OpacitySlider opacitySlider ) {
        // Use the current slider value and limits to set the number editor.
        final DoubleEditor opacityEditor = makeOpacityEditor( clientProperties,
                                                              null,
                                                              opacitySlider
                                                                      .getMeasurementUnitString(),
                                                              opacitySlider.getMin(),
                                                              opacitySlider.getMax(),
                                                              opacitySlider.getValue() );

        return opacityEditor;
    }

    public static final Spinner< Double > makeDoubleSpinner( final ClientProperties clientProperties,
                                                             final boolean toolbarContext,
                                                             final String valueDescriptor,
                                                             final double minimumNumericValue,
                                                             final double maximumNumericValue,
                                                             final double defaultNumericValue,
                                                             final double numericIncrement,
                                                             final boolean wrapAround,
                                                             final String numericFormatterPattern,
                                                             final String measurementUnit,
                                                             final double maximumSpinnerWidth ) {
        // Start with a fully initialized Spinner, with range specified.
        final Spinner< Double > doubleSpinner = new Spinner<>( minimumNumericValue,
                                                               maximumNumericValue,
                                                               defaultNumericValue,
                                                               numericIncrement );
        DoubleSpinnerStringConverter.createFor( doubleSpinner,
                                                valueDescriptor,
                                                defaultNumericValue,
                                                maximumSpinnerWidth,
                                                wrapAround,
                                                numericFormatterPattern,
                                                measurementUnit,
                                                clientProperties.locale );

        if ( toolbarContext ) {
            // Apply drop-shadow effects when the mouse enters this Node.
            GuiUtilities.applyDropShadowEffect( doubleSpinner );
        }
        else {
            // Set the full list of shared Spinner Properties (CSS etc.).
            GuiUtilities.setSpinnerProperties( doubleSpinner );
        }

        // Return the fully initialized double Spinner.
        return doubleSpinner;
    }

    public static final Spinner< Integer > makeIntegerSpinner( final ClientProperties clientProperties,
                                                               final boolean toolbarContext,
                                                               final String valueDescriptor,
                                                               final int minimumNumericValue,
                                                               final int maximumNumericValue,
                                                               final int defaultNumericValue,
                                                               final int numericIncrement,
                                                               final boolean wrapAround,
                                                               final String numericFormatterPattern,
                                                               final String measurementUnit,
                                                               final double maximumSpinnerWidth ) {
        // Start with a fully initialized Spinner, with range specified.
        final Spinner< Integer > integerSpinner = new Spinner<>( minimumNumericValue,
                                                                 maximumNumericValue,
                                                                 defaultNumericValue,
                                                                 numericIncrement );
        IntegerSpinnerStringConverter.createFor( integerSpinner,
                                                 valueDescriptor,
                                                 defaultNumericValue,
                                                 maximumSpinnerWidth,
                                                 wrapAround,
                                                 numericFormatterPattern,
                                                 measurementUnit,
                                                 clientProperties.locale );

        if ( toolbarContext ) {
            // Apply drop-shadow effects when the mouse enters this Node.
            GuiUtilities.applyDropShadowEffect( integerSpinner );
        }
        else {
            // Set the full list of shared Spinner Properties (CSS etc.).
            GuiUtilities.setSpinnerProperties( integerSpinner );
        }

        // Return the fully initialized integer Spinner.
        return integerSpinner;
    }

    // Helper method to get a custom Temperature Editor.
    public static final TemperatureEditor makeTemperatureEditor( final ClientProperties clientProperties ) {
        // Format the default Temperature value as the initial text.
        final double initialValue = TemperatureSlider.INITIAL_TEMPERATURE_KELVIN_DEFAULT;
        final String initialText = Double.toString( initialValue );

        final TemperatureEditor temperatureEditor =
                                                  new TemperatureEditor( clientProperties,
                                                                         initialText,
                                                                         null,
                                                                         TemperatureSlider.MINIMUM_TEMPERATURE_KELVIN_DEFAULT,
                                                                         TemperatureSlider.MAXIMUM_TEMPERATURE_KELVIN_DEFAULT,
                                                                         initialValue );

        return temperatureEditor;
    }

    // Helper method to get a custom Pressure Editor.
    public static final PressureEditor makePressureEditor( final ClientProperties clientProperties ) {
        // Format the default Pressure value as the initial text.
        final double initialValue = PressureSlider.INITIAL_PRESSURE_PASCALS_DEFAULT;
        final String initialText = Double.toString( initialValue );

        final PressureEditor pressureEditor =
                                            new PressureEditor( clientProperties,
                                                                initialText,
                                                                null,
                                                                PressureSlider.MINIMUM_PRESSURE_PASCALS_DEFAULT,
                                                                PressureSlider.MAXIMUM_PRESSURE_PASCALS_DEFAULT,
                                                                initialValue );

        return pressureEditor;
    }

    // Helper method to get a humidity editor to pair with a slider.
    public static final HumidityEditor makeHumiditySliderEditor( final ClientProperties clientProperties,
                                                                 final HumiditySlider humiditySlider ) {
        // Get the current slider value and format it as initial text.
        final double initialValue = HumiditySlider.INITIAL_RELATIVE_HUMIDITY_DEFAULT;
        final String initialText = Double.toString( initialValue );

        final HumidityEditor humidityEditor =
                                            new HumidityEditor( clientProperties,
                                                                initialText,
                                                                null,
                                                                HumiditySlider.MINIMUM_RELATIVE_HUMIDITY_DEFAULT,
                                                                HumiditySlider.MAXIMUM_RELATIVE_HUMIDITY_DEFAULT,
                                                                initialValue );

        final String measurementUnitString = humiditySlider.getMeasurementUnitString();
        humidityEditor.setMeasurementUnitString( measurementUnitString );

        return humidityEditor;
    }

    public static final TextArea makeNoticeTextArea( final String noticeTextContent,
                                                     final boolean editable,
                                                     final int numberOfColumns,
                                                     final int numberOfRows ) {
        final TextArea noticeTextArea = new TextArea( noticeTextContent );
        noticeTextArea.setEditable( editable );
        noticeTextArea.setWrapText( true );
        noticeTextArea.setPrefColumnCount( numberOfColumns );
        noticeTextArea.setPrefRowCount( numberOfRows );

        final Background background = LayoutFactory.makeRegionBackground( NOTES_BACKGROUND_COLOR );
        noticeTextArea.setBackground( background );

        return noticeTextArea;
    }

    public static final TextFlow makeNoticeTextFlow( final Text noticeText,
                                                     final int numberOfColumns,
                                                     final int numberOfRows ) {
        // NOTE: The sizing is a temporary hack to avoid full-screen.
        final TextFlow noticeTextFlow = new TextFlow( noticeText );
        noticeTextFlow.setMaxWidth( numberOfRows * 12d );
        noticeTextFlow.setMaxHeight( numberOfColumns * 12d );

        final Background background = LayoutFactory.makeRegionBackground( NOTES_BACKGROUND_COLOR );
        noticeTextFlow.setBackground( background );

        return noticeTextFlow;
    }

    public static final WebView makeNoticeWebView( final String noticeHtmlContent ) {
        final WebView noticeWebView = new WebView();
        noticeWebView.getEngine().loadContent( noticeHtmlContent );
        noticeWebView.autosize();

        return noticeWebView;
    }

    public static final WebView makeNoticeWebView( final URL noticeUrl ) {
        final WebView noticeWebView = new WebView();
        final WebEngine noticeWebEngine = noticeWebView.getEngine();
        noticeWebEngine.load( noticeUrl.toString() );

        // NOTE: We're having problems with horizontal scroll bars showing up
        // on Windows 10, if not also on Mac OS, unless we down-scale the font.
        // NOTE: This has changed with recent Java updates, so the DPI based
        // scaling was backed out and replaced downstream with font scaling.
        // noticeWebView.setZoom( Screen.getPrimary().getDpi() / 96 );
        noticeWebView.autosize();

        return noticeWebView;
    }

}
