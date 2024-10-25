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
 * You should have received a copy of the MIT License along with the FxGuiToolkit
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.action;

import java.util.Arrays;
import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.input.MouseToolMode;

/**
 * This is a struct-like container for Mouse Tool choices.
 */
public final class MouseToolChoices {

    // Declare all of the Mouse Tool choices.
    // TODO: Add one for Pan Tool.
    public XAction      _selectToolChoice;
    public XAction      _zoomToolChoice;
    public XAction      _rotateToolChoice;
    public XAction      _architectureToolChoice;

    // Cache the associated choice group, for ease of overall enablement.
    public XActionGroup _mouseToolChoiceGroup;

    // Default constructor
    @SuppressWarnings("nls")
    public MouseToolChoices( final ClientProperties pClientProperties ) {
        _selectToolChoice = LabeledActionFactory.getSelectToolChoice( pClientProperties );
        _zoomToolChoice = LabeledActionFactory.getZoomToolChoice( pClientProperties );
        _rotateToolChoice = LabeledActionFactory.getRotateToolChoice( pClientProperties );
        _architectureToolChoice =
                                LabeledActionFactory.getLineToolChoice( pClientProperties );

        final Collection< Action > mouseToolChoiceCollection = getMouseToolChoiceCollection();

        _mouseToolChoiceGroup = ActionFactory.makeChoiceGroup( pClientProperties,
                                                               mouseToolChoiceCollection,
                                                               LabeledActionFactory.BUNDLE_NAME,
                                                               "mouseTool",
                                                               null,
                                                               true );
    }

    public Collection< Action > getMouseToolChoiceCollection() {
        final Collection< Action > mouseToolChoiceCollection = Arrays
                .asList( _selectToolChoice,
                         _zoomToolChoice,
                         _rotateToolChoice,
                         _architectureToolChoice );
        return mouseToolChoiceCollection;
    }

    public XActionGroup getMouseToolChoiceGroup() {
        return _mouseToolChoiceGroup;
    }

    public void setDisabled( final boolean disabled ) {
        _mouseToolChoiceGroup.setDisabled( disabled );
    }

    public void setMouseMode( final MouseToolMode mouseToolMode ) {
        // Update the Active Mouse Tool.
        // NOTE: We ignore passive tools like MOVE, MEASURE, and PASTE.
        switch ( mouseToolMode ) {
        case SELECT:
            _selectToolChoice.setSelected( true );
            break;
        case MOVE:
            break;
        case ROTATE:
            _rotateToolChoice.setSelected( true );
            break;
        case ZOOM:
            _zoomToolChoice.setSelected( true );
            break;
        case PAN:
            break;
        case DRAW:
            _architectureToolChoice.setSelected( true );
            break;
        case MEASURE:
            break;
        case COPY:
            break;
        case PASTE:
            break;
        default:
            break;
        }
    }

}
