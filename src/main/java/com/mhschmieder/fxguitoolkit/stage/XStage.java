/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
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

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxgraphicstoolkit.print.PrintUtilities;
import com.mhschmieder.fxguitoolkit.ForegroundManager;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.action.MruFileActions;
import com.mhschmieder.fxguitoolkit.action.WindowSizeActions;
import com.mhschmieder.fxguitoolkit.action.XAction;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * {@code XStage} is a skeletal abstract base class that extends the JavaFX
 * Stage class enough to serve as a better boilerplate starting point for most.
 */
public abstract class XStage extends Stage implements ForegroundManager, FileHandler {

    // To avoid cut/paste errors with resource references, make global constants
    // for the CSS theme to be used for dark vs. light backgrounds.
    @SuppressWarnings("nls") public static final String DARK_BACKGROUND_CSS  =
                                                                            "/css/theme-dark.css";
    @SuppressWarnings("nls") public static final String LIGHT_BACKGROUND_CSS =
                                                                             "/css/theme-light.css";

    private static final int                            TOOL_BAR_HEIGHT      = 40;

    // Declare a root pane for centering the main content pane.
    protected BorderPane                                _root;

    // Declare an action pane for clients and derived classes to fill.
    private BorderPane                                  _actionPane;

    // Declare a main Content Node for clients and derived classes to fill.
    protected Node                                      _content;

    // Declare a flag for whether this window is its own frame title manager.
    private final boolean                               _frameTitleManager;

    // Cache a default title that is unique to each usage.
    protected StringBuilder                             _defaultTitle;

    // Cache a default window size that is unique to each usage.
    protected Dimension2D                               _defaultWindowSize;

    // Cache a preferred window size that is unique to each usage.
    protected Dimension2D                               _preferredWindowSize;

    // Flag for whether this window is exempt from handling Full Screen Mode.
    protected boolean                                   _fullScreenModeExempt;

    // We need an always active Printer Job in order to support Page Setup.
    protected PrinterJob                                _printerJob;

    // Cache the window key prefix that is used for window layout preferences.
    private final String                                _windowKeyPrefix;

    // Declare a file object to hold the most recent directory used in a file
    // chooser.
    protected File                                      _defaultDirectory;

    // Declare a reference to the optional MRU File actions.
    protected MruFileActions                            _mruFileActions;

    // Declare a cache for most recently used files.
    public LinkedList< String >                         _mruFilenameCache;

    // Cache the pop-up window owner for warning/error dialogs, pop-ups, etc.
    protected final Window                              _popupOwner;

    // Declare flag for whether this window shows the dirty flag.
    private final boolean                               _showDirtyFlag;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat                              _numberFormat;

    // Percent format cache used for locale-specific percent formatting.
    protected NumberFormat                              _percentFormat;

    // Number format cache used for locale-specific angle formatting.
    protected NumberFormat                              _angleFormat;

    // Number format cache used for locale-specific number parsing.
    protected NumberFormat                              _numberParse;

    // Percent format cache used for locale-specific percent parsing.
    protected NumberFormat                              _percentParse;

    // Cache a reference to the product branding information.
    protected final ProductBranding                     _productBranding;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties                             clientProperties;

    protected XStage( final Modality modality,
                      final String title,
                      final String windowKeyPrefix,
                      final boolean showDirtyFlag,
                      final boolean frameTitleManager,
                      final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        // NOTE: The commented-out example code shows how to remove the
        // minimize and maximize buttons on the Mac. It isn't possible to just
        // remove the maximize button, nor is there a callback to ignore, and as
        // we need the minimize button, this code is disabled for now.
        // super( SystemType.MACOS.equals( sessionContext.systemType ) ?
        // StageStyle.UTILITY : StageStyle.DECORATED );
        super( StageStyle.DECORATED );

        // Initialize the Modality as soon as possible (API contract).
        initModality( modality );

        // Immediately set external window close events (such as via the Mac's
        // Application Quit Menu Item) to ignore the request, and override as
        // necessary. We will manage our windows directly instead.
        // NOTE: This also blocks the "red circle" window closing button on
        // macOS, as we have to back this out for now.
        // setOnCloseRequest( evt -> evt.consume() );

        _defaultTitle = new StringBuilder( title );
        _windowKeyPrefix = windowKeyPrefix;
        _showDirtyFlag = showDirtyFlag;
        _frameTitleManager = frameTitleManager;
        _productBranding = productBranding;
        clientProperties = pClientProperties;

        // Default the pop-up window owner for warning/error dialogs to a
        // self-reference of this stage, in case a higher-level node in the GUI
        // hierarchy cannot be supplied (as in the context of a hybrid app).
        _popupOwner = this;

        // By default, most windows are obligated to handle Full Screen Mode.
        _fullScreenModeExempt = false;
    }

    protected XStage( final String title,
                      final String windowKeyPrefix,
                      final boolean showDirtyFlag,
                      final boolean frameTitleManager,
                      final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        this( Modality.NONE,
              title,
              windowKeyPrefix,
              showDirtyFlag,
              frameTitleManager,
              productBranding,
              pClientProperties );
    }

    protected XStage( final String title,
                      final String windowKeyPrefix,
                      final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        this( title, windowKeyPrefix, false, false, productBranding, pClientProperties );
    }

    // Actions are not required for stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected void addActionHandlers() {}

    // Add all of the event listeners and handlers associated with common
    // functionality.
    private final void addAllListeners() {
        // Add callback handlers for relevant actions.
        addActionHandlers();

        // Add callback listeners for relevant nodes.
        addCallbackListeners();

        // Add the Context Pop-Up Menu event listeners.
        addContextMenuListeners();

        // Initialize the Tool Bar event listeners.
        addToolBarListeners();
    }

