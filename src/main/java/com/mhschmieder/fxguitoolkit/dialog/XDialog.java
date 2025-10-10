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
package com.mhschmieder.fxguitoolkit.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Background;

import java.util.Optional;

/**
 * {@code XDialog} is an abstract extension of the standard {@link Dialog} that
 * establishes a contract for model/view syncing, to be implemented downstream.
 * <p>
 * This is a placeholder implementation, as the full version needs my Commons
 * Library to be published first, for the SystemTytpe enumeration.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public abstract class XDialog extends Dialog< ButtonType > {

    /**
     * Cache the System Type, for translucency handling.
     */
    // public SystemType systemType;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Default constructor. This is the preferred constructor for this class.
     * <p>
     * Creates a new {@code XDialog} instance.
     *
     * @param title
     *            The title bar text to use for the Dialog
     * @param headerText
     *            The Header Text to use as a simplified description
     *
     * @since 1.0
     */
    public XDialog( final String title,
                    // final String headerText,
                    final String headerText ) {
        // final SystemType systemTypeCandidate ) {
        // Always call the superclass constructor first!
        super();

        // systemType = systemTypeCandidate;

        try {
            initDialog( title, headerText );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * Initializes this dialog in an encapsulated way that protects all
     * constructors from run-time exceptions that might prevent instantiation.
     * <p>
     * The method is declared final, as any derived classes should avoid
     * unwanted side effects and simply write their own GUI initialization
     * method that adds any extended behaviour or components to the layout.
     *
     * @param title
     *            The title bar text to use for the Dialog
     * @param headerText
     *            The Header Text to use as a simplified description
     *
     * @since 1.0
     */
    private final void initDialog( final String title, final String headerText ) {
        setResizable( false );

        setTitle( title );
        setHeaderText( headerText );

        final DialogPane dialogPane = getDialogPane();
        final Background background = DialogUtilities.makeDialogBackground();
        dialogPane.setBackground( background );

        // Make the Dialog just translucent enough to see what's behind it.
        // NOTE: Translucency throws exceptions on Linux and Windows 8.1, but
        // we don't want to penalize all Windows users and Windows 8.1 has other
        // issues such as incorrect image width, so we only disable for Linux.
        // if ( !SystemType.LINUX.equals( systemType ) ) {
        // dialogPane.setOpacity( 0.92125 );
        // }
    }

    /**
     * This method is essentially a wrapper around the usual showAndWait() call
     * for a modal dialog, ensuring that the model and view are first synced
     * before displaying the dialog.
     *
     * @return The {@link ButtonType} of the button that the user clicked to
     *         dismiss this dialog
     */
    public Optional< ButtonType > showModalDialog() {
        // Sync the initial state to the current data model before showing.
        updateView();

        // Forward the show as a modal blocking call.
        final Optional< ButtonType > response = showAndWait();

        // Return the user's dismissal ButtonType status to the caller.
        return response;
    }

    ////////////////////// Model/View syncing methods ////////////////////////

    // NOTE: We are phasing out Swing-style model/view syncing in favor of Data
    // Binding, so these methods are no longer required of subclasses.
    public void updateModel() {}

    // NOTE: We are phasing out Swing-style model/view syncing in favor of Data
    // Binding, so these methods are no longer required of subclasses.
    public void updateView() {}

}
