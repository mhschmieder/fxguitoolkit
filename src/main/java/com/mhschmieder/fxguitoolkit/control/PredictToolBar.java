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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.fxguitoolkit.action.SimulationActions;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.scene.control.ToolBar;

public class PredictToolBar extends ToolBar {

    // Declare tool bar buttons for shortcuts, etc.
    public PredictButtons predictButtons;

    // Default constructor
    public PredictToolBar( final ClientProperties pClientProperties,
                           final SimulationActions simulationActions ) {
        // Always call the superclass constructor first!
        super();

        try {
            initToolBar( pClientProperties, simulationActions );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Initialize the JavaFX components in this GUI sub-hierarchy.
    private final void initToolBar( final ClientProperties pClientProperties,
                                    final SimulationActions simulationActions ) {
        // Make the JavaFX Nodes for the Tool Bar.
        predictButtons = new PredictButtons( pClientProperties, 
                                             simulationActions );

        // Add all the Nodes to the Tool Bar.
        getItems().addAll( predictButtons.predictButton, 
                           predictButtons.clearButton );
    }
}
