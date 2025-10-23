/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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
package com.mhschmieder.fxcontrols.action;

import com.mhschmieder.jcommons.util.ClientProperties;
import org.controlsfx.control.action.Action;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a struct-like container for common Load actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class LoadActions {

    public XAction _projectSettingsAction;
    
    /**
     * This is the default constructor, when no customization is required.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     */
    public LoadActions( final ClientProperties pClientProperties ) {
        _projectSettingsAction = LabeledActionFactory
                .getLoadProjectSettingsAction( pClientProperties );
    }

    public final Collection< Action > getLoadActionCollection() {
        final Collection< Action > loadActionCollection = new ArrayList<>();

        // Flag for support project settings.
        loadActionCollection.add( _projectSettingsAction );

        return loadActionCollection;
    }
}
