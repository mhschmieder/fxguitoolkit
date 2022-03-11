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

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;

/**
 * {@code TextFlowAlert} is a special Dialog that extends a normal Alert with a
 * hyperlink (and surrounding text) via a supplied Text Flow control.
 */
public class TextFlowAlert extends Alert {

    /**
     * Local Grid Pane needed to augment default Dialog Pane layout.
     */
    private GridPane         grid;

    /**
     * Store a copy of the base class text content converted to a label.
     */
    private Label            label;

    /**
     * Store a reference to the Text Flow control, to defer layout details.
     */
    protected final TextFlow textFlow;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Default constructor. This is the preferred constructor for this class.
     * <p>
     * Creates a new {@code TextFlowAlert} instance.
     *
     * @param alertType
     *            The basic Alert Type
     * @param contentText
     *            The text to use for the content pane
     * @param textFlowCandidate
     *            The {@link TextFlow} instance to use for formatting the text
     *            content
     *
     * @since 1.0
     */
    public TextFlowAlert( final AlertType alertType,
                          final String contentText,
                          final TextFlow textFlowCandidate ) {
        // Always call the superclass constructor first!
        super( alertType, contentText );

        textFlow = textFlowCandidate;

        try {
            initDialog();
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
     * @since 1.0
     */
    @SuppressWarnings("nls")
    private final void initDialog() {
        final DialogPane dialogPane = getDialogPane();

        // Make a Label from the basic content text, for re-layout.
        label = new Label( dialogPane.getContentText() );
        label.setMaxWidth( Double.MAX_VALUE );
        label.setMaxHeight( Double.MAX_VALUE );
        label.setPrefWidth( Region.USE_COMPUTED_SIZE );
        label.setPrefHeight( Region.USE_COMPUTED_SIZE );
        label.setWrapText( true );
        label.getStyleClass().add( "content" );
        label.textProperty().bind( dialogPane.contentTextProperty() );

        grid = new GridPane();
        grid.setHgap( 16d );
        grid.setVgap( 16d );
        grid.setMaxWidth( Double.MAX_VALUE );
        grid.setMaxHeight( Double.MAX_VALUE );
        grid.setAlignment( Pos.CENTER_LEFT );

        dialogPane.contentTextProperty().addListener( listener -> updateGrid() );

        updateGrid();
    }

    /**
     * This method updates the grid layout for this component, and generally
     * should only be called at initialization time, but theoretically could be
     * called post-initialization to handle any changes to the {@link TextFlow}
     * container used to host the content.
     */
    private void updateGrid() {
        grid.getChildren().clear();

        // Add the Text Flow to the existing Alert content.
        grid.add( label, 0, 0 );
        grid.add( textFlow, 0, 1 );

        getDialogPane().setContent( grid );
    }

}