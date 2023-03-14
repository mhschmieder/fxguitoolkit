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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.stage;

import org.apache.commons.math3.util.FastMath;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;

/**
 * This is the task wrapper for waiting on the main application class to load.
 */
public final class MainApplicationLoadTask extends Task< Void > {

    // 15 seconds (30 x 500ms) ought to be enough for even the slowest computer
    // to initialize the main application.
    private static final int THREAD_SLEEP_INTERVAL_MS        = 500;
    private static final int MAXIMUM_THREAD_SLEEP_TIME_MS    = 15000;
    private static final int MAXIMUM_THREAD_SLEEP_INCREMENTS = FastMath
            .round( MAXIMUM_THREAD_SLEEP_TIME_MS / THREAD_SLEEP_INTERVAL_MS );

    // Cache the progress text so we can initialize it via the constructor.
    private String           progressText;

    // Cache a reference to the main application stage.
    private Stage            mainApplicationStage;

    public MainApplicationLoadTask( final String progressText ) {
        // Always call the super-constructor first!
        super();

        this.progressText = progressText;

        mainApplicationStage = null;
    }

    @Override
    protected Void call() throws InterruptedException {
        // Initialize to not started, or it goes to indeterminate.
        updateProgress( 0, MAXIMUM_THREAD_SLEEP_INCREMENTS );

        for ( int i = 0; i < MAXIMUM_THREAD_SLEEP_INCREMENTS; i++ ) {
            progressText += '.';
            updateMessage( progressText );

            Thread.sleep( THREAD_SLEEP_INTERVAL_MS );

            updateProgress( i + 1, MAXIMUM_THREAD_SLEEP_INCREMENTS );

            // If the main application stage has finished initializing, exit the
            // Splash Screen timer loop.
            // TODO: Once there is more going on with the main Desktop
            // Application window, implement the "isInitialized()" function and
            // pass it to the logic here.
            // TODO: Make this an observable, if bindings help.
            // if ( ( mainApplicationStage != null ) &&
            // mainApplicationStage.isInitialized() ) {
            if ( mainApplicationStage != null ) {
                break;
            }
        }

        return null;
    }

    /**
     * Set the main application stage.
     *
     * @param mainApplicationStage
     *            The main stage for this application
     */
    public void setMainApplicationStage( final Stage mainApplicationStage ) {
        this.mainApplicationStage = mainApplicationStage;
    }

    /**
     * Show the main application stage (if set).
     */
    public void showMainApplicationStage() {
        // Start an application session. This also shows the main stage.
        // NOTE: We run this on a deferred thread, to give the GUI
        // initialization more time to complete any deferred tasks.
        // TODO: Refactor this back to the more elaborate session starter
        // sequence once pulling in my GitHub library, not yet on Maven Central.
        Platform.runLater( () -> {
            if ( mainApplicationStage != null ) {
                if ( mainApplicationStage.isIconified() ) {
                    mainApplicationStage.setIconified( false );
                }
                else if ( !mainApplicationStage.isShowing() ) {
                    mainApplicationStage.show();
                }

                mainApplicationStage.toFront();
            }
        } );
    }

}