    // Callback listeners are not required for stages, so this method is not
    // declared abstract but is instead given a default no-op implementation.
    protected void addCallbackListeners() {}

    // Context menus are not required for stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected void addContextMenuListeners() {}

    // Dirty flags are not required for stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected void addDirtyFlagListeners() {}

    // The MRU File List is handled uniformly, when supported, so register all
    // the action callbacks here as otherwise we would have cut/paste code that
    // is hard to maintain and verify as complete and correct.
    public final void addMruFileActionListeners( final MruFileActions mruFileActions ) {
        // Cache the MRU Menu Item Group, for later updates.
        _mruFileActions = mruFileActions;

        // Load the event handlers for the MRU File actions.
        final int mruSize = mruFileActions._mruFileActions.length;
        for ( int i = 0; i < mruSize; i++ ) {
            final XAction mruFileAction = mruFileActions._mruFileActions[ i ];
            final int mruFileNumber = i + 1;
            mruFileAction.setEventHandler( evt -> doMruFile( mruFileNumber ) );
        }
    }

    // Tool bars are not required for stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected void addToolBarListeners() {}

    // Window size is handled uniformly, when supported, so register all the
    // action handlers here as otherwise we would have cut/paste code that
    // is hard to maintain and verify as complete and correct.
    public final void addWindowSizeActionHandlers( final WindowSizeActions windowSizeActions ) {
        windowSizeActions._windowSizePreferredSizeAction
                .setEventHandler( evt -> doWindowSizePreferredSize() );
        windowSizeActions._windowSizeDefaultSizeAction
                .setEventHandler( evt -> doWindowSizeDefaultSize() );
        windowSizeActions._windowSizeMaximumSizeAction
                .setEventHandler( evt -> doWindowSizeMaximumSize() );
    }

    public final void addWindowSizeListeners() {
        // The correct way to listen for window resizes is to add Change
        // Listeners to the width and height properties of the scene.
        // NOTE: These events happen incrementally, which is way too much.
        // NOTE: Returning from Full Screen Mode works pretty well, if we
        // re-enable these listeners, but they don't really help with manual
        // resize actions.
        getScene().widthProperty()
                .addListener( ( observableValue,
                                oldSceneWidth,
                                newSceneWidth ) -> handleWindowWidthChange() );
        getScene().heightProperty()
                .addListener( ( observableValue,
                                oldSceneHeight,
                                newSceneHeight ) -> handleWindowHeighthChange() );
    }

    public final void adjustStageWithinBounds() {
        // Unfortunately, the window may not be fully visible if it was last
        // saved on a different screen setup, so if it is fully or partially on
        // another screen, we need to re-center it on the main screen and adjust
        // the size if necessary.
        if ( FxWindowManager.isStageOutOfBounds( this ) ) {
            // Get the compensated bounds for the primary screen. This is
            // guaranteed to account for things like the dock, application menu
            // bar, etc., regardless of which platform we are running on.
            final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

            // Adjust and cache the preferred size for this window.
            final double adjustedWidth = Math.min( getWidth(), bounds.getWidth() );
            final double adjustedHeight = Math.min( getHeight(), bounds.getHeight() );
            setPreferredWindowSize( adjustedWidth, adjustedHeight );

            // Adjust and set the window's location on the screen (in pixels).
            final double adjustedX = bounds.getMinX()
                    + ( 0.5d * ( bounds.getWidth() - adjustedWidth ) );
            final double adjustedY = bounds.getMinY()
                    + ( 0.5d * ( bounds.getHeight() - adjustedHeight ) );
            setX( adjustedX );
            setY( adjustedY );

            // Attempt to explicitly set the adjusted preferred window size.
            setWindowSize( _preferredWindowSize );
        }
    }

    // Clear all of the User Preferences for this Stage.
    public final void clearPreferences() {
        // Get the user node for this package/class, so that we get the
        // preferences specific to this stage and the application's user.
        final Preferences prefs = Preferences.userNodeForPackage( getClass() );

        // Clear the preferences, leaving it to the OS to deal with the Backing
        // Store and other persistence so that the next session starts with a
        // blank slate of defaults just like the initial session.
        try {
            prefs.clear();
        }
        catch ( final BackingStoreException bse ) {
            bse.printStackTrace();
        }

        // Now reload the preferences so we go back to the defaults, as
        // otherwise the previous settings still are what get cached when the
        // session ends, thus effectively undoing the Clear Preferences action.
        loadPreferences();
    }

    // NOTE: This method should be overridden if a Stage works with file
    // objects in an MDI context and instead needs to close the current document
    // (usually the window) and switch to another active and loaded document.
    public void doCloseWindow() {
        // Hide this window by setting invisible (was close() in Swing).
        setVisible( false );
    }

    public final void doMruFile( final int mruFileNumber ) {
        // Open a new project using the cached filename.
        fileOpenMru( mruFileNumber );
    }

    public final void doPageSetup() {
        final String printCategory = "Page Setup"; //$NON-NLS-1$
        final boolean printJobVerified = verifyPrinterJob( printCategory );
        if ( !printJobVerified ) {
            return;
        }

        try {
            // NOTE: This runs internally on a separate synced thread.
            // NOTE: On macOS, setting this window as owner, causes the menu
            // system to freeze up until another primary window is shown, and
            // the behavior and window stacking order seem no different when
            // setting the dialog's owner to null, so it is safer to do so.
            // NOTE: On Windows, however, there was no side effect in using
            // this window as the dialog's owner, and not doing so causes it to
            // go to the upper left of the screen, and thus hard to notice.
            final Window windowOwner = SystemType.MACOS.equals( clientProperties.systemType )
                ? null
                : this;
            _printerJob.showPageSetupDialog( windowOwner );
        }
        catch ( final IllegalStateException ise ) {
            ise.printStackTrace();
        }
    }

