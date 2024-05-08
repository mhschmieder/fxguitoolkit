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
package com.mhschmieder.fxguitoolkit.demo;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.LogUtilities;
import com.mhschmieder.commonstoolkit.net.HttpServletRequestProperties;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.fxguitoolkit.stage.MainApplicationLoadTask;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.application.Application.Parameters;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A dummy bare bones demo application that shows how to make use of some of
 * this libraries facilities for using task managers for loading splash screens,
 * redirecting logging at startup, and other stuff that is common to most apps.
 * <p>
 * In some cases, this will serve as an appropriate base class for your own
 * application, but it should at least help template your own version.
 */
public class DemoApplication extends Application {

    // This interface is solely for the purpose of providing a simple mechanism
    // for completing the display of the startup Splash Screen.
    public interface InitCompletionHandler {
        void complete();
    }

    /**
     * It is advised to provide a main() method that at least explicitly calls
     * launch() so that standalone contexts are supported. The launch() method
     * calls the init() method followed by the start() method.
     * <p>
     * The main() method is ignored in correctly deployed JavaFX applications.
     * main() serves only as a fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited JavaFX
     * support. NetBeans ignores main().
     *
     * @param args
     *            Variable command-line argument list when launched standalone.
     */
    public static void main( final String[] args ) {
        // This looks weird, but is proven to be required in order to get access
        // control permissions for loading the Google Sign-in webpage in WebView.
        System.setProperty( "sun.net.http.allowRestrictedHeaders", "true" );

        // Forward the command line arguments to the internal launcher.
        launch( args );
    }

    public static final Logger LOG = Logger.getLogger( DemoApplication.class.getName() );

    // Declare class-level variables for context that might be needed by several
    // overridden methods called by the JVM during Application start() and
    // stop() methods. Everything else can be local method variables.
    protected String                sessionLogFileName;
    protected ClientProperties      clientProperties;
    protected ProductBranding       productBranding;
    protected String                cssStylesheet;

    /**
     * Cache the Server Request Properties, which are static except for Login.
     */
    private HttpServletRequestProperties httpServletRequestProperties;

    /** The JAR-relative path name for the Splash Screen image to use. */
    protected String                jarRelativeSplashScreenFilename;

