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

import com.mhschmieder.commonstoolkit.io.CsvUtilities;
import com.mhschmieder.commonstoolkit.io.FileMode;
import com.mhschmieder.commonstoolkit.io.FileMover;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.io.FilenameUtilities;
import com.mhschmieder.commonstoolkit.io.LogUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.image.ImageSize;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxgraphicstoolkit.io.RasterGraphicsExportOptions;
import com.mhschmieder.fxgraphicstoolkit.io.RenderedGraphicsExportOptions;
import com.mhschmieder.fxgraphicstoolkit.io.VectorGraphicsExportOptions;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;
import com.mhschmieder.graphicstoolkit.image.ImageConversionUtilities;
import com.mhschmieder.graphicstoolkit.image.ImageFormatUtilities;
import javafx.scene.Node;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@code FileActionHandler} is an interface that contracts Window-derived classes
 * to supply basic file action handling. To minimize the amount of coupling, it 
 * attempts to standardize as much as possible in default implementations herein.
 * <p>
 * In most contexts, the generic "file" reference refers to an application-domain
 * project file, but to be more generally applicable for a variety of downstream
 * implementers, we are keeping the terminology as simple and basic as possible.
 */
public interface FileActionHandler {

    /** 
     * Opens a new file, after closing and/or saving the current file. 
     * 
     * @param saveProject {@code true} if called from a context where there is a
     *                    valid current file; {@code false} if coming from a
     *                    failed File Open action where we need a new default file
     */
    default void fileNew( final boolean saveProject ) {}
    
    // Basic File Open is generally specific to domain objects in an application,
    // so this is a default no-opo method that needs to be overridden if relevant.
    default FileStatus fileOpen( final File file ) {
        return FileStatus.NOT_OPENED;
    }

