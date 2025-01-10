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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.mhschmieder.commonstoolkit.io.FileMode;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.io.RasterGraphicsExportOptions;
import com.mhschmieder.fxgraphicstoolkit.io.VectorGraphicsExportOptions;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/**
 * {@code FileActionHandler} is an interface that contracts Window-derived classes
 * to supply basic file action handling. To minimize the amount of coupling, it 
 * attempts to standardize as much as possible in default implementations herein.
 */
public interface FileActionHandler {

    default void fileOpen( final Window parent,
                           final FileMode fileMode,
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
                    if ( fileOpen( file, fileMode ) ) {
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
                if ( fileOpen( file, fileMode ) ) {
                    // Set the default directory to the parent of the
                    // successfully selected file.
                    final File parentDirectory = file.getParentFile();
                    setDefaultDirectory( parentDirectory );
                }
            }
        }
    }

    /**
     * Opens the specified file using the specified {@link FileMode}.
     * <p>
     * This is the lowest-level shared call for file open and import actions.
     * <p>
     * NOTE: Due to the MRU list, this method may be the main entry point for
     *  file open, and therefore must verify the file exists and update the
     *  MRU accordingly, vs. performing this check in the main higher-level
     *  dialog-based file open.
     *
     * @param file The {@link File} to open
     * @param fileMode The {@link FileMode} to use when opening the File
     * @return {@code true} if the file opened successfully; {@code false}
     *         if it did not open
     */
    default boolean fileOpen( final File file,
                              final FileMode fileMode ) {
        // Find out if the file can be opened for read.
        final String errorMessage = FileChooserUtilities.verifyForRead( file );

        // If the file doesn't exist, is read-protected, or is denied access by
        // a security manager, remove it from the MRU cache and exit.
        if ( errorMessage != null ) {
            updateMruCache( file, false );

            // Alert the user that there were problems with the file open.
            DialogUtilities.showFileOpenErrorAlert( errorMessage );

            return false;
        }

        // Pre-process the file open or import action. Exit if unable to save
        // the current file first, or if user canceled, to avoid data loss.
        if ( !fileOpenPreProcessing( file, fileMode ) ) {
            return false;
        }

        // Load the file contents.
        final FileStatus fileStatus = loadFromFile( file, fileMode );

        // Post-process the File Open or Import action.
        return fileOpenPostProcessing( file, fileMode, fileStatus );
    }

    // File Save As is not required for all stages, so this method is not
    // declared abstract but is instead given a default implementation.
    default boolean fileSaveAs( final Window parent,
                                final FileMode fileMode,
                                final ClientProperties clientProperties,
                                final RasterGraphicsExportOptions rasterGraphicsExportOptions,
                                final VectorGraphicsExportOptions vectorGraphicsExportOptions,
                                final String fileChooserTitle,
                                final File initialDirectory,
                                final List< ExtensionFilter > extensionFilterAdditions,
                                final ExtensionFilter defaultExtensionFilter,
                                final File defaultFile ) {
        // Pre-process the file save or export action. Exit if user canceled.
        if ( !fileSavePreProcessing( fileMode, 
                                     clientProperties, 
                                     rasterGraphicsExportOptions, 
                                     vectorGraphicsExportOptions ) ) {
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
        return fileSave( file, fileMode );
    }

    /**
     * Saves the specified file using the provided {@link FileMode}.
     * <p>
     * This is the lowest-level shared call for file save and export actions.
     *
     * @param file
     *            The {@link File} to save.
     * @param fileMode The {@link FileMode} to use when saving the File
     * @return {@code true} if the file saved successfully; {@code false}
     *         if it did not save.
     */
    default boolean fileSave( final File file,
                              final FileMode fileMode) {
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
        final FileStatus fileStatus = fileSave( file, tempFile, fileMode );

        // Post-process the File Save or Export action.
        fileSavePostProcessing( file, tempFile, fileMode, fileStatus );

        return true;
    }

    // NOTE: Not all implementing Windows support File Save actions, so this
    //  lower level method is given a default implementation that signifies
    //  failure. Implementing classes should override and forward per File
    //  Mode and File Type to specific handlers that aren't in this interface.
    // TODO: Build out some general handling that calls new interface methods
    //  that can be overridden, as much as can be done without class variables.
    default FileStatus fileSave( final File file, 
                                 final File tempFile,
                                 final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: Not all implementing Windows support File Close actions, so this
    //  method is given a default implementation that indicates cancellation.
    default boolean fileClose() {
        return false;
    }

    // NOTE: Not all implementing Windows support MRU File Lists, but the
    //  behavior tends to be generic across all applications when they do.
    default void fileOpenMru( final int mruId ) {
        // Open a new file of default type, using the cached filename.
        // NOTE: By default, the MRU Cache is non-null and empty, but when
        //  populated, it has a list of primary application objects/files.
        final File mruFile = getMruFile( mruId );
        if ( mruFile != null ) {
            fileOpen( mruFile, FileMode.OPEN );
        }
    }

    // File Save is not required for all Windows, so this method is not
    // declared abstract but is instead given a default implementation.
    default FileStatus fileSaveExtensions( final File file, 
                                           final File tempFile ,
                                           final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }

    /**
     * Returns the file prefix to use for interim write operations that insert a
     * randomizer-generated unique substring. Usually this is the application
     * name.
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
     *  this interface, and may be unneeded in many implementations.
     *
     * @param file The {@link File} to pre-process.
     * @param fileMode The {@link FileMode} to use when opening the File
     * @return {@code true} if the pre-processing succeeded; {@code false}
     *         if it did not succeed.
     */
    default boolean fileOpenPreProcessing( final File file,
                                           final FileMode fileMode ) {
        // By default, we assume that no pre-processing is required, which in
        // practical terms means that no additional user input is needed to
        // determine how to parameterize a file load request.
        return true;
    }

    /**
     * Post-processes the file, for example if a dirty flag needs to be set, or
     * if error processing is required in a specific implementation.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     *  this interface, and may be unneeded in many implementations.
     *
     * @param file
     *            The {@link File} to post-process.
     * @param fileMode The {@link FileMode} to use when opening the File
     * @param fileStatus
     *            The incoming status of whether the file loaded
     * @return {@code true} if the post-processing succeeded; {@code false}
     *         if it did not succeed.
     */
    default boolean fileOpenPostProcessing( final File file, 
                                            final FileMode fileMode,
                                            final FileStatus fileStatus ) {
        // By default, the only post-processing required is to check for errors
        // and cancellations.
        return fileOpenErrorHandling( file, fileMode, fileStatus );
    }

    /**
     * Checks the file for errors, most trivially just looking at the incoming
     * status of the file load, but possibly also checking for non-empty
     * results.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     *  this interface, and may be unneeded in most implementations.
     * <p>
     * TODO: Review whether the boolean return logic is counter-intuitive.
     * <p>
     * TODO: Come up with a generalized File Status enum vs. a boolean status?
     *
     * @param file
     *            The {@link File} to check for errors.
     * @param fileMode The {@link FileMode} to use when opening the File
     * @param fileStatus
     *            The incoming status of whether the file loaded
     * @return {@code true} if the error handling found no errors; {@code false}
     *         if the error handling found errors.
     */
    default boolean fileOpenErrorHandling( final File file, 
                                           final FileMode fileMode,
                                           final FileStatus fileStatus ) {
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
    //  some applications need to do pre-processing in advance of other file
    //  handling, this method is supplied so that the main file save
    //  implementations can insert a call to this method early on. Implementing
    //  classes can override this default. Basic graphics export is supported in
    //  this default implementation, but acts as a no-op if apps don't support it.
    default boolean fileSavePreProcessing( final FileMode fileMode,
                                           final ClientProperties clientProperties,
                                           final RasterGraphicsExportOptions rasterGraphicsExportOptions,
                                           final VectorGraphicsExportOptions vectorGraphicsExportOptions ) {
        // Gather the custom enablement and labels via class overrides.
        final String graphicsExportAllLabel = getGraphicsExportAllLabel();
        final String graphicsExportChartLabel = getGraphicsExportChartLabel();
        final String graphicsExportAuxiliaryLabel = getGraphicsExportAuxiliaryLabel();
        final boolean hasChart = ( graphicsExportChartLabel.trim().length() > 0 );
        final boolean hasAuxiliary = ( graphicsExportAuxiliaryLabel.trim().length() > 0 );

        switch ( fileMode ) {
        case EXPORT_RASTER_GRAPHICS:
            // Query the Image Graphics Export Options.
            if ( !DialogUtilities.showRasterGraphicsExportOptions( 
                    clientProperties,
                    rasterGraphicsExportOptions,
                    hasChart,
                    hasAuxiliary,
                    graphicsExportAllLabel,
                    graphicsExportChartLabel,
                    graphicsExportAuxiliaryLabel ) ) {
                return false;
            }

            // Update the Image Graphics Export source node based on which
            // elements are specified for export (if none, we use the main
            // content pane, as this is equivalent to "Export All Information").
            updateRasterGraphicsExportSource();

            break;
        case EXPORT_VECTOR_GRAPHICS:
            // Query the Vector Graphics Export Options.
            if ( !DialogUtilities.showVectorGraphicsExportOptions( 
                    clientProperties,
                    vectorGraphicsExportOptions,
                    true,
                    hasChart,
                    hasAuxiliary,
                    graphicsExportAllLabel,
                    graphicsExportChartLabel,
                    graphicsExportAuxiliaryLabel ) ) {
                return false;
            }

            // Update the Vector Graphics Export source node based on which
            // elements are specified for export (if none, we use the main
            // content pane, as this is equivalent to "Export All Information").
            updateVectorGraphicsExportSource();

            break;
        // $CASES-OMITTED$
        default:
            break;
        }

        return true;
    }

    default boolean fileSavePostProcessing( final File file,
                                            final File tempFile,
                                            final FileMode fileMode,
                                            final FileStatus fileStatus ) {
        // Rename the temporary file to a permanent file.
        final boolean fileSaved = FileStatus.SAVED.equals( fileStatus )
                || FileStatus.EXPORTED.equals( fileStatus );
        final boolean fileRenamed = fileSaved 
                ? FileUtilities.moveFile( tempFile, file ) 
                : false;

        // Modify the file status, but be careful not to write over existing
        // error codes.
        final FileStatus fileRenamedStatus = fileRenamed
            ? fileStatus
            : fileSaved ? FileStatus.WRITE_ERROR : fileStatus;

        // Check for errors and cancellations.
        final boolean filePostProcessed = fileSaveErrorHandling( file,
                                                                 fileMode,
                                                                 fileRenamedStatus );
        return filePostProcessed;
    }

    default boolean fileSaveErrorHandling( final File file, 
                                           final FileMode fileMode,
                                           final FileStatus fileStatus ) {
        boolean sawErrors = true;
        switch ( fileStatus ) {
        case GRAPHICS_WRITE_ERROR:
            // Alert the user that a graphics file write error occurred.
            final String graphicsFileWriteErrorMessage = MessageFactory
                    .getGraphicsFileWriteErrorMessage( file );
            DialogUtilities.showFileSaveErrorAlert( graphicsFileWriteErrorMessage );
            break;
        case READ_ERROR:
            switch ( fileMode ) {
            case NEW:
            case OPEN:
            case IMPORT_DATA:
            case IMPORT_IMAGE:
            case IMPORT_RASTER_GRAPHICS:
            case IMPORT_VECTOR_GRAPHICS:
            case IMPORT_CAD:
            case LOAD:
                // Alert the user that a file open error occurred.
                final String fileReadErrorMessage = MessageFactory
                        .getFileReadErrorMessage( file );
                DialogUtilities.showFileOpenErrorAlert( fileReadErrorMessage );
                break;
            //$CASES-OMITTED$
            default:
                break;
            }
            break;
        case WRITE_ERROR:
            switch ( fileMode ) {
            case SAVE_REPORT:
                // Alert the user that a save error occurred for a generated report.
                final String generatedReportWriteErrorMessage = MessageFactory
                        .getGeneratedReportWriteErrorMessage( file );
                com.mhschmieder.fxguitoolkit.dialog.DialogUtilities
                        .showFileSaveErrorAlert( generatedReportWriteErrorMessage );
                break;
            case SAVE_SERVER_REQUEST:
                // Alert the user that a server request save error occurred.
                final String serverRequestWriteErrorMessage = MessageFactory
                        .getServerRequestFileWriteErrorMessage( file );
                DialogUtilities.showFileSaveErrorAlert( serverRequestWriteErrorMessage );
                break;
            case SAVE_SERVER_RESPONSE:
                // Alert the user that a server response save error occurred.
                final String serverResponseWriteErrorMessage = MessageFactory
                        .getServerResponseFileWriteErrorMessage( file );
                DialogUtilities.showFileSaveErrorAlert( serverResponseWriteErrorMessage );
                break;
            case CLOSE:
            case SAVE:
            case SAVE_CONVERTED:
            case SAVE_LOG:
            case EXPORT_DATA:
            case EXPORT_IMAGE:
            case EXPORT_RASTER_GRAPHICS:
            case EXPORT_VECTOR_GRAPHICS:
            case EXPORT_CAD:
                // Alert the user that a file save error occurred.
                final String fileWriteErrorMessage = MessageFactory
                        .getFileWriteErrorMessage( file );
                DialogUtilities.showFileSaveErrorAlert( fileWriteErrorMessage );
                break;
            //$CASES-OMITTED$
            default:
                break;
            }
            break;
        case EXPORTED:
        case SAVED:
            sawErrors = false;
            break;
        case CANCELED:
        case NOT_SAVED:
            // TODO: Review whether we should flag as no resulting errors?
            break;
        // $CASES-OMITTED$
        default:
            break;
        }

        return !sawErrors;
    }

    @SuppressWarnings("nls")
    default FileStatus loadFromFile( final File file,
                                     final FileMode fileMode ) {
        // Pre-declare the File Load status in case of exceptions or unsupported
        // File Modes or file types. Not all applications support File Load of
        // types that are not native to the application, or of partial projects.
        FileStatus fileStatus = FileStatus.NOT_OPENED;

        final String fileExtension = FileUtilities.getExtension( file );
        switch ( fileExtension ) {
        case "gif":
        case "jpe":
        case "jpeg":
        case "jpg":
        case "png":
            // Load the raster image file.
            //
            // Chain a BufferedInputStream to a FileInputStream, for better
            // performance.
            try ( final FileInputStream fileInputStream = new FileInputStream( file );
                    final BufferedInputStream bufferedInputStream 
                            = new BufferedInputStream( fileInputStream ) ) {
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
                                        final FileMode fileMode,
                                        final String fileChooserTitle,
                                        final File initialDirectory ) {
        // Prepare to throw up a file chooser for the raster image filename.
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getRasterImageExtensionFilters();

        // Load a single raster image file using the JavaFX File Chooser.
        fileOpen( parent,
                  fileMode,
                  fileChooserTitle,
                  initialDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.PNG_EXTENSION_FILTER,
                  false );
    }

    // NOTE: Not all implementing Windows will support importing images, but we
    //  are providing a convenience mechanism to do so in the file open hierarchy
    //  of methods, so we default the image importer to return false.
    default boolean importImage( final InputStream inputStream, final String fileExtension ) {
        return false;
    }

    // NOTE: Not all implementing Windows support MRU File Lists, so this lower
    //  level method is given a default implementation that returns a null File.
    default File getMruFile( final int mruId ) {
        return null;
    }

    // NOTE: Not all implementing Windows support an MRU list, so we define a
    //  no-op default implementation rather than forcing an override.
    default void updateMruCache( final File file, final boolean addToCache ) {}

    // NOTE: We allow separate default directories per implementing Window as
    //  often they have different functionality and the user generally needs one
    //  default directory per functional scope.
    default void setDefaultDirectory( final File defaultDirectory ) {}
    
    // Implementing classes should set the class variable used for raster
    // graphics export. Interfaces can't do that due to no class variables.
    default void updateRasterGraphicsExportSource() {}
    
    // Implementing classes should set the class variable used for vector
    // graphics export. Interfaces can't do that due to no class variables.
    default void updateVectorGraphicsExportSource() {}
    
    // NOTE: This label is for Graphics Export Options.
    // NOTE: Implementing classes should override this default if they expose
    //  either charts or auxiliary information for Graphics Export.
    default String getGraphicsExportAllLabel() {
        return "";
    }

    // NOTE: This label is for Graphics Export Options.
    // NOTE: Implementing classes should override this default if they expose
    //  auxiliary information for Graphics Export.
    default String getGraphicsExportAuxiliaryLabel() {
        return "";
    }

    // NOTE: This label is for Graphics Export Options.
    // NOTE: Implementing classes should override this default if they expose 
    //  charts for Graphics Export.
    default String getGraphicsExportChartLabel() {
        return "";
    }
}
