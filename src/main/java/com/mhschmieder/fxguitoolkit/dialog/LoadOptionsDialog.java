/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.dialog;

import com.mhschmieder.commonstoolkit.net.SessionContext;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public abstract class LoadOptionsDialog extends XDialog {

    public ButtonType        _loadButton;

    protected SessionContext _sessionContext;

    public LoadOptionsDialog( final String title,
                              final String masthead,
                              final SessionContext sessionContext ) {
        // Always call the superclass constructor first!
        super( title, masthead ); // , sessionContext.systemType );

        _sessionContext = sessionContext;

        try {
            initDialog();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initDialog() {
        // Replace the standard "OK" button text with "Load".
        _loadButton = new ButtonType( "Load", ButtonData.OK_DONE ); //$NON-NLS-1$

        final DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll( _loadButton, ButtonType.CANCEL );
    }

}
