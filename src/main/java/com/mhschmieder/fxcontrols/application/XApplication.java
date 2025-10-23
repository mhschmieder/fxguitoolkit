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
package com.mhschmieder.fxcontrols.application;

import com.mhschmieder.fxcontrols.demo.DemoStage;
import com.mhschmieder.fxcontrols.stage.MainApplicationLoadTask;
import com.mhschmieder.fxcontrols.stage.MainApplicationStage;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.branding.ProductVersion;
import com.mhschmieder.jcommons.io.IoUtilities;
import com.mhschmieder.jcommons.io.LogUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.GlobalUtilities;
import com.mhschmieder.jcommons.util.SystemUtilities;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An extension of JavaFX Application class that shows how to make use of this
 * library's facilities for using task managers for loading splash screens, to
 * redirect logging at startup, and other features common to most applications.
 * <p>
 * In some cases, this may serve as an appropriate base class for your own
 * application, but it should at least help template your own version.
 */
public class XApplication extends Application {

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
        // Forward the command line arguments to the internal launcher.
        launch( args );
    }

    public static final Logger LOG = Logger.getLogger( XApplication.class.getName() );

    // Declare class-level variables for context that might be needed by several
    // overridden methods called by the JVM during Application start() and
    // stop() methods. Everything else can be local method variables.
    protected String                sessionLogFilename;
    protected ClientProperties      clientProperties;
    protected ProductVersion        productVersion;
    protected ProductBranding       productBranding;
    protected String                cssStylesheet;

    /** The JAR-relative path name for the Splash Screen image to use. */
    protected String                jarRelativeSplashScreenFilename;
    
    /** 
     * The Splash Screen Manager that lays out the layout and display, and
     * manages its life cycle relative to the application startup sequence.
     */
    protected SplashScreenManager   splashScreenManager ;

    /**
     * Declare an executor for convenient access to concurrency and threading.
     */
    private final ScheduledExecutorService executor 
        = Executors.newScheduledThreadPool( 1,
                                            Executors.defaultThreadFactory() );

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file (when present) has been loaded.
     */
    @Override
    public void init() {
        // Set up logging in the way that your application prefers.
        setUpLogging();

        // Get the encapsulated application parameters to forward to the session
        // context initialization. This includes JNLP parameters and command-line
        // JVM arguments. The preferred CSS Style Sheet can be passed here too.
        // NOTE: We currently set system parameters and do not pass command
        //  line arguments, but it is worth revisiting this strategy.
        final Parameters parameters = getParameters();
        final Map< String, String > namedArguments = parameters.getNamed();

        // Initialize anything not related to creating scenes or stages.
        initVariables( namedArguments );

        // Layout the Splash Screen so it is ready to go when we get a Stage.
        // NOTE: This is OK to do on the JavaFX Launcher Thread vs. the JavaFX
        //  Application Thread, as we hold off on working with Scenes or Stages,
        //  and as the image loading might take awhile so should be front-loaded.
        splashScreenManager = new SplashScreenManager( jarRelativeSplashScreenFilename, 
                                                       productBranding );

        // Generate critical info about the client, the user and their computer.
        LogUtilities.generateSessionLogHeader( clientProperties, 
                                               productBranding, 
                                               cssStylesheet );
    }

    /**
     * The main entry point for all JavaFX applications. The start method is
     * called after the init method has returned, and after the system is ready
     * for the application to begin running.
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * 
     * @param primaryStage The primary stage for this application, returned by
     *                     the JavaFX platform at startup, and which a scene can
     *                     be set for a primary application window or a startup
     *                     splash screen. Applications may create other stages,
     *                     and the primary stage is not as configurable anyway.
     * @throws Exception if a security block or other issue happens at startup
     */
    @Override
    public void start( final Stage primaryStage ) throws Exception {
        // Make sure to set the preferred CSS style sheet explicitly, in case a
        // future JavaFX update changes the default and it has a negative impact
        // on application look and feel until GUI coding tweaks are performed.
        setUserAgentStylesheet( cssStylesheet );

        // Try to set system-specific properties, where relevant.
        setSystemProperties();

        try {
            // Create the main application load task so that we can bootstrap
            // the rest of the initialization sequence.
            final MainApplicationLoadTask mainApplicationLoadTask
                = splashScreenManager.makeMainApplicationLoadTask();

            // Show the Splash Screen until the main stage shows.
            splashScreenManager.showSplashScreen( primaryStage,
                                                  mainApplicationLoadTask,
                                                  () -> mainApplicationLoadTask.showMainApplicationWindow() );

            // Start the progress bar updater on the Splash Screen.
            new Thread( mainApplicationLoadTask ).start();

            // Instantiate the main application stage.
            final MainApplicationStage mainApplicationStage = getMainApplicationStage();

            // Pass the main stage reference to the application load task.
            mainApplicationLoadTask.setMainApplicationWindowHandler( mainApplicationStage );

            // Host Services cannot be statically invoked, and are needed for
            // stuff like launching the default browser.
            final HostServices hostServices = getHostServices();
            mainApplicationStage.setHostServices( hostServices );

            // Now it is safe to start longer tasks, such as initializing the
            // main application stage, as the Splash Screen should now be
            // showing, and nothing expensive should have been done beforehand.
            // NOTE: This also makes all the secondary stages, pop-ups, etc.
            mainApplicationStage.initStage( true );

            // Now that the GUI itself is built, we can initialize the application.
            mainApplicationStage.initApplication();
        }
        catch ( final Throwable throwable ) {
            // Recoverable Throwables should be handled lower down.
            throwable.printStackTrace();
            System.err.println( "Exiting due to error while starting the app." );
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
        // NOTE: It is safer for JavaFX Platform manage application shutdown.
        System.exit( 0 );
    }
    
    /**
     * Sets up logging for the full application life cycle.
     * <p>
     * TODO: Your application should override this method if it uses log4j or
     *  has another preferred way of redirecting logging and System prints.
     */
    public void setUpLogging() {
        // Redirect STDERR and STDOUT to account for legacy use (and third-party
        // library use) of System.err.println(), System.out.println(), and
        // Exception.printStackTrace() -- all of which are now deprecated.
        // NOTE: The default temporary directory may not be writable.
        try {
            final String sessionLogName = getSessionLogName();
            final File sessionLogFile = File.createTempFile( sessionLogName, ".txt" );

            // During early development, it is best to leave the log file on disc.
            // In a mature application, this file is used instead to refresh a log
            // viewer with export capabilities, either automatically or on demand.
            sessionLogFile.deleteOnExit();

            sessionLogFilename = sessionLogFile.getCanonicalPath();
            LogUtilities.redirectLogging( sessionLogFilename );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
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
    public void initVariables( final Map< String, String > namedArguments ) {
        // Add a shutdown hook so that JavaFX can close the app, which is cleaner
        // and safer than calling System.exit(), which is deprecated.
        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            LOG.log( Level.INFO, "System Shutting Down" );
            executor.shutdownNow();
        } ) );

        // Get the Session Context from Java and JNLP properties etc.
        // NOTE: This culls information from a variety of sources, subsystems,
        //  and API's, so needs to be vetted carefully for threading as well as
        //  safety in terms of accessibility of the data at this point in the
        //  application initialization cycle. For example, the screen dimensions
        //  are queried via JavaFX. Maybe that's dangerous at this point?
        clientProperties = GlobalUtilities.makeClientProperties( namedArguments );

        // Get the application's product version information.
        productVersion = getProductVersion();

        // Get the application's product branding information.
        productBranding = getProductBranding();
        
        // Get the application's Splash Screen filename.
        jarRelativeSplashScreenFilename = getJarRelativeSplashScreenFilename();

        // Get the application's preferred default CSS stylesheet URL.
        cssStylesheet = getCssStylesheet();
    }
    
    /**
     * Returns the simple name of the application's session log, sans suffix.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  the preferred name of your application's session log file.
     * 
     * @return the simple name of the application's session log, sans suffix
     */
    public String getSessionLogName() {
        // Get the Session Log Name, using a demo placeholder name.
        return "DemoSessionLog";
    }
    
    /**
     * Returns an instance of {@link ProductVersion} to identify product name, 
     * version, build date, and other product attributes. This is a convenient
     * struct-style wrapper that avoids copy/paste code throughout an app as
     * well as eliminating the need for repeated queries to system properties.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  the preferred branding details of your application's build, date, etc.
     * 
     * @return an instance of {@link ProductVersion}
     */
    public ProductVersion getProductVersion() {
        // Get Product Version for the application using demo placeholders.
        return new ProductVersion( "",
                                   "JavaFX",
                                   "Demo",
                                   "", 
                                   1,
                                   0,
                                   0,
                                   0L,
                                   "",
                                   "10 May 2024" );    
    }
   
    /**
     * Returns an instance of {@link ProductBranding} to identify product name, 
     * version, build date, and other product attributes. This is a convenient
     * struct-style wrapper that avoids copy/paste code throughout an app as
     * well as eliminating the need for repeated queries to system properties.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  the preferred branding details of your application's build, date, etc.
     * 
     * @return an instance of {@link ProductBranding}
     */
    public ProductBranding getProductBranding() {
        // Get Product Branding for the application using demo placeholders.
        return new ProductBranding( "",
                                    productVersion );    
    }
    
    /**
     * Returns the URL of the preferred default CSS stylesheet, as a String.
     * <p>
     * NOTE: This method should be overridden by your application if you want
     *  to use something other than the default JavaFX 8 Modena CSS stylesheet
     *  of if you want to add OS-switching logic for Aqua CSS or Metro CSS.
     * 
     * @return the URL of the preferred default CSS stylesheet, as a String
     */
    public String getCssStylesheet() {
        // Set the Modena CSS style sheet explicitly, in case a future JavaFX
        // update changes the default and it has a negative impact on GUI LAF.
        return Application.STYLESHEET_MODENA;
    }
    
    /**
     * Returns the JAR-relative filename of the application's splash screen.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  the filename of your application's splash screen.
     * 
     * @return the JAR-relative filename of the application's splash screen
     */
    public String getJarRelativeSplashScreenFilename() {
        // Get the Splash Screen filename, using a demo JavaFX placeholder image.
        return IoUtilities.getJarResourceFilename( "/java/", 
                                                   "JavaFxTextLogo", 
                                                   "png" );        
    }
    
    /**
     * Returns an instance of the Main Application Stage for this application.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  the Main Application Stage that is defined for your application.
     * 
     * @return an instance of the Main Application Stage for this application
     */
    public MainApplicationStage getMainApplicationStage() {
        // Get a demo stage as a placeholder, so this class can be tested.
        return new DemoStage( productBranding, clientProperties );
    }
    
    /**
     * Try to set system-specific properties, where relevant.
     * <p>
     * NOTE: This method should be overridden by your application to provide
     *  a custom set of system property settings that are needed at startup.
     */
    public void setSystemProperties() {
        // NOTE: Effectively a no-op in this default implementation.
        SystemUtilities.setSystemProperties( clientProperties.systemType,
                                             productBranding.productName,
                                             false,
                                             false );
    }
}
