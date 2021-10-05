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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

/**
 * {@code DialogUtilities} is a static utilities class for ensuring a reduction
 * in copy/paste code for similar functionality that should be maintained
 * consistently across all {@code Dialog} derived classes.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class DialogUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private DialogUtilities() {}

    /**
     * This is meant to be used as the layout pane background in windows
     * that don't support custom colors per user. It is designed to be a fairly
     * close match to Day Mode, adjusted for ideal contrast.
     */
    public static final Color WINDOW_BACKGROUND_COLOR = Color.GAINSBORO;

    /**
     * This is meant to be used as the layout pane background in windows
     * that don't support custom colors per user. It is designed to be a fairly
     * close match to Day Mode, adjusted for ideal contrast.
     */
    public static final Color WINDOW_FOREGROUND_COLOR = Color.BLACK;

    /**
     * The purpose of this method is to produce a consistent background style
     * for use on all dialog content panes, using a default background color and
     * no corner radii or insets (for a flush fill against the parent).
     *
     * @return A @Background object targeted to setBackground()
     */
    public static Background makeDialogBackground() {
        final Background background = makeDialogBackground( Insets.EMPTY );

        return background;
    }

    /**
     * The purpose of this method is to produce a consistent background style
     * for use on all dialog content panes, using a default background color and
     * no corner radii and user-provided insets (to avoid corner gaps).
     *
     * @param insets
     *            The {@link Insets} to use for the background
     * @return A @Background object targeted to setBackground()
     */
    public static Background makeDialogBackground( final Insets insets ) {
        final Background background = LayoutFactory.makeRegionBackground( WINDOW_BACKGROUND_COLOR,
                                                                          insets );

        return background;
    }

    /**
     * Blocks on the alert dialog.
     *
     * @param alert
     *            the {@link Alert} to show and wait for.
     * @param masthead
     *            if not null, passed into {@link Alert#setHeaderText(String)}
     * @param title
     *            passed into {@link Alert#setTitle(String)}
     * @return The response from {@link Alert#showAndWait()}
     */
    private static final Optional< ButtonType > showAlertDialog( final Alert alert,
                                                                 final String masthead,
                                                                 final String title ) {
        if ( masthead != null ) {
            alert.setHeaderText( masthead );
        }
        alert.setTitle( title );

        // Forward the show as a modal blocking call.
        final Optional< ButtonType > response = alert.showAndWait();

        // Return the user's dismissal ButtonType status to the caller.
        return response;
    }

    /**
     * Blocks while waiting for confirmation.
     *
     * @param message
     *            The message for the user to confirm
     * @param masthead
     *            The header text
     * @param title
     *            The title of the confirmation dialog
     * @param showCancel
     *            Whether the confirmation dialog shows a cancel button
     * @return the result of {@link Alert#showAndWait()}
     */
    public static final Optional< ButtonType > showConfirmationAlert( final String message,
                                                                      final String masthead,
                                                                      final String title,
                                                                      final boolean showCancel ) {
        final Optional< ButtonType > response = showConfirmationAlert( message,
                                                                       masthead,
                                                                       title,
                                                                       showCancel,
                                                                       AlertType.CONFIRMATION );
        return response;
    }

    /**
     * Blocks while waiting for confirmation.
     *
     * @param message
     *            The message for the user to confirm
     * @param masthead
     *            The header text
     * @param title
     *            The title of the confirmation dialog
     * @param showCancel
     *            Whether the confirmation dialog shows a cancel button
     * @param alertType
     *            The type of alert to indicate with the banner icon
     * @return the result of {@link Alert#showAndWait()}
     */
    public static final Optional< ButtonType > showConfirmationAlert( final String message,
                                                                      final String masthead,
                                                                      final String title,
                                                                      final boolean showCancel,
                                                                      final AlertType alertType ) {
        // Most confirmation dialogs do not need a Cancel button.
        final ArrayList< ButtonType > buttonTypes = new ArrayList<>();
        buttonTypes.add( ButtonType.YES );
        buttonTypes.add( ButtonType.NO );
        if ( showCancel ) {
            buttonTypes.add( ButtonType.CANCEL );
        }

        final ButtonType[] buttons = buttonTypes.toArray( new ButtonType[ 0 ] );

        final Alert alert = new Alert( alertType, message, buttons );
        final Optional< ButtonType > response = showAlertDialog( alert, masthead, title );

        return response;
    }

    /**
     * Creates a new error {@link Alert} and shows it, blocking until it
     * returns.
     *
     * @param message
     *            the error to display
     * @param masthead
     *            header text
     * @param title
     *            the title of the alert
     */
    public static final void showErrorAlert( final String message,
                                             final String masthead,
                                             final String title ) {
        final Alert alert = new Alert( AlertType.ERROR, message );
        showAlertDialog( alert, masthead, title );
    }

    public static final void showInformationAlert( final String message,
                                                   final String masthead,
                                                   final String title ) {
        final Alert alert = new Alert( AlertType.INFORMATION, message );
        showAlertDialog( alert, masthead, title );
    }

    public static final Optional< ButtonType > showFileExitConfirmationAlert( final File file,
                                                                              final String productName ) {
        final String message = MessageFactory.getSaveFileChangesMessage( file );
        final String masthead = MessageFactory.getFileExitMasthead();
        final String title = MessageFactory.getFileExitTitle( productName );
        final Optional< ButtonType > response = showConfirmationAlert( message,
                                                                       masthead,
                                                                       title,
                                                                       true );

        return response;
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            The message to display for File Open error alerts
     */
    public static final void showFileOpenErrorAlert( final String message ) {
        final String masthead = MessageFactory.getFileNotOpenedMasthead();
        showFileOpenErrorAlert( message, masthead );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            error message
     * @param masthead
     *            header text
     */
    public static final void showFileOpenErrorAlert( final String message, final String masthead ) {
        final String title = MessageFactory.getFileOpenErrorTitle();
        showErrorAlert( message, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            The message to display for File Read error alerts
     */
    public static final void showFileReadErrorAlert( final String message ) {
        final String title = MessageFactory.getFileReadErrorTitle();
        showErrorAlert( message, null, title );
    }

    public static final Optional< ButtonType > showFileSaveConfirmationAlert( final File file,
                                                                              final String title ) {
        final String message = MessageFactory.getSaveFileChangesMessage( file );
        final String masthead = MessageFactory.getSaveFileChangesMasthead();
        final Optional< ButtonType > response = showConfirmationAlert( message,
                                                                       masthead,
                                                                       title,
                                                                       true );

        return response;
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            The message to display for File Save error alerts
     */
    public static final void showFileSaveErrorAlert( final String message ) {
        final String masthead = MessageFactory.getFileNotSavedMasthead();
        final String title = MessageFactory.getFileSaveErrorTitle();
        showErrorAlert( message, masthead, title );
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            The message to display for File Save warning alerts
     */
    public static final void showFileSaveWarningAlert( final String message ) {
        final String masthead = MessageFactory.getFilePartiallySavedMasthead();
        final String title = MessageFactory.getFileSaveErrorTitle();
        showWarningAlert( message, masthead, title );
    }

    /**
     * Blocks while waiting for confirmation, but provides a hyperlink for
     * follow-up action, hosted in a Text Flow control.
     *
     * @param message
     *            The message for the user to confirm
     * @param masthead
     *            The header text
     * @param title
     *            The title of the confirmation dialog
     * @param textFlow
     *            The Text Flow to use for user follow-up action
     * @param alertType
     *            The type of alert to indicate with the banner icon
     */
    public static void showTextFlowAlert( final String message,
                                          final String masthead,
                                          final String title,
                                          final TextFlow textFlow,
                                          final AlertType alertType ) {
        final TextFlowAlert alert = new TextFlowAlert( alertType, message, textFlow );
        if ( masthead != null ) {
            alert.setHeaderText( masthead );
        }
        alert.setTitle( title );

        // Forward the show as a modal blocking call.
        alert.showAndWait();
    }

    /**
     * Blocks while showing an error message.
     *
     * @param message
     *            The message to display in the alert
     * @param masthead
     *            header text
     * @param title
     *            title of the alert
     */
    public static final void showWarningAlert( final String message,
                                               final String masthead,
                                               final String title ) {
        final Alert alert = new Alert( AlertType.WARNING, message );
        showAlertDialog( alert, masthead, title );
    }

}
