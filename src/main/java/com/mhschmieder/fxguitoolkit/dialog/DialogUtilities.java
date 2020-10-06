/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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

import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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
    public static Background getDialogBackground() {
        final Background background = getDialogBackground( Insets.EMPTY );

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
    public static Background getDialogBackground( final Insets insets ) {
        final Background background =
                                    new Background( new BackgroundFill( WINDOW_BACKGROUND_COLOR,
                                                                        CornerRadii.EMPTY,
                                                                        insets ) );

        return background;
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

}
