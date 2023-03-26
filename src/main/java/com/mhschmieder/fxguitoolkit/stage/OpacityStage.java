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
package com.mhschmieder.fxguitoolkit.stage;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.layout.OpacityPane;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;

/**
 * This is a generalized Stage for hosting a simple Opacity control.
 */
public final class OpacityStage extends XStage {

    // Declare the main content pane.
    public OpacityPane  _opacityPane;

    // Cache the Opacity Editor Label so we can use it at layout manager
    // construction time.
    public final String _opacityEditorLabel;

    public OpacityStage( final String title,
                         final String windowKeyPrefix,
                         final String opacityEditorLabel,
                         final ProductBranding productBranding,
                         final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, productBranding, pClientProperties );

        _opacityEditorLabel = opacityEditorLabel;

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public double getOpacityPercent() {
        // Forward this method to the Opacity Pane.
        return _opacityPane.getOpacityPercent();
    }

    @SuppressWarnings("nls")
    private void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/ahaSoft/TransparentColor16.png", 450d, 120d, false );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _opacityPane = new OpacityPane( clientProperties, _opacityEditorLabel );
        return _opacityPane;
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Opacity Pane.
        _opacityPane.setGesturesEnabled( gesturesEnabled );
    }

    // Set and bind the Opacity Percent property reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setOpacityPercentProperty( final DoubleProperty pOpacityPercent ) {
        // Forward this method to the Opacity Pane.
        _opacityPane.setOpacityPercentProperty( pOpacityPercent );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Opacity Pane.
        _opacityPane.setScrollingSensitivity( scrollingSensitivity );
    }

    public void toggleGestures() {
        // Forward this method to the Opacity Pane.
        _opacityPane.toggleGestures();
    }
}