    // TODO: Find a way to indicate and generate multiple pages.
    public final void doPrint() {
        final String printCategory = "Print"; //$NON-NLS-1$
        final boolean printJobVerified = verifyPrinterJob( printCategory );
        if ( !printJobVerified ) {
            return;
        }

        boolean printConfirmed = false;
        try {
            // NOTE: This runs internally on a separate synced thread.
            // NOTE: On macOS, setting this window as owner, causes the menu
            // system to freeze up until another primary window is shown, and
            // the behavior and window stacking order seem no different when
            // setting the dialog's owner to null, so it is safer to do so.
            // NOTE: On Windows, however, there was no side effect in using
            // this window as the dialog's owner, and not doing so causes it to
            // go to the upper left of the screen, and thus hard to notice.
            final Window windowOwner = SystemType.MACOS.equals( clientProperties.systemType )
                ? null
                : this;
            printConfirmed = _printerJob.showPrintDialog( windowOwner );
        }
        catch ( final IllegalStateException ise ) {
            ise.printStackTrace();
        }
        finally {
            if ( printConfirmed ) {
                // Print the requested pages.
                print( printCategory );
            }
            else {
                // Make sure the printer doesn't lock out new jobs.
                renewPrintJob();
            }
        }
    }

    // Set the window to its configured default size.
    public final void doWindowSizeDefaultSize() {
        // Attempt to set the default window size.
        setWindowSize( _defaultWindowSize );
    }

    // Set the window to Full Screen and Undecorated.
    public final void doWindowSizeMaximumSize() {
        // Attempt to set the maximum window size via Maximized Mode.
        setMaximized( true );

        // Make sure we explicitly exit Full Screen Mode.
        setFullScreen( false );
    }

    // Set the window to its last cached user preferred size.
    public final void doWindowSizePreferredSize() {
        // Attempt to set the preferred window size.
        setWindowSize( _preferredWindowSize );
    }

    /**
     * @return The main Content Node
     */
    public final Node getContent() {
        return _content;
    }

    // NOTE: StringBuilder returns a reference and thus can result in side
    // effects if a copy is not returned; unlike String which can be set
    // directly to another String with no side effects.
    public final StringBuilder getDefaultTitle() {
        return new StringBuilder( _defaultTitle );
    }

