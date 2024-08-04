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

import java.io.File;
import java.util.Optional;

import com.mhschmieder.commonstoolkit.io.FileAction;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * {@code FileCloseHandler} is an interface that contracts Window-derived classes
 * to supply file close handling details, such as checking whether file changes
 * would be lost if not saved first. To minimize the amount of coupling, it 
 * attempts to standardize as much as possible in default implementations herein.
 */
public interface FileCloseHandler {
     
    /**
     * Returns the user confirmation response ("save" vs. "cancel") to changes.
     * <p>
     * Checks whether there are file changes to save before closing a file, and
     * if so, asks the user to save the changes to disc.
     * <p>
     * If the user cancels, do not save, do not erase; do nothing (i.e. return
     * from this method immediately).
     * <p>
     * If the user says "no", do not save the file even if it changed; just
     * continue so that we execute the code that follows the switch clauses.
     * <p>
     * If the user says "yes", throw up a file chooser to save the current file
     * to disc on the user's local file system, if it hasn't been saved and/or
     * renamed before, or do a silent save otherwise (this means the user knows
     * its name).
     * <p>
     * This method and its invoked helpers, is designed to work in every context
     * where file changes may need to be saved first. Examples include starting a
     * new file, opening a file from disc, and closing a window that edits a file.
     * 
     * @param title The title to use for the confirmation dialog
     * @param file The file to check for unsaved changes
     * @param fileCategory The descriptive category of the file (not file suffix)
     * @param fileAction The file action type that triggered the "Save Changes"
     * @param isChanged {@code true} if the file has changed; {@code false} if not
     * @return {@code true} if the user clicked "cancel"; {@code false} if "save"
     */
    default boolean checkFileClose( final String title,
                                    final File file,
                                    final String fileCategory,
                                    final FileAction fileAction,
                                    final boolean isChanged ) {
        boolean closeFile = true;
        
        // Check if the user could lose changes to the file before closing.
        if ( isChanged ) {
            closeFile = confirmFileClose( title, file, fileCategory, fileAction );
        }
        
        return closeFile;
        
    }
       
    /**
     * Returns the status of the user action ("save" vs. "cancel").
     * <p>
     * Checks whether there are file changes to save before closing a file, and
     * if so, asks the user to save the changes to disc.
     * <p>
     * If the user cancels, do not save, do not erase; do nothing (i.e. return
     * from this method immediately).
     * <p>
     * If the user says "no", do not save the project even if it changed; just
     * continue so we execute the code that follows the switch clauses.
     * <p>
     * If the user says "yes", throw up a file chooser to save the current file
     * to disc on the user's local file system, if it hasn't been saved and/or
     * renamed before, or do a silent save otherwise (this means the user knows
     * its name).
     * 
     * @param title The title to use for the confirmation dialog
     * @param file The file to check for unsaved changes
     * @param fileCategory The descriptive category of the file (not file suffix)
     * @param fileAction The file action type that triggered the "Save Changes"
     * @return {@code true} if the user clicked "cancel"; {@code false} if "save"
     */
    default boolean confirmFileClose( final String title,
                                      final File file,
                                      final String fileCategory,
                                      final FileAction fileAction ) {
        // Make sure we can handle unexpected edge cases by using Optionals.
        Optional< ButtonType > response = Optional.empty();

        // Conditionally give the user the opportunity to save the current file,
        // if anything changed since the file was last opened.
        final String message = getSaveFileChangesMessage( file, fileCategory, fileAction );
        String masthead = null;
        switch ( fileAction ) {
        case NEW:
        case OPEN:
        case RUN_BATCH:
            masthead = MessageFactory.getSaveFileChangesMasthead();
            break;
        case CLOSE:
            masthead = MessageFactory.getFileCloseMasthead();
            break;
        case EXIT:
            masthead = MessageFactory.getFileExitMasthead();
            break;
        // $CASES-OMITTED$
        default:
            break;
        }
        
        // If the action clause is pertinent, we now have a valid masthead.
        if ( masthead != null ) {
            response = DialogUtilities.showConfirmationAlert( message, masthead, title, true );
        }

        // Avoid NoSuchElementException from Optional<ButtonType>
        if ( !response.isPresent() ) {
            // If they somehow abstain from clicking any buttons, treat it as a
            // cancellation, meaning neither save nor close the file.
            return false;
        }

        // Get the Button Type that was pressed, but avoid Lambda Expressions as
        // they do not give us the flexibility of exiting this method directly
        // or of sharing common code after initial special-case handling of the
        // three user options ("Yes", "No", and "Cancel") and their variants.
        final ButtonType buttonType = response.get();

        // Handle the full enumeration of potential confirmation responses.
        return handleFileClose( buttonType.getButtonData() );
    }
    
    /**
     * Returns the message to use in the Confirm File Changes alert box.
     * <p>
     * The default implementation returns the basic prepared message from this
     * library's Message Factory. Implementing classes may need to override this
     * method to provide a more detailed message relating to domain specifics.
     * 
     * @param file The file to check for unsaved changes
     * @param fileCategory The descriptive category of the file (not file suffix)
     * @param fileAction The file action type that triggered the "Save Changes"
     * @return the message to use in the Confirm File Changes alert box
     */
    default String getSaveFileChangesMessage( final File file,
                                              final String fileCategory,
                                              final FileAction fileAction ) {
        return MessageFactory.getSaveFileChangesMessage( file );
    }

    /**
     * Returns {@code true} if the file should close; {@code false} if canceled.
     *  
     * @param buttonData The button data for the user confirmation response
     * @return {@code true} if the file should close; {@code false} if canceled
     */
    default boolean handleFileClose( final ButtonBar.ButtonData buttonData ) {
        // Handle the full enumeration of potential confirmation responses.
        boolean closeFile = false;
        
        switch ( buttonData ) {
        case HELP:
        case HELP_2:
        case BACK_PREVIOUS:
        case CANCEL_CLOSE:
            // These confirmation options equate to cancellation of File Close.
            break;
        case NO:
            // This confirmation option equates to rejecting changes. That is,
            // it results in the file being closed without saving the changes.
            closeFile = true;
            break;
        case APPLY:
        case FINISH:
        case NEXT_FORWARD:
        case OK_DONE:
        case YES:
            // These confirmation options equate to saving changes to disc.
            fileSave();
            closeFile = true;
            break;
        case BIG_GAP:
        case SMALL_GAP:
        case LEFT:
        case RIGHT:
        case OTHER:
            // It is unlikely that these cases will ever be called, but it
            // is safest to treat them like a cancellation request.
            break;
        default:
            break;
        }
        
        return closeFile;
    }
     
    /**
     * Saves the file. 
     * <p>
     * This by default does nothing; it is a contract for the implementor of
     * this interface to provide the details of a file save in the context of
     * a user selecting a file action that results in saving the file before 
     * executing that action, such as closing a window that includes unsaved
     * changes. Usually, implementors will invoke FileActionHandler methods.
     */
     default void fileSave() {}
}
