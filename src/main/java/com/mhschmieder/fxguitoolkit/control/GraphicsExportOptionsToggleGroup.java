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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.fxgraphicstoolkit.io.GraphicsExportOptions;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * This Toggle Group collects the generalized Graphics Export Options and
 * ensures that they are mutually exclusive.
 */
public class GraphicsExportOptionsToggleGroup extends ToggleGroup {

    // Declare Radio Buttons for the shared Graphics Export Options.
    public RadioButton _exportAllRadioButton;
    public RadioButton _exportChartRadioButton;
    public RadioButton _exportAuxiliaryRadioButton;

    // Default constructor
    public GraphicsExportOptionsToggleGroup( final GraphicsExportOptions graphicsExportOptions,
                                             final String graphicsExportAllLabel,
                                             final String graphicsExportChartLabel,
                                             final String graphicsExportAuxiliaryLabel ) {
        // Always call the superclass constructor first!
        super();

        // Make the Radio Buttons.
        _exportAllRadioButton = GuiUtilities.getRadioButton( graphicsExportAllLabel,
                                                             this,
                                                             graphicsExportOptions.isExportAll() );
        _exportChartRadioButton = GuiUtilities
                .getRadioButton( graphicsExportChartLabel,
                                 this,
                                 graphicsExportOptions.isExportChart() );
        _exportAuxiliaryRadioButton = GuiUtilities
                .getRadioButton( graphicsExportAuxiliaryLabel,
                                 this,
                                 graphicsExportOptions.isExportAuxiliary() );

        // Bind the Export All Radio Button to its associated property.
        _exportAllRadioButton.selectedProperty()
                .bindBidirectional( graphicsExportOptions.exportAllProperty() );

        // Bind the Export Chart Radio Button to its associated property.
        _exportChartRadioButton.selectedProperty()
                .bindBidirectional( graphicsExportOptions.exportChartProperty() );

        // Bind the Export Auxiliary Radio Button to its associated property.
        _exportAuxiliaryRadioButton.selectedProperty()
                .bindBidirectional( graphicsExportOptions.exportAuxiliaryProperty() );
    }

}
