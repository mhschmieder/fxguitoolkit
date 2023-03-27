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
package com.mhschmieder.fxguitoolkit.control.cell;

import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;

import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

/**
 * This class gives us a way to interact with the Color Picker in tables.
 */
public final class LayerColorTableCell extends ColorPickerTableCell< LayerProperties > {

    public LayerColorTableCell( final TableColumn< LayerProperties, Color > column ) {
        // Always call the superclass constructor first!
        super( column, "Click to select color for this Layer" ); //$NON-NLS-1$
    }

    @Override
    protected void setBeanProperty( final LayerProperties selectedRecord ) {
        // TODO: This is redundant, so is commented out, as bindings outside
        // the table cell handlers already take care of syncing the bean
        // property, and as we don't filter the value or enforce any constraints
        // or rules and what are allowable colors (for now).
        // selectedRecord.setLayerColor( selectedRecord.getLayerColor() );
    }

}// class LayerColorTableCell
