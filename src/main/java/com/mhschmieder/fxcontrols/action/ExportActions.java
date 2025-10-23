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
package com.mhschmieder.fxcontrols.action;

import com.mhschmieder.jcommons.util.ClientProperties;
import org.controlsfx.control.action.Action;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a struct-like container for common Export actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class ExportActions {

    public XAction _exportTableDataAction;
    public XAction _exportSpreadsheetDataAction;
    public XAction _exportRasterGraphicsAction;
    public XAction _exportVectorGraphicsAction;
    public XAction _exportRenderedGraphicsAction;

    public ExportActions( final ClientProperties pClientProperties ) {
        _exportTableDataAction = LabeledActionFactory
                .getExportTableDataAction( pClientProperties );
        _exportSpreadsheetDataAction = LabeledActionFactory
                .getExportSpreadsheetDataAction( pClientProperties );
        _exportRasterGraphicsAction = LabeledActionFactory
                .getExportRasterGraphicsAction( pClientProperties );
        _exportVectorGraphicsAction = LabeledActionFactory
                .getExportVectorGraphicsAction( pClientProperties );
        _exportRenderedGraphicsAction = LabeledActionFactory
                .getExportRenderedGraphicsAction( pClientProperties );
    }

    // NOTE: This method is not final, so that it can be derived for
    //  additions.
    public Collection< Action > getExportActionCollection( final boolean vectorGraphicsSupported,
                                                           final boolean renderedGraphicsSupported ) {
        final Collection< Action > exportActionCollection = new ArrayList<>();

        // TODO: Pass in flags to conditionally add table and spreadsheet data.
        exportActionCollection.add( _exportTableDataAction );
        exportActionCollection.add( _exportSpreadsheetDataAction );
        
        // Raster Graphics generally correspond to a JavaFX-generated screenshot.
        exportActionCollection.add( _exportRasterGraphicsAction );

        if ( vectorGraphicsSupported ) {
            exportActionCollection.add( _exportVectorGraphicsAction );
        }

        if ( renderedGraphicsSupported ) {
            exportActionCollection.add( _exportRenderedGraphicsAction );
        }
        
        // TODO: Pass a flag so it doesn't get added at all unless needed.
        _exportTableDataAction.setDisabled( true );
        _exportSpreadsheetDataAction.setDisabled( true );

        return exportActionCollection;
    }
}
