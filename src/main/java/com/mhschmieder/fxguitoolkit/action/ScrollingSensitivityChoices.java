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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.action;

import java.util.Arrays;
import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;

/**
 * This is a struct-like container for generic Scrolling Sensitivity choices,
 * generally referring to the granularity or sensitivity of mouse scrolling,
 * from coarse to fine as well as on/off.
 */
public final class ScrollingSensitivityChoices {

    // Declare all of the Scrolling Sensitivity choices.
    public XAction _scrollingOffChoice;
    public XAction _scrollingCoarseChoice;
    public XAction _scrollingMediumChoice;
    public XAction _scrollingFineChoice;

    // Default constructor
    public ScrollingSensitivityChoices( final ClientProperties clientProperties ) {
        _scrollingOffChoice = LabeledActionFactory.makeScrollingOffChoice( clientProperties );
        _scrollingCoarseChoice = LabeledActionFactory.makeScrollingCoarseChoice( clientProperties );
        _scrollingMediumChoice = LabeledActionFactory.makeScrollingMediumChoice( clientProperties );
        _scrollingFineChoice = LabeledActionFactory.makeScrollingFineChoice( clientProperties );
    }

    public ScrollingSensitivity getScrollingSensitivity() {
        final ScrollingSensitivity scrollingSensitivity = _scrollingOffChoice.isSelected()
            ? ScrollingSensitivity.OFF
            : _scrollingCoarseChoice.isSelected()
                ? ScrollingSensitivity.COARSE
                : _scrollingMediumChoice.isSelected()
                    ? ScrollingSensitivity.MEDIUM
                    : _scrollingFineChoice.isSelected()
                        ? ScrollingSensitivity.FINE
                        : ScrollingSensitivity.defaultValue();
        return scrollingSensitivity;
    }

    public Collection< Action > getScrollingSensitivityChoiceCollection() {
        final Collection< Action > scrollingSensitivityChoiceCollection = Arrays
                .asList( _scrollingOffChoice,
                         _scrollingCoarseChoice,
                         _scrollingMediumChoice,
                         _scrollingFineChoice );
        return scrollingSensitivityChoiceCollection;
    }

    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        switch ( scrollingSensitivity ) {
        case OFF:
            _scrollingOffChoice.setSelected( true );
            break;
        case COARSE:
            _scrollingCoarseChoice.setSelected( true );
            break;
        case MEDIUM:
            _scrollingMediumChoice.setSelected( true );
            break;
        case FINE:
            _scrollingFineChoice.setSelected( true );
            break;
        default:
            break;
        }
    }

}
