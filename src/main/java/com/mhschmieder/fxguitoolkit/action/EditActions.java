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
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This is a struct-like container for common Edit actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class EditActions {

    public XAction _undoAction;
    public XAction _redoAction;
    public XAction _cutAction;
    public XAction _copyAction;
    public XAction _pasteAction;
    public XAction _cancelPasteAction;
    public XAction _deleteAction;

    // Default constructor
    public EditActions( final ClientProperties pClientProperties ) {
        _undoAction = LabeledActionFactory.getEditUndoAction( pClientProperties );
        _redoAction = LabeledActionFactory.getEditRedoAction( pClientProperties );
        _cutAction = LabeledActionFactory.getEditCutAction( pClientProperties );
        _copyAction = LabeledActionFactory.getEditCopyAction( pClientProperties );
        _pasteAction = LabeledActionFactory.getEditPasteAction( pClientProperties );
        _cancelPasteAction = LabeledActionFactory.getEditCancelPasteAction( pClientProperties );
        _deleteAction = LabeledActionFactory.getEditDeleteAction( pClientProperties );

        // NOTE: Undo and Redo are disabled until there is useful context.
        _undoAction.setDisabled( true );
        _redoAction.setDisabled( true );

        // NOTE: Cut, Copy and Paste are disabled until there is useful
        // context.
        _cutAction.setDisabled( true );
        _copyAction.setDisabled( true );
        _pasteAction.setDisabled( true );
        _cancelPasteAction.setDisabled( true );
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getEditActionCollection( final ClientProperties pClientProperties ) {
        final Collection< Action > editActionCollection = new ArrayList<>();

        editActionCollection.add( _undoAction );
        editActionCollection.add( _redoAction );
        editActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        editActionCollection.add( _cutAction );
        editActionCollection.add( _copyAction );
        editActionCollection.add( _pasteAction );
        editActionCollection.add( _cancelPasteAction );
        editActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        editActionCollection.add( _deleteAction );

        return editActionCollection;
    }
}
