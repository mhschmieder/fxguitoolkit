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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class TableControlPane extends BorderPane {

    // Declare and instantiate all of the table action buttons.
    public Button _insertRowButton;
    public Button _deleteRowButton;

    // Fully qualified constructor.
    public TableControlPane( final ClientProperties clientProperties,
                             final Orientation orientation,
                             final Button insertRowButton,
                             final Button deleteRowButton,
                             final boolean deleteButtonDisabledDefault ) {
        // Always call the superclass constructor first!
        super();

        _insertRowButton = insertRowButton;
        _deleteRowButton = deleteRowButton;

        try {
            initPane( clientProperties, orientation, deleteButtonDisabledDefault );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties clientProperties,
                           final Orientation orientation,
                           final boolean deleteButtonDisabledDefault ) {
        // Conditionally disable the Delete Button initially.
        _deleteRowButton.setDisable( deleteButtonDisabledDefault );

        // Layout the table control pane with five pixels between all adjacent
        // elements, except for the top (if horizontal), since that would double
        // the border of the main panel above.
        if ( Orientation.HORIZONTAL.equals( orientation ) ) {
            final HBox hbox = new HBox();
            hbox.setSpacing( 6.0d );
            hbox.setAlignment( Pos.CENTER_RIGHT );
            hbox.getChildren().addAll( _insertRowButton, _deleteRowButton );
            setRight( hbox );
        }
        else {
            final VBox vbox = new VBox();
            vbox.setSpacing( 6.0d );
            vbox.setAlignment( Pos.BOTTOM_CENTER );
            vbox.getChildren().addAll( _insertRowButton, _deleteRowButton );
            setBottom( vbox );
        }

        setPadding( new Insets( 0.0d ) );
    }

}