    @Override
    public final File getMruFile( final int mruId ) {
        // Avoid side effects of the user saving the current file and thereby
        // changing the cache order.
        try {
            final int mruIndex = mruId - 1;
            final String mruFilename = _mruFilenameCache.get( mruIndex );
            final File mruFile = new File( mruFilename );
            return mruFile;
        }
        catch ( final IndexOutOfBoundsException | NullPointerException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public final Dimension2D getPreferredWindowSize() {
        return _preferredWindowSize;
    }

    // TODO: Re-enable the Mac conditionals once we figure out how to do the
    // Mac's unique dirty flag in JavaFX (known as "document modified", where it
    // puts a black dot in the red circle in the upper left corner of the
    // window or frame's title bar, instead of the asterisk at the end).
    @SuppressWarnings("static-method")
    protected final StringBuilder getSubtitle( final String documentFileName,
                                               final Boolean documentModified ) {
        // Append the current document file name, with an asterisk at the end if
        // the document is modified.
        final StringBuilder subtitle = new StringBuilder( " - [" ); //$NON-NLS-1$
        subtitle.append( documentFileName );
        // if ( documentModified
        // && !SystemType.MACOS.equals( clientProperties.systemType ) ) {
        if ( documentModified ) {
            subtitle.append( "*" ); //$NON-NLS-1$
        }
        subtitle.append( "]" ); //$NON-NLS-1$
        return subtitle;
    }

    /**
     * This method returns the window key prefix for window preferences.
     *
     * @return The string to be used as the window key prefix for window layout
     *         preferences
     */
    protected final String getWindowKeyPrefix() {
        return _windowKeyPrefix;
    }

    protected void handleWindowHeighthChange() {
        // The correct way to listen for window resizes is to add Change
        // Listeners to the width and height properties of the scene.
        // NOTE: These events happen incrementally, which is way too much.
        // NOTE: Returning from Full Screen Mode works pretty well, if we
        // re-enable this listener, but it doesn't really help with manual
        // resize actions.
        if ( !isFullScreen() && !isMaximized() ) {
            // If neither in Full Screen Mode nor Maximized Mode, cache the new
            // window height as the preferred size's new height.
            _preferredWindowSize = new Dimension2D( _preferredWindowSize.getWidth(), getHeight() );
        }
    }

    protected void handleWindowWidthChange() {
        if ( !isFullScreen() && !isMaximized() ) {
            // If neither in Full Screen Mode nor Maximized Mode, cache the new
            // window width as the preferred size's new width.
            _preferredWindowSize = new Dimension2D( getWidth(), _preferredWindowSize.getHeight() );
        }
    }

    @Override
    public void hide() {
        // NOTE: We have to be careful if in Full Screen Mode, to avoid
        // crashes! So we must turn Full Screen Mode off before closing.
        if ( !_fullScreenModeExempt && isFullScreen() ) {
            setFullScreen( false );
        }

        super.hide();
    }

    // Hide secondary windows that depend on state.
    public void hideSecondaryWindows() {}

    // NOTE: Not all stages have file loaders, so a concrete no-op
    // implementation is provided rather than making this method abstract and
    // requiring all derived classes to implement it.
    protected void initFileLoaders() {}

    protected void initProperties() {}

    // This is the main Stage initializer.
    // NOTE It is the responsibility of the subclasses to invoke this method,
    // as it needs to happen after basic initialization is completed and as this
    // avoids complicated parameter lists.
    // NOTE: This is declared "final" to reduce the chance of accidentally
    // overriding it and losing the contracted behavior in a subclass.
    public final void initStage( final String jarRelativeIconFilename,
                                 final double defaultWidth,
                                 final double defaultHeight,
                                 final boolean resizable ) {
        // Initialize variables that do not depend on deferred input.
        initVariables();

        // Initialize all property-related variables.
        initProperties();

        // Cache the default size so it can be reasserted via the menu.
        setDefaultWindowSize( defaultWidth, defaultHeight );

        // NOTE: This enables/disables the maximize and full screen buttons.
        setResizable( resizable );

        // Set the title bar icon (also for minimize).
        setIcon( jarRelativeIconFilename );

        // Set the initial default title for the Stage's title bar.
        // NOTE: It appears safer to set this after the title bar icons.
        setTitle( _defaultTitle.toString() );

        // Use a Border Pane as the root for the Scene, as this makes it easier
        // to lay out traditional Windows with a Menu Bar, Tool Bars, Status
        // Bar, Action Button Bar, and main Content Node (centered).
        _root = new BorderPane();

        // Make the Scene as early as possible, as it involves CSS loading,
        // which is slow. It is OK to make the Scene before finalizing content
        // details, and we supply the default width and height so that certain
        // internal JavaFX engine calls are less likely to fail-safe to unusable
        // values. This is not the same as the cached User Preferences, which
        // are loaded later and are not yet available to us this early on. Note
        // that we don't use these values if the window isn't resizable, as the
        // window becomes too large in such cases, due to thread execution order
        // vs. our later preferred size assertions.
        final Scene scene = loadScene( _root, defaultWidth, defaultHeight, resizable );

        // Load all of the relevant actions, if any are declared for this Stage.
        loadActions();

        // Load and set the Action Pane, if valid.
        final MenuBar menuBar = loadActionPane();

        // Load and set the main Content Node, if valid.
        final Node content = loadContent();
        if ( content != null ) {
            setContent( content );
        }

        // The main window construction is complete; now we can set the scene.
        setScene( scene );

        // Make sure to limit the minimum possible window size during dragging.
        // NOTE: Non-resizable windows should have smaller minimum height and
        // width, as they likely are small windows and we don't want to force
        // unnecessary empty space.
        setMinWidth( resizable ? 400d : 300d );
        setMinHeight( resizable ? 400d : 200d );

        // Load all of the secondary Windows associated with this Stage.
        loadAllWindows();

        // Add all of the listeners associated with common functionality.
        addAllListeners();

        // Prepare the Stage and its subsidiary Windows for user input.
        // NOTE: This also sets the menu bar on the Mac, which we do last as it
        // rebuilds the GUI and can cause problems with menu icon image loading
        // if done too soon.
        prepareForInput( menuBar );
    }

    protected void initVariables() {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        _percentFormat = NumberFormat.getPercentInstance( clientProperties.locale );
        _angleFormat = ( NumberFormat ) _numberFormat.clone();
        _numberParse = ( NumberFormat ) _numberFormat.clone();
        _percentParse = ( NumberFormat ) _percentFormat.clone();

        // Use two decimal places of precision for angles, in the default
        // locale.
        _angleFormat.setMinimumFractionDigits( 0 );
        _angleFormat.setMaximumFractionDigits( 2 );

        // Set the precision for parsing, which needs to be more flexible than
        // formatting.
        _numberParse.setMinimumFractionDigits( 0 );
        _numberParse.setMaximumFractionDigits( 10 );
        _percentParse.setMinimumFractionDigits( 0 );
        _percentParse.setMaximumFractionDigits( 10 );

        // By default, the MRU Cache is non-null and empty.
        _mruFilenameCache = new LinkedList<>();
    }

    public final boolean isFrameTitleManager() {
        return _frameTitleManager;
    }

    protected MenuBar loadActionPane() {
        // The Action Pane is a way of combining the Menu Bar and Tool Bar in a
        // way that avoids gaps or stretching height beyond preferred height.
        _actionPane = new BorderPane();

        // Load and set the Menu Bar, if present.
        final MenuBar menuBar = loadMenuBar();
        if ( menuBar != null ) {
            setMenuBar( menuBar );
        }

        // Load and set the Tool Bar, if present.
        final ToolBar toolBar = loadToolBar();
        if ( toolBar != null ) {
            setToolBar( toolBar );
        }

        // Set the Action Pane to the top of the Border Layout.
        _root.setTop( _actionPane );

        // Return the Menu Bar so we can set it properly on the Mac.
        return menuBar;
    }

    // Actions are not required for Stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected void loadActions() {}

    protected void loadAllStages() {}

    // Load all of the Windows associated with this Stage.
    public void loadAllWindows() {
        // Load all of the Pop-Ups.
        loadPopups();

        // Load all of the Primary and Secondary Stages.
        loadAllStages();
    }

    // Every stage must have a Main Content Node, so this method is mandated.
    protected abstract Node loadContent();

    // Menu bars are not required for Stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected MenuBar loadMenuBar() {
        return null;
    }

    protected void loadPopups() {}

    // Preferences are not applicable to all Stages, so this method is not
    // declared abstract and is instead given a default implementation that at
    // least loads the additional custom styles pertinent to the default
    // background being dark or light (currently it is light).
    public void loadPreferences() {
        setForegroundFromBackground( ColorConstants.WINDOW_BACKGROUND_COLOR );
    }

    protected final Scene loadScene( final Parent parent,
                                     final double defaultWidth,
                                     final double defaultHeight,
                                     final boolean resizable ) {
        // NOTE: Our CSS default is already White, as is the JavaFX default,
        // but setting it in code here, does no harm as it just avoids one level
        // of constructor wrapping and gives us more flexibility if we change
        // our minds about the default background color later on.
        final Scene scene = resizable
            ? new Scene( parent, defaultWidth, defaultHeight, Color.WHITE )
            : new Scene( parent, Color.WHITE );

        // TODO: Move the core CSS setting to the Application startup, so that
        // non-stage windows also use our preferred styles?
        // NOTE: Unfortunately, the only way to do this involves Private API.
        final List< String > jarRelativeStylesheetFilenames = GuiUtilities
                .getJarRelativeStylesheetFilenames( clientProperties.systemType );
        GuiUtilities.addStylesheetsAsJarResource( scene, jarRelativeStylesheetFilenames );

        return scene;
    }

    // Tool bars are not required for Stages, so this method is not declared
    // abstract but is instead given a default no-op implementation.
    protected ToolBar loadToolBar() {
        return null;
    }

    // Prepare the Stage and its subsidiary windows for input.
    protected void prepareForInput( final MenuBar menuBar ) {
        // Instantiate and initialize the file loaders.
        initFileLoaders();

        // The correct way to listen for window resizes is to add Change
        // Listeners to the width and height properties of the scene.
        // NOTE: These events happen incrementally, which is way too much.
        // NOTE: We remove before adding, as the default menu item construction
        // for the Window Size Menu, and Preference loading, may otherwise
        // result in multiple listeners, so that we never remove them all.
        removeWindowSizeListeners();
        addWindowSizeListeners();

        // NOTE: Although primarily for the Mac, this setting does no harm on
        // systems that don't have a system-level menu bar.
        // NOTE: We do this last, as it hierarchically rebuilds the Menu Bar
        // GUI and can thus cause menu icons to not show until the next time
        // this window is brought to front.
        // NOTE: If this isn't run on a deferred thread, some menu shortcuts
        // using the Alt/Option key don't work on macOS for some reason.
        // NOTE: After countless experiments (all removed from the code by
        // now), we are temporarily giving up on the Apple Application Menu Bar,
        // as it has continued to backslide while Oracle falls behind Apple's
        // continuous changes that they keep private to themselves. Using the
        // standard window-level menu bar fixes numerous problems on the macOS
        // so it is better for now to hold off on using the Application Menu
        // Bar; especially issues with ignored modifier keys on F1 shortcuts.
        if ( menuBar != null ) {
            // Platform.runLater( () -> menuBar.setUseSystemMenuBar( true ) );
        }
    }

    protected void print( final String printCategory ) {
        // Print the main Content Node, which excludes not only the Frame Title
        // Bar, but also the Menu Bar, Tool Bar, Status Bar, Action Button Bar.
        final Node printNode = _content;
        final Scale printJobScale = PrintUtilities.getPrintJobScale( _printerJob, printNode );
        try {
            // Scale the Print Job to fit the printed page.
            printNode.getTransforms().add( printJobScale );
        }
        catch ( final UnsupportedOperationException | ClassCastException | NullPointerException
                | IllegalArgumentException e ) {
            e.printStackTrace();
        }

        try {
            // Print the requested node, with scaling applied.
            final boolean pagePrinted = _printerJob.printPage( printNode );
            if ( !pagePrinted ) {
                GuiUtilities.handlePrintJobError( _printerJob, printCategory );
            }
        }
        catch ( final NullPointerException npe ) {
            npe.printStackTrace();
        }

        // Prepare for the next Print Job so the Printer isn't hung.
        renewPrintJob();

        try {
            // Remove the print job scale transform or the node itself gets
            // permanently scaled on the screen as well.
            printNode.getTransforms().remove( printJobScale );
        }
        catch ( final UnsupportedOperationException | ClassCastException
                | NullPointerException e ) {
            e.printStackTrace();
        }
    }

    // Callback listeners are not required for stages, so this method is not
    // declared abstract but is instead given a default no-op implementation.
    protected void removeCallbackListeners() {}

    public final void removeWindowSizeListeners() {
        getScene().widthProperty()
                .removeListener( ( observableValue,
                                   oldSceneWidth,
                                   newSceneWidth ) -> handleWindowWidthChange() );
        getScene().heightProperty()
                .removeListener( ( observableValue,
                                   oldSceneHeight,
                                   newSceneHeight ) -> handleWindowHeighthChange() );
    }

    // Prepare for the next Print Job so the Printer isn't hung.
    protected final void renewPrintJob() {
        // End the job even if it failed, as this also cancels a hung job and
        // thus makes the printer available for another try.
        _printerJob.endJob();

        try {
            // Make a new print job, or we can only print once per session.
            _printerJob = PrinterJob.createPrinterJob();
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();
        }
    }

    // A reset capability is not required for Stages, so this method is not
    // declared abstract and is instead given a default no-op implementation.
    protected void reset() {}

    /**
     * This method restores the Window Layout Preferences for this window. It
     * starts by checking the desired menu setting (if available), which is
     * saved from the previous session. Only if "Preferred Size" is desired,
     * does it also restore the cached window size from preferences.
     * <p>
     * Generally this method is only called at the application level, so this
     * override takes care of invoking it on all windows owned by the
     * application.
     *
     * @param prefs
     *            The @Preferences reference for the key/value pairs
     */
    @SuppressWarnings("nls")
    public final void restoreWindowLayout( final Preferences prefs ) {
        // Get the window key prefix for Window Layout Preferences.
        final String windowKeyPrefix = getWindowKeyPrefix();

        // If this window has no key prefix, it can't restore preferences.
        if ( ( windowKeyPrefix == null ) || windowKeyPrefix.isEmpty() ) {
            // Attempt to set the default window size.
            setWindowSize( _defaultWindowSize );
            return;
        }

        // Restore the preferred window size from the last session.
        final String windowWidthKey = windowKeyPrefix + "Width";
        final double windowWidthValue = prefs.getDouble( windowWidthKey,
                                                         _defaultWindowSize.getWidth() );
        final String windowHeightKey = windowKeyPrefix + "Height";
        final double windowHeightValue = prefs.getDouble( windowHeightKey,
                                                          _defaultWindowSize.getHeight() );
        setPreferredWindowSize( windowWidthValue, windowHeightValue );

        // Determine whether the user was in Full Screen Mode when they exited.
        final String fullScreenModeKey = windowKeyPrefix + "FullScreenMode";
        final boolean fullScreenMode = prefs.getBoolean( fullScreenModeKey, false );
        if ( fullScreenMode ) {
            // Make sure we explicitly enter Full Screen Mode.
            setFullScreen( true );

            // Exit early, as we shouldn't try to set location or size for
            // explicit modes like Full Screen Mode.
            return;
        }

        // Determine whether the user was in Maximized Mode when they exited.
        final String maximizedModeKey = windowKeyPrefix + "MaximizedMode";
        final boolean maximizedMode = prefs.getBoolean( maximizedModeKey, false );
        if ( maximizedMode ) {
            // Make sure we explicitly enter Maximized Mode.
            setMaximized( true );

            // Exit early, as we shouldn't try to set location or size for
            // explicit modes like Maximized Mode.
            return;
        }

        // Restore the window's cached layout location from the last session.
        // NOTE: Default location accounts for issues with corner areas on some
        // OS versions, but may be too much for smaller screens.
        final String windowXKey = windowKeyPrefix + "X";
        final double windowXValue = prefs.getDouble( windowXKey, 100d );
        final String windowYKey = windowKeyPrefix + "Y";
        final double windowYValue = prefs.getDouble( windowYKey, 100d );
        setWindowLocation( windowXValue, windowYValue );

        // Attempt to explicitly set the adjusted preferred window size.
        setWindowSize( _preferredWindowSize );

        // If necessary, adjust this stage to be within bounds.
        adjustStageWithinBounds();
    }

    // Preferences are not applicable to all Stages, so this method is not
    // declared abstract and is instead given a default no-op implementation.
    public void savePreferences() {}

    /**
     * This method saves the Window Layout Preferences for this window. It
     * starts by checking the current menu setting (if available), which is
     * saved for the next session. Only if "Preferred Size" is currently
     * selected, does it also store the current window size as a preference.
     *
     * @param prefs
     *            The @Preferences reference for the key/value pairs
     */
    @SuppressWarnings("nls")
    public final void saveWindowLayout( final Preferences prefs ) {
        // Get the window key prefix for Window Layout Preferences.
        final String windowKeyPrefix = getWindowKeyPrefix();

        // If this window has no key prefix, it can't save preferences.
        if ( ( windowKeyPrefix == null ) || windowKeyPrefix.isEmpty() ) {
            return;
        }

        // Save the window's preferred layout location.
        final String windowXKey = windowKeyPrefix + "X";
        final double windowXValue = getX();
        prefs.putDouble( windowXKey, windowXValue );
        final String windowYKey = windowKeyPrefix + "Y";
        final double windowYValue = getY();
        prefs.putDouble( windowYKey, windowYValue );

        // Determine whether the user was in Full Screen Mode when they exited.
        final String fullScreenModeKey = windowKeyPrefix + "FullScreenMode";
        final boolean fullScreenMode = isFullScreen();
        prefs.putBoolean( fullScreenModeKey, fullScreenMode );

        // Determine whether the user was in Maximized Mode when they exited.
        final String maximizedModeKey = windowKeyPrefix + "MaximizedMode";
        final boolean maximizedMode = isMaximized();
        prefs.putBoolean( maximizedModeKey, maximizedMode );

        // Save the window's current size as the new preferred layout bounds,
        // unless in Full Screen Mode or Maximized Mode, where it is safer to
        // save the last cached Preferred Size instead, as Full Screen Mode Size
        // and Maximized Mode Size should only be modes and never used directly
        // (the OS knows best how to apply them).
        final String windowWidthKey = windowKeyPrefix + "Width";
        final double windowWidthValue = ( fullScreenMode || maximizedMode )
            ? _preferredWindowSize.getWidth()
            : getWidth();
        prefs.putDouble( windowWidthKey, windowWidthValue );
        final String windowHeightKey = windowKeyPrefix + "Height";
        final double windowHeightValue = ( fullScreenMode || maximizedMode )
            ? _preferredWindowSize.getHeight()
            : getHeight();
        prefs.putDouble( windowHeightKey, windowHeightValue );
    }

    // This method enforces a certain consistent layout strategy at the
    // outer-most level of stages, with the idea that every stage has a centered
    // primary content node that is separate from standard stuff like window
    // decorators, menu bars, tool bars, and action button bars.
    public final void setContent( final Node content ) {
        // Cache the main Content Node so we can apply CSS styles later on.
        _content = content;

        // Make sure the main Content Node is centered in all contexts.
        _root.setCenter( _content );
    }

    // NOTE: Unlike most applications, we allow separate default directories
    // per window as usually there is different functionality that may use files
    // that need to be in different locations due to how they are used in
    // overall user workflow with other applications.
    @Override
    public void setDefaultDirectory( final File defaultDirectory ) {
        _defaultDirectory = defaultDirectory;
    }

    public final void setDefaultWindowSize( final double defaultWidth,
                                            final double defaultHeight ) {
        // Cache the default size so it can be reasserted via the menu.
        _defaultWindowSize = new Dimension2D( defaultWidth, defaultHeight );

        // Also use this as the initial preferred size, in case absent from user
        // preferences. This can be overwritten later during preference loading.
        _preferredWindowSize = new Dimension2D( defaultWidth, defaultHeight );
    }

    // NOTE: Do not set background on the Stage itself, as this ends up
    // affecting Tool Bars, Status Bars, and possibly even Menu Bars.
    // NOTE: This method is a minimal implementation shared by all windows,
    // whether they need to override for more complex layout forwarding or not.
    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        // NOTE: It appears to be more reliable to set this before loading CSS,
        // as otherwise we seem to occasionally get cases where subtle changes
        // in background color cause the CSS loading to be ignored. Perhaps this
        // is because a switch from dark to light background causes the removal
        // of one CSS before adding the new one. So maybe there is a race
        // condition of sorts, regarding derivation within the infrastructure.
        // More likely still, Core JavaFX CSS calls get triggered by the
        // background property change and/or Java API calls happen, that step on
        // our own custom CSS settings, due to Java taking precedence over CSS,
        // or the CSS being on a deferred thread that executes after this one.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        _root.setBackground( background );

        // Try to globally change the foreground theme for elements not exposed
        // in Java API calls, using our custom dark vs. light theme CSS files.
        // NOTE: In many cases, we need to load the main CSS first, before
        // applying styles to layout children, as there are additional CSS
        // stylesheets to load that depend on symbols defined in the main ones.
        final Scene scene = _content.getScene();
        GuiUtilities.setStylesheetForTheme( scene,
                                            backColor,
                                            DARK_BACKGROUND_CSS,
                                            LIGHT_BACKGROUND_CSS );
    }

