/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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

import org.controlsfx.control.TaskProgressView;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxcommonstoolkit.concurrent.DataRequestTask;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 * This is a window-level wrapper for a Task Progress View, which can show
 * multiple tasks, but for our purposes will probably only ever show one at
 * a time (specifically a data update request).
 */
public final class DataRequestStatusViewer extends XStage {

    public static final String DATA_REQUEST_STATUS_VIEWER_TITLE_DEFAULT = "Data Request Status";

    // Declare the main content pane for the primary task status layout.
    protected TaskProgressView< DataRequestTask > taskProgressView;

    // Declare a callback that will be used to determine the task icon.
    private Callback< DataRequestTask, Node > taskIconFactory;

    // Pre-cache the task icons for each category of task, for efficiency.
    protected ImageView fullUpdateTaskIcon;
    protected ImageView dynamicUpdateTaskIcon;
    
    /**
     * Placeholder text to use in place of the default in TaskProgressView.
     * <p>
     * NOTE: This depends on a not-yet-published enhancement to ControlsFX.
     */
    protected String placeholderText;
    
    /**
     * Makes a DataRequestStatusViewer instance with all parameters specified.
     * 
     * @param modality The modality of this window, par JavaFX Stage
     * @param pProductBranding Product Branding to pass to frame titles etc.
     * @param pClientProperties Client Properties for platform-specific GUI
     * @param pPlaceholderText Placeholder text to use when no tasks running
     * @param fullUpdateIconJarRelativePath JAR-relative path for icon to use
     *                                      for full update tasks
     * @param dynamicUpdateIconJarRelativePath JAR-relative path for icon to
     *                                         use for dynamic update tasks
     */
    @SuppressWarnings("nls")
    public DataRequestStatusViewer( final Modality modality,
                                    final ProductBranding pProductBranding,
                                    final ClientProperties pClientProperties,
                                    final String pPlaceholderText,
                                    final String fullUpdateIconJarRelativePath,
                                    final String dynamicUpdateIconJarRelativePath ) {
        // Always call the superclass constructor first!
        super( modality,
               DATA_REQUEST_STATUS_VIEWER_TITLE_DEFAULT,
               "dataRequestStatusViewer",
               false,
               false,
               pProductBranding,
               pClientProperties );
        
        placeholderText = pPlaceholderText;

        // Due to threading conflicts with OS-level handling of Full Screen Mode
        // on macOS, when we have server prediction tasks running on a separate
        // thread, we need to make this window exempt from Full Screen Mode.
        _fullScreenModeExempt = true;

        try {
            initStage( fullUpdateIconJarRelativePath,
                       dynamicUpdateIconJarRelativePath );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void addTask( final DataRequestTask dataRequestTask ) {
        // Add this task to the Task Progress View, whether any previous tasks
        // are still running or not. Clear any still-running tasks first.
        // TODO: Modify this if we decide to support multiple tasks later on.
        final ObservableList< DataRequestTask > dataRequestTasks = taskProgressView
                .getTasks();
        dataRequestTasks.clear();
        dataRequestTasks.add( dataRequestTask );
    }

    @SuppressWarnings("nls")
    protected void initStage( final String fullUpdateIconJarRelativePath,
                              final String dynamicUpdateIconJarRelativePath ) {
        final double preferredHeight = SystemType.MACOS.equals( clientProperties.systemType )
            ? 90
            : 110;

        initStage( "/icons/glyphish/Calculator16.png",
                   460d,
                   preferredHeight,
                   false );

        // NOTE: For this particular window, we want to tightly manage the
        //  size, as we only ever show one task at a time (currently).
        Platform.runLater( () -> {
            setMinHeight( preferredHeight );
            setMaxHeight( preferredHeight );
        } );

        // Pre-cache the task icons for each category of task, for efficiency.
        fullUpdateTaskIcon = ImageUtilities
                .createIcon( fullUpdateIconJarRelativePath );
        dynamicUpdateTaskIcon = ImageUtilities
                .createIcon( dynamicUpdateIconJarRelativePath );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        // NOTE: We have reverted to the vanilla version of ControlsFX for 
        //  now, as the enhanced version is not yet published to GitHub.
        //taskProgressView = new TaskProgressView<>( placeholderText );
        taskProgressView = new TaskProgressView<>();

        taskIconFactory = task -> {
            Node content = null;
            
            switch ( task.getDataUpdateType() ) {
            case DYNAMIC_UPDATE:
                content = dynamicUpdateTaskIcon;
                break;
            case FULL_UPDATE:
                content = fullUpdateTaskIcon;
                break;
            default:
                break;
            }
            
            return content;
        };

        taskProgressView.setGraphicFactory( taskIconFactory );

        // Set the custom CSS Style Class.
        // NOTE: Make sure that this stage loads the CSS that has this tag.
        // TODO: Use a different Style Class as this is now a Stage vs. Pop-up?
        taskProgressView.getStyleClass().add( "status-box" );

        return taskProgressView;
    }
}
