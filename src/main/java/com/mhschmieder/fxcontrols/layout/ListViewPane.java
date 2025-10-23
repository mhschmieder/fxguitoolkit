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
package com.mhschmieder.fxcontrols.layout;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

// This is a working example of a stylized List View layout pane that applies
// certain LAF and constraints that are far from the defaults in JavaFX.
public class ListViewPane extends BorderPane {

    private Label              headerLabel;
    private ListView< String > itemList;

    // This is just an empty placeholder for a bottom layout element that would
    // likely have hard constraints, maybe host buttons, and in turn would help
    // constrain the ListView's sizing and spacing.
    private Pane               bottomButtonPane;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties    clientProperties;

    // TODO: Review the constructor(s) to provide the best choices in
    // populating the List View, and/or simply add methods that update the
    // list post-construction.
    public ListViewPane( final String listHeader,
                         final String[] items,
                         final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;

        try {
            initPane( listHeader, items );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initPane( final String listHeader, final String[] items ) {
        // Make the large stylized masthead label and place at top.
        headerLabel = GuiUtilities.getTitleLabel( listHeader );
        final HBox masthead = GuiUtilities.getTitlePane( headerLabel );

        // Make the scrollable List View for Delay Integration Product Types.
        itemList = new ListView<>();
        final ObservableList< String > observableItems = FXCollections.observableArrayList( items );
        itemList.setItems( observableItems );

        // Try to style the list to use semi-transparent forest green to
        // highlight selected items, as an initial LAF placeholder.
        GuiUtilities.addStylesheetAsJarResource( itemList, "/css/listView.css" ); //$NON-NLS-1$

        // TODO: Make a useful bottom pane, maybe with buttons, or pass one
        // in to the constructor if this avoids having to subclass this class.
        bottomButtonPane = new Pane();

        setTop( masthead );
        setCenter( itemList );
        setBottom( bottomButtonPane );

        setPadding( new Insets( 16d ) );

        setMargin( itemList, new Insets( 12d ) );

        // Try to avoid first-time problems with selector pane's width.
        itemList.setPrefWidth( masthead.getWidth() );

        // Due to so many factors, including different platforms and screens, it
        // is better to explicitly set the size that we want.
        itemList.minWidthProperty().bind( masthead.prefWidthProperty() );
        bottomButtonPane.minWidthProperty().bind( itemList.widthProperty() );

        // Check for Product Type list selection events, to enable relevant
        // Phase Curve Frequency Buttons and set the selection style.
        // NOTE: it's not clear if we can grab the list cell from here, so we
        // may need to apply the another list selection styling via CSS.
        itemList.setOnMouseClicked( mouseEvent -> {
            final String item = itemList.getSelectionModel().getSelectedItem();
            if ( ( item == null ) || item.isEmpty() ) {
                return;
            }

            // TODO: Potentially do some view-to-model syncing here, to cache
            // any valid selected value. Might need to do this in a derived
            // class that is domain specific and which passes in a reference.

            // Make sure to update which buttons are contextually allowed.
            Platform.runLater( () -> setButtonsEnabled( item ) );
        } );
    }

    public final void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );
    }

    // TODO: Perhaps push this to a subclass, for determining enablement
    // criteria of additional layout elements (such as action buttons) that
    // are shown below the List View, based on the current selection or active
    // highlighted list item.
    private void setButtonsEnabled( final String selectedItem ) {
        // Nothing to do at the moment; this is a placeholder example.
    }

    // TODO: Use Generics at the class definition level for the data type
    // modeled by the List, and use Generics for this method argument.
    public final void updateSelectedItem( final String selectedItem ) {
        itemList.getSelectionModel().select( selectedItem );

        // Make sure to update which Phase Curve Frequencies are allowed.
        setButtonsEnabled( selectedItem );
    }

}
