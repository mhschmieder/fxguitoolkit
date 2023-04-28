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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.action;

import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This is a struct-like container for generic Window Size actions.
 */
public final class WindowSizeActions {

    public XAction _windowSizePreferredSizeAction;
    public XAction _windowSizeDefaultSizeAction;
    public XAction _windowSizeMaximumSizeAction;

    public WindowSizeActions( final ClientProperties pClientProperties ) {
        _windowSizePreferredSizeAction = LabeledActionFactory
                .makeWindowSizePreferredSizeAction( pClientProperties );
        _windowSizeDefaultSizeAction = LabeledActionFactory
                .makeWindowSizeDefaultSizeAction( pClientProperties );
        _windowSizeMaximumSizeAction = LabeledActionFactory
                .makeWindowSizeMaximumSizeAction( pClientProperties );
    }

    public Collection< Action > getWindowSizeActionCollection( final boolean maximumSizeSupported ) {
        final Collection< Action > windowSizeActionCollection = new ArrayList<>();

        windowSizeActionCollection.add( _windowSizePreferredSizeAction );
        windowSizeActionCollection.add( _windowSizeDefaultSizeAction );

        if ( maximumSizeSupported ) {
            windowSizeActionCollection.add( _windowSizeMaximumSizeAction );
        }

        return windowSizeActionCollection;
    }

}
