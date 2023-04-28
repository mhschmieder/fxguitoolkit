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
package com.mhschmieder.fxguitoolkit.action;

import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.scene.paint.Color;

/**
 * This is a struct-like container for common Settings actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class SettingsActions {

    public BackgroundColorChoices _backgroundColorChoices;
    public WindowSizeActions      _windowSizeActions;

    public SettingsActions( final ClientProperties pClientProperties ) {
        _backgroundColorChoices = new BackgroundColorChoices( pClientProperties );
        _windowSizeActions = new WindowSizeActions( pClientProperties );
    }

    public final Collection< Action > getBackgroundColorChoiceCollection() {
        // Forward this method to the Background Color choices container.
        return _backgroundColorChoices.getBackgroundColorChoiceCollection();
    }

    public final Color getSelectedBackgroundColor() {
        // Forward this method to the Background Color choices container.
        return _backgroundColorChoices.getSelectedBackgroundColor();
    }

    public final String getSelectedBackgroundColorName() {
        // Forward this method to the Background Color choices container.
        return _backgroundColorChoices.getSelectedBackgroundColorName();
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getSettingsActionCollection( final ClientProperties pClientProperties,
                                                             final boolean maximumSizeSupported ) {
        final XActionGroup backgroundColorChoiceGroup = LabeledActionFactory
                .getBackgroundColorChoiceGroup( pClientProperties, _backgroundColorChoices );

        final XActionGroup windowSizeActionGroup =
                                                 com.mhschmieder.fxguitoolkit.action.LabeledActionFactory
                                                         .makeWindowSizeActionGroup( pClientProperties,
                                                                                     _windowSizeActions,
                                                                                     maximumSizeSupported );

        final Collection< Action > settingsActionCollection = new ArrayList<>();

        settingsActionCollection.add( backgroundColorChoiceGroup );
        settingsActionCollection.add( windowSizeActionGroup );

        return settingsActionCollection;
    }

    public final Collection< Action > getWindowSizeActionCollection( final boolean maximumSizeSupported ) {
        // Forward this method to the Window Size actions container.
        return _windowSizeActions.getWindowSizeActionCollection( maximumSizeSupported );
    }

    public final Color selectBackgroundColor( final String backgroundColorName ) {
        // Forward this method to the Background Color choices container.
        return _backgroundColorChoices.selectBackgroundColor( backgroundColorName );
    }
}
