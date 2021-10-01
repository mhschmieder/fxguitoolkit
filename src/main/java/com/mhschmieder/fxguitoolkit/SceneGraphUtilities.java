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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import com.mhschmieder.iotoolkit.net.SessionContext;
import com.mhschmieder.iotoolkit.util.ResourceUtilities;

import java.util.ResourceBundle;

// :TODO: Split this up into more specialized utilities for Buttons, etc.?
public class SceneGraphUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SceneGraphUtilities() {}

    @SuppressWarnings("nls")
    public static String getButtonToolTipText( final String groupName,
                                               final String itemName,
                                               final ResourceBundle resourceBundle ) {
        // There must always at least be a group name for each Button.
        if ( ( groupName == null ) || groupName.trim().isEmpty() ) {
            return null;
        }

        // Composite the button name from the group and item names.
        final String buttonName = ( ( itemName == null ) || itemName.trim().isEmpty() )
            ? groupName
            : groupName + "." + itemName;

        // Generate the resource lookup key for the Button Tool Tip.
        final String resourceKey = buttonName + ".toolTip";

        try {
            // :NOTE: Not all actions have Tool Tips, so we have to check first
            // to see if one is present, to avoid unnecessary exceptions.
            if ( !resourceBundle.containsKey( resourceKey ) ) {
                return null;
            }
            return resourceBundle.getString( resourceKey );
        }
        catch ( final Exception e ) {
            // :NOTE: It is OK to be missing a Tool Tip, but as we first check
            // for a key entry, this exception indicates a structural problem
            // that shouldn't be allowed to confuse the end users, but which
            // might benefit the developers or indicate file corruption.
            return null;
        }
    }

    public static String getLabeledControlLabel( final SessionContext sessionContext,
                                                 final String bundleName,
                                                 final String groupName,
                                                 final String itemName,
                                                 final boolean replaceMnemonic ) {
        final ResourceBundle resourceBundle = ResourceUtilities
                .getResourceBundle( sessionContext, bundleName, true );

        // Get the control label from the resource bundle, if applicable.
        final String buttonLabel = FxGuiUtilities
                .getButtonLabel( groupName, itemName, resourceBundle );
        if ( buttonLabel.trim().isEmpty() ) {
            return null;
        }

        // Conditionally strip the mnemonic marker from the label, or
        // replace the Swing mnemonic marker with the JavaFX version.
        final String buttonText =
                                FxGuiUtilities.handleMnemonicMarker( buttonLabel, replaceMnemonic );
        if ( ( buttonText == null ) || buttonText.trim().isEmpty() ) {
            return null;
        }

        return buttonText;
    }

}
