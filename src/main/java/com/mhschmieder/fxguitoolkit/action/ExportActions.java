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

import java.util.Collection;
import java.util.Vector;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This is a struct-like container for common Export actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class ExportActions {

    public XAction _exportImageGraphicsAction;
    public XAction _exportVectorGraphicsAction;
    public XAction _exportFormattedVectorGraphicsAction;

    public ExportActions( final ClientProperties pClientProperties ) {
        _exportImageGraphicsAction = LabeledActionFactory
                .getExportImageGraphicsAction( pClientProperties );
        _exportVectorGraphicsAction = LabeledActionFactory
                .getExportVectorGraphicsAction( pClientProperties );
        _exportFormattedVectorGraphicsAction = LabeledActionFactory
                .getExportFormattedVectorGraphicsAction( pClientProperties );
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getExportActionCollection( final boolean vectorGraphicsSupported,
                                                           final boolean formattedVectorGraphicsSupported ) {
        final Collection< Action > exportActionCollection = new Vector<>();

        exportActionCollection.add( _exportImageGraphicsAction );

        if ( vectorGraphicsSupported ) {
            exportActionCollection.add( _exportVectorGraphicsAction );
        }

        if ( formattedVectorGraphicsSupported ) {
            exportActionCollection.add( _exportFormattedVectorGraphicsAction );
        }

        return exportActionCollection;
    }
}
