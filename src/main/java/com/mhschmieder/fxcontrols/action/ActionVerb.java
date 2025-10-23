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
package com.mhschmieder.fxcontrols.action;

/**
 * Actions can be classified by verbs that describe what they do in abstract
 * terms, which in turn can be useful towards deciding what kind of control to
 * use to expose the action. For instance, one might be an exclusive "choice".
 * <p>
 * DO = typical action that invokes a method directly, or shows a control
 * CHECK = binary variable whose two states are exact opposites; uses check box
 * TOGGLE = binary valued variable, usually shown with a toggle button
 * CHOOSE = mutually exclusive choice, generally modeled with radio buttons
 * SELECT = inclusive or exclusive choice from longer list, requiring container
 * SPIN = action to choose a value via incremental +/- from a finite list
 * PICK_DATE = action to pick a date using the Time/Date API for representation
 * PICK_COLOR = action to pick a color using a specialized control
 * <p>
 * Only the first three can be modeled directly by special Menu Item classes;
 * the others act as "DO" in that context but may launch a window or control to
 * complete the action, and if used as a generator for a button or control in a
 * tool bar or elsewhere outside a menu bar or context menu, give additional
 * hints as to what type of control to make (otherwise we would only know when
 * to make a Button or a Toggle Button, and maybe a Radio Button).
 * <p>
 * We distinguish between toggle actions that should be modeled as a check for
 * on/off, as a binary choice does not always imply opposites. To be clear, a
 * check box doesn't change its text, but a toggle button usually does. We
 * should try to be consistent about this rationale across the application.
 */
public enum ActionVerb {
    DO, CHECK, TOGGLE, CHOOSE, SELECT, SPIN, PICK_DATE, PICK_COLOR;

    public static ActionVerb defaultValue() {
        return DO;
    }

}