    public final void setIcon( final String jarRelativeIconFilename ) {
        // NOTE: It is traditional to not use title bar icons on the Mac.
        if ( SystemType.MACOS.equals( clientProperties.systemType ) ) {
            return;
        }

        // Load the Title Bar's Minimize Icon Image as a JAR-resident resource.
        final Image minimizeIconImage = ImageUtilities
                .loadImageAsJarResource( jarRelativeIconFilename, false );
        if ( minimizeIconImage == null ) {
            return;
        }

        // Add the Icon to the Title Bar.
        // NOTE: It is recommended to add the Icons vs. using setAll() to
        // replace them all, so we don't lose platform-provided Icons.
        // TODO: Supply both a 16x16 and 32x32 Icon for each Stage? We only
        // supply 16x16 currently. Some platforms use the biggest they can.
        getIcons().add( minimizeIconImage );
    }

    public void setMenuBar( final MenuBar menuBar ) {
        // Make sure the Menu Bar displays above the Tool Bar.
        // :NOTE: The Menu Bar must be added to the Stage's Layout even if it is
        // going to be set to use the System Menu Bar.
        _actionPane.setTop( menuBar );
    }

    public void setPreferredWindowSize( final double stageWidth, final double stageHeight ) {
        // To prevent Application startup issues with Windows only having a
        // minimal Frame Title Bar and no Content Pane (due to exceptions or
        // other problems), we enforce minimum dimensions (taken as the cached
        // default Window Size).
        double stageWidthAdjusted = ( stageWidth > GuiUtilities.MINIMUM_WINDOW_WIDTH )
            ? stageWidth
            : _defaultWindowSize.getWidth();
        double stageHeightAdjusted = ( stageHeight > GuiUtilities.MINIMUM_WINDOW_HEIGHT )
            ? stageHeight
            : _defaultWindowSize.getHeight();

        // Likewise, as the Screen Size or resolution may have changed since the
        // previous session, we ensure that the Preferred Size can still fit.
        // :NOTE: We subtract a bit of a margin to make sure the Window can be
        // grabbed, resized, dragged, just in case decorations cause overflow,
        // and also to account for the dock and other related OS-level stuff.
        stageWidthAdjusted = Math.min( clientProperties.screenWidth - 60, stageWidthAdjusted );
        stageHeightAdjusted = Math.min( clientProperties.screenHeight - 60, stageHeightAdjusted );

        // Cache the Window's adjusted Preferred Size on the Screen (in pixels).
        _preferredWindowSize = new Dimension2D( stageWidthAdjusted, stageHeightAdjusted );
    }

