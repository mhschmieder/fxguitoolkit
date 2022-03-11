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

import com.mhschmieder.commonstoolkit.geo.LatitudeCardinalDirection;
import com.mhschmieder.commonstoolkit.geo.LongitudeCardinalDirection;
import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.text.StringUtilities;
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

    /**
     * Add the two latitude cardinal directions in presentation format, listing
     * the positive direction first (as the default).
     */
    private static final String[] LATITUDE_CARDINAL_DIRECTIONS  =
                                                               new String[] {
                                                                              LatitudeCardinalDirection.NORTH
                                                                                      .toAbbreviatedString(),
                                                                              LatitudeCardinalDirection.SOUTH
                                                                                      .toAbbreviatedString() };

    /**
     * Add the two longitude cardinal directions in presentation format, listing
     * the positive direction first (as the default).
     */
    private static final String[] LONGITUDE_CARDINAL_DIRECTIONS =
                                                                new String[] {
                                                                               LongitudeCardinalDirection.EAST
                                                                                       .toAbbreviatedString(),
                                                                               LongitudeCardinalDirection.WEST
                                                                                       .toAbbreviatedString() };

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

    public static final TextSelector makeLatitudeCardinalDirectionSelector( final ClientProperties clientProperties ) {
        final TextSelector selector = new TextSelector( clientProperties,
                                                        "Latitude Cardinal Directions", //$NON-NLS-1$
                                                        false,
                                                        false,
                                                        false,
                                                        LATITUDE_CARDINAL_DIRECTIONS,
                                                        LatitudeCardinalDirection.defaultValue()
                                                                .toAbbreviatedString() );

        return selector;
    }

    public static final TextSelector makeLongitudeCardinalDirectionSelector( final ClientProperties clientProperties ) {
        final TextSelector selector = new TextSelector( clientProperties,
                                                        "Longitude Cardinal Directions", //$NON-NLS-1$
                                                        false,
                                                        false,
                                                        false,
                                                        LONGITUDE_CARDINAL_DIRECTIONS,
                                                        LongitudeCardinalDirection.defaultValue()
                                                                .toAbbreviatedString() );

        return selector;
    }

    /**
     * Returns an integer-based editor for Latitude Degrees in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { -89, +89 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Latitude
     */
    public static final IntegerEditor makeLatitudeDegreesEditor( final ClientProperties clientProperties ) {
        // Declare value increment/decrement amount for up and down arrow keys,
        // set to 1 degree as we are using DMS as separate values.
        final int valueIncrementDegrees = 1;

        final IntegerEditor latitudeDegreesEditor = new IntegerEditor( clientProperties,
                                                                       "0", //$NON-NLS-1$
                                                                       "Latitude Degrees", //$NON-NLS-1$
                                                                       -89,
                                                                       89,
                                                                       0,
                                                                       valueIncrementDegrees );

        latitudeDegreesEditor.setMeasurementUnitString( StringUtilities.DEGREES_SYMBOL );

        return latitudeDegreesEditor;
    }

    /**
     * Returns an integer-based editor for Longitude Degrees in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { -179, +179 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Longitude
     */
    public static final IntegerEditor makeLongitudeDegreesEditor( final ClientProperties clientProperties ) {
        // Declare value increment/decrement amount for up and down arrow keys,
        // set to 1 degree as we are using DMS as separate values.
        final int valueIncrementDegrees = 1;

        final IntegerEditor longitudeDegreesEditor = new IntegerEditor( clientProperties,
                                                                        "0", //$NON-NLS-1$
                                                                        "Longitude Degrees", //$NON-NLS-1$
                                                                        -179,
                                                                        179,
                                                                        0,
                                                                        valueIncrementDegrees );

        longitudeDegreesEditor.setMeasurementUnitString( StringUtilities.DEGREES_SYMBOL );

        return longitudeDegreesEditor;
    }

    /**
     * Returns an integer-based editor for Latitude Minutes in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Latitude
     */
    public static final IntegerEditor makeLatitudeMinutesEditor( final ClientProperties clientProperties ) {
        final IntegerEditor latitudeMinutesEditor = makeMinutesIntegerEditor( clientProperties,
                                                                              "Latitude Minutes" ); //$NON-NLS-1$

        return latitudeMinutesEditor;
    }

    /**
     * Returns an integer-based editor for Longitude Minutes in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Longitude
     */
    public static final IntegerEditor makeLongitudeMinutesEditor( final ClientProperties clientProperties ) {
        final IntegerEditor longitudeMinutesEditor =
                                                   makeMinutesIntegerEditor( clientProperties,
                                                                             "Longitude Minutes" ); //$NON-NLS-1$

        return longitudeMinutesEditor;
    }

    /**
     * Returns an integer-based editor for Minutes, in unspecified context as
     * there are certain aspects that hold for all domains where this unit is
     * used. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @param tooltipText
     *            The optional Tool Tip Text to display when the user hovers
     * @return An {@link IntegerEditor} set to the allowed range for Minutes
     */
    public static final IntegerEditor makeMinutesIntegerEditor( final ClientProperties clientProperties,
                                                                final String tooltipText ) {
        // Declare value increment/decrement amount for up and down arrow keys,
        // set to 1 minute as this editor only works with integers.
        final int valueIncrementMinutes = 1;

        final IntegerEditor minutesEditor = new IntegerEditor( clientProperties,
                                                               "0", //$NON-NLS-1$
                                                               tooltipText,
                                                               0,
                                                               59,
                                                               0,
                                                               valueIncrementMinutes );

        minutesEditor.setMeasurementUnitString( "'" ); //$NON-NLS-1$

        return minutesEditor;
    }

    /**
     * Returns an integer-based editor for Latitude Seconds in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Latitude
     */
    public static final IntegerEditor makeLatitudeSecondsEditor( final ClientProperties clientProperties ) {
        final IntegerEditor latitudeSecondsEditor = makeSecondsIntegerEditor( clientProperties,
                                                                              "Latitude Seconds" ); //$NON-NLS-1$

        return latitudeSecondsEditor;
    }

    /**
     * Returns an integer-based editor for Longitude Seconds in the context of a
     * DMS triplet as opposed to floating-point single-number DD (Decimal
     * Degrees) format. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @return An {@link IntegerEditor} set to the allowed range for Longitude
     */
    public static final IntegerEditor makeLongitudeSecondsEditor( final ClientProperties clientProperties ) {
        final IntegerEditor longitudeSecondsEditor =
                                                   makeSecondsIntegerEditor( clientProperties,
                                                                             "Longitude Seconds" ); //$NON-NLS-1$

        return longitudeSecondsEditor;
    }

    /**
     * Returns an integer-based editor for Seconds, in unspecified context as
     * there are certain aspects that hold for all domains where this unit is
     * used. No wrap is supported; only clamping to { 0, 59 }.
     *
     * @param clientProperties
     *            The {@link ClientProperties} for OS, Locale, etc.
     * @param tooltipText
     *            The optional Tool Tip Text to display when the user hovers
     * @return An {@link IntegerEditor} set to the allowed range for Seconds
     */
    public static final IntegerEditor makeSecondsIntegerEditor( final ClientProperties clientProperties,
                                                                final String tooltipText ) {
        // Declare value increment/decrement amount for up and down arrow keys,
        // set to 1 second as this editor only works with integers.
        final int valueIncrementSeconds = 1;

        final IntegerEditor secondsEditor = new IntegerEditor( clientProperties,
                                                               "0", //$NON-NLS-1$
                                                               tooltipText,
                                                               0,
                                                               59,
                                                               0,
                                                               valueIncrementSeconds );

        secondsEditor.setMeasurementUnitString( "\"" ); //$NON-NLS-1$

        return secondsEditor;
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