    default void fileOpen( final Window parent,
                           final FileMode fileMode,
                           final String fileChooserTitle,
                           final File initialDirectory,
                           final List< ExtensionFilter > extensionFilterAdditions,
                           final ExtensionFilter defaultExtensionFilter,
                           final boolean multiSelectionEnabled ) {
        // NOTE: JavaFX uses a different file chooser action for multi-select.
        final List< File > files = new ArrayList<>();
        if ( multiSelectionEnabled ) {
            // Get a file (or list of files) for a "File Open" action.
            files.addAll( FileChooserUtilities.getFilesForOpen( 
                    fileChooserTitle,
                    initialDirectory,
                    extensionFilterAdditions,
                    defaultExtensionFilter,
                    parent ) );
        }
        else {
            // Get a file for a "File Open" action.
            files.add( FileChooserUtilities.getFileForOpen( 
                    fileChooserTitle,
                    initialDirectory,
                    extensionFilterAdditions,
                    defaultExtensionFilter,
                    parent ) );
        }
        
        // Make sure the user actually selected a file and didn't cancel.
        if ( files.isEmpty() ) {
            return;
        }
        
        File lastOpenedFile = null;
        for ( final File file : files ) {
            // Open the chosen (but possibly renamed) file.
            if ( fileOpen( file, fileMode ) ) {
                lastOpenedFile = file;
            }
        }
 
        if ( lastOpenedFile != null ) {
            // Set the default directory to the parent of the last opened file.
            final File parentDirectory = lastOpenedFile.getParentFile();
            setDefaultDirectory( parentDirectory );
        }
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

    // TODO: Flesh out this method by borrowing stock implementations from
    //  downstream Stages and writing forwarding methods for low-level details
    //  that each Stage can override for its specific handling. This has been
    //  done already for a few File Modes, and it reduces boilerplate code. Do
    //  as much as is possible without access to downstream class variables.
    // NOTE: This implies that downstream classes needn't override this method.
    default FileStatus loadFromFile( final File file,
                                     final FileMode fileMode ) {
        // Pre-declare the File Load status in case of exceptions or unsupported
        // File Modes or file types. Not all applications support File Load of
        // types that are not native to the application, or of partial projects.
        FileStatus fileStatus = FileStatus.NOT_OPENED;
        
        // TODO: Forward more output modes to skeletal default methods for classes
        //  to override, to minimize the need for OTHER and fileSaveExtensions().
        switch ( fileMode ) {
        case NEW:
            break;
        case OPEN:
            fileStatus = fileOpen( file );
            break;
        case IMPORT_TEXT_DATA:
            break;
        case IMPORT_TABLE_DATA:
            fileStatus = importTableData( file, fileMode );
            break;
        case IMPORT_SPREADSHEET_DATA:
            break;
        case IMPORT_BINARY_DATA:
            break;
        case IMPORT_IMAGE_DATA:
            fileStatus = importImageData( file, fileMode );
            break;
        case IMPORT_RASTER_GRAPHICS:
            break;
        case IMPORT_VECTOR_GRAPHICS:
            fileStatus = importVectorGraphics( file, fileMode );
            break;
        case IMPORT_RENDERED_GRAPHICS:
            break;
        case IMPORT_CAD:
            break;
        case LOAD:
            break;
        case CLOSE:
        case SAVE:
        case SAVE_CONVERTED:
        case SAVE_LOG:
        case SAVE_REPORT:
        case SAVE_SERVER_REQUEST:
        case SAVE_SERVER_RESPONSE:
        case EXPORT_TEXT_DATA:
        case EXPORT_TABLE_DATA:
        case EXPORT_SPREADSHEET_DATA:
        case EXPORT_BINARY_DATA:
        case EXPORT_IMAGE_DATA:
        case EXPORT_RASTER_GRAPHICS:
        case EXPORT_VECTOR_GRAPHICS:
        case EXPORT_RENDERED_GRAPHICS:
        case EXPORT_CAD:
           break;
        case OTHER:
            // TODO: Call a default handler for this, for classes to override.
            break;
        default:
            break;
        }

        return fileStatus;
    }

    // NOTE: Not all implementing Windows support File Close actions, so this
    //  method is given a default implementation that indicates cancellation.
    default boolean fileClose() {
        return false;
    }

    // File Save is not required for all stages, so we provide a default.
    // This uses the supplied file, if it exists; otherwise it does a save as.
    // NOTE: We also apply logic for whether confirmation is required, as is
    //  the case if the user started a new file with the default name vs.
    //  opening one from disc.
    default boolean fileSave( final Window parent,
                              final ClientProperties clientProperties,
                              final File file,
                              final boolean fileChanged,
                              final String fileChooserTitle,
                              final File initialDirectory,
                              final List< ExtensionFilter > extensionFilterAdditions,
                              final ExtensionFilter defaultExtensionFilter,
                              final File defaultFile ) {
        // NOTE: We avoid redundant saves as well as querying the user for a
        //  filename when there is nothing to save (they can always do this
        //  deliberately with a direct "Save As" anyway).
        boolean fileSaved = false;
        if ( fileChanged ) {
            try {
                if ( ( file != null ) && Files.isRegularFile( 
                        file.toPath(), LinkOption.NOFOLLOW_LINKS ) ) {
                    // Save the project in the same file location as was
                    // originally opened.
                    fileSaved = fileSave( file, FileMode.SAVE );
                }
                else {
                    // Force the user to provide a new file name for
                    // uninitialized files. The default file is usually null.
                    // NOTE: We must forward any user cancellation events in
                    //  order to prevent the project from being reset.
                    fileSaved = fileSaveAs( parent,
                                            FileMode.SAVE,
                                            clientProperties,
                                            fileChooserTitle,
                                            initialDirectory,
                                            extensionFilterAdditions,
                                            defaultExtensionFilter,
                                            defaultFile );
                }
            }
            catch ( final Exception e ) {
                e.printStackTrace();
            }
        }
        
        return fileSaved;
    }

    // File Save is not required for all stages, so we provide a default.
    default boolean fileSaveAs( final Window parent,
                                final FileMode fileMode,
                                final ClientProperties clientProperties,
                                final String fileChooserTitle,
                                final File initialDirectory,
                                final List< ExtensionFilter > extensionFilterAdditions,
                                final ExtensionFilter defaultExtensionFilter,
                                final File defaultFile ) {
        // Pre-process the file save or export action. Exit if user canceled.
        if ( !fileSavePreProcessing( fileMode, 
                                     clientProperties ) ) {
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

        // Save the content to the chosen (but possibly renamed) file.
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
        final File tempFile = FileChooserUtilities.getTempFile( file, 
                                                                prefixForTempFile );
        if ( tempFile == null ) {
            return false;
        }

        // Save the file.
        // NOTE: The temp file doesn't include the file extension.
        final FileStatus fileStatus = saveToFile( file, tempFile, fileMode );

        // Post-process the File Save or Export action.
        fileSavePostProcessing( file, tempFile, fileMode, fileStatus );

        return true;
    }

    // TODO: Flesh out this method by borrowing stock implementations from
    //  downstream Stages and writing forwarding methods for low-level details
    //  that each Stage can override for its specific handling. This has been
    //  done already for a few File Modes, and it reduces boilerplate code. Do
    //  as much as is possible without access to downstream class variables.
    // NOTE: This implies that downstream classes needn't override this method.
    default FileStatus saveToFile( final File file, 
                                   final File tempFile,
                                   final FileMode fileMode ) {
       // Pre-declare the File Load status in case of exceptions or unsupported
       // File Modes or file types. Not all applications support File Load of
       // types that are not native to the application, or of partial projects.
       FileStatus fileStatus = FileStatus.NOT_SAVED;
       
       // TODO: Forward more output modes to skeletal default methods for classes
       //  to override, to minimize the need for OTHER and fileSaveExtensions().
       switch ( fileMode ) {
       case NEW:
       case OPEN:
       case IMPORT_TEXT_DATA:
       case IMPORT_TABLE_DATA:
       case IMPORT_SPREADSHEET_DATA:
       case IMPORT_BINARY_DATA:
       case IMPORT_IMAGE_DATA:
       case IMPORT_RASTER_GRAPHICS:
       case IMPORT_VECTOR_GRAPHICS:
       case IMPORT_RENDERED_GRAPHICS:
       case IMPORT_CAD:
       case LOAD:
       case CLOSE:
       case SAVE:
       case SAVE_CONVERTED:
       case SAVE_LOG:
           fileStatus = exportSessionLog( file, tempFile, fileMode );
           break;
       case SAVE_REPORT:
       case SAVE_SERVER_REQUEST:
       case SAVE_SERVER_RESPONSE:
           break;
       case EXPORT_TEXT_DATA:
           break;
       case EXPORT_TABLE_DATA:
           break;
       case EXPORT_SPREADSHEET_DATA:
           fileStatus = exportSpreadsheetData( file, tempFile, fileMode );
          break;
       case EXPORT_BINARY_DATA:
           break;
       case EXPORT_IMAGE_DATA:
           fileStatus = exportImageData( file, tempFile, fileMode );
           break;
       case EXPORT_RASTER_GRAPHICS:
           fileStatus = exportRasterGraphics( file, tempFile, fileMode );
           break;
       case EXPORT_VECTOR_GRAPHICS:
           fileStatus = exportVectorGraphics( file, tempFile, fileMode );
           break;
       case EXPORT_RENDERED_GRAPHICS:
           fileStatus = exportRenderedGraphics( file, tempFile, fileMode );
           break;
       case EXPORT_CAD:
           fileStatus = exportCadGraphics( file, tempFile, fileMode );
           break;
       case OTHER:
           // TODO: Call a default handler for this, for classes to override.
           break;
       default:
           break;
       }

       return fileStatus;
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
     * Checks the file for errors, most trivially by looking at the status of
     * the incoming file load, but possibly also checking for non-empty results.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     *  this interface, and may be unneeded in some implementations.
     * <p>
     * TODO: Review whether the boolean return logic is counter-intuitive.
     * <p>
     * TODO: Come up with a generalized File Status enum vs. a boolean status?
     *
     * @param file
     *            The {@link File} to check for errors.
     * @param fileMode The {@link FileMode} to use when opening the File
     * @param fileStatus
     *            The status of whether the incoming file loaded
     * @return {@code true} if the error handling found no errors; {@code false}
     *         if the error handling found errors.
     */
    default boolean fileOpenErrorHandling( final File file, 
                                           final FileMode fileMode,
                                           final FileStatus fileStatus ) {
        // The only error handling for most file loads is to check the incoming
        // load status and to alert the user if there were file load errors. It
        // is possible that additional logic may be needed in overrides to this
        // method, such as to make sure the results of file load are non-empty.
        boolean sawErrors = true;
        switch ( fileStatus ) {
        case CANCELED:
        case CREATED:
        case IMPORTED:
        case LOADED:
        case OPENED:
        case OPENED_FOR_RENAME:
            sawErrors = false;
            break;
        case NOT_OPENED:
            // Alert the user that the file was not opened.
            final String fileNotOpenedMessage = MessageFactory
                    .getFileNotOpenedMessage( file );
            DialogUtilities.showFileOpenErrorAlert( fileNotOpenedMessage );
            break;
        case READ_ERROR:
            final String fileReadErrorMessage = MessageFactory
                    .getFileReadErrorMessage( file );
            switch ( fileMode ) {
            case OPEN:
                // Reset to the next default file, in case of a partial open.
                fileNew( false );

                // Alert the user that a file open error occurred.
                DialogUtilities.showFileOpenErrorAlert( fileReadErrorMessage );
                break;
            case NEW:
            case IMPORT_TEXT_DATA:
            case IMPORT_TABLE_DATA:
            case IMPORT_SPREADSHEET_DATA:
            case IMPORT_BINARY_DATA:
            case IMPORT_IMAGE_DATA:
            case IMPORT_RASTER_GRAPHICS:
            case IMPORT_VECTOR_GRAPHICS:
            case IMPORT_RENDERED_GRAPHICS:
            case IMPORT_CAD:
            case LOAD:
                // Alert the user that a file open error occurred.
                DialogUtilities.showFileOpenErrorAlert( fileReadErrorMessage );
                break;
            case OTHER:
                sawErrors = otherReadErrorHandling( file );
                break;
            //$CASES-OMITTED$
            default:
                break;
            }
            break;
        case CLIENT_INCOMPATIBLE:
            // Alert the user that the file is too new to open, and give
            // them the chance to check for updates via the browser.
            final String fileNewerThanClientMessage = MessageFactory
                    .getFileNewerThanClientMessage();
            DialogUtilities.showFileOpenErrorAlert( fileNewerThanClientMessage );
            break;
        case OUT_OF_MEMORY_ERROR:
            // Clear all errors and and associated status from partial file open.
            clearFileOpenErrors();

            // Alert the user that a graphics file out of memory error
            // occurred.
            final String outOfMemoryMessage = MessageFactory
                    .getFileImportOutOfMemoryMessage( fileMode, file );
            DialogUtilities.showFileReadErrorAlert( outOfMemoryMessage );
            break;
        case GRAPHICS_READ_ERROR:
            // Clear all the DXF errors and and associated status.
            clearFileOpenErrors();

            // Alert the user that a graphics file read error occurred.
            final String graphicsFileReadErrorMessage = MessageFactory
                    .getGraphicsFileReadErrorMessage( fileMode, file );
            DialogUtilities.showFileReadErrorAlert( graphicsFileReadErrorMessage );
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
    
    // This method is for implementing classes to detail. It provides a hook for
    // cleaning up after potential incomplete file open actions, whereas file not
    // opened errors and basic file read errors do not usually corrupt app state.
    default void clearFileOpenErrors() {}


    // NOTE: This is by default a no-op that does no harm to the file status. As
    //  some applications need to do pre-processing in advance of other file
    //  handling, this method is supplied so that the main file save
    //  implementations can insert a call to this method early on. Implementing
    //  classes can override this default. Basic graphics export is supported in
    //  this default implementation, but acts as a no-op if apps don't support it.
    default boolean fileSavePreProcessing( final FileMode fileMode,
                                           final ClientProperties clientProperties ) {
        // Gather the custom enablement and labels via class overrides.
        final String graphicsExportAllLabel = getGraphicsExportAllLabel();
        final String graphicsExportChartLabel = getGraphicsExportChartLabel();
        final String graphicsExportAuxiliaryLabel = getGraphicsExportAuxiliaryLabel();
        final boolean hasChart = ( graphicsExportChartLabel.trim().length() > 0 );
        final boolean hasAuxiliary = ( graphicsExportAuxiliaryLabel.trim().length() > 0 );

        // TODO: Do the same for rendered graphics export? Code support is present.
        switch ( fileMode ) {
        case EXPORT_RASTER_GRAPHICS:
            // Query the Image Graphics Export Options.
            if ( !DialogUtilities.showRasterGraphicsExportOptions( 
                    clientProperties,
                    getRasterGraphicsExportOptions(),
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
                    getVectorGraphicsExportOptions(),
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
                ? FileMover.moveFile( tempFile, file ) 
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
        
        // Conditionally add the chosen file to the cache and move it to the
        // head of the list. If the file doesn't exist, delete it from the
        // cache instead.
        if ( filePostProcessed && FileStatus.SAVED.equals( fileStatus ) ) {
            updateMruCache( file, true );
        }
        
        return filePostProcessed;
    }

    /**
     * Checks the file for errors, most trivially by looking at the status of
     * the outgoing file save, but possibly also checking for non-empty results.
     * <p>
     * NOTE: This method is here for API flexibility in downstream clients of
     *  this interface, and may be unneeded in some implementations.
     * <p>
     * TODO: Review whether the boolean return logic is counter-intuitive.
     * <p>
     * TODO: Come up with a generalized File Status enum vs. a boolean status?
     *
     * @param file
     *            The {@link File} to check for errors.
     * @param fileMode The {@link FileMode} to use when saving the File
     * @param fileStatus
     *            The status of whether the outgoing file saved
     * @return {@code true} if the error handling found no errors; {@code false}
     *         if the error handling found errors.
     */
    default boolean fileSaveErrorHandling( final File file, 
                                           final FileMode fileMode,
                                           final FileStatus fileStatus ) {
        boolean sawErrors = true;
        switch ( fileStatus ) {
        case CANCELED:
        case EXPORTED:
        case SAVED:
            sawErrors = false;
            break;
        case NOT_SAVED:
            // Alert the user that the file was not saved.
            final String fileNotSavedMessage = MessageFactory
                    .getFileNotSavedMessage( file );
            DialogUtilities.showFileSaveErrorAlert( fileNotSavedMessage );
            break;
        case GRAPHICS_WRITE_ERROR:
            // Alert the user that a graphics file write error occurred.
            final String graphicsFileWriteErrorMessage = MessageFactory
                    .getGraphicsFileWriteErrorMessage( file );
            DialogUtilities.showFileSaveErrorAlert( graphicsFileWriteErrorMessage );
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
            case EXPORT_TEXT_DATA:
            case EXPORT_TABLE_DATA:
            case EXPORT_SPREADSHEET_DATA:
            case EXPORT_BINARY_DATA:
            case EXPORT_IMAGE_DATA:
            case EXPORT_RASTER_GRAPHICS:
            case EXPORT_VECTOR_GRAPHICS:
            case EXPORT_RENDERED_GRAPHICS:
            case EXPORT_CAD:
                // Alert the user that a file save error occurred.
                final String fileWriteErrorMessage = MessageFactory
                        .getFileWriteErrorMessage( file );
                DialogUtilities.showFileSaveErrorAlert( fileWriteErrorMessage );
                break;
            case OTHER:
                sawErrors = otherWriteErrorHandling( file );
                break;
            //$CASES-OMITTED$
            default:
                break;
            }
            break;
        // $CASES-OMITTED$
        default:
            break;
        }

        return !sawErrors;
    }
    
    // This method is for overriding to handle custom File Modes not in the enum.
    // Applications should define their own and reference a pre-cached class var.
    default boolean otherReadErrorHandling( final File file ) {
        return true;
    }
    
    // This method is for overriding to handle custom File Modes not in the enum.
    // Applications should define their own and reference a pre-cached class var.
    default boolean otherWriteErrorHandling( final File file ) {
        return true;
    }

    // NOTE: Not all implementing Windows support MRU File Lists, so this lower
    //  level method is given a default implementation that returns a null File.
    default File getMruFile( final int mruId ) {
        return null;
    }

    // NOTE: Not all implementing Windows support an MRU list, so we define a
    //  no-op default implementation rather than forcing an override.
    default void updateMruCache( final File file, final boolean addToCache ) {}

    /**
     * Returns the file prefix to use for interim write operations that insert a
     * random-generated unique substring. Usually this is the application name.
     *
     * @return The prefix to use for generating a random temp file name.
     */
    @SuppressWarnings("nls")
    default String getPrefixForTempFile() {
        return "";
    }

    // File Open is not required for all stages, so this default is a no-op.
    default FileStatus fileOpenExtensions( final File file, 
                                           final FileMode fileMode ) {
        return FileStatus.NOT_OPENED;
    }

    // File Save is not required for all stages, so this default is a no-op.
    default FileStatus fileSaveExtensions( final File file, 
                                           final File tempFile,
                                           final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: We allow separate default directories per implementing Window as
    //  often they have different functionality and the user generally needs one
    //  default directory per functional scope.
    default void setDefaultDirectory( final File defaultDirectory ) {}

    default void fileImportTableData( final Window parent,
                                      final File initialDirectory ) {
        // Prepare to throw up a file chooser for the table data filename.
        final String title = "Import Table Data From";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getCsvExtendedExtensionFilters();

        // Load a single CSV file using the JavaFX File Chooser.
        fileOpen( parent,
                  FileMode.IMPORT_TABLE_DATA,
                  title,
                  initialDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.CSV_EXTENSION_FILTER,
                  false );
    }

    default void fileImportImageData( final Window parent,
                                      final String fileChooserTitle,
                                      final File initialDirectory ) {
        // Prepare to throw up a file chooser for the raster image filename.
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getRasterImageExtensionFilters();

        // Load a single raster image file using the JavaFX File Chooser.
        fileOpen( parent,
                  FileMode.IMPORT_IMAGE_DATA,
                  fileChooserTitle,
                  initialDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.PNG_EXTENSION_FILTER,
                  false );
    }

    default void fileImportVectorGraphics( final Window parent,
                                           final File initialDirectory ) {
        // Prepare to throw up a file chooser for the vector graphics filename.
        // NOTE: Only SVG is supported for Vector Graphics Import for now.
        final String title = "Import Vector Graphics To Scene Graph From";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getSvgExtensionFilters();

        // Load a single vector graphics file using the JavaFX File Chooser.
        fileOpen( parent,
                  FileMode.IMPORT_VECTOR_GRAPHICS,
                  title,
                  initialDirectory,
                  extensionFilterAdditions,
                  ExtensionFilters.SVG_EXTENSION_FILTER,
                  false );
    }

    // This is a wrapper to ensure that all session log export actions are
    // treated uniformly.
    default void fileExportSessionLog( final Window parent,
                                       final File initialDirectory,
                                       final ClientProperties clientProperties ) {
        // Throw up a file chooser for the Session Log filename.
        final String title = "Export Session Log As";
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getSessionLogExtensionFilters();

        // Save a Session Log file using the selected filename.
        // TODO: To avoid writing atop the actual Session Log file and causing
        //  potential deadlock or other such problems, we must provide a blank
        //  initial file for the re-save. Passing a null file accomplishes this?
        fileSaveAs( parent,
                    FileMode.SAVE_LOG,
                    clientProperties,
                    title,
                    initialDirectory,
                    extensionFilterAdditions,
                    ExtensionFilters.TXT_EXTENSION_FILTER,
                    null );
    }

    // This is a wrapper to ensure that all spreadsheet data export actions
    // are treated uniformly.
    default void fileExportSpreadsheetData( final Window parent,
                                            final File initialDirectory,
                                            final ClientProperties clientProperties,
                                            final String dataCategory ) {
        // Throw up a file chooser for the spreadsheet filename.
        final String title = "Export " + dataCategory + " Spreadsheet Data To";
        final List< ExtensionFilter > extensionFilterAdditions = ExtensionFilterUtilities
                .getXlsxExtensionFilters();

        // Save a data file using the selected spreadsheet filename.
        fileSaveAs( parent,
                    FileMode.EXPORT_SPREADSHEET_DATA,
                    clientProperties,
                    title,
                    initialDirectory,
                    extensionFilterAdditions,
                    ExtensionFilters.XLSX_EXTENSION_FILTER,
                    null );
    }

    // This is a wrapper to ensure that all image data export actions are 
    // treated uniformly.
    default void fileExportImageData( final Window parent,
                                      final File initialDirectory,
                                      final ClientProperties clientProperties,
                                      final String dataCategory ) {
        // Throw up a file chooser for the raster image filename.
        final String title = "Export " + dataCategory + " Image Data To";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getRasterImageExtensionFilters();

        // Save a raster image file using the selected filename.
        fileSaveAs( parent,
                    FileMode.EXPORT_IMAGE_DATA,
                    clientProperties,
                    title,
                    initialDirectory,
                    extensionFilterAdditions,
                    ExtensionFilters.PNG_EXTENSION_FILTER,
                    null );
    }

    // This is a wrapper to ensure that all raster graphics export actions
    // are treated uniformly.
    default void fileExportRasterGraphics( final Window parent,
                                           final File initialDirectory,
                                           final ClientProperties clientProperties,
                                           final String graphicsCategory ) {
        // Throw up a file chooser for the raster graphics filename.
        final String title = "Export " + graphicsCategory + " Raster Graphics To";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getImageGraphicsExtensionFilters();

        // Save a raster graphics file using the selected filename.
        fileSaveAs( parent,
                    FileMode.EXPORT_RASTER_GRAPHICS,
                    clientProperties,
                    title,
                    initialDirectory,
                    extensionFilterAdditions,
                    ExtensionFilters.IMAGE_GRAPHICS_EXTENSION_FILTER,
                    null );
    }

    // This is a wrapper to ensure that all vector graphics export actions are
    // treated uniformly.
    default boolean fileExportVectorGraphics( final Window parent,
                                              final File initialDirectory,
                                              final ClientProperties clientProperties,
                                              final String graphicsCategory ) {
        // Throw up a file chooser for the vector graphics filename.
        final String title = "Export " + graphicsCategory + " Vector Graphics To";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getVectorGraphicsExtensionFilters();

        // Save a vector graphics file using the selected filename.
        // TODO: Default to PDF here, and save EPS as default for Rendered Graphics?
        return fileSaveAs( parent,
                           FileMode.EXPORT_VECTOR_GRAPHICS,
                           clientProperties,
                           title,
                           initialDirectory,
                           extensionFilterAdditions,
                           ExtensionFilters.EPS_EXTENSION_FILTER,
                           null );
    }

    // This is a wrapper to ensure that all rendered graphics export actions are
    // treated uniformly.
    default boolean fileExportRenderedGraphics( final Window parent,
                                                final File initialDirectory,
                                                final ClientProperties clientProperties,
                                                final String graphicsCategory ) {
        // Throw up a file chooser for the vector graphics filename.
        final String title = "Export " + graphicsCategory + " Rendered Graphics To";
        final List< ExtensionFilter > extensionFilterAdditions 
                = ExtensionFilterUtilities.getVectorGraphicsExtensionFilters();

        // Save a rendered graphics file using the selected filename.
        // TODO: Default to EPS here, and save PDF as default for Vector Graphics?
        return fileSaveAs( parent,
                           FileMode.EXPORT_RENDERED_GRAPHICS,
                           clientProperties,
                           title,
                           initialDirectory,
                           extensionFilterAdditions,
                           ExtensionFilters.VECTOR_GRAPHICS_EXTENSION_FILTER,
                           null );
    }

    // NOTE: Not all implementing Windows support importing images, but we
    //  provide a convenience mechanism to do so in the hierarchy of File Open.
    default FileStatus importImageData( final File file,
                                        final FileMode fileMode ) {
        // Pre-declare the File Load status in case of exceptions or unsupported
        // File Modes or file types. Not all applications support Image Import.
        FileStatus fileStatus = FileStatus.NOT_OPENED;
        
        // TODO: Review whether we should adjust the file extension first for
        //  simplicity. This requires following the downstream code to see how
        //  the original extension is used. Java imaging is extension-neutral?
        final String fileExtension = FilenameUtilities.getExtension( file );
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
                fileStatus = importImageData( bufferedInputStream, fileExtension );
            }
            catch ( final SecurityException | IOException e ) {
                e.printStackTrace();
            }
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileOpenExtensions( file, fileMode );
            break;
        }
        
        return fileStatus;
    }
    
    // NOTE: Not all Stages support importing an image that is a data item.
    default FileStatus importImageData( final InputStream inputStream,
                                        final String fileExtension ) {
        return FileStatus.NOT_SAVED; // Overrides should return IMPORTED
    }

    default FileStatus importTableData( final File file,
                                        final FileMode fileMode ) {
        // Pre-declare the File Load status in case of exceptions or unsupported
        // File Modes or file types. Not all applications support Table Import.
        FileStatus fileStatus = FileStatus.NOT_OPENED;
        
        final String fileExtension = FilenameUtilities.getExtension( file );
        switch ( fileExtension ) {
        case "csv":
        case "zip":
            // Load the data into a String from a CSV or ZIP-contained ZIP file.
            // TODO: Convert the CSV Utilities class to use Apache CSV Parser.
            final Collection< Collection< String > > rows = new ArrayList<>();
            if ( CsvUtilities.convertCsvToStringVector( file, rows ) ) {
                processTableData( file, rows );
                fileStatus = FileStatus.IMPORTED;
            }
            else {
                fileStatus = FileStatus.READ_ERROR;
            }
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileOpenExtensions( file, fileMode );
            break;
        }
        
        return fileStatus;
    }
    
    default void processTableData( final File file,
                                   final Collection< Collection< String > > rows ) {
        // Nothing to do without access to the implementing class, but not every
        // file handling Stage supports Import Table Data, so this is a no-op
        // default method rather than an empty method requiring overrides.
    }
 

    default FileStatus importVectorGraphics( final File file,
                                             final FileMode fileMode ) {
        // Pre-declare the File Load status in case of exceptions or unsupported
        // File Modes or file types. Not all applications support Vector Graphics.
        FileStatus fileStatus = FileStatus.NOT_OPENED;
        
        final String fileExtension = FilenameUtilities.getExtension( file );
        switch ( fileExtension ) {
        case "svg":
            // Load the Scene Graph data from an SVG file.
            try {
                final Document doc = Jsoup.parse( file, "UTF-8", "" );
                processSvgDocument( file, doc );
                fileStatus = FileStatus.IMPORTED;
            }
            catch ( final IOException e ) {
                e.printStackTrace();
                fileStatus = FileStatus.READ_ERROR;
            }
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileOpenExtensions( file, fileMode );
           break;
        }
        
        return fileStatus;
    }
    
    default void processSvgDocument( final File file,
                                     final Document doc ) {
        // Nothing to do without access to the implementing class, but not every
        // file handling Stage supports Import Table Data, so this is a no-op
        // default method rather than an empty method requiring overrides.
    }

    // NOTE: Not all Stages support exporting session logs.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportSessionLog( final File file, 
                                         final File tempFile,
                                         final FileMode fileMode ) {
        // Pre-declare the File Save status in case of exceptions.
        FileStatus fileStatus = FileStatus.WRITE_ERROR;

        // TODO: Switch these and others to Apache Commons I/O library, which
        //  has a SuffixFileFilter with accept() methods?
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "txt":
        case "log":
            // Export the Session Log to a standard ASCII text log file,
            // overwriting it if it already exists.
            //
            // Chain a PrintWriter to a BufferedWriter to an OutputStreamWriter
            // (using UTF-8 encoding) to a FileOutputStream, for better 
            // performance and to guarantee platform-independence of newlines
            // and overall system-neutrality and locale-sensitivity of text data.
            try ( final FileOutputStream fileOutputStream 
                            = new FileOutputStream( tempFile );
                    final OutputStreamWriter outputStreamWriter 
                            = new OutputStreamWriter( fileOutputStream, "UTF8" );
                    final BufferedWriter bufferedWriter 
                            = new BufferedWriter( outputStreamWriter );
                    final PrintWriter printWriter 
                            = new PrintWriter( bufferedWriter ) ) {
                fileStatus = exportSessionLog( printWriter );
            }
            catch ( final NullPointerException | SecurityException | IOException e ) {
                e.printStackTrace();
            }
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }

    // NOTE: Not all Stages support exporting session logs.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportSessionLog( final PrintWriter printWriter ) {
        // Avoid throwing unnecessary exceptions by filtering for bad print
        // writers.
        if ( printWriter == null ) {
            return FileStatus.WRITE_ERROR;
        }

        // Get the current Session Log File Cache.
        final String sessionLogFilename = getSessionLogFilename();
        final String sessionLog = LogUtilities.loadSessionLogFromCache( 
                sessionLogFilename );

        // Print the entire Session Log out as one write-to-disc operation.
        printWriter.println( sessionLog );

        // Return the print writer's status, which subsumes exceptions.
        return printWriter.checkError() ? FileStatus.WRITE_ERROR : FileStatus.SAVED;
    }

    // NOTE: Not all Stages support exporting spreadsheet data.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportSpreadsheetData( final File file, 
                                              final File tempFile,
                                              final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "xslx":
            // Export a cached image in the specified format.
            fileStatus = exportSpreadsheetData( tempFile, fileExtension );
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }

    // NOTE: Not all Stages support exporting spreadsheet data.
    default FileStatus exportSpreadsheetData( final File file, 
                                              final String fileExtension ) {
        // Pre-declare the File Save status in case of exceptions.
        FileStatus fileStatus = FileStatus.WRITE_ERROR;

        // Export the results to an Excel Spreadsheet file, overwriting
        // it if it already exists. Don't buffer it, as POI uses ZIP's.
        // TODO: Base this on selected rows (if applicable).
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file ) ) {
            fileStatus = exportToXslx( fileOutputStream, fileExtension );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileStatus;
    }

    // NOTE: Not all Stages support exporting an image that is a data item.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportImageData( final File file, 
                                        final File tempFile,
                                        final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "bmp":
        case "gif":
        case "jpg":
        case "png":
        case "pnm":
        case "tiff":
        case "wbmp":
            // Export a cached image in the specified format.
            fileStatus = exportImageData( tempFile, fileExtension );
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }

    // NOTE: Not all Stages support exporting an image that is a data item.
    default FileStatus exportImageData( final File file, 
                                        final String fileExtension ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: Not all Stages support exporting a screenshot as raster graphics.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportRasterGraphics( final File file, 
                                             final File tempFile,
                                             final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "bmp":
        case "gif":
        case "jpg":
        case "png":
        case "pnm":
        case "tiff":
        case "wbmp":
            final RasterGraphicsExportOptions rasterGraphicsExportOptions 
                    = getRasterGraphicsExportOptions();
            if ( rasterGraphicsExportOptions != null ) {
                // Rasterize the main content pane to file in the specified format.
                final ImageSize imageSize = rasterGraphicsExportOptions.getImageSize();
                fileStatus = exportRasterGraphics( tempFile, 
                                                   fileExtension, 
                                                   imageSize );
            }
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }
   
    default FileStatus exportRasterGraphics( final File file,
                                             final String fileExtension,
                                             final ImageSize imageSize ) {
        // Avoid throwing unnecessary exceptions by filtering for no-ops.
        final Node rasterGraphicsExportSource = getRasterGraphicsExportSource();
        if ( rasterGraphicsExportSource == null ) {
            return FileStatus.NOT_SAVED;
        }

        // Get an AWT BufferedImage as the snapshot of the source Node.
        // TODO: Switch this to use the new JavaFX Imaging Engine.
       final BufferedImage bufferedImage = ImageUtilities
                .getBufferedImageSnapshot( rasterGraphicsExportSource );

        // If necessary, correct bugs in Oracle's Image Type assignment.
        final BufferedImage correctedImage = ImageConversionUtilities
                .swapImageType( bufferedImage, fileExtension );

        // Avoid unnecessary file buffering and image tasks if the image
        // writer's validity check would reject this image and/or its format.
        if ( !ImageFormatUtilities.isImageTypeSupportedForWrite( correctedImage,
                                                                 fileExtension ) ) {
            return FileStatus.NOT_SAVED;
        }

        // Chain a BufferedOutputStream to a FileOutputStream, for better
        // performance.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final BufferedOutputStream bufferedOutputStream 
                        = new BufferedOutputStream( fileOutputStream ) ) {
            // As long as no compression or other customization is needed, it is
            // simpler and less risky to use the default raster image writer.
            ImageIO.write( correctedImage, fileExtension, bufferedOutputStream );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return FileStatus.NOT_SAVED;
        }

        return FileStatus.EXPORTED;
    }    

    // NOTE: Not all Stages support exporting a screenshot as vector graphics.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportVectorGraphics( final File file,
                                             final File tempFile, 
                                             final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "eps":
            // Vectorize the main content pane to an EPS file.
            fileStatus = exportToEps( tempFile, file, fileMode );
            break;
        case "pdf":
            // Vectorize the main content pane to a PDF file.
            fileStatus = exportToPdf( tempFile, file, fileMode );
            break;
        case "ppt":
            // Vectorize the main content pane to a PPT file.
            fileStatus = exportToPpt( tempFile, file, fileMode );
        case "svg":
            // Vectorize the main content pane to an SVG file.
            fileStatus = exportToSvg( tempFile, file, fileMode );
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }

    // NOTE: Not all Stages support curated content as rendered graphics.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportRenderedGraphics( final File file,
                                               final File tempFile, 
                                               final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "eps":
            // Vectorize the curated and styled content to an EPS file.
            fileStatus = exportToEps( tempFile, file, fileMode );
            break;
        case "pdf":
            // Vectorize the curated and styled content to a PDF file.
            fileStatus = exportToPdf( tempFile, file, fileMode );
            break;
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }

    // NOTE: Not all Stages support structured data as CAD graphics.
    // TODO: Review which of these formats is presented in the file chooser.
    default FileStatus exportCadGraphics( final File file,
                                          final File tempFile, 
                                          final FileMode fileMode ) {
        FileStatus fileStatus = FileStatus.NOT_SAVED;
        
        final String fileExtension = FilenameUtilities.getAdjustedFileExtension( file );
        switch ( fileExtension ) {
        case "dxf":
        default:
            // Take care of any extensions specific to sub-classes.
            fileStatus = fileSaveExtensions( file, tempFile, fileMode );
            break;
        }
        
        return fileStatus;
    }
 
    // NOTE: Very few classes support spreadsheet export, so the default
    //  implementation is a no-op. This avoids wasteful empty overrides.
    default FileStatus exportToXslx( final OutputStream outputStream,
                                     final String fileExtension ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: This default implementation is a no-op as the generic version
    //  would introduce a circular dependency with jfxconvertertoolkit.
    default FileStatus exportToEps( final File tempFile,
                                    final File file,
                                    final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }

    // NOTE: This default implementation is a no-op as the generic version
    //  would introduce a circular dependency with jfxconvertertoolkit.
    default FileStatus exportToPdf( final File tempFile,
                                    final File file,
                                    final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }
        
    // NOTE: This default implementation is a no-op as the generic version
    //  would introduce a circular dependency with jfxconvertertoolkit.
    default FileStatus exportToPpt( final File tempFile,
                                    final File file,
                                    final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }
    
    // NOTE: This default implementation is a no-op as the generic version
    //  would introduce a circular dependency with jfxconvertertoolkit.
    default FileStatus exportToSvg( final File tempFile,
                                    final File file,
                                    final FileMode fileMode ) {
        return FileStatus.NOT_SAVED;
    }
    
    // NOTE: Not all Stages support exporting session logs. Avoid nulls.
    default String getSessionLogFilename() {
        return "";
    }
    
    // NOTE: Implementing classes that support raster graphics export should
    //  override this method to return a local class variable. Stages can
    //  just use the class variable returned by the XStage parent class.
    default Node getRasterGraphicsExportSource() {
        return null;
    }
    
    // NOTE: Implementing classes that support vector graphics export should
    //  override this method to return a local class variable. Stages can
    //  just use the class variable returned by the XStage parent class.
    default Node getVectorGraphicsExportSource() {
        return null;
    }

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
    
    // NOTE: Implementing classes that support raster graphics export should
    //  override this method to return a local class variable. Stages can
    //  just use the class variable returned by the XStage parent class.
    default RasterGraphicsExportOptions getRasterGraphicsExportOptions() {
        return null;
    }
    
    // NOTE: Implementing classes that support vector graphics export should
    //  override this method to return a local class variable. Stages can
    //  just use the class variable returned by the XStage parent class.
    default VectorGraphicsExportOptions getVectorGraphicsExportOptions() {
        return null;
    }
    
    // NOTE: Implementing classes that support rendered graphics export should
    //  override this method to return a local class variable.
    default RenderedGraphicsExportOptions getRenderedGraphicsExportOptions() {
        return null;
    }
}
