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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxguitoolkit.GuiUtilities;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;

/**
 * {@code XTextField} is a concrete base class that serves as a specialization
 * of {@link TextField}, primarily to augment the core API so that derived
 * classes don't have to write copy/paste code that might diverge over time.
 * <p>
 * In particular, this class properly handles ESC and ENTER keys consistently.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class XTextField extends TextField {

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public XTextField( final String initialText,
                       final String tooltipText,
                       final boolean applyToolkitCss,
                       final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( initialText );

        clientProperties = pClientProperties;

        try {
            initEditor( tooltipText, applyToolkitCss );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initEditor( final String tooltipText,
                                   final boolean applyToolkitCss ) {
        // Set the default generic text field tool tip if none is provided.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            setTooltip( new Tooltip( tooltipText ) );
        }
        else {
            setTooltip( new Tooltip( "Use ENTER or TAB Key to Commit Edits, and ESC to Cancel Edits" ) ); //$NON-NLS-1$
        }

        if ( applyToolkitCss ) {
            // Set the full list of this toolkit's custom Text Field Properties.
            GuiUtilities.setTextFieldProperties( this );
        }
        else {
            // Apply drop-shadow effects when the mouse enters this Text Field.
            GuiUtilities.applyDropShadowEffect( this );
        }

        // Try to make sure the text field uses enough height so that commas
        // don't look like periods. More height is required on the Mac for these
        // and other characters involving descenders, to avoid clipping.
        setPrefHeight( SystemType.MACOS.equals( clientProperties.systemType ) ? 24d : 22d );
        setMinHeight( SystemType.MACOS.equals( clientProperties.systemType ) ? 24d : 22d );

        // Just in case we use or derive this class without overriding special
        // key handling, add some rudimentary behavior that minimizes surprises.
        setOnKeyPressed( keyEvent -> {
            final KeyCode keyCode = keyEvent.getCode();
            switch ( keyCode ) {
            case ENTER:
                // Commit the current selection as-is, without giving up focus.
                commitValue();

                break;
            case ESCAPE:
                // Revert to the most recent committed value.
                cancelEdit();

                break;
            case TAB:
                // NOTE: Nothing to do, as Text Input Controls commit edits and
                // then release focus when the TAB key is pressed, so the Focus
                // Lost handler is where value restrictions should be applied.
                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }
}
