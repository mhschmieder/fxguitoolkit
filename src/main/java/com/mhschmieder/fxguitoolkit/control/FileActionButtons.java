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

import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.scene.control.Button;

public class FileActionButtons {

    // Declare JavaFX image buttons for file related actions.
    public Button _fileOpenButton;
    public Button _fileSaveAsButton;
    public Button _fileExportSessionLogButton;
    public Button _filePageSetupButton;
    public Button _filePrintButton;

    // Default constructor
    public FileActionButtons( final ClientProperties pClientProperties ) {
        // Make the image-based buttons.
        _fileOpenButton = LabeledControlFactory.getOpenButton( pClientProperties );
        _fileSaveAsButton = LabeledControlFactory.getSaveAsButton( pClientProperties );
        _fileExportSessionLogButton = LabeledControlFactory
                .getExportSessionLogButton( pClientProperties );
        _filePageSetupButton = LabeledControlFactory.getPageSetupButton( pClientProperties );
        _filePrintButton = LabeledControlFactory.getPrintButton( pClientProperties );
    }

}
