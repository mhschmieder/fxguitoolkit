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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * {@code FileHandler} is an interface that contracts Window-derived classes to
 * supply basic file handling. To minimize the amount of coupling, it attempts
 * to standardize as much as possible in default implementations herein.
 */
public interface FileHandler {

    default void fileOpen( final Window parent,
                           final String fileChooserTitle,
                           final File initialDirectory,
                           final List< ExtensionFilter > extensionFilterAdditions,
                           final ExtensionFilter defaultExtensionFilter,
                           final boolean multiSelectionEnabled ) {
        // NOTE: JavaFX uses a different file chooser action for multi-select.
        if ( multiSelectionEnabled ) {
            // Get a file (or list of files) for a "File Open" action.
            final List< File > files =
                                     FileChooserUtilities.getFilesForOpen( fileChooserTitle,
                                                                           initialDirectory,
                                                                           extensionFilterAdditions,
                                                                           defaultExtensionFilter,
                                                                           parent );

            // Make sure the user actually selected a file and didn't cancel.
            if ( ( files != null ) && ( files.size() >= 0 ) ) {
                File openedFile = null;
                for ( final File file : files ) {
                    // Open the contextual content from the chosen (but possibly
                    // renamed) filename.
                    if ( fileOpen( file ) ) {
                        openedFile = file;
                    }
                }
                if ( openedFile != null ) {
                    // Set the default directory to the parent of any of the
                    // successfully selected files.
                    final File parentDirectory = openedFile.getParentFile();
                    setDefaultDirectory( parentDirectory );
                }
            }
        }
        else {
            // Get a file for a "File Open" action.
            final File file = FileChooserUtilities.getFileForOpen( fileChooserTitle,
                                                                   initialDirectory,
                                                                   extensionFilterAdditions,
                                                                   defaultExtensionFilter,
                                                                   parent );

            // Make sure the user actually selected a file and didn't cancel.
            if ( file != null ) {
                // Open the contextual content from the chosen (but possibly
                // renamed) filename.
                if ( fileOpen( file ) ) {
                    // Set the default directory to the parent of the
                    // successfully selected file.
                    final File parentDirectory = file.getParentFile();
                    setDefaultDirectory( parentDirectory );
                }
            }
        }
    }

    /**
     * This file open uses a specified file for the open, and is the
     * lowest-level shared call for all file open and import actions.
     * <p>
     * NOTE: Due to the MRU list, this method may be the main entry point for
     * file open, and therefore must verify the file exists and update the MRU
     * accordingly, vs. performing this check in the main higher-level
     * dialog-based file open.
     *
     * @param file
     *            The {@link File} to open.
     * @return {@code true} if the file opened successfully; {@code false}
     *         if it did not open.
     */
    default boolean fileOpen( final File file ) {
        // Find out if the file can be opened for read.
        final String errorMessage = FileChooserUtilities.verifyForRead( file );

        // If the file doesn't exist, is read-protected, or is denied access by
        // a security manager, remove it from the MRU cache and exit.
        if ( errorMessage != null ) {
            updateMruCache( file, false );

            // Alert the user that there were problems with the file open.
            final String masthead = MessageFactory.getFileNotOpenedMasthead();
            DialogUtilities.showFileOpenErrorAlert( errorMessage, masthead );

            return false;
        }

        // Pre-process the file open or import action. Exit if unable to save
        // the current file first, or if user canceled, to avoid data loss.
        if ( !fileOpenPreProcessing( file ) ) {
            return false;
        }

        // Load the file contents.
        final FileStatus fileStatus = loadFromFile( file );

        // Post-process the File Open or Import action.
        return fileOpenPostProcessing( file, fileStatus );
    }

    // File Save As is not required for all stages, so this method is not
    // declared abstract but is instead given a default implementation.
    default boolean fileSaveAs( final Window parent,
                                final String fileChooserTitle,
                                final File initialDirectory,
                                final List< ExtensionFilter > extensionFilterAdditions,
                                final ExtensionFilter defaultExtensionFilter,
                                final File defaultFile ) {
        // Pre-process the file save or export action. Exit if user canceled.
        if ( !fileSavePreProcessing() ) {
            return false;
        }

        // Get a file for a "File Save As" action.
        final File file = FileChooserUtilities.getFileForSave( fileChooserTitle,
                                                               initialDirectory,
                                                               extensionFilterAdditions,
                                                               defaultExtensionFilter,
                                                               defaultFile,
                                                               parent );

        // Make sure the user actually selected a file and didn't cancel.
        if ( file == null ) {
            return false;
        }

        // Set the default directory to the parent of the selected file.
        final File parentDirectory = file.getParentFile();
        setDefaultDirectory( parentDirectory );

        // Save the contextual content to the chosen (but possibly renamed)
        // filename.
        return fileSave( file );
    }

