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
import com.mhschmieder.fxguitoolkit.action.ToolsActions;

import javafx.scene.control.Button;

public class PredictButtons {

    // Declare all of the prediction related action buttons.
    public Button              _predictButton;
    public Button              _clearButton;

    // Cache the Client properties for Locale, etc.
    protected ClientProperties _clientProperties;

    // Default constructor
    public PredictButtons( final ClientProperties pClientProperties,
                           final ToolsActions toolsActions ) {
        _clientProperties = pClientProperties;

        if ( toolsActions != null ) {
            // Make the Action Buttons.
            _predictButton = LabeledControlFactory
                    .getPredictButton( _clientProperties, toolsActions._predictAction );
            _clearButton = LabeledControlFactory
                    .getClearButton( _clientProperties, toolsActions._clearAction );
        }
        else {
            // Make the Action Buttons.
            _predictButton = LabeledControlFactory
                    .getPredictButton( _clientProperties );
            _clearButton = LabeledControlFactory
                    .getClearButton( _clientProperties );
        }
    }

}// class PredictButtons