    public void setToolBar( final ToolBar toolBar ) {
        // TODO: Review whether we want to enforce this sizing anymore.
        toolBar.setPrefHeight( TOOL_BAR_HEIGHT );
        toolBar.setMinHeight( TOOL_BAR_HEIGHT );
        toolBar.setMaxHeight( TOOL_BAR_HEIGHT );

        // Make sure the Tool Bar displays beneath the Menu Bar.
        _actionPane.setBottom( toolBar );
    }

    // TODO: Improve these comments in javadocs format.
    // NOTE: This method is a wrapper around show() and hide(), to enforce
    // legal use of those methods as exceptions are thrown if you show
    // something that is already being shown or if you hide something that is
    // already hidden. This may not be the best name for this method, but it
    // is also illegal to override show() and hide() so I decided to combine.
    // TODO: Make greater use of this method in place of having manually
    // duplicated it in so many places before enhancing our JavaFX base class
    // with more of the functionality from our older Swing base class.
    public void setVisible( final boolean visible ) {
        setVisible( visible, true );
    }

    // TODO: Improve these comments in javadocs format.
    // NOTE: This method is a wrapper around show() and hide(), to enforce
    // legal use of those methods as exceptions are thrown if you show
    // something that is already being shown or if you hide something that is
    // already hidden. This may not be the best name for this method, but it
    // is also illegal to override show() and hide() so I decided to combine.
    // NOTE: This is an enhanced version for when "force to front" is optional.
    // TODO: Make greater use of this method in place of having manually
    // duplicated it in so many places before enhancing our JavaFX base class
    // with more of the functionality from our older Swing base class.
    public void setVisible( final boolean visible, final boolean forceToFront ) {
        if ( visible ) {
            if ( isIconified() ) {
                setIconified( false );
            }
            else if ( !isShowing() ) {
                show();
            }

            if ( forceToFront ) {
                toFront();
            }
        }
        else {
            if ( isShowing() ) {
                hide();
            }
        }
    }

