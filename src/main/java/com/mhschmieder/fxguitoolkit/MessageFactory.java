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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import com.mhschmieder.commonstoolkit.security.LoginType;

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
        final String acceptEulaMasthead = "Accept " + productName //$NON-NLS-1$
                + " End User License Agreement?"; //$NON-NLS-1$
        return acceptEulaMasthead;
    }

    public static final String getAccountManagementPreamble() {
        return "To review your login credentials, go to"; //$NON-NLS-1$ ;
    }

    public static final String getAutoAppendExtensionMayOverwriteMasthead() {
        return "Auto-Append of Default Extension May Overwrite Existing File"; //$NON-NLS-1$
    }

    public static final String getBadUrlMasthead() {
        return "Bad URL or URI deviation from RFC 2396"; //$NON-NLS-1$
    }

    public static final String getBrowserLaunchErrorMasthead() {
        return "Bad URL or Unsupported Browser"; //$NON-NLS-1$
    }

    public static final String getBrowserLaunchErrorMessage() {
        return "Unable to launch default browser. See Session Log for details."; //$NON-NLS-1$
    }

    public static final String getBrowserLaunchErrorTitle() {
        return "Browser Launch Error"; //$NON-NLS-1$
    }

    public static final String getCheckForUpdatesPreamble() {
        return "Check for"; //$NON-NLS-1$
    }

    public static final String getClientServerProtocolErrorTitle() {
        return "Client-Server Protocol Error"; //$NON-NLS-1$
    }

    public static final String getConfirmCoordinatesMasthead() {
        return "Please Confirm Coordinates"; //$NON-NLS-1$
    }

    public static final String getContinueWithFileSaveMessage() {
        return "Continue with File Save?"; //$NON-NLS-1$
    }

    public static final String getEulaBanner( final String productName ) {
        final String eulaBanner = productName + " End User License Agreement"; //$NON-NLS-1$
        return eulaBanner;
    }

    public static final String getFileAutoSaveTitle() {
        return "File Auto-Save"; //$NON-NLS-1$
    }

    public static final String getFileErrorMessage( final String errorMessageBody,
                                                    final File file ) {
        try {
            final Path path = file.toPath();
            final String fileError = "File: " + '"' + path.toString() + '"' //$NON-NLS-1$
                    + " " + errorMessageBody; //$NON-NLS-1$
            return fileError;
        }
        catch ( final InvalidPathException ipe ) {
            ipe.printStackTrace();
            return errorMessageBody;
        }
    }

    public static final String getFileExitMasthead() {
        return "Confirm Save File Changes and Exit"; //$NON-NLS-1$
    }

    public static final String getFileExitMessage( final File file ) {
        final String promptMessageBody = " has been modified." //$NON-NLS-1$
                + " Save changes and exit?"; //$NON-NLS-1$
        final String saveFileChangesMessage = getFilePromptMessage( promptMessageBody, file );
        return saveFileChangesMessage;
    }

    public static final String getFileExitTitle( final String productName ) {
        return "Exit " + productName; //$NON-NLS-1$
    }

    public static final String getFileNameConflictTitle() {
        return "File Name Conflict"; //$NON-NLS-1$
    }

    public static final String getFileNewerThanClientMessage() {
        return "Selected file contains new parameters not supported by this client. Please upgrade to the latest client and try again."; //$NON-NLS-1$
    }

    public static final String getFileNotLoadedMessage( final File file ) {
        final String errorMessageBody = " could not be loaded."; //$NON-NLS-1$
        return getFileErrorMessage( errorMessageBody, file );
    }

    public static final String getFileNotOpenedMasthead() {
        return "File Not Opened"; //$NON-NLS-1$
    }

    public static final String getFileNotOpenedMessage( final File file ) {
        final String errorMessageBody = " could not be opened."; //$NON-NLS-1$
        final String fileNotOpenedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotOpenedMessage;
    }

    public static final String getFileNotSavedMasthead() {
        return "File Will Not Be Saved"; //$NON-NLS-1$
    }

    public static final String getFileNotSavedMessage( final File file ) {
        final String errorMessageBody = " could not be saved."; //$NON-NLS-1$
        final String fileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotSavedMessage;
    }

    public static final String getFileOpenErrorTitle() {
        return "File Open Error"; //$NON-NLS-1$
    }

    public static final String getFileOpenOptionsTitle() {
        return "File Open Options"; //$NON-NLS-1$
    }

    public static final String getFilePartiallySavedMasthead() {
        return "File Partially Saved"; //$NON-NLS-1$
    }

    public static final String getFilePromptMessage( final String promptMessageBody,
                                                     final File file ) {
        final String fileName = file.getName();
        final String fileError = "File: " + '"' + fileName + '"' + " " //$NON-NLS-1$ //$NON-NLS-2$
                + promptMessageBody;
        return fileError;
    }

    public static final String getFileReadErrorTitle() {
        return "File Read Error"; //$NON-NLS-1$
    }

    public static final String getFileSaveErrorTitle() {
        return "File Save Error"; //$NON-NLS-1$
    }

    public static final String getFileSaveOptionsTitle() {
        return "File Save Options"; //$NON-NLS-1$
    }

    public static final String getIncompatibleClientMasthead( final String productName ) {
        return "Incompatible Client"; //$NON-NLS-1$
    }

    public static final String getIncompatibleClientMessage( final String productName ) {
        final String incompatibleClientMessage = "Your " //$NON-NLS-1$
                + productName + " client is out of date and incompatible with the server."; //$NON-NLS-1$
        return incompatibleClientMessage;
    }

    public static final String getInvalidUserAccountMasthead() {
        return "Invalid User Account or Login Credentials"; //$NON-NLS-1$
    }

    public static final String getLoginCredentialsMasthead( final LoginType loginType,
                                                            final String loginTarget ) {
        final String loginCredentialsMasthead = "Please Log In to the " //$NON-NLS-1$
                + loginTarget + " " + loginType.toPresentationString(); //$NON-NLS-1$
        return loginCredentialsMasthead;
    }

    public static final String getLoginCredentialsTitle( final LoginType loginType ) {
        final String loginCredentialsTitle =
                                           loginType.toPresentationString() + " Login Credentials"; //$NON-NLS-1$
        return loginCredentialsTitle;
    }

    public static final String getLoginErrorMasthead() {
        return "Logins Disabled for This Session"; //$NON-NLS-1$
    }

    public static final String getLoginErrorTitle() {
        return "Login Error"; //$NON-NLS-1$
    }

    public static final String getNoPrinterAvailableMessage() {
        return "No Printer Available to Application"; //$NON-NLS-1$
    }

    public static final String getNoSuchFileMessage( final File file ) {
        final String errorMessageBody = " does not exist."; //$NON-NLS-1$
        final String noSuchFileMessage = getFileErrorMessage( errorMessageBody, file );
        return noSuchFileMessage;
    }

    public static final String getNoTempFileMessage( final File file ) {
        final String errorMessageBody = " could not be saved as an intermediary temp file" //$NON-NLS-1$
                + " cannot be created on the file system."; //$NON-NLS-1$
        final String fileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return fileNotSavedMessage;
    }

    public static final String getNullPrintJobMessage() {
        return "Nothing to print, or Printer not set up correctly"; //$NON-NLS-1$
    }

    public static final String getPasteCommandRejectedTitle() {
        return "Paste Command Rejected"; //$NON-NLS-1$
    }

    public static final String getPasteReferencePointTitle() {
        return "Paste Reference Point"; //$NON-NLS-1$
    }

    public static final String getPrinterBlockedMessage() {
        return "Cannot print due to Printer blocked by other Print Job"; //$NON-NLS-1$
    }

    public static final String getPrintJobCanceledMessage() {
        return "Print Job Canceled"; //$NON-NLS-1$
    }

    public static final String getPrintJobNotStartedMessage() {
        return "Unknown internal failure; Print Job not started"; //$NON-NLS-1$
    }

    public static final String getPrintServicesProblemMasthead() {
        return "Problem with Print Services"; //$NON-NLS-1$
    }

    public static final String getProjectFileReadErrorMessage( final File file ) {
        final String errorMessageBody = " could not open." //$NON-NLS-1$
                + " Check the Session Log for possible run-time exceptions."; //$NON-NLS-1$
        final String projectFileNotOpenedMessage = getFileErrorMessage( errorMessageBody, file );
        return projectFileNotOpenedMessage;
    }

    public static final String getProjectFileWriteErrorMessage( final File file ) {
        final String errorMessageBody = " could not save." //$NON-NLS-1$
                + " Check the Session Log for possible run-time exceptions."; //$NON-NLS-1$
        final String projectFileNotSavedMessage = getFileErrorMessage( errorMessageBody, file );
        return projectFileNotSavedMessage;
    }

    public static final String getRasterGraphicsExportOptionsMasthead() {
        return "Raster Graphics Export Options"; //$NON-NLS-1$
    }

    public static final String getReadProtectedFileMessage( final File file ) {
        final String errorMessageBody = " is read-protected."; //$NON-NLS-1$
        final String readProtectedFileMessage = getFileErrorMessage( errorMessageBody, file );
        return readProtectedFileMessage;
    }

    public static final String getSaveFileChangesMasthead() {
        return "Confirm Save File Changes"; //$NON-NLS-1$
    }

    public static final String getSaveFileChangesMessage( final File file ) {
        final String promptMessageBody = " has been modified." //$NON-NLS-1$
                + " Save changes?"; //$NON-NLS-1$
        final String saveFileChangesMessage = getFilePromptMessage( promptMessageBody, file );
        return saveFileChangesMessage;
    }

    public static final String getSecurityManagedFileMessage( final File file,
                                                              final String fileMode ) {
        final String errorMessageBody = "is denied " + fileMode //$NON-NLS-1$
                + " access by the Security Manager."; //$NON-NLS-1$
        final String securityManagedFileMessage = getFileErrorMessage( errorMessageBody, file );
        return securityManagedFileMessage;
    }

    public static final String getServerRequestFileWriteErrorMessage( final File file ) {
        final String errorMessageBody = " could not save server request due to write access denied." //$NON-NLS-1$
                + " Please see Session Log for details (if the JRE forwarded exceptions)."; //$NON-NLS-1$
        final String serverRequestFileNotSavedMessage =
                                                      getFileErrorMessage( errorMessageBody, file );
        return serverRequestFileNotSavedMessage;
    }

    public static final String getServerResponseFileWriteErrorMessage( final File file ) {
        final String errorMessageBody =
                                      " could not save server response due to write access denied." //$NON-NLS-1$
                                              + " Please see Session Log for details (if the JRE forwarded exceptions)."; //$NON-NLS-1$
        final String serverResponseFileNotSavedMessage = getFileErrorMessage( errorMessageBody,
                                                                              file );
        return serverResponseFileNotSavedMessage;
    }

    public static final String getUserAuthorizationErrorTitle() {
        return "User Authorization Error"; //$NON-NLS-1$
    }

    public static final String getVectorGraphicsExportOptionsMasthead() {
        return "Vector Graphics Export Options"; //$NON-NLS-1$
    }

    public static final String getWriteProtectedFileMessage( final File file ) {
        final String errorMessageBody = " is write-protected."; //$NON-NLS-1$
        final String writeProtectedFileMessage = MessageFactory
                .getFileErrorMessage( errorMessageBody, file );
        return writeProtectedFileMessage;
    }

}
