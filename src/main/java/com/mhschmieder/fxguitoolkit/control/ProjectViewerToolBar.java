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

import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;

public final class ProjectViewerToolBar extends ToolBar {

    // Declare all of the tool bar components.
    public FileActionButtons _fileActionButtons;
    public NavigationButtons _navigationButtons;

    // Default constructor
    public ProjectViewerToolBar( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initToolBar( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initToolBar( final ClientProperties pClientProperties ) {
        // Make the Nodes for the Tool Bar.
        _fileActionButtons = new FileActionButtons( pClientProperties );
        _navigationButtons = new NavigationButtons( pClientProperties );

        // Add some spacers to separate logical groupings.
        final int spacerWidth = 40;
        final Region spacer1 = new Region();
        spacer1.setPrefWidth( spacerWidth );

        // Add all the Nodes to the Tool Bar.
        getItems().addAll( _fileActionButtons._fileOpenButton,
                           _fileActionButtons._fileSaveAsButton,
                           _fileActionButtons._filePageSetupButton,
                           _fileActionButtons._filePrintButton,
                           spacer1,
                           _navigationButtons._backButton,
                           _navigationButtons._forwardButton );
    }
}
