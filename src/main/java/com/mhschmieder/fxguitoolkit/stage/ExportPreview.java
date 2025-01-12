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

import java.io.File;
import java.util.prefs.Preferences;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * This is an abstract base class for common behavior and controls that pertain
 * to all domain-specific preview windows for export file actions.
 */
public abstract class ExportPreview extends XStage {

    // Declare the main action button bar.
    protected ButtonBar _actionButtonBar;

    // Declare the main action buttons.
    protected Button    _exportButton;
    protected Button    _cancelButton;

    // For the sake of post-processing, keep track of user cancellation.
    private boolean     _canceled;

    public ExportPreview( final Modality modality,
                          final String title,
                          final String windowKeyPrefix,
                          final boolean showDirtyFlag,
                          final boolean frameTitleManager,
                          final ProductBranding productBranding,
                          final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( modality,
               title,
               windowKeyPrefix,
               showDirtyFlag,
               frameTitleManager,
               productBranding,
               pClientProperties );

        // Always default to not canceled, until user edits begin.
        _canceled = false;
    }

    /**
     * Cancel Button callback.
     */
    public final void cancel() {
        // Set the "canceled" status to query in any context.
        setCanceled( true );

        // Now exit the window, whether modal or modeless.
        setVisible( false, false );
    }

    /**
     * Export Button callback.
     */
    public final void export() {
        // Set the "canceled" status to query in any context.
        setCanceled( false );

        // Export the file, after querying the user for a file name.
        final boolean fileSaved = fileExport();

        // Unless the user canceled the file action, or there were errors, exit
        // this window, whether it is modal or modeless.
        if ( fileSaved ) {
            setVisible( false, false );
        }
    }

    /*
     * The File Export method must be overridden by subclasses, to take care
     * of the domain-specific export actions associated with the preview.
     */
    protected abstract boolean fileExport();

    /*
     * The domain-specific Cancel Button must be provided by the subclasses.
     */
    protected abstract Button getCancelButton();

    /*
     * The domain-specific Export Button must be provided by the subclasses.
     */
    protected abstract Button getExportButton();

    /*
     * Not all Export Previews need to expose Export Options, so this method
     * is declared as a null return, and is not enforced to be implemented by
     * subclasses, unless the "hasExportOptions" is passed to "initStage()".
     */
    protected VBox getExportOptionsBox() {
        return null;
    }

    // This is the main Export Preview initializer.
    // :NOTE It is the responsibility of the subclasses to invoke this method,
    // as it needs to happen after basic initialization is completed and as this
    // avoids complicated parameter lists.
    // NOTE: This is declared "final" to reduce the chance of accidentally
    // overriding it and losing the contracted behavior in a subclass.
    public final void initStage( final String jarRelativeIconFilename,
                                 final double defaultWidth,
                                 final double defaultHeight,
                                 final boolean resizable,
                                 final boolean hasExportOptions ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, defaultWidth, defaultHeight, resizable );

        // Build the main action button bar, and register its callbacks.
        // NOTE: We can customize the text, but are taking advantage of a
        // common implementation from JavaFX that is also OS-sensitive in its
        // layout order.
        _actionButtonBar = new ButtonBar();
        _actionButtonBar.setPadding( new Insets( 6.0d, 12d, 6.0d, 12d ) );

        _exportButton = getExportButton();
        ButtonBar.setButtonData( _exportButton, ButtonData.OK_DONE );
        _exportButton.setPrefWidth( 120d );

        _cancelButton = getCancelButton();
        ButtonBar.setButtonData( _cancelButton, ButtonData.CANCEL_CLOSE );
        _cancelButton.setPrefWidth( 120d );

        // Make the Export Button consume the ENTER key when possible.
        // NOTE: This is commented out, because it does not play well with the
        // File Chooser in the workflow of an Export Preview window, as exiting
        // the File Chooser doesn't consume its key event and thus it can get
        // processed twice, resulting in an endless loop of invoking the File
        // Chooser, if using the keyboard only and not the mouse.
        // _exportButton.setDefaultButton( true );

        // Add the Export Preview's Action Buttons to the Action Button Bar.
        final ObservableList< Node > actionButtons = _actionButtonBar.getButtons();
        actionButtons.add( _exportButton );
        actionButtons.add( _cancelButton );

        // Divide the bottom of the layout between the Export Options and the
        // Action Buttons. This might require a different layout scheme.
        final BorderPane buttonPane = new BorderPane();
        if ( hasExportOptions ) {
            final VBox exportOptionsBox = getExportOptionsBox();
            buttonPane.setLeft( exportOptionsBox );
            exportOptionsBox.setAlignment( Pos.TOP_LEFT );
        }
        final VBox actionButtonBox = new VBox( _actionButtonBar );
        buttonPane.setRight( actionButtonBox );
        actionButtonBox.setAlignment( Pos.BOTTOM_RIGHT );

        // Set the Export Options (when present) and Action Buttons to the
        // bottom of the layout.
        _root.setBottom( buttonPane );

        // Load the event handler for the Export Button.
        _exportButton.setOnAction( evt -> export() );

        // Load the event handler for the Cancel Button.
        _cancelButton.setOnAction( evt -> cancel() );

        // Filter for the platform-specific window-closing icon, and treat it
        // like a "Cancel" request.
        setOnCloseRequest( evt -> cancel() );

        // Detect the ENTER key while the Export Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _exportButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Move the focus one level up, as the File Chooser does not
                // consume its key events, and therefore can re-trigger this
                // callback if the File Chooser is canceled vs. saved.
                _actionButtonBar.requestFocus();

                // Consume the ENTER key so it doesn't get processed twice.
                // Trigger the Export action.
                export();

                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Cancel Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _cancelButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Cancel action.
                cancel();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );
    }

    public final boolean isCanceled() {
        return _canceled;
    }

    // Load all of the User Preferences for this Stage.
    // TODO: Make a class with get/set methods for user preferences, a la
    // Listing 3.3 on p. 37 of "More Java Pitfalls" (Wiley), and including
    // static default values for better modularity.
    @Override
    public final void loadPreferences() {
        // Get the user node for this package/class, so that we get the
        // preferences specific to this stage and user.
        final Preferences prefs = Preferences.userNodeForPackage( getClass() );

        // Load the Default Directory from User Preferences.
        final File defaultDirectory = FileUtilities.loadDefaultDirectoryPreferences( prefs );

        // Forward the preferences data from the stored preferences to the
        // common preferences handler.
        updatePreferences( defaultDirectory );
    }

    // Save all of the User Preferences for this Stage.
    // TODO: Make a class with get/set methods for User Preferences, a la
    //  Listing 3.3 on p. 37 of "More Java Pitfalls" (Wiley).
    @Override
    public final void savePreferences() {
        // Get the user node for this package/class, so that we get the
        // preferences specific to this stage and user.
        final Preferences prefs = Preferences.userNodeForPackage( getClass() );

        // Save the Default Directory to User Preferences.
        FileUtilities.saveDefaultDirectoryPreferences( _defaultDirectory, prefs );
    }

    public final void setCanceled( final boolean canceled ) {
        _canceled = canceled;
    }

    // Common open method for opening an textField in modal Insert Mode.
    @Override
    public final void showAndWait() {
        // Always default to not canceled, until user edits begin.
        setCanceled( false );

        // In case the previous showing canceled, default to Export Button.
        _exportButton.requestFocus();

        // Wait for the user to dismiss via OK or Cancel Button.
        // NOTE: The visibility test is defensive programming, as the base
        // class throws an exception vs. recovering nicely.
        if ( isShowing() ) {
            toFront();
        }
        else {
            super.showAndWait();
        }
    }

    // Update all of the user preferences for this stage.
    // TODO: Make a preferences object instead, with get/set methods, which can
    //  be set from HTML, XML, or stored user preferences?
    private final void updatePreferences( final File defaultDirectory ) {
        // Set the background color for most layout content.
        // NOTE: This is mostly needed so that the CSS theme gets loaded and
        //  its tags are available for custom button rendering.
        setForegroundFromBackground( ColorConstants.WINDOW_BACKGROUND_COLOR );

        // Reset the default directory for local file operations.
        setDefaultDirectory( defaultDirectory );
    }
}
