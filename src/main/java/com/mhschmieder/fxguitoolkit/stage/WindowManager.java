/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.stage.Popup;

/**
 * This is a container class for collections of windows of various types and
 * classifications, used to enable macro-like operations on multiple windows so
 * as to avoid cut/paste errors and oversights. Primarily this will be used for
 * secondary windows (including pop-ups) that are owned by other windows.
 */
public final class WindowManager {

    // Declare a cache for all Pop-Ups used by the application.
    public final List< Popup >                  _popups;

    // Declare a cache for all Object Property Editors and Insert Dialogs.
    public final List< ObjectPropertiesEditor > _objectPropertiesEditors;

    // Declare a cache for all secondary Stages used by the application.
    public final List< XStage >                 _stages;

    public WindowManager() {
        // Always call the superclass constructor first!
        super();

        _popups = new ArrayList<>();
        _objectPropertiesEditors = new ArrayList<>();
        _stages = new ArrayList<>();
    }

    public void addObjectPropertiesEditor( final ObjectPropertiesEditor objectPropertiesEditor ) {
        _objectPropertiesEditors.add( objectPropertiesEditor );
    }

    public void addPopup( final Popup popup ) {
        _popups.add( popup );
    }

    public void addStage( final XStage stage ) {
        _stages.add( stage );
    }

    /**
     * Clear User Preferences for all of the Windows managed by this container.
     */
    public void clearAllPreferences() {
        // Clear User Preferences for all of the Object Properties Editors and
        // Insert Dialogs.
        _objectPropertiesEditors.stream().forEach( XStage::clearAllPreferences );

        // Clear User Preferences for all of the secondary Stages.
        _stages.stream().forEach( XStage::clearAllPreferences );
    }

    /**
     * Hide all of the Windows managed by this container.
     */
    public void hideAllWindows() {
        // Hide all of the Pop-Ups.
        hidePopups();

        // Hide all of the Object Properties Editors and Insert Dialogs.
        hideObjectPropertiesEditors();

        // Hide all of the secondary Stages.
        _stages.stream().forEach( XStage::hideAllWindows );
    }

    /**
     * Hide all of the Object Properties Editors and Insert Dialogs.
     */
    public void hideObjectPropertiesEditors() {
        _objectPropertiesEditors.stream()
                .forEach( objectPropertiesEditor -> {
                    objectPropertiesEditor.hideAllWindows();
                    objectPropertiesEditor.cancel();
                } );
    }

    /**
     * Hide all of the Pop-Ups.
     */
    public void hidePopups() {
        // Hide all of the Pop-Ups by making them invisible.
        _popups.stream().filter( Popup::isShowing ).forEach( Popup::hide );
    }

    /**
     * Load the User Preferences for the entire Window Set.
     */
    public void loadAllPreferences() {
        // Load the User Preferences for each Object Properties Editor.
        _objectPropertiesEditors.stream().forEach( ObjectPropertiesEditor::loadAllPreferences );

        // Load the User Preferences for each individual Stage.
        _stages.stream().forEach( XStage::loadAllPreferences );
    }

    /**
     * This method refreshes the Object Properties Editors when their
     * properties are changed outside the editor, such as via mouse move/rotate.
     */
    public void refreshObjectPropertiesEditors() {
        // NOTE: There is no recursion here, so it is safe to run in parallel.
        _objectPropertiesEditors.stream().filter( ObjectPropertiesEditor::isEditMode )
                .forEach( ObjectPropertiesEditor::updatePositioning );
    }

    /**
     * This method restores the Window Layout Preferences for all Windows.
     *
     * @param prefs
     *            The @Preferences reference for the key/value pairs
     */
    public void restoreAllWindowLayouts( final Preferences prefs ) {
        // Restore the Object Properties Editors' preferred Layouts.
        _objectPropertiesEditors.stream().filter( ObjectPropertiesEditor::isResizable )
                .forEach( objectPropertiesEditor -> objectPropertiesEditor
                        .restoreWindowLayout( prefs ) );

        // Restore the secondary Stages' preferred Window Layouts.
        _stages.stream().filter( XStage::isResizable )
                .forEach( stage -> stage.restoreWindowLayout( prefs ) );
    }

    /**
     * Save the User Preferences for the entire Window Set.
     */
    public void saveAllPreferences() {
        // Save the User Preferences for each Object Properties Editor.
        _objectPropertiesEditors.stream().forEach( ObjectPropertiesEditor::savePreferences );

        // Save the User Preferences for each individual Stage.
        _stages.stream().forEach( XStage::savePreferences );
    }

    /**
     * This method saves the Window Layout Preferences for all Windows.
     *
     * @param prefs
     *            The @Preferences reference for the key/value pairs
     */
    public void saveAllWindowLayouts( final Preferences prefs ) {
        // Save the Object Properties Editors' preferred Window Layouts.
        _objectPropertiesEditors.stream().forEach( objectPropertiesEditor -> objectPropertiesEditor
                .saveWindowLayout( prefs ) );

        // Save the individual stages' preferred Window Layouts.
        _stages.stream().forEach( stage -> stage.saveWindowLayout( prefs ) );
    }

    /**
     * Update the Frame Titles with the dirty flag, where appropriate.
     *
     * @param documentFile
     *            The full file path of the document whose name is to be
     *            displayed
     * @param documentModified
     *            The state of whether the document has been modified since last
     *            saved
     */
    public void updateFrameTitles( final File documentFile, final boolean documentModified ) {
        // Update the Object Properties Editors' Frame Titles.
        _objectPropertiesEditors.stream().filter( ObjectPropertiesEditor::isFrameTitleManager )
                .forEach( objectPropertiesEditor -> objectPropertiesEditor
                        .updateFrameTitle( documentFile, documentModified ) );

        // Update the Stages' Frame Titles.
        _stages.stream().filter( XStage::isFrameTitleManager )
                .forEach( stage -> stage.updateFrameTitle( documentFile, documentModified ) );
    }
}
