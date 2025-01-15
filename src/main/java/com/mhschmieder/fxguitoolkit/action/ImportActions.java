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
package com.mhschmieder.fxguitoolkit.action;

import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This is a struct-like container for common Import actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class ImportActions {

    public XAction _importSpreadsheetDataAction;
    public XAction _importTableDataAction;
    public XAction _importRasterGraphicsAction;
    public XAction _importVectorGraphicsAction;
    public XAction _importCadGraphicsAction;

    public ImportActions( final ClientProperties clientProperties ) {
        _importSpreadsheetDataAction = LabeledActionFactory
                .getImportSpreadsheetDataAction( clientProperties );
        _importTableDataAction = LabeledActionFactory
                .getImportTableDataAction( clientProperties );
        _importRasterGraphicsAction = LabeledActionFactory
                .getImportRasterGraphicsAction( clientProperties );
        _importVectorGraphicsAction = LabeledActionFactory
                .getImportVectorGraphicsAction( clientProperties );
        _importCadGraphicsAction = LabeledActionFactory
                .getImportCadGraphicsAction( clientProperties );
    }

    // NOTE: This method is not final, so that it can be derived for
    //  additions.
    public Collection< Action > getImportActionCollection( final boolean rasterGraphicsSupported,
                                                           final boolean vectorGraphicsSupported,
                                                           final boolean cadGraphicsSupported ) {
        final Collection< Action > importActionCollection = new ArrayList<>();

        // TODO: Pass in flags to conditionally add table and spreadsheet data.
        importActionCollection.add( _importTableDataAction );
        importActionCollection.add( _importSpreadsheetDataAction );
        
        if ( rasterGraphicsSupported ) {
            importActionCollection.add( _importRasterGraphicsAction );
        }

        if ( vectorGraphicsSupported ) {
            importActionCollection.add( _importVectorGraphicsAction );
        }

        if ( cadGraphicsSupported ) {
            importActionCollection.add( _importCadGraphicsAction );
        }
        
        // TODO: Pass a flag so it doesn't get added at all unless needed.
        //_importTableDataAction.setDisabled( true );
        _importSpreadsheetDataAction.setDisabled( true );

        return importActionCollection;
    }
}