    /**
     * This file save uses a specified file for the save, and is the
     * lowest-level shared call for all file save and export actions.
     *
     * @param file
     *            The {@link File} to save.
     * @return {@code true} if the file saved successfully; {@code false}
     *         if it did not save.
     */
    default boolean fileSave( final File file ) {
        // Find out if the file can be opened for write.
        final String errorMessage = FileChooserUtilities.verifyForWrite( file );

        // If the file is write-protected, or is denied access by a security
        // manager, exit.
        if ( errorMessage != null ) {
            // Alert the user that there were problems with the file save.
            DialogUtilities.showFileSaveErrorAlert( errorMessage );

            return false;
        }

        // Get a temporary file for interim write operations.
        // TODO: Switch to Apache Commons I/O for safer handling (FileUtils).
        final String prefixForTempFile = getPrefixForTempFile();
        final File tempFile = FileChooserUtilities.getTempFile( file, prefixForTempFile );
        if ( tempFile == null ) {
            return false;
        }

        // Save the file.
        final FileStatus fileStatus = fileSave( file, tempFile );

        // Post-process the File Save or Export action.
        fileSavePostProcessing( file, tempFile, fileStatus );

        return true;
    }

    // NOTE: Not all implementing Windows support File Save actions, so this
    // lower level method is given a default implementation that signifies
    // failure.
    default FileStatus fileSave( final File file, final File tempFile ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: Not all implementing Windows support File Close actions, so this
    // lower level method is given a default implementation that signifies
    // failure.
    default boolean fileClose() {
        return true;
    }

    // NOTE: Not all implementing Windows support MRU File Lists, but the
    // behavior is pretty generic across all applications when they do.
    default void fileOpenMru( final int mruId ) {
        // Open a new file of default type, using the cached filename.
        final File mruFile = getMruFile( mruId );
        if ( mruFile != null ) {
            fileOpen( mruFile );
        }
    }

    // File Save is not required for all Windows, so this method is not
    // declared abstract but is instead given a default implementation.
    default FileStatus fileSaveExtensions( final File file, final File tempFile ) {
        return FileStatus.NOT_SAVED;
    }

    /**
     * Returns the file prefix to use for interim write operations that insert a
     * randomizer-generated unique substring. Usually this is the application
     * name
     *
     * @return The prefix to use for generating a random temp file name.
     */
    @SuppressWarnings("nls")
    default String getPrefixForTempFile() {
        return "";
    }

    /**
     * Pre-processes the file, for example if additional parameters are needed
     * from the user in order to determine how to load the file or what to
     * retain.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     * this interface, and may be unneeded in many implementations.
     *
     * @param file
     *            The {@link File} to pre-process.
     * @return {@code true} if the pre-processing succeeded; {@code false}
     *         if it did not succeed.
     */
    default boolean fileOpenPreProcessing( final File file ) {
        // By default, we assume that no pre-processing is required, which in
        // practical terms means that no additional user input is needed in
        // order to determine how to parameterize a file load request.
        return true;
    }

    /**
     * Post-processes the file, for example if a dirty flag needs to be set, or
     * if error processing is required in a specific implementation.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     * this interface, and may be unneeded in many implementations.
     *
     * @param file
     *            The {@link File} to post-process.
     * @param fileStatus
     *            The incoming status of whether the file loaded
     * @return {@code true} if the post-processing succeeded; {@code false}
     *         if it did not succeed.
     */
    default boolean fileOpenPostProcessing( final File file, final FileStatus fileStatus ) {
        // By default, the only post-processing required is to check for errors
        // and cancellations.
        return fileOpenErrorHandling( file, fileStatus );
    }

    /**
     * Checks the file for errors, most trivially just looking at the incoming
     * status of the file load, but possibly also checking for non-empty
     * results.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     * this interface, and may be unneeded in most implementations.
     * <p>
     * TODO: Review whether the boolean return logic is counter-intuitive.
     * <p>
     * TODO: Come up with a generalized File Status enum vs. a boolean status?
     *
     * @param file
     *            The {@link File} to check for errors.
     * @param fileStatus
     *            The incoming status of whether the file loaded
     * @return {@code true} if the error handling found no errors; {@code false}
     *         if the error handling found errors.
     */
    default boolean fileOpenErrorHandling( final File file, final FileStatus fileStatus ) {
        // The only error handling for most file loads is to check the incoming
        // load status and to alert the user if there were file load errors. It
        // is possible that additional logic may be needed in overrides to this
        // method, such as to make sure the results of the file load are
        // non-empty.
        boolean sawErrors = true;
        switch ( fileStatus ) {
        case CREATED:
        case IMPORTED:
        case LOADED:
        case OPENED:
        case OPENED_FOR_RENAME:
            sawErrors = false;
            break;
        // $CASES-OMITTED$
        default:
            // Alert the user that a general file load error occurred.
            final String fileLoadErrorMessage = MessageFactory.getFileNotOpenedMessage( file );
            DialogUtilities.showFileOpenErrorAlert( fileLoadErrorMessage );
            break;
        }

        return !sawErrors;
    }

    // NOTE: This is by default a no-op that does no harm to the file status. As
    // some applications need to do pre-processing in advance of other file
    // handling, this method is supplied so that the main file save
    // implementations can insert a call to this method early on. Implementing
    // classes can override this default.
    default boolean fileSavePreProcessing() {
        return true;
    }

    default boolean fileSavePostProcessing( final File file,
                                            final File tempFile,
                                            final FileStatus fileStatus ) {
        // Rename the temporary file to a permanent file.
        final boolean fileSaved = FileStatus.SAVED.equals( fileStatus )
                || FileStatus.EXPORTED.equals( fileStatus );
        final boolean fileRenamed = fileSaved ? FileUtilities.moveFile( tempFile, file ) : false;

        // Modify the file status, but be careful not to write over existing
        // error codes.
        final FileStatus fileRenamedStatus = fileRenamed
            ? fileStatus
            : fileSaved ? FileStatus.WRITE_ERROR : fileStatus;

        // Check for errors and cancellations.
        final boolean filePostProcessed = fileSaveErrorHandling( file, fileRenamedStatus );
        return filePostProcessed;
    }

    default boolean fileSaveErrorHandling( final File file, final FileStatus fileStatus ) {
        boolean sawErrors = true;
        switch ( fileStatus ) {
        case EXPORTED:
        case SAVED:
            sawErrors = false;
            break;
        // $CASES-OMITTED$
        default:
            // Alert the user that a general file save error occurred.
            final String fileSaveErrorMessage = MessageFactory.getFileNotSavedMessage( file );
            DialogUtilities.showFileSaveErrorAlert( fileSaveErrorMessage );
            break;
        }

        return !sawErrors;
    }

    @SuppressWarnings("nls")
    default FileStatus loadFromFile( final File file ) {
        // Pre-declare the File Load status in case of exceptions.
        FileStatus fileStatus = FileStatus.NOT_OPENED;

        final String fileExtension = FileUtilities.getExtension( file );
        switch ( fileExtension ) {
        case "gif":
        case "jpe":
        case "jpeg":
        case "jpg":
        case "png":
            // Load the image file.
            //
            // Chain a BufferedInputStream to a FileInputStream, for better
            // performance.
            try ( final FileInputStream fileInputStream = new FileInputStream( file );
                    final BufferedInputStream bufferedInputStream =
                                                                  new BufferedInputStream( fileInputStream ) ) {
                if ( importImage( bufferedInputStream, fileExtension ) ) {
                    fileStatus = FileStatus.IMPORTED;
                }
            }
            catch ( final SecurityException | IOException e ) {
                e.printStackTrace();
            }
            break;
        default:
            break;
        }

        return fileStatus;
    }

    default void fileImportRasterImage( final Window parent,
                                        final String fileChooserTitle,
                                        final File initialDirectory ) {
        // Prepare to throw up a file chooser for the raster image filename.
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getRasterImageExtensionFilters();

        // Load a single raster image file using the JavaFX File Chooser.
        fileOpen( parent,
                  fileChooserTitle,
                  initialDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.PNG_EXTENSION_FILTER,
                  false );
    }

    // NOTE: Not all implementing Windows will support importing images, but we
    // are providing a convenience mechanism to do so in the file open hierarchy
    // of methods, so we default the image importer to return false.
    default boolean importImage( final InputStream inputStream, final String fileExtension ) {
        return false;
    }

    // NOTE: Not all implementing Windows support MRU File Lists, so this lower
    // level method is given a default implementation that returns a null File.
    default File getMruFile( final int mruId ) {
        return null;
    }

    // NOTE: Not all implementing Windows support an MRU list, so we define a
    // no-op default implementation rather than forcing an override.
    default void updateMruCache( final File file, final boolean addToCache ) {}

    // NOTE: We allow separate default directories per implementing Window as
    // often they have different functionality and the user needs one default
    // directories per functional scope.
    void setDefaultDirectory( final File defaultDirectory );

}
