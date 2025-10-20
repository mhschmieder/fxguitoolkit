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
package com.mhschmieder.fxguitoolkit.stage;

import javafx.application.Platform;
import javafx.stage.WindowEvent;

/**
 * Defines the contract for methods that main application windows and stages
 * must implement for handling basic primary application window functionality.
 */
public interface MainApplicationWindowHandler extends MacAppMenuEventHandler {

    void startSession();
    
    boolean isInitialized();

    void disposeAllResources();

    /**
     * Like fileClose, but upon Cancel, consumes a WindowEvent to avoid exiting.
     * 
     * @param event The {@link WindowEvent} that triggered this window close
     */
    default void windowClose( final WindowEvent event ) {
        // Make sure the window doesn't auto-close, by consuming the event.
        // NOTE: This only goes so far towards preventing other windows from
        // receiving the event and closing, but is better than nothing.
        event.consume();

        // Now it is safe to process this like a normal Quit action.
        quit();
    }

    /**
     * Take care of exit tasks that pertain to all applications, e.g. save User
     * Preferences and dispose of all resources.
     *
     * @param exitPlatform
     *            Flag for whether to also exit the Java Platform
     */
    default void exitApplication( final boolean exitPlatform ) {
        // Dispose of all resources allocated by the main application window.
        disposeAllResources();

        // Exit the application via the JavaFX exit call. This is considered
        // safer than the standard System.exit() call, and shuts down the GUI.
        // NOTE: Without this call, Application.stop() never gets invoked,
        //  which in turn means the application never gets properly shut down.
        if ( exitPlatform ) {
            Platform.exit();
        }
    }
}
