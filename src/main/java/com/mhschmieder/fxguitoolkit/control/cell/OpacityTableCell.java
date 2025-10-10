/**
 * MIT License
 *
 * Copyright (c) 2025 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.control.cell;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import javafx.scene.control.TextField;

import java.util.List;

public class OpacityTableCell< TD >
        extends DoubleEditorTableCell< TD, Double > {

    public OpacityTableCell( final boolean pAllowedToBeBlank,
                             final ClientProperties pClientProperties ) {
        this( null, pAllowedToBeBlank, pClientProperties );
    }

    public OpacityTableCell( final List< Integer > pUneditableRows,
                             final boolean pAllowedToBeBlank,
                             final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( pUneditableRows, pAllowedToBeBlank, pClientProperties );

        setMeasurementUnit( "" );
    }

    @Override
    protected TextField makeTextField() {
        return ControlFactory.makeOpacityEditor( clientProperties );
    }

    @Override
    public void setBeanProperty( final TD selectedRecord ) {
        // NOTE: This method is redundant here, as bindings outside the table
        //  cell handlers already take care of syncing bean properties, and the
        //  opacity values have no constraints or impact on other table cells.
    }
}
