/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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

import java.text.NumberFormat;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.util.StringConverter;

/**
 * A catch-all numeric slider base class, with some tricks via special
 * parameter handling, to account for the JavaFX Slider not accepting
 * a generic argument, meaning we can't make special versions for long,
 * integer, float, or double, as {@link Slider} uses doubles internally.
 */
public class NumberSlider extends Slider {

    /** Flag for determining whether to support gestures. */
    private boolean gesturesEnabled;

    /** Keep track of the current Scrolling Sensitivity for the Mouse */
    private ScrollingSensitivity scrollingSensitivity;

    /** Maintain a reference to the Measurement Unit label (can be blank). */
    private String measurementUnitString;

    /** Number format cache used to control the mantissa in label formatter. */
    protected NumberFormat numberFormat;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public NumberSlider( final ClientProperties pClientProperties,
                         final double minimumValue,
                         final double maximumValue,
                         final double initialValue,
                         final double majorTickSpacing,
                         final double minorTickSpacing,
                         final double blockIncrement,
                         final boolean useContextMenu ) {
        // NOTE: As this is the older constructor that assumed a precise
        //  associated text field for the slider, we stick to integer-only
        //  tick labels and "snap to ticks" turned on.
        this( pClientProperties, 
              minimumValue, 
              maximumValue, 
              initialValue,
              0,
              0,
              majorTickSpacing,
              minorTickSpacing,
              blockIncrement,
              useContextMenu,
              true );
    }

