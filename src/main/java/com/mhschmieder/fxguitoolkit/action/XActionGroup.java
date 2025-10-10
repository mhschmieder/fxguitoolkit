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

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.Collection;

/**
 * The ControlsFX ActionGroup doesn't know when a ToggleGroup is required to
 * enforce mutually exclusive choices, so we extend that class in order to add
 * that flag to the data model.
 */
public class XActionGroup extends ActionGroup {

    // Flag for whether this Action Group is a set of exclusive choices.
    private final boolean choiceGroup;

    // Keep track of whether we want disabled actions to be hidden or not.
    private boolean       hideIfDisabled;

    public XActionGroup( final String text, final Collection< Action > actions ) {
        this( text, actions, false );
    }

    public XActionGroup( final String text,
                         final Collection< Action > actions,
                         final boolean pChoiceGroup ) {
        super( text, actions );

        choiceGroup = pChoiceGroup;

        // To simplify constructors, for now we set this later if non-default.
        hideIfDisabled = false;
    }

    public final boolean isChoiceGroup() {
        return choiceGroup;
    }

    public final boolean isHideIfDisabled() {
        return hideIfDisabled;
    }

    public final void setHideIfDisabled( final boolean pHideIfDisabled ) {
        hideIfDisabled = pHideIfDisabled;
    }

}