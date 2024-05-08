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

import java.util.ArrayList;
import java.util.List;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.stage.AboutBox;
import com.mhschmieder.fxguitoolkit.stage.MacAppMenuEventHandler;
import com.mhschmieder.fxguitoolkit.stage.NoticeBox;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

// This class implements the basic features that all applications should share.
// TODO: Find a JavaFX equivalent for the OSXAdapter, or delete for now.
public abstract class MainApplicationStage extends XStage implements MacAppMenuEventHandler {

    // Cache the Splash Screen File Name, as we load it in several places.
    public final String      _splashScreenFileName;

    // Cache the Host Services, which are loaded from the Application instance.
    public HostServices      _hostServices;

    // Declare windows and pop-ups used by most desktop applications.
    protected AboutBox       _aboutBox;
    protected NoticeBox      _eula;
    protected NoticeBox      _preferencesNotice;

    // Declare list of third-party attributions.
    protected List< String > _thirdPartyAttributions;

    // Declare flag to indicate when the main application stage is initialized.
    protected boolean        _initialized = false;

    protected MainApplicationStage( final String title,
                                    final String windowKeyPrefix,
                                    final String jarRelativeSplashScreenFileName,
                                    final boolean supportsRenderedGraphicsExport,
                                    final ProductBranding productBranding,
                                    final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title,
               windowKeyPrefix,
               true,
               true,
               supportsRenderedGraphicsExport,
               productBranding,
               pClientProperties );

        _splashScreenFileName = jarRelativeSplashScreenFileName;

        // Defer loading Host Services, as it might slow down initialization.
        _hostServices = null;

        // Hide this Stage until application initialization is done.
        blockSession();
    }

    @Override
    public void about() {
        // Display the About Box until the user dismisses it.
        if ( !_aboutBox.isShowing() ) {
            _aboutBox.show( _popupOwner );
        }
    }

    public void blockSession() {
        // Hide this Stage until initialization is done.
        hide();
        toBack();
    }

    // NOTE: This behavior is for SDI applications, not MDI applications.
    @Override
    public void close() {
        // Force the default window closing operation (the "X" in the upper
        // right hand corner of the frame) to exit the application, first
        // saving user input.
        quit();
    }

    public final void doAbout() {
        // Display the About Box until the user dismisses it.
        about();
    }

    public final void doEula() {
        // Display the EULA until the user dismisses it.
        eula( _popupOwner );
    }

    public final void doExit() {
        // Exit the application.
        quit();
    }

    public final void doPreferences() {
        // Display the Preferences Notice until the user dismisses it.
        preferences();
    }

    public final void eula( final Window popupOwner ) {
        // Display the EULA until the user dismisses it.
        if ( !_eula.isShowing() ) {
            _eula.show( popupOwner );
        }
    }

    /**
     * Take care of exit tasks that pertain to all applications, e.g. save User
     * Preferences and dispose of all resources.
     *
     * @param exitPlatform
     *            Flag for whether to also exit the Java Platform
     */
    protected final void exitApplication( final boolean exitPlatform ) {
        // Save the User Preferences for the entire Application (if applicable).
        // NOTE: We do this BEFORE disposing of all resources, to avoid race
        //  conditions with windows closing before their preferences have been
        //  saved (if done as part of a window closing implementation).
        saveAllPreferences();

        // Dispose of all resources allocated by this Stage.
        disposeAllResources();

        // Exit the application via the JavaFX exit call. This is considered
        // safer than the standard System.exit() call, and shuts down the GUI.
        // NOTE: Without this call, Application.stop() never gets invoked,
        //  which in turn means the application never gets properly shut down.
        if ( exitPlatform ) {
            Platform.exit();
        }
    }

    // NOTE: This must be overridden unconditionally so that the OSXAdapter can
    //  find it on this class.
    @Override
    public void hide() {
        super.hide();
    }

    /**
     * Take care of initialization tasks that pertain to all applications, e.g.
     * load User Preferences and set the default project.
     */
    protected final void initApplication() {
        // It should now be safe to register dirty flag listeners, without
        // causing false dirty flags at startup.
        addDirtyFlagListeners();

        // Load the User Preferences for the entire Application (if applicable).
        // NOTE: We do this AFTER preparing for input, as otherwise some of the
        //  stored settings could be set back to their default values.
        loadAllPreferences();

        // Set the default project, which might be referenced from preferences.
        setDefaultProject( true );

        // Indicate that the main application stage is now initialized.
        _initialized = true;
    }

    // Set local properties that are static per application session.
    @Override
    protected void initProperties() {
        // Initialize the superclass properties.
        super.initProperties();

        // NOTE: We now pass in the fully qualified application name so we can
        // distinguish which server the deployment is targeted towards, making
        // it easier to compare behavior between the test server, the staging
        // server and the public server.
        _defaultTitle = new StringBuilder( _productBranding.applicationName );

        // Make the container for the necessary third-party attributions.
        // NOTE: This is done in this super-class, in case there are any
        //  general licenses to add that cover all downstream derived main
        //  application stages.
        _thirdPartyAttributions = new ArrayList<>();
    }

    public final boolean isInitialized() {
        return _initialized;
    }

    @Override
    protected void loadPopups() {
        super.loadPopups();

        // Load the Splash Screen Image as a JAR-resident resource.
        final Image splashScreenImage = ImageUtilities
                .loadImageAsJarResource( _splashScreenFileName, true );

        // Make the About Box, using the designated Splash Screen Image.
        _aboutBox = new AboutBox( splashScreenImage,
                                  true,
                                  clientProperties.systemType,
                                  _productBranding,
                                  _thirdPartyAttributions,
                                  true );
        _windowManager.addPopup( _aboutBox );

        // TODO: Find a way to pass in necessary arguments for a general
        //  construction of the EULA and Preferences Pane here, as otherwise they
        //  are potentially null unless all derived primary application stages
        //  properly take care of initializing those pop-ups themselves.
    }

    // Open a supplied project file given its file name alone (as a full file
    // path). This is launched via drag/drop from a desktop file icon.
    // NOTE: This method is abstract as we need to know application domain.
    @Override
    public abstract void open( final String filePath );

    // TODO: Add a global preferences menu item for non-Mac version and
    //  implement this method for all platforms, bringing up a comprehensive
    //  preferences dialog.
    @Override
    public final void preferences() {
        // Display the Preferences Notice until the user dismisses it.
        if ( !_preferencesNotice.isShowing() ) {
            _preferencesNotice.show( _popupOwner );
        }
    }

    // Prepare this Stage and its subsidiary components for input.
    @Override
    protected void prepareForInput( final MenuBar menuBar ) {
        // On some platforms, closing the window sometimes ends the process.
        setOnCloseRequest( this::windowClose );

        // It is safer to set the general input preparation last, as method
        // overload may otherwise access uninitialized variables.
        super.prepareForInput( menuBar );
    }

    // Close this stage and exit the application, after first giving the user
    // the option to save or close the current file or to cancel the Quit.
    @Override
    public final void quit() {
        // Perform the normal File Close action, which asks for confirmation.
        final boolean canceled = fileClose();

        // If the user cancels, exit the Quit request.
        if ( canceled ) {
            return;
        }

        // Follow the common exit strategy for application-level Window Close.
        exitApplication( true );
    }

    // Define method for derived classes to implement for context reset per
    // session (or other criteria for reset).
    protected abstract void resetSessionContext();

    // Set the default project, which might be referenced from preferences.
    protected abstract void setDefaultProject( final boolean applicationInitMode );

    public final void setHostServices( final HostServices hostServices ) {
        _hostServices = hostServices;
    }

    public void startSession() {
        // Reset the session context for visualization, export, undo, redo, etc.
        resetSessionContext();

        // Try to force the garbage collector, as there's usually a lot of junk
        // generated during initialization that otherwise may take awhile to get
        // removed, resulting in unexpected memory caps and slow performance.
        // NOTE: Commented out, as this can slow down window layout
        //  stabilization since that also runs on a deferred thread.
        // Platform.runLater( () -> System.gc() );

        // Show this Stage until the user dismisses it.
        // NOTE: This is run on a deferred thread so that the window doesn't
        //  show until all layout managers and resizings that are on deferred
        //  threads have also been run. This avoids startup glitches with the
        //  window appearing to grow or re-layout after its initial showing.
        // NOTE: A second level of thread-nesting is required, unfortunately.
        Platform.runLater( () -> { Platform.runLater( () -> { setVisible( true, true ); } ); } );
    }

    // NOTE: This must be overridden unconditionally so that the OSXAdapter can
    // find it on this class.
    @Override
    public void toFront() {
        super.toFront();
    }

    /**
     * Like fileClose, but upon Cancel, consumes a WindowEvent to avoid exiting.
     */
    protected final void windowClose( final WindowEvent event ) {
        // Make sure the window doesn't auto-close, by consuming the event.
        // NOTE: This only goes so far towards preventing other windows from
        // receiving the event and closing, but is better than nothing.
        event.consume();

        // Now it is safe to process this like a normal Quit action.
        quit();
    }
}
