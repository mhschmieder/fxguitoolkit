/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 * {@code XColorPicker} is an enhancement to {@link ColorPicker} that takes care
 * of some of its anomalies.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class XColorPicker extends ColorPicker {

    public XColorPicker( final String tooltipText ) {
        // Always call the superclass constructor first!
        super();

        try {
            initColorPicker( tooltipText );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initColorPicker( final String tooltipText ) {
        // Conditionally provide tool tips in case this component is used in a
        // sparse context like a toolbar.
        if ( ( tooltipText != null ) && !tooltipText.isEmpty() ) {
            setTooltip( new Tooltip( tooltipText ) );
        }

        // Apply drop-shadow effects when the mouse enters this node.
        GuiUtilities.applyDropShadowEffect( this );
    }

    // Get the JavaFX color value from the color picker.
    public final Color getColor() {
        return getValue();
    }

    // Set the Java FX color to the color picker.
    public final void setColor( final Color newColor,
                                final Object object,
                                final EventTarget eventTarget ) {
        // First make sure the color actually changed.
        final Color oldColor = getValue();
        if ( newColor.equals( oldColor ) ) {
            return;
        }

        // We have to add this as a custom color to be found.
        final ObservableList< Color > customColors = getCustomColors();
        if ( customColors.contains( newColor ) ) {
            setValue( newColor );
        }
        else {
            // This seems to be the only way to get a programmatically selected
            // color to show up in the main Combo Box, due to a known bug in
            // JavaFX 2.2.
            // TODO: Review whether this is still necessary in JavaFX 8.60.
            customColors.add( newColor );
            setValue( newColor );
            fireEvent( new ActionEvent( object, eventTarget ) );
        }
    }
}