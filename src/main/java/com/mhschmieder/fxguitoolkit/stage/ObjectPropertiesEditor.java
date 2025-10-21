/*
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

import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

/**
 * This is the base class for Object Properties Editors and Insert Dialogs. It
 * is meant to consolidate the shared functionality, such as the button row at
 * the bottom for applying or reverting changes. This also makes it easy to
 * enforce common behavior, and to distinguish Modal Insert from Modeless Edit.
 */
public abstract class ObjectPropertiesEditor extends XStage {

    // Declare the main action button bar.
    protected ButtonBar    _actionButtonBar;

    // Declare the main action buttons.
    protected Button       _helpAlternateButton;
    protected Button       _resetButton;
    protected Button       _revertButton;
    protected Button       _applyButton;
    protected Button       _doneButton;
    protected Button       _cancelButton;
    protected Button       _helpButton;

    // For the sake of the modal version, keep track of user cancellation.
    private boolean        _canceled;

    // Also keep track of when we are disabled due to deleted objects.
    private boolean        _disabled;

    // In place of hidden check boxes, maintain observable boolean flags.
    // NOTE: These fields must follow JavaFX Property Bean naming conventions.
    public BooleanProperty objectPropertiesChanged;
    
    // Flag for whether Reset Actions are applicable to this textField instance.
    
    private boolean         resetApplicable;

    @SuppressWarnings("nls")
    protected ObjectPropertiesEditor( final boolean insertMode,
                                      final String objectType,
                                      final String windowKeyPrefix,
                                      final ProductBranding productBranding,
                                      final ClientProperties pClientProperties,
                                      final boolean pResetApplicable ) {
        // Always call the superclass constructor first!
        // NOTE: We set the modality in case this is for Insert Mode vs. Edit
        // Mode, and vary the frame title based on this mode as well.
        // NOTE: This textField can be made as more than one instance, so it is
        // simpler to block the application and thus avoid confusion of state or
        // complexity of dismissing and canceling redundant copies of the textField
        // launched from more than one owning window, with different settings.
        super( insertMode ? Modality.APPLICATION_MODAL : Modality.NONE,
               insertMode ? "Insert " + objectType : objectType + " Properties",
               insertMode ? windowKeyPrefix + "InsertDialog" : windowKeyPrefix + "Editor",
               false,
               false,
               false,
               productBranding,
               pClientProperties );
        
        resetApplicable = pResetApplicable;

        // Always default to not canceled, until user edits begin.
        _canceled = false;

        // Always default to disabled, until user edits begin.
        _disabled = false;

        objectPropertiesChanged = new SimpleBooleanProperty( false );
    }

    // NOTE: This basic implementation may be enough for all sub-cases.
    protected void apply() {
        // Propagate values from the GUI to the selected object's properties.
        updateModel();

        // Notify all clients that an object's properties were edited.
        if ( !isInsertMode() ) {
            setObjectPropertiesChanged( true );
        }
    }

    /**
     * Cancel Button callback, for Insert Mode and Window Close.
     */
    public void cancel() {
        // Set the "canceled" status to query in any context.
        setCanceled( true );

        // Now exit the window, whether modal or modeless.
        setVisible( false, false );
    }

    /**
     * Done Button callback.
     */
    public void done() {
        // Set the "canceled" status to query in any context.
        setCanceled( false );

        // Apply all edits, as for the Apply or Done Button.
        apply();

        // Now exit the window, whether modal or modeless.
        setVisible( false, false );
    }

    // Detect whether a multi-row table has editing focus, as those should be
    // allowed to process the ENTER key as a local commit with focus moving to
    // the cell below (similar to TAB committing edits and moving to the
    // horizontally adjacent cell).
    // NOTE: We provide a default implementation as most editors don't have
    // multi-row tables.
    // TODO: Rename this to serve as a general capture of when a complex
    // control has editing focus and should own the ENTER key (for example).
    public boolean hasMultiRowTableCellFocus() {
        return false;
    }

    // NOTE: Help is not required so has a default no-op implementation and
    // does not need to be overridden.
    protected void help() {}

    // NOTE: Help Alternate is not required so has a default no-op
    // implementation and does not need to be overridden.
    protected void helpAlternate() {}

    // Hide all of the Windows associated with this Object Properties Editor.
    @Override
    public void hideAllWindows() {
        // First, hide all the secondary windows owned by this Stage.
        _windowManager.hideAllWindows();

        // Finally, hide this window as well, via cancel action, so that new
        // objects aren't created during new project setup by mistake.
        cancel();
    }

    /*
     * It is the responsibility of the subclasses to invoke this
     * method, as it needs to happen after basic initialization is
     * completed and as this avoids complicated parameter lists. Also,
     * not all subclasses need to do the same things so this is a more
     * flexible approach overall. This replicates some XFrame features.
     */
    protected void initStage( final String jarRelativeIconFilename,
                              final String objectType,
                              final double defaultWidth,
                              final double defaultHeight,
                              final boolean resizable,
                              final boolean useHelpButton,
                              final boolean useHelpAlternateButton ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, defaultWidth, defaultHeight, resizable );

        // Rediscover whether we are in Insert Mode or Edit Mode.
        final boolean insertMode = isInsertMode();

        // Build the main action button bar, and register its callbacks.
        _actionButtonBar = new ButtonBar();
        _actionButtonBar.setPadding( new Insets( 12d ) );

        _helpAlternateButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getHelpAlternateButton();
        ButtonBar.setButtonData( _helpAlternateButton, ButtonData.HELP_2 );

        _resetButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getResetButton( objectType );
        ButtonBar.setButtonData( _resetButton, ButtonData.OTHER );

        _revertButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getRevertButton( objectType );
        ButtonBar.setButtonData( _revertButton, ButtonData.BACK_PREVIOUS );

        _applyButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getApplyButton( objectType );
        ButtonBar.setButtonData( _applyButton, ButtonData.APPLY );

        _doneButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getDoneButton( objectType, insertMode );
        ButtonBar.setButtonData( _doneButton, ButtonData.OK_DONE );

        _cancelButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory
                .getCancelButton( objectType );
        ButtonBar.setButtonData( _cancelButton, ButtonData.CANCEL_CLOSE );

        _helpButton = com.mhschmieder.fxguitoolkit.control.LabeledControlFactory.getHelpButton( false );
        ButtonBar.setButtonData( _helpButton, ButtonData.HELP );

        // Disable the Reset, Help, and Help Alternate Buttons as these are not
        // implemented yet in the case of Reset and Help and in the case of Help
        // Alternate it's a special button that is only rarely relevant.
        _helpAlternateButton.setDisable( !useHelpAlternateButton );
        _helpAlternateButton.setVisible( useHelpAlternateButton );
        _resetButton.setDisable( !resetApplicable );
        _resetButton.setVisible( resetApplicable );
        _helpButton.setDisable( !useHelpButton );
        _helpButton.setVisible( useHelpButton );

        // Make the OK/Close Button consume the ENTER key when possible.
        // NOTE: We do not want a one-shoe-fits-all exit criteria from the
        // textField, when no control has focus, and enabling this feature
        // prevents the action buttons from retaining focus for ENTER.
        // _doneButton.setDefaultButton( true );

        final ObservableList< Node > actionButtons = _actionButtonBar.getButtons();
        if ( useHelpAlternateButton ) {
            actionButtons.add( _helpAlternateButton );
        }
        if ( resetApplicable ) {
            actionButtons.add( _resetButton );
        }
        actionButtons.add( _revertButton );
        actionButtons.add( _doneButton );
        if ( insertMode ) {
            actionButtons.add( _cancelButton );
        }
        else {
            actionButtons.add( _applyButton );
        }
        if ( useHelpButton ) {
            actionButtons.add( _helpButton );
        }

        _root.setBottom( _actionButtonBar );

        // Load the event handler for the Help Button.
        _helpButton.setOnAction( evt -> help() );

        // Load the event handler for the Help Alternate Button.
        _helpAlternateButton.setOnAction( evt -> helpAlternate() );

        // Load the event handler for the Reset Button.
        _resetButton.setOnAction( evt -> reset() );

        // Load the event handler for the Revert Button.
        _revertButton.setOnAction( evt -> revert() );

        // Load the event handler for the Apply Button.
        _applyButton.setOnAction( evt -> apply() );

        // Load the event handler for the Done Button.
        _doneButton.setOnAction( evt -> done() );

        // Load the event handler for the Cancel Button.
        _cancelButton.setOnAction( evt -> cancel() );

        // Filter for the platform-specific window-closing icon, and treat it
        // like a "Revert" request if there is a valid reference; otherwise (if
        // this window is disabled due to an invalid reference, or is in Insert
        // Mode) treat it like a "Cancel" request. Do not consume the event.
        setOnCloseRequest( evt -> {
            if ( isInsertMode() || isDisabled() ) {
                cancel();
            }
            else {
                revert();
            }
        } );

        // The geometry preview requires the window to be shown in order to grab
        // layout bounds, so we must actively request an update once the window
        // is shown as the normal syncing methods called by the showAndWait()
        // method end up being a no-op when the window is not yet on-screen.
        // NOTE: For some reason, onShowingProperty() and onShownProperty() are
        // never reached. Unsurprisingly, focusedProperty() happens too often.
        // So, it is safer to use this property which only triggers one event.
        showingProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            if ( newValue ) {
                updatePreview();
            }
        } );

        // Filter for the ENTER key, with modified key combinations, so that we
        // can trigger various global actions even when editing focus is on
        // another control (where the ENTER key on its own, commits edits).
        addEventFilter( KeyEvent.KEY_RELEASED, keyEvent -> {
            final KeyCombination enterKeyCombo = new KeyCodeCombination( KeyCode.ENTER,
                                                                         KeyCombination.ALT_DOWN );
            final KeyCombination enterModifiedKeyCombo =
                                                       new KeyCodeCombination( KeyCode.ENTER,
                                                                               KeyCombination.SHORTCUT_DOWN );
            if ( enterKeyCombo.match( keyEvent ) ) {
                // if ( !hasMultiRowTableCellFocus() ) {
                // Emulate the OK or Apply and Close Button being pressed.
                done();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
                // }
            }
            else if ( enterModifiedKeyCombo.match( keyEvent ) ) {
                // Emulate the Apply Button being pressed.
                apply();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Revert Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _revertButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Revert action.
                revert();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Apply Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _applyButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Apply action.
                apply();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Done Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _doneButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Done action.
                done();

                // Consume the ENTER key so it doesn't get processed twice.
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

    public final boolean isApplyDisable() {
        return _applyButton.isDisable();
    }

    public final boolean isCanceled() {
        return _canceled;
    }

    public final boolean isDisabled() {
        // Determine whether this textField is disabled, so we don't apply changes
        // to deselected objects and inactive editors.
        return _disabled;
    }

    public final boolean isEditMode() {
        // Rediscover whether we are in Insert Mode or Edit Mode.
        return Modality.NONE.equals( getModality() );
    }

    public final boolean isInsertMode() {
        // Rediscover whether we are in Insert Mode or Edit Mode.
        return !Modality.NONE.equals( getModality() );
    }

    public final boolean isObjectPropertiesChanged() {
        return objectPropertiesChanged.get();
    }

    public final boolean isRevertDisable() {
        return _revertButton.isDisable();
    }

    public final BooleanProperty objectPropertiesChangedProperty() {
        return objectPropertiesChanged;
    }

    @Override
    protected abstract void reset();

    // NOTE: This basic implementation may be enough for all sub-cases.
    protected void revert() {
        // Propagate properties of the cached reference object to the GUI
        // components (this throws out current GUI edits).
        updateView();

        // Notify all clients that an object's properties were reverted.
        if ( !isInsertMode() ) {
            setObjectPropertiesChanged( true );
        }
    }

    public final void setApplyDisable( final boolean applyDisable ) {
        _applyButton.setDisable( applyDisable );
    }

    public final void setCanceled( final boolean canceled ) {
        _canceled = canceled;
    }

    // NOTE: Derived classes must call this parent class method, as the
    // action button bar must be part of the contract for what is disabled.
    protected void setDisable( final boolean disable ) {
        // Cache the disabled status, so we can use it when exiting.
        _disabled = disable;

        // If this textField is visible, in Edit Mode, and about to be disabled, we
        // also want to revert any user changes, by re-syncing the view to the
        // cached model, without triggering dirty flags and extra computations.
        if ( disable && isShowing() && isEditMode() ) {
            updateView();
        }

        // The action buttons are top-level controls and thus must be explicitly
        // disabled here as the forwards to the layout panes don't cover them.
        _actionButtonBar.setDisable( disable );
    }

    public final void setObjectPropertiesChanged( final boolean pObjectPropertiesChanged ) {
        objectPropertiesChanged.set( pObjectPropertiesChanged );
    }

    public final void setRevertDisable( final boolean revertDisable ) {
        _revertButton.setDisable( revertDisable );
    }

    // Common open method for opening an textField in modal Insert Mode.
    @Override
    public final void showAndWait() {
        // Always default to not canceled, until user edits begin.
        setCanceled( false );

        // In case the previous showing canceled, default to Done Button.
        _doneButton.requestFocus();

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

    // NOTE: All objects must have at least some editing controls that must be
    // updated when the object reference is switched for another one or when
    // changes happen outside this textField.
    // TODO: Rename as "updateProperties()" or "storeProperties()"?
    protected abstract void updateObjectPropertiesView();

    // NOTE: We make this final because we want to discourage this old Swing
    // based terminology once we're at the level of object-editing.
    @Override
    public final void updateModel() {
        // Propagate values from the GUI to the selected object's properties.
        updateObjectPropertiesModel();

        // Update the preview of the current object reference.
        updatePreview();

        // Make sure the contextual settings are properly enabled/disabled.
        updateContextualSettings();
    }

    // NOTE: All objects must have at least some editing controls that must be
    // updated when the object reference is switched for another one or when
    // changes happen outside this textField.
    // TODO: Rename as "loadProperties()" or "recallProperties()"?
    protected abstract void updateObjectPropertiesModel();

    // NOTE: We make this final because we want to discourage this old Swing
    // based terminology once we're at the level of object-editing.
    @Override
    public final void updateView() {
        // Propagate properties of the selected object to the GUI.
        updateObjectPropertiesView();

        // Update the preview of the current object reference.
        updatePreview();

        // Make sure the contextual settings are properly enabled/disabled.
        updateContextualSettings();
    }

    // NOTE: Not all objects have labels, so there is a default no-op
    // implementation and derived classes are not required to implement this
    // method.
    protected void uniquefyObjectLabel() {}

    public abstract void updatePositioning();

    // NOTE: Not all objects need a geometry preview, so there is a default
    // no-op implementation and derived classes are not required to implement
    // this method.
    public void updatePreview() {}

}// class ObjectPropertiesEditor