    // Declare GUI components related to Splash Screen and startup.
    protected Pane                  splashScreenLayout;
    protected ImageView             splashScreenImageView;
    protected ProgressBar           loadProgress;
    protected String                progressText;
    protected Label                 progressLabel;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file (when present) has been loaded.
     */
    @SuppressWarnings("nls")
    @Override
    public void init() {
        // Redirect STDERR and STDOUT to account for legacy use (and third-party
        // library use) of System.err.println(), System.out.println(), and
        // Exception.printStackTrace() -- all of which are now deprecated.
        // NOTE: The default temporary directory may not be writable.
        try {
            final File sessionLogFile = File.createTempFile( "DemoSessionLog", ".txt" );

            // During early development, it is best to leave the log file on disc.
            // In a mature application, this file is used instead to refresh a log
            // viewer with export capabilities, either automatically or on demand.
            sessionLogFile.deleteOnExit();

            sessionLogFileName = sessionLogFile.getCanonicalPath();
            LogUtilities.redirectLogging( sessionLogFileName );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        // Get the encapsulated application parameters to forward to the session
        // context initialization. It includes JNLP parameters and command-line
        // JVM arguments. The preferred CSS Style Sheet can be passed here too.
        // NOTE: We currently set system parameters and do not pass command
        //  line arguments, but it is worth revisiting this strategy.
        final Parameters applicationParameters = getParameters();
        final Map< String, String > namedArguments = applicationParameters.getNamed();

        // Initialize anything not related to creating scenes or stages.
        initVariables( namedArguments );

        // Layout the Splash Screen so it is ready to go when we get a Stage.
        // :NOTE This is OK to do on the JavaFX Launcher Thread vs. the JavaFX
        //  Application Thread, as we hold off on working with Scenes or Stages,
        //  and as the image loading might take awhile so should be front-loaded.
        layoutSplashScreen();

        // Generate critical info about the client, the user and their computer.
        LogUtilities.generateSessionLogHeader( clientProperties, productBranding, cssStylesheet );
    }

    /**
     * Initialize variables, excepting any scene or stage building, which must
     * be deferred until inside the start() method or any methods it invokes.
     * <p>
     * NOTE: The initialization order is designed to minimize the chance that a
     *  startup error could result in a blank Session Log or before the header.
     * <p>
     * NOTE: As the Session Log Header has to redundantly grab the system
     *  properties anyway, it makes sense to prioritize Session Context loading.
     *
     * @param namedArguments
     *            The named arguments passed to the application launcher,
     *            including JNLP parameters and command-line JVM arguments
     */
    private void initVariables( final Map< String, String > namedArguments ) {
        // Get the Session Context from Java and JNLP properties etc.
        // NOTE: This culls information from a variety of sources, subsystems,
        //  and API's, so needs to be vetted carefully for threading as well as
        //  safety in terms of accessibility of the data at this point in the
        //  application initialization cycle. For example, the screen dimensions
        //  are queried via JavaFX. Maybe that's dangerous at this point?
        clientProperties = GlobalUtilities.makeClientProperties( namedArguments );

        //httpServletRequestProperties = DemoUtilities.getServerRequestProperties( namedArguments );

        // Get the product branding information.
        productBranding = new ProductBranding( "JavaFX Demo - Basic", 
                                               "JavaFX Demo", 
                                               "Beta", 
                                               "Release",
                                               "2024-05-08" );
        
        // Get the Splash Screen filename, based on Client Type.
        //jarRelativeSplashScreenFilename = DemoUtilities.getJarRelativeSplashScreenFilename();

        // Make sure to set the Modena CSS style sheet explicitly, in case a
        // future JavaFX update changes the default and it has a negative impact
        // on application look and feel until GUI coding tweaks are performed.
        // NOTE: This also makes this a placeholder for setting Aqua CSS for
        //  macOS and/or Metro CSS for Windows, if we choose to later on.
        cssStylesheet = Application.STYLESHEET_MODENA;
    }

    /**
     * Layout the Splash Screen so it is ready to go when we get a Stage.
     */
    private void layoutSplashScreen() {
        // Background-load the Splash Screen Image as a JAR-resident resource,
        // then place it into an Image View container, so it can be displayed
        // and scaled.
        splashScreenImageView = ImageUtilities.getImageView( jarRelativeSplashScreenFilename,
                                                              true );

        // Create a Progress Bar the same width as the Splash Screen Image.
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth( GuiUtilities.SPLASH_WIDTH );
        progressText = "Loading " + productBranding.productVersion; //$NON-NLS-1$
        progressLabel = new Label( progressText );

        splashScreenLayout = new VBox();
        splashScreenLayout.getChildren()
                .addAll( splashScreenImageView, loadProgress, progressLabel );

        progressLabel.setAlignment( Pos.CENTER );

        // Set border and colors to make the Splash Screen easier to see.
        // NOTE: This mimics the custom CSS tag #status-box for the About Box.
        final Background background = LayoutFactory.makeRegionBackground( Color.ALICEBLUE );
        splashScreenLayout.setBackground( background );
        splashScreenLayout.setPadding( new Insets( 3.0d ) );
        final BorderStroke borderStroke = new BorderStroke( Color.LIGHTSTEELBLUE,
                                                            BorderStrokeStyle.SOLID,
                                                            CornerRadii.EMPTY,
                                                            new BorderWidths( 3.0d ) );
        splashScreenLayout.setBorder( new Border( borderStroke ) );
    }

    /**
     * Create the Splash Screen and show it, using the initial stage.
     *
     * @param initialStage
     *            The initial stage created by the JavaFX engine
     * @param task
     *            The task used to link the Splash Screen to the startup cycle
     * @param initCompletionHandler
     *            The handler for watching startup completion
     */
    private void showSplashScreen( final Stage initialStage,
                                   final Task< ? > task,
                                   final InitCompletionHandler initCompletionHandler ) {
        progressLabel.textProperty().bind( task.messageProperty() );
        loadProgress.progressProperty().bind( task.progressProperty() );
        task.stateProperty().addListener( ( observableValue, oldState, newState ) -> {
            if ( newState == Worker.State.SUCCEEDED ) {
                // Once the task is done, unbind the properties and set to
                // indeterminate so that there is no confusion if still visible.
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress( -1d );

                initialStage.toFront();

                final FadeTransition fadeSplash = new FadeTransition( Duration.seconds( 1.2 ),
                                                                      splashScreenLayout );
                fadeSplash.setFromValue( 1.0d );
                fadeSplash.setToValue( 0.0d );

                fadeSplash.setOnFinished( actionEvent -> initialStage.hide() );

                fadeSplash.play();

                initCompletionHandler.complete();
            }
        } );

        // Now that we are on the JavaFX Application Thread, it is safe to
        // create a Scene from the prepared layout pane.
        // TODO: Set the specific size at Scene creation time as well?
        final Scene splashScene = new Scene( splashScreenLayout, Color.TRANSPARENT );

        // Attempt to set transparency on the Splash Screen, if supported.
        initialStage.initStyle( StageStyle.TRANSPARENT );

        // Always center the Splash Screen on the screen.
        GuiUtilities.centerOnScreen( initialStage );

        // Set the Splash Screen to top, so it isn't blocked by other
        // applications. It is easy to dismiss, so shouldn't prove annoying.
        initialStage.setAlwaysOnTop( true );

        // Use the primary Stage created by the JavaFX engine to host the Scene
        // containing the Splash Screen and Progress Bar.
        initialStage.setScene( splashScene );

        // Show the Splash Screen, now that it is fully initialized.
        initialStage.show();
    }

    @Override
    public void start( final Stage primaryStage ) throws Exception {
        // Make sure to set the preferred CSS style sheet explicitly, in case a
        // future JavaFX update changes the default and it has a negative impact
        // on application look and feel until GUI coding tweaks are performed.
        setUserAgentStylesheet( cssStylesheet );

        // Try to set system-specific properties, where relevant.
        // NOTE: Commented out, because it appears these are Swing-specific.
        // MappUtilities.setSystemProperties( clientProperties.systemType );

        try {
            // Create the main application load task so that we can bootstrap
            // the rest of the initialization sequence.
            final MainApplicationLoadTask mainApplicationLoadTask =
                                                                  new MainApplicationLoadTask( progressText );

            // Show the Splash Screen until the main stage shows.
            showSplashScreen( primaryStage,
                              mainApplicationLoadTask,
                              () -> mainApplicationLoadTask.showMainApplicationStage() );

            // Start the progress bar updater on the Splash Screen.
            new Thread( mainApplicationLoadTask ).start();

            // Instantiate the main application stage (i.e. Demo Stage).
            final DemoStage demoStage = new DemoStage( "JavaFX Demo",
                                                       "demo",
                                                       jarRelativeSplashScreenFilename,
                                                       false,
                                                       productBranding,
                                                       clientProperties );

            // Pass the main stage reference to the application load task.
            mainApplicationLoadTask.setMainApplicationStage( demoStage );

            // Host Services cannot be statically invoked, and are needed for
            // stuff like launching the default browser.
            final HostServices hostServices = getHostServices();
            demoStage.setHostServices( hostServices );

            // Now it is safe to start longer tasks, such as initializing the
            // main application stage, as the Splash Screen should now be
            // showing, and nothing expensive should have been done beforehand.
            // NOTE: This also makes all the secondary stages, pop-ups, etc.
            demoStage.initStage( true );
        }
        catch ( final Throwable throwable ) {
            // Recoverable Throwables should be handled lower down.
            throwable.printStackTrace();
            System.err.println( "Exiting due to error while starting the app." ); //$NON-NLS-1$
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // TODO: Ensure that User Preferences are saved. See XStage#disposeAllResources.
        // TODO: Flush the Session Log to disc, so that Tech Support can direct
        //  users to its likely location and have them open it in the event that
        //  logging occurred but the GUI never launched?

        // This is a fail-safe in case exiting the JavaFX Application Thread
        // failed to exit the Java Runtime Environment for this Application.
        System.exit( 0 );
    }
}
