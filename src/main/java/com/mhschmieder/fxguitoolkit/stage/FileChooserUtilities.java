/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mhschmieder.commonstoolkit.text.StringUtilities;
import com.mhschmieder.fxguitoolkit.MessageFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public final class FileChooserUtilities {

    // NOTE: The constructor is disabled, as this is a static utilities class.
    private FileChooserUtilities() {}

    // Correct the extension of a given filename to use a default filter.
    public static String correctFileExtension( final String filename,
                                               final ExtensionFilter extensionFilter ) {
        // NOTE: We can't rename as the originally specified file may not
        // exist, or may be a directory and not a file.
        final List< String > defaultExtensions = extensionFilter.getExtensions();
        final String defaultExtension = StringUtilities.replace( defaultExtensions.get( 0 ),
                                                                 "*.", //$NON-NLS-1$
                                                                 "." ); //$NON-NLS-1$
        final String correctedFilename = filename + defaultExtension;
        return correctedFilename;
    }

    // Get a File Chooser with fully initialized properties.
    public static FileChooser getFileChooser( final String title,
                                              final File initialDirectory,
                                              final List< ExtensionFilter > extensionFilterAdditions,
                                              final ExtensionFilter defaultExtensionFilter ) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( title );
        try {
            if ( ( initialDirectory != null ) && Files.isDirectory( initialDirectory.toPath() ) ) {
                fileChooser.setInitialDirectory( initialDirectory );
            }
        }
        catch ( final SecurityException | InvalidPathException | NullPointerException e ) {
            e.printStackTrace();
        }

        if ( extensionFilterAdditions != null ) {
            final ObservableList< ExtensionFilter > extensionFilters = fileChooser
                    .getExtensionFilters();
            for ( final ExtensionFilter extensionFilter : extensionFilterAdditions ) {
                extensionFilters.add( extensionFilter );
            }

            if ( ( defaultExtensionFilter != null )
                    && extensionFilterAdditions.contains( defaultExtensionFilter ) ) {
                fileChooser.setSelectedExtensionFilter( defaultExtensionFilter );
            }
            else {
                fileChooser.setSelectedExtensionFilter( extensionFilterAdditions.get( 0 ) );
            }
        }

        return fileChooser;
    }

    /**
     * Get a file for a "File Open" action. Wraps
     * {@link FileChooser#showOpenDialog(javafx.stage.Window) showOpenDialog}.
     *
     * @param title
     *            the title to use for the file chooser
     * @param initialDirectory
     *            the initial default directory for the user
     * @param extensionFilterAdditions
     *            additions to the general file extensions
     * @param defaultExtensionFilter
     *            the default file extension filter to use
     * @param parent
     *            If an invalid or null parent is provided, the dialog will not
     *            be modal.
     * @return a file corresponding to the one selected in the chooser
     */
    public static File getFileForOpen( final String title,
                                       final File initialDirectory,
                                       final List< ExtensionFilter > extensionFilterAdditions,
                                       final ExtensionFilter defaultExtensionFilter,
                                       final Window parent ) {
        // Get a file chooser with fully initialized properties.
        final FileChooser fileChooser = getFileChooser( title,
                                                        initialDirectory,
                                                        extensionFilterAdditions,
                                                        defaultExtensionFilter );

        // Throw up a modal file chooser dialog for the "Open" filename.
        final File file = fileChooser.showOpenDialog( parent );

        // Return the file, even if none was chosen.
        return file;
    }

    // Get a file for a "File Save As" action.
    public static File getFileForSave( final String title,
                                       final File initialDirectory,
                                       final List< ExtensionFilter > extensionFilterAdditions,
                                       final ExtensionFilter defaultExtensionFilter,
                                       final File defaultFile,
                                       final Window parent ) {
        // Get a file chooser with fully initialized properties.
        final FileChooser fileChooser = getFileChooser( title,
                                                        initialDirectory,
                                                        extensionFilterAdditions,
                                                        defaultExtensionFilter );

        // It's OK for there to not be a valid default file for save.
        if ( defaultFile != null ) {
            fileChooser.setInitialFileName( defaultFile.getName() );
        }

        // Throw up a modal file chooser dialog for the "Save As" filename.
        final File file = fileChooser.showSaveDialog( parent );
        if ( file == null ) {
            return null;
        }

        // Return the name-corrected file, even if none was chosen.
        final List< ExtensionFilter > extensionFilters = fileChooser.getExtensionFilters();
        final ExtensionFilter selectedExtensionFilter = fileChooser.getSelectedExtensionFilter();
        final File nameCorrectedFile = getNameCorrectedFile( file,
                                                             extensionFilters,
                                                             selectedExtensionFilter );
        return nameCorrectedFile;
    }

    /**
     * Get a file (or list of files) for a "File Open" action. Wraps
     * {@link FileChooser#showOpenMultipleDialog(javafx.stage.Window)
     * showOpenMultipleDialog}.
     *
     * @param title
     *            the title to use for the file chooser
     * @param initialDirectory
     *            the initial default directory for the user
     * @param extensionFilterAdditions
     *            additions to the general file extensions
     * @param defaultExtensionFilter
     *            the default file extension filter to use
     * @param parent
     *            If an invalid or null parent is provided, the dialog will not
     *            be modal.
     * @return a file corresponding to the one selected in the chooser
     */
    public static List< File > getFilesForOpen( final String title,
                                                final File initialDirectory,
                                                final List< ExtensionFilter > extensionFilterAdditions,
                                                final ExtensionFilter defaultExtensionFilter,
                                                final Window parent ) {
        // Get a file chooser with fully initialized properties.
        final FileChooser fileChooser = getFileChooser( title,
                                                        initialDirectory,
                                                        extensionFilterAdditions,
                                                        defaultExtensionFilter );

        // Throw up a modal file chooser dialog for the "Open" filename.
        final List< File > files = fileChooser.showOpenMultipleDialog( parent );

        // Return the file(s), even if none was chosen.
        return files;
    }

    // Conditionally rename a file (such as when it is missing an extension).
    public static File getNameCorrectedFile( final File file,
                                             final List< ExtensionFilter > extensionFilters,
                                             final ExtensionFilter selectedExtensionFilter ) {
        try {
            // Loop through all installed extension filters to see if any are
            // present on this file as typed into the file chooser.
            final String filename = file.getCanonicalPath();
            final boolean extensionFound = verifyFileExtension( filename, extensionFilters );

            // If there is a valid extension, just return the file as is.
            if ( extensionFound ) {
                return file;
            }

            // Append the selected extension, if no acceptable extension is
            // present on the supplied filename. The default is the first
            // extension in the first list installed on the file chooser.
            // NOTE: We can't rename the file as the originally specified file
            // may not exist, or may be a directory and not a file.
            final String correctedFilename = correctFileExtension( filename,
                                                                   selectedExtensionFilter );

            // Check for potential conflict with existing file after name
            // change, and confirm with user before overwriting.
            // NOTE: This doesn't affect the Mac, where the native file chooser
            // pre-pends a valid file extension and also checks for conflicts.
            // TODO: Tag a revision number and/or product version and/or
            // date/time and do an auto-save without confirmation but possibly
            // with an informational alert instead?
            final Path nameCorrectedPath = Paths.get( correctedFilename.toString() );
            if ( Files.isRegularFile( nameCorrectedPath, LinkOption.NOFOLLOW_LINKS ) ) {
                // Throw up an alert about possible overwrite.
                final String message = MessageFactory.getContinueWithFileSaveMessage();
                final String masthead = MessageFactory.getAutoAppendExtensionMayOverwriteMasthead();
                final String title = MessageFactory.getFileNameConflictTitle();
                final Optional< ButtonType > response =
                                                      com.mhschmieder.fxguitoolkit.dialog.DialogUtilities
                                                              .showConfirmationAlert( message,
                                                                                      masthead,
                                                                                      title,
                                                                                      false );

                // Unless the user dismissed due to possible overwrite,
                // construct the file object from the corrected path.
                if ( !ButtonType.NO.equals( response.get() ) ) {
                    final File nameCorrectedFile = nameCorrectedPath.toFile();
                    return nameCorrectedFile;
                }
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    // Get a temporary file for interim write operations.
    public static File getTempFile( final File file, final String filePrefix ) {
        File tempFile = null;

        // Set the default error message and error message title for general
        // non-specific file save problems.
        String errorMessage = MessageFactory.getFileNotSavedMessage( file );

        try {
            // Use the temporary file facility to generate a unique filename in
            // the system's temp directory for writing the new file. This
            // protects against overwriting the original file (if it exists)
            // until the file save is complete and known to be error-free.
            tempFile = File.createTempFile( filePrefix, null ); // use the
                                                                // default
                                                                // temporary
                                                                // file suffix
            tempFile.deleteOnExit();
        }
        catch ( final IllegalArgumentException iae ) {
            // NOTE: We guarantee this error cannot occur by providing a prefix
            // that is greater than three characters (such as an abbreviated or
            // full application name), but have to catch this exception in order
            // to compile with no errors.
            iae.printStackTrace();
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();

            // Throw up a message dialog that the security manager denies write
            // access for the selected file.
            errorMessage = MessageFactory.getSecurityManagedFileMessage( file, "write" ); //$NON-NLS-1$
        }
        catch ( final IOException ioe ) {
            ioe.printStackTrace();

            // Throw up a message dialog that the security manager denies write
            // access for the selected file.
            errorMessage = MessageFactory.getNoTempFileMessage( file );
        }
        finally {
            if ( tempFile == null ) {
                // Alert the user that a file save error occurred.
                com.mhschmieder.fxguitoolkit.dialog.DialogUtilities
                        .showFileSaveErrorAlert( errorMessage );
            }
        }

        return tempFile;
    }

    // Verify the extension of a given filename for a set of valid filters.
    // NOTE: This method must execute on the JavaFX event thread.
    public static boolean verifyFileExtension( final String filename,
                                               final List< ExtensionFilter > extensionFilters ) {
        // Loop through all installed extension filters to see if any are
        // present on this filename as typed into the file chooser.
        boolean extensionFound = false;
        final Iterator< ExtensionFilter > extensionFiltersIterator = extensionFilters.iterator();
        while ( extensionFiltersIterator.hasNext() ) {
            final ExtensionFilter extensionFilter = extensionFiltersIterator.next();
            final List< String > extensionsList = extensionFilter.getExtensions();
            final Iterator< String > extensionsIterator = extensionsList.iterator();
            while ( extensionsIterator.hasNext() ) {
                final String extension = StringUtilities.replace( extensionsIterator.next(),
                                                                  "*.", //$NON-NLS-1$
                                                                  "." ); //$NON-NLS-1$
                if ( filename.endsWith( extension ) ) {
                    extensionFound = true;
                    break;
                }
            }

            if ( extensionFound ) {
                break;
            }
        }

        return extensionFound;
    }

    // Verify a selected file can be opened for read. Returns an error message.
    public static String verifyForRead( final File file ) {
        // Verify that the chosen file exists and is read-enabled.
        try {
            // Verify that the chosen file exists.
            final Path path = file.toPath();
            if ( !Files.isRegularFile( path, LinkOption.NOFOLLOW_LINKS ) ) {
                // Alert the user that the selected file does not exist.
                return MessageFactory.getNoSuchFileMessage( file );
            }

            // Verify that the chosen file is read-enabled.
            if ( !Files.isReadable( path ) ) {
                // Alert the user that the selected file is not readable.
                return MessageFactory.getReadProtectedFileMessage( file );
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();

            // Alert the user that the security manager denies read access for
            // the selected file.
            return MessageFactory.getSecurityManagedFileMessage( file, "read" ); //$NON-NLS-1$
        }

        // No more errors to detect at this point.
        return null;
    }

    // Verify a selected file can be opened for write. Returns an error message.
    public static String verifyForWrite( final File file ) {
        // Verify that the chosen file exists and is write-enabled.
        try {
            // Verify that the chosen file exists.
            final Path path = file.toPath();
            if ( !Files.isRegularFile( path, LinkOption.NOFOLLOW_LINKS ) ) {
                // If the selected file doesn't exist, we assume it is a new
                // file and can be written.
                return null;
            }

            // Verify that the chosen file is write-enabled.
            if ( !Files.isWritable( path ) ) {
                // Alert the user that the selected file is not writable.
                return MessageFactory.getWriteProtectedFileMessage( file );
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();

            // Alert the user that the security manager denies write access for
            // the selected file.
            return MessageFactory.getSecurityManagedFileMessage( file, "write" ); //$NON-NLS-1$
        }

        // No more errors to detect at this point.
        return null;
    }

}