    public NumberSlider( final ClientProperties pClientProperties,
                         final double minimumValue,
                         final double maximumValue,
                         final double initialValue,
                         final int minFractionDigitsFormat,
                         final int maxFractionDigitsFormat,
                         final double majorTickSpacing,
                         final double minorTickSpacing,
                         final double blockIncrement,
                         final boolean useContextMenu,
                         final boolean snapToTicks ) {
        // Always call the superclass constructor first!
        super( minimumValue, maximumValue, initialValue );

        clientProperties = pClientProperties;

        gesturesEnabled = false;
        scrollingSensitivity = ScrollingSensitivity.defaultValue();
        measurementUnitString = "";

        try {
            initSlider( minFractionDigitsFormat,
                        maxFractionDigitsFormat,
                        majorTickSpacing, 
                        minorTickSpacing, 
                        blockIncrement, 
                        useContextMenu,
                        snapToTicks );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initSlider( final int minFractionDigitsFormat,
                                   final int maxFractionDigitsFormat,
                                   final double majorTickSpacing,
                                   final double minorTickSpacing,
                                   final double blockIncrement,
                                   final boolean useContextMenu,
                                   final boolean snapToTicks ) {
        numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        numberFormat.setGroupingUsed( true );
        numberFormat.setMinimumFractionDigits( minFractionDigitsFormat );
        numberFormat.setMaximumFractionDigits( maxFractionDigitsFormat );

        // Always show the tick labels and tick marks.
        setShowTickLabels( true );
        setShowTickMarks( true );
        setTickResolution( majorTickSpacing, minorTickSpacing );
        setBlockIncrement( blockIncrement );

        GuiUtilities.applyDropShadowEffect( this );

        // Sliders do not have Context Menus by default, but we may need to
        // present the user with a choice regarding the Snap to Ticks feature.
        if ( useContextMenu ) {
            initContextMenu( snapToTicks );
        }

        setOnScroll( this::scroll );
        
        // Set a custom label formatter to show and strip measurement units.
        setLabelFormatter( new StringConverter< Double >() {
            @Override
            public String toString( final Double doubleValue ) {
                // Do a simple string conversion to a number, in case we get 
                // arithmetic exceptions using the number formatter.
                String label = Double.toString( doubleValue );

                try {
                    label = numberFormat.format( doubleValue );
                }
                catch ( final ArithmeticException ae ) {
                    ae.printStackTrace();
                }
                
                return measurementUnitString.isEmpty()
                        ? label
                        : label + measurementUnitString;
            }

            @Override
            public Double fromString( final String label ) {
                final int measurementUnitIndex = measurementUnitString.isEmpty()
                        ? -1
                        : label.indexOf( measurementUnitString );
                final String strippedLabel = ( measurementUnitIndex < 0 )
                        ? label
                        : label.substring( 0, measurementUnitIndex + 1 );
                return Double.valueOf( strippedLabel );
            }
        } );
    }

    private final void initContextMenu( final boolean snapToTicks ) {
        // Sliders do not have Context Menus by default, but we may need to
        // present the user with a choice regarding the Snap to Ticks feature.
        final ContextMenu contextMenu = new ContextMenu();
        final CheckMenuItem snapToTicksMenuItem = new CheckMenuItem( "Snap to Ticks" );

        // Set the requested default for the Snap to Ticks feature. If the
        // older constructor was called, this will be set to true, as it is 
        // hard to fine-tune a slider anyway, and the associated Text Field
        // is generally used for that instead. But some sliders don't have
        // associated text fields, or they aren't editable, and need precision.
        setSnapToTicks( snapToTicks );
        snapToTicksMenuItem.setSelected( snapToTicks );

        snapToTicksMenuItem.setOnAction( evt -> {
            // Toggle the current value of "Snap to Ticks".
            setSnapToTicks( !isSnapToTicks() );
        } );
        contextMenu.getItems().add( snapToTicksMenuItem );

        setOnContextMenuRequested( evt -> contextMenu
                .show( this, evt.getScreenX(), evt.getScreenY() ) );
    }

    public final void setTickResolution( final double majorTickSpacing,
                                         final double minorTickSpacing ) {
        // NOTE: The tick count is for how many are between major ticks.
        final int minorTickCount = ( int ) FastMath.round( majorTickSpacing / minorTickSpacing ) - 1;

        setMajorTickUnit( majorTickSpacing );
        setMinorTickCount( minorTickCount );
    }

    public final boolean isGesturesEnabled() {
        return gesturesEnabled;
    }

    public final void setGesturesEnabled( final boolean pGesturesEnabled ) {
        gesturesEnabled = pGesturesEnabled;
    }

    public final void toggleGestures() {
        // Toggle the "Gestures Enabled" state.
        setGesturesEnabled( !isGesturesEnabled() );
    }

    /**
     * This is a standard getter method for the Scrolling Sensitivity setting.
     *
     * @return The current Scrolling Sensitivity setting
     */
    public final ScrollingSensitivity getScrollingSensitivity() {
        return scrollingSensitivity;
    }

    /**
     * Set the new Scrolling Sensitivity for the Slider.
     *
     * @param pScrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public final void setScrollingSensitivity( 
            final ScrollingSensitivity pScrollingSensitivity ) {
        // Cache the new Scrolling Sensitivity preference.
        scrollingSensitivity = pScrollingSensitivity;
    }

    protected void scroll( final ScrollEvent event ) {
        // Ignore inertia events that happen past scrolling's end.
        if ( event.isInertia() ) {
            return;
        }

        // If Mouse Gestures are disabled, ignore this gesture event.
        if ( !isGesturesEnabled() ) {
            return;
        }

        // If Scrolling Sensitivity is off, then we are supposed to ignore
        // traditional mouse scroll wheel events.
        if ( ScrollingSensitivity.OFF.equals( scrollingSensitivity ) ) {
            return;
        }

        // Try for slightly coarser resolution (pixels), to improve performance.
        final double scrollDeltaY = event.getDeltaY();
        if ( FastMath.abs( scrollDeltaY ) < 3.0d ) {
            return;
        }

        double scrolledDelta = 0.0d;
        switch ( scrollingSensitivity ) {
        case COARSE:
            scrolledDelta = getMajorTickUnit();
            break;
        case MEDIUM:
            final int minorTickCount = getMinorTickCount();
            final int tickRatio = minorTickCount + 1;
            scrolledDelta = getMajorTickUnit() / tickRatio;
            break;
        case FINE:
            scrolledDelta = getBlockIncrement();
            break;
        case OFF:
            break;
        default:
            break;
        }

        // NOTE: The scroll direction convention on macOS tends to be inverted.
        switch ( clientProperties.systemType ) {
        case MACOS:
            if ( scrollDeltaY >= 0.0d ) {
                scrolledDelta = -scrolledDelta;
            }
            break;
        case WINDOWS:
        case LINUX:
        case UNIX:
        case SOLARIS:
        default:
            if ( scrollDeltaY < 0.0d ) {
                scrolledDelta = -scrolledDelta;
            }
            break;
        }

        final double currentValue = getValue();
        final double scrolledValue = currentValue + scrolledDelta;

        setValue( scrolledValue );
    }

    protected void updateTooltipText() {
        setTooltip( new Tooltip( "Use ARROW Keys to Step by "
                + Double.toString( getBlockIncrement() ) + measurementUnitString ) );
    }

    public final String getMeasurementUnitString() {
        return measurementUnitString;
    }

    public final void setMeasurementUnitString( final String pMeasurementUnitString ) {
        measurementUnitString = pMeasurementUnitString;

        // Update the tool tip text as it embeds the measurement unit.
        updateTooltipText();
    }
}