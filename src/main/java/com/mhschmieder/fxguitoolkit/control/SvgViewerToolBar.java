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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;

/**
 * This is the tool bar for the SVG Viewer window.
 */
public class SvgViewerToolBar extends ToolBar {

    // Declare all of the tool bar components.
    public FileActionButtons _fileActionButtons;

    // Default constructor
    public SvgViewerToolBar( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initToolBar( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initToolBar( final ClientProperties pClientProperties ) {
        // Make the Nodes for the Tool Bar.
        _fileActionButtons = new FileActionButtons( pClientProperties );

        // Add some spacers to separate logical groupings.
        final int spacerWidth = 40;
        final Region spacer1 = new Region();
        spacer1.setPrefWidth( spacerWidth );

        // Add all the Nodes to the Tool Bar.
        // TODO: Implement the Back and Forward buttons.
        getItems().addAll( _fileActionButtons._fileOpenButton,
                           _fileActionButtons._filePageSetupButton,
                           _fileActionButtons._filePrintButton,
                           spacer1 );
    }
}