    public void setWindowLocation( final double stageX, final double stageY ) {
        // Get the full list of screens that are currently accessible.
        final List< Screen > screenList = Screen.getScreens();

        // If there are no accessible screens, don't try to set location.
        if ( screenList.size() < 1 ) {
            return;
        }

        // Make sure the coordinates are within range of the full set of
        // available screens.
        boolean locationOutOfBounds = true;
        for ( final Screen screen : screenList ) {
            final Rectangle2D bounds = screen.getVisualBounds();

            if ( bounds.contains( stageX, stageY ) ) {
                locationOutOfBounds = false;
                break;
            }
        }

        // Adjust the window location if it is out of bounds.
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        final double windowX = locationOutOfBounds ? bounds.getMinX() : stageX;
        final double windowY = locationOutOfBounds ? bounds.getMinY() : stageY;

        // Set and cache the window's location on the screen (in pixels).
        setX( windowX );
        setY( windowY );
    }

    /**
     * This method exits Full Screen Mode and sets the supplied window size.
     *
     * @param windowSize
     *            The window size to set as current window size
     */
    public void setWindowSize( final Dimension2D windowSize ) {
        // Make sure we explicitly exit Full Screen Mode.
        setFullScreen( false );

        // Make sure we explicitly exit Maximized Mode.
        setMaximized( false );

        // Set the new window size, but don't bother checking if the window is
        // resizable as we wouldn't have reached this method if the window
        // width and height change listeners are registered on a non-resizable
        // window. JavaFX takes care of managing these rules for us.
        setWidth( windowSize.getWidth() );
        setHeight( windowSize.getHeight() );
    }

    // NOTE: We generally only sync in one direction as the model is mostly in
    // the Main Application Stage.
    public void syncModelToView() {}

    public void syncViewToModel() {}

    // NOTE: Not all Stages have contextual settings, so a concrete no-op
    // implementation is provided rather than making this method abstract and
    // requiring all derived classes to implement it.
    protected void updateContextualSettings() {}

    // Conditionally append an asterisk to the filename, if document modified.
    // TODO: Learn how to set the black dot in the red circle in JavaFX --
    // perhaps this is documented in the Stage API docs, or search for a tool.
    public final void updateFrameTitle( final File documentFile, final boolean documentModified ) {
        final StringBuilder frameTitle = new StringBuilder( getDefaultTitle() );
        if ( _showDirtyFlag ) {
            frameTitle.append( getSubtitle( documentFile.getName(), documentModified ) );
        }
        setTitle( frameTitle.toString() );

        // The Mac handles window-associated documents and modification markers
        // differently from Windows and other OS's.
        // TODO: Port this logic to JavaFX, if possible.
        if ( SystemType.MACOS.equals( clientProperties.systemType ) ) {
            // final JRootPane rootPane = getRootPane();
            // rootPane.putClientProperty( "Window.documentFile", documentFile
            // );
            // rootPane.putClientProperty( "Window.documentModified",
            // documentModified );
        }
    }

    @Override
    public final void updateMruCache( final File file, final boolean addToCache ) {
        try {
            // Add the specified file to the head of the MRU Filename Cache if
            // not already present, or move it to the head of the list if
            // already present. If the cache is full and the specified file is
            // not yet in the cache, remove the last filename from the cache.
            // :NOTE: We special case for whether the file exists and is
            // read-enabled, to remove deleted and invalid files from the cache.
            // It is the caller's responsibility to determine this criteria and
            // pass it in using the "addToCache" flag.
            final String filename = file.getCanonicalPath();
            if ( _mruFilenameCache.contains( filename ) ) {
                _mruFilenameCache.remove( filename );
            }
            else if ( _mruFilenameCache.size() >= FileUtilities.MRU_CACHE_SIZE ) {
                if ( addToCache ) {
                    _mruFilenameCache.removeLast();
                }
            }
            if ( addToCache ) {
                _mruFilenameCache.addFirst( filename );
            }

            // Update the MRU File actions in the overall File actions from the
            // new MRU filename cache.
            if ( _mruFileActions != null ) {
                _mruFileActions.updateMruFileActions( _mruFilenameCache );
            }
        }
        catch ( final IOException ioe ) {
            ioe.printStackTrace();
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();
        }
    }

    protected final boolean verifyPrinterJob( final String printCategory ) {
        // NOTE: We need an always active Printer Job in order to support Page
        // Setup and Printing. Note that a printer may come on-line while the
        // application is running, and that each printer job is good once only.
        if ( _printerJob == null ) {
            try {
                _printerJob = PrinterJob.createPrinterJob();
            }
            catch ( final SecurityException se ) {
                se.printStackTrace();
            }
        }

        // If the Printer Job is still null, alert the user that there are no
        // printers available to the application.
        if ( _printerJob == null ) {
            final String noPrinterAvailableErrorMessage = MessageFactory
                    .getNoPrinterAvailableMessage();
            final String masthead = MessageFactory.getPrintServicesProblemMasthead();
            com.mhschmieder.fxguitoolkit.dialog.DialogUtilities
                    .showErrorAlert( noPrinterAvailableErrorMessage, masthead, printCategory );

            return false;
        }

        return true;
    }

}
