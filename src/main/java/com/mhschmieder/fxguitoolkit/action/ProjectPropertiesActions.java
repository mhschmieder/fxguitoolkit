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
package com.mhschmieder.fxguitoolkit.action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import org.controlsfx.control.action.Action;

import java.util.Arrays;
import java.util.Collection;

/**
 * This is a struct-like container for actions used by Project Properties.
 */
public final class ProjectPropertiesActions {

    public XAction resetAction;

    public ProjectPropertiesActions( final String pProjectCategory,
                                     final ClientProperties pClientProperties ) {
        resetAction = LabeledActionFactory.getResetAction( pClientProperties );

        // The tool tip for "Reset" is unique per context so isn't in the
        // locale-sensitive resources for the generic action lookup.
        final String tooltipText 
                = "Reset " + pProjectCategory + " Properties to Blank Fields";
        resetAction.setLongText( tooltipText );
    }

    public Collection< Action > getProjectPropertiesActionCollection() {
        final Collection< Action > projectPropertiesActionCollection 
                = Arrays.asList( resetAction );

        return projectPropertiesActionCollection;
    }
}
