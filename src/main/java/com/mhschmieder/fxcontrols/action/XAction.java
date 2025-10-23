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

import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;

import java.util.function.Consumer;

/**
 * This is effectively a copy of ControlsFX sample code for DummyAction and
 * extends the Action base class in ways similar to the derived ActionGroup
 * class. It is a mystery why ControlsFX didn't just create the Action class
 * like this to start with, as the API is almost unusable without these
 * extensions. We also pass in the event handler rather than making a dummy.
 */
public class XAction extends Action {

    // Tag the Action Verb associated with this Action.
    private final ActionVerb actionVerb;

    // Keep track of whether we want disabled actions to be hidden or not.
    private boolean          hideIfDisabled;

    public XAction() {
        this( ActionVerb.defaultValue() );
    }

    @SuppressWarnings("nls")
    public XAction( final ActionVerb pActionVerb ) {
        this( pActionVerb, "" );
    }

    public XAction( final ActionVerb pActionVerb, final String pName ) {
        this( pActionVerb, pName, null );
    }

    public XAction( final ActionVerb pActionVerb, final String pName, final Node image ) {
        this( pActionVerb, pName, image, null );
    }

    public XAction( final ActionVerb pActionVerb,
                    final String pName,
                    final Node image,
                    final Consumer< ActionEvent > eventHandler ) {
        // Always call the superclass constructor first!
        super( pName );

        actionVerb = pActionVerb;

        // To simplify constructors, for now we set this later if non-default.
        hideIfDisabled = false;

        if ( image != null ) {
            setGraphic( image );
        }

        if ( eventHandler != null ) {
            setEventHandler( eventHandler );
        }
    }

    public final ActionVerb getActionVerb() {
        return actionVerb;
    }

    public final boolean isAction() {
        return ActionVerb.DO.equals( actionVerb );
    }

    public final boolean isCheck() {
        return ActionVerb.CHECK.equals( actionVerb );
    }

    public final boolean isChoice() {
        return ActionVerb.CHOOSE.equals( actionVerb );
    }

    public final boolean isColor() {
        return ActionVerb.PICK_COLOR.equals( actionVerb );
    }

    public final boolean isDate() {
        return ActionVerb.PICK_DATE.equals( actionVerb );
    }

    public final boolean isHideIfDisabled() {
        return hideIfDisabled;
    }

    public final boolean isSelect() {
        return ActionVerb.SELECT.equals( actionVerb );
    }

    public final boolean isSpin() {
        return ActionVerb.SPIN.equals( actionVerb );
    }

    public final boolean isToggle() {
        return ActionVerb.TOGGLE.equals( actionVerb );
    }

    @Override
    public void setEventHandler( final Consumer< ActionEvent > eventHandler ) {
        super.setEventHandler( eventHandler );
    }

    public final void setHideIfDisabled( final boolean hideIfDisabled ) {
        this.hideIfDisabled = hideIfDisabled;
    }

    @Override
    public String toString() {
        return getText();
    }

}