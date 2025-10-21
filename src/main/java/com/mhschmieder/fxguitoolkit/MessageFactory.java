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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import com.mhschmieder.jcommons.io.FileAction;
import com.mhschmieder.jcommons.io.FileMode;
import com.mhschmieder.jcommons.security.LoginType;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * {@code MessageFactory} is a factory class for methods related to general
 * messages.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class MessageFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private MessageFactory() {}

    public static final String getAcceptEulaMasthead( final String productName ) {
        final String acceptEulaMasthead = "Accept " + productName
                + " End User License Agreement?";
        return acceptEulaMasthead;
    }

    public static final String getAccountManagementPreamble() {
        return "To review your login credentials, go to";
    }

    public static final String getAutoAppendExtensionMayOverwriteMasthead() {
        return "Auto-Append of Default Extension May Overwrite Existing File";
    }

    public static final String getBadUrlMasthead() {
        return "Bad URL or URI deviation from RFC 2396";
    }

    public static final String getBrowserLaunchErrorMasthead() {
        return "Bad URL or Unsupported Browser";
    }

    public static final String getBrowserLaunchErrorMessage() {
        return "Unable to launch default browser. See Session Log for details.";
    }

    public static final String getBrowserLaunchErrorTitle() {
        return "Browser Launch Error";
    }

    public static final String getCheckForUpdatesPreamble() {
        return "Check for";
    }

    public static final String getClientServerProtocolErrorTitle() {
        return "Client-Server Protocol Error";
    }

    public static final String getConfirmCoordinatesMasthead() {
        return "Please Confirm Coordinates";
    }

    public static final String getContinueWithFileSaveMessage() {
        return "Continue with File Save?";
    }

    public static final String getEulaBanner( final String productName ) {
        final String eulaBanner = productName + " End User License Agreement";
        return eulaBanner;
    }
    
    public static final String getFileAutoSaveTitle() {
        return "File Auto-Save";
    }

    public static final String getFileCloseMasthead() {
        return "Confirm Save File Changes and Close Window";
    }

    public static final String getFileErrorMessage( final String errorMessageBody,
                                                    final File file ) {
        try {
            final Path path = file.toPath();
            final String fileError = "File: " + '"' + path.toString() + '"'
                    + " " + errorMessageBody;
            return fileError;
        }
        catch ( final InvalidPathException ipe ) {
            ipe.printStackTrace();
            return errorMessageBody;
        }
    }

    public static final String getFileExitMasthead() {
        return "Confirm Save File Changes and Exit Application";
    }

    public static final String getFileExitMessage( final File file ) {
        final String promptMessageBody = " has been modified."
                + " Save changes and exit?";
        final String saveFileChangesMessage = getFilePromptMessage( promptMessageBody, file );
        return saveFileChangesMessage;
    }

    public static final String getFileExitTitle( final String productName ) {
        return "Exit " + productName;
    }

    public static final String getFileImportErrorMessage( final FileMode fileMode,
                                                          final File file ) {
        final String errorMessageBody = FileMode.IMPORT_CAD.equals( fileMode )
            ? " could not load graphics data due to invalid file content."
            : " was opened for project data, but could not load graphics data due to invalid file content.";
        final String graphicsFileNotOpenedMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return graphicsFileNotOpenedMessage;
    }

    public static final String getFileImportOutOfMemoryMessage( final FileMode fileMode,
                                                                final File file ) {
        final String errorMessageBody = FileMode.IMPORT_CAD.equals( fileMode )
            ? " could not load graphics data as it requires more Java heap space memory than is available."
            : " was opened for project data, but could not load graphics data as it requires"
                    + " more Java heap space memory than is available.";
        final String graphicsImportOutOfMemoryMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return graphicsImportOutOfMemoryMessage;
    }

    public static final String getFileNameConflictTitle() {
        return "File Name Conflict";
    }

    public static final String getFileNewerThanClientMessage() {
        return "Selected file contains new parameters not supported by this client."
                + "\nPlease upgrade to the latest client and try again.";
    }

    public static final String getFileNotLoadedMessage( final File file ) {
        final String errorMessageBody = " could not be loaded.";
        return getFileErrorMessage( errorMessageBody, file );
    }

    public static final String getFileNotOpenedMasthead() {
        return "File Not Opened";
    }

    public static final String getFileNotOpenedMasthead( final FileMode fileMode ) {
        final String fileNotOpenedMasthead = FileMode.IMPORT_CAD.equals( fileMode )
            ? "File Partially Opened"
            : getFileNotOpenedMasthead();
        return fileNotOpenedMasthead;
    }
    
    public static final String getFileNotOpenedMessage( final File file ) {
        final String errorMessageBody = " could not be opened (in full or in part).";
        final String fileNotOpenedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotOpenedMessage;
    }

    public static final String getFileNotSavedMasthead() {
        return "File Not Saved";
    }

    public static final String getFileNotSavedMessage( final File file ) {
        final String errorMessageBody = " could not be saved.";
        final String fileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotSavedMessage;
    }

    public static final String getFileReadErrorMessage( final FileMode fileMode, 
                                                        final File file ) {
        final String errorMessageBody = FileMode.IMPORT_CAD.equals( fileMode )
            ? " could not load file contents due to parsing errors."
            : " could not fully load file contents due to parsing errors."
                    + " File content may be wrong data type for selected action.";
        final String graphicsFileNotOpenedMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return graphicsFileNotOpenedMessage;
    }


    public static final String getFileOpenErrorTitle() {
        return "File Open Error";
    }

    public static final String getFileOpenOptionsTitle() {
        return "File Open Options";
    }

    public static final String getFilePartiallySavedMasthead() {
        return "File Partially Saved";
    }

    public static final String getFilePromptMessage( final String promptMessageBody,
                                                     final File file ) {
        final String fileName = file.getName();
        final String fileError = "File: " + '"' + fileName + '"' + " "
                + promptMessageBody;
        return fileError;
    }

    public static final String getFileReadErrorMessage( final File file ) {
        final String errorMessageBody = " could not open."
                + " Check the Session Log for possible run-time exceptions.";
        return getFileErrorMessage( errorMessageBody, file );
    }
    
    public static final String getFileReadErrorTitle() {
        return "File Read Error";
    }

    /**
     * Returns the descriptive clause to insert in "Confirm File Changes" alert
     * boxes for the action that follows the File Save.
     * 
     * @param fileAction The file action type that determines the post-save clause
     * @return the descriptive clause to insert in "Confirm File Changes" alert
     */
    public static final String getFileSavePostActionClause( final FileAction fileAction ) {
        String actionClause = "";
        switch ( fileAction ) {
        case NEW:
            actionClause = "creating a new one";
            break;
        case OPEN:
            actionClause = "opening another one";
            break;
        case RUN_BATCH:
            actionClause = "running a batch directory";
            break;
        case CLOSE:
            actionClause = "closing the window";
            break;
        case EXIT:
            actionClause = "exiting the application";
            break;
            //$CASES-OMITTED$
        default:
            break;       
        }
        
        return actionClause;
    }

    public static final String getFileSaveErrorTitle() {
        return "File Save Error";
    }

    public static final String getFileSaveOptionsTitle() {
        return "File Save Options";
    }

    public static final String getFileWriteErrorMessage( final File file ) {
        final String errorMessageBody = " could not save."
                + " Check the Session Log for possible run-time exceptions.";
        return getFileErrorMessage( errorMessageBody, file );
    }

    public static final String getGeneratedReportWriteErrorMessage( final File file ) {
        final String errorMessageBody =
                                      " could not save generated report due to write access denied."
                                              + " Please see Session Log for details (if the JRE forwarded exceptions).";
        final String reportNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return reportNotSavedMessage;
    }

    public static final String getGraphicsFileReadErrorMessage( final FileMode fileMode,
                                                                final File file ) {
        final String errorMessageBody = FileMode.IMPORT_CAD.equals( fileMode )
            ? " could not load graphics data due to parsing errors."
            : " was opened for project data, but could not load graphics data due to parsing errors.";
        final String graphicsFileNotOpenedMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return graphicsFileNotOpenedMessage;
    }

    public static final String getGraphicsFileWriteErrorMessage( final File file ) {
        final String errorMessageBody = " could not save graphics data due to parsing errors."
                + " Please Zoom to Extents and try again.";
        final String graphicsFileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return graphicsFileNotSavedMessage;
    }

    public static final String getGraphicsImportStatusBanner() {
        return "Graphics Import Status";
    }

    public static final String getIncompatibleClientMasthead( final String productName ) {
        return "Incompatible Client";
    }

    public static final String getIncompatibleClientMessage( final String productName ) {
        final String incompatibleClientMessage = "Your "
                + productName + " client is out of date and incompatible with the server.";
        return incompatibleClientMessage;
    }

    public static final String getInvalidUserAccountMasthead() {
        return "Invalid User Account or Login Credentials";
    }

    public static final String getLoginCredentialsMasthead( final LoginType loginType,
                                                            final String loginTarget ) {
        final String loginCredentialsMasthead = "Please Log In to the "
                + loginTarget + " " + loginType.label();
        return loginCredentialsMasthead;
    }

    public static final String getLoginCredentialsTitle( final LoginType loginType ) {
        final String loginCredentialsTitle =
                                           loginType.label() + " Login Credentials";
        return loginCredentialsTitle;
    }

    public static final String getLoginErrorMasthead() {
        return "Logins Disabled for This Session";
    }

    public static final String getLoginErrorTitle() {
        return "Login Error";
    }

    public static final String getMissingGraphicsSourceOnSaveMessage( final File file ) {
        final String errorMessageBody =
                                      " was partially saved as the graphics import source has moved,"
                                              + " is missing from file system, or is corrupted.";
        final String filePartiallySavedMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return filePartiallySavedMessage;
    }

    public static final String getNoPrinterAvailableMessage() {
        return "No Printer Available to Application";
    }

    public static final String getNoSuchFileMessage( final File file ) {
        final String errorMessageBody = " does not exist.";
        final String noSuchFileMessage = getFileErrorMessage( errorMessageBody, file );
        return noSuchFileMessage;
    }

    public static final String getNoTempFileMessage( final File file ) {
        final String errorMessageBody = " could not be saved as an intermediary temp file"
                + " cannot be created on the file system.";
        final String fileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotSavedMessage;
    }

    public static final String getNullPrintJobMessage() {
        return "Nothing to print, or Printer not set up correctly";
    }

    public static final String getObjectPropertyEditorApplyToolTip() {
        return "Applies current parameters and overwites related project settings";
    }

    public static final String getObjectPropertyEditorInsertToolTip() {
        return "Inserts at selected location using current parameters and overwites related project settings";
    }

    public static final String getPasteCommandRejectedTitle() {
        return "Paste Command Rejected";
    }

    public static final String getPasteReferencePointTitle() {
        return "Paste Reference Point";
    }

    public static final String getPrinterBlockedMessage() {
        return "Cannot print due to Printer blocked by other Print Job";
    }

    public static final String getPrintJobCanceledMessage() {
        return "Print Job Canceled";
    }

    public static final String getPrintJobNotStartedMessage() {
        return "Unknown internal failure; Print Job not started";
    }

    public static final String getPrintServicesProblemMasthead() {
        return "Problem with Print Services";
    }

    public static final String getProjectReportHelpBanner() {
        return "Project Report Help";
    }

    public static final String getRasterGraphicsExportOptionsMasthead() {
        return "Raster Graphics Export Options";
    }

    public static final String getReadProtectedFileMessage( final File file ) {
        final String errorMessageBody = " is read-protected.";
        final String readProtectedFileMessage = getFileErrorMessage( errorMessageBody, file );
        return readProtectedFileMessage;
    }

    public static final String getSaveFileChangesMasthead() {
        return "Confirm Save File Changes";
    }

    public static final String getSaveFileChangesMessage( final File file ) {
        final String promptMessageBody = " has been modified."
                + " Save changes?";
        final String saveFileChangesMessage = getFilePromptMessage( promptMessageBody, file );
        return saveFileChangesMessage;
    }

    public static final String getSecurityManagedFileMessage( final File file,
                                                              final String fileMode ) {
        final String errorMessageBody = "is denied " + fileMode
                + " access by the Security Manager.";
        final String securityManagedFileMessage = getFileErrorMessage( errorMessageBody, file );
        return securityManagedFileMessage;
    }

    public static final String getServerRequestFileWriteErrorMessage( final File file ) {
        final String errorMessageBody = " could not save server request due to write access denied."
                + " Please see Session Log for details (if the JRE forwarded exceptions).";
        final String serverRequestFileNotSavedMessage =
                                                      getFileErrorMessage( errorMessageBody, file );
        return serverRequestFileNotSavedMessage;
    }

    public static final String getServerResponseFileWriteErrorMessage( final File file ) {
        final String errorMessageBody =
                                      " could not save server response due to write access denied."
                                              + " Please see Session Log for details (if the JRE forwarded exceptions).";
        final String serverResponseFileNotSavedMessage = getFileErrorMessage( errorMessageBody,
                                                                              file );
        return serverResponseFileNotSavedMessage;
    }

    public static final String getUserAuthorizationErrorTitle() {
        return "User Authorization Error";
    }

    public static final String getVectorGraphicsExportOptionsMasthead() {
        return "Vector Graphics Export Options";
    }

    public static final String getWriteProtectedFileMessage( final File file ) {
        final String errorMessageBody = " is write-protected.";
        final String writeProtectedFileMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return writeProtectedFileMessage;
    }

}
