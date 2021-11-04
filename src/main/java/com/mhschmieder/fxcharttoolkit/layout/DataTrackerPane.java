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
package com.mhschmieder.fxcharttoolkit.layout;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.control.ClickLocation;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;

/**
 * This is an abstract base class for layouts that need to supply data tracking
 * capabilities. By using a Stack Pane as the basis, we can avoid a lot of
 * layout issues that otherwise would develop in complex layout hierarchies.
 */
public abstract class DataTrackerPane extends StackPane {

    /**
     * Declare the contextual pop-up menu.
     */
    public ContextMenu      _contextMenu;

    /**
     * Keep track of which window owns the context menu, for focus and dismissal
     */
    protected Window        _contextMenuOwner;

    // Cache the last Click Location so we can use it to update after window
    // resizing events.
    protected ClickLocation _clickLocation;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    public DataTrackerPane( final ClientProperties pClientProperties,
                            final ContextMenu contextMenu,
                            final Window contextMenuOwner ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;
        _contextMenu = contextMenu;
        _contextMenuOwner = contextMenuOwner;

        try {
            initPane();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Register all of the mouse event handlers (e.g. data tracker triggers and
    // updaters). Pop-up menu triggers are now handled via Context Menu Events.
    protected final void addMouseEventHandlers() {
        // NOTE: Triggered when the mouse clicks within this layout pane.
        setOnMouseClicked( mouseEvent -> {
            // NOTE: Different platforms handle pop-up triggers differently.
            if ( !mouseEvent.isPopupTrigger() && !_contextMenu.isShowing() ) {
                updateDataTracking( mouseEvent, true );
            }
        } );

        // NOTE: Triggered when the mouse drags within this layout pane.
        setOnMouseDragged( mouseEvent -> {
            final MouseButton button = mouseEvent.getButton();
            if ( MouseButton.PRIMARY.equals( button ) ) {
                updateDataTracking( mouseEvent, false );
            }
        } );
    }

    private final void initPane() {
        // Register all of the mouse event handlers (e.g. pop-up menu and data
        // tracker triggers and updaters, along with mouse drag handling etc.).
        addMouseEventHandlers();

        // Register the context menu request handler, separate from mouse events
        // as it could also be triggered by keyboard events or by combined
        // keyboard and mouse actions (e.g. SHIFT-F10 on Windows -- though that
        // only seems to apply to larger contexts such as Scenes vs. Nodes).
        setOnContextMenuRequested( evt -> {
            final double popupX = evt.getScreenX();
            final double popupY = evt.getScreenY();

            // Consume this event so we don't also get related Mouse Events.
            evt.consume();

            // Show the context pop-up menu until dismissed.
            _contextMenu.show( _contextMenuOwner, popupX, popupY );
        } );
    }

    public final boolean isContextMenuActive() {
        return _contextMenu.isShowing();
    }

    public abstract void setDataTrackerColor( final Color gridColor );

    public abstract void setForegroundFromBackground( final Color backColor );

    protected final void setForegroundFromBackground( final Color backColor,
                                                      final String jarRelativeStylesheetFilenameDark,
                                                      final String jarRelativeStylesheetFilenameLight ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );
    }

    public final void updateDataTracking() {
        updateDataTracking( _clickLocation, false );
    }

    protected abstract void updateDataTracking( final ClickLocation clickLocation,
                                                final boolean mouseClicked );

    protected final void updateDataTracking( final MouseEvent mouseEvent,
                                             final boolean mouseClicked ) {
        // Cache the current click location in case we need to update the data
        // values after a change to the underlying data sets while the data
        // tracker is already active but not being dragged.
        // TODO: Determine whether this should happen after the final early
        // exit criteria have been examined.
        _clickLocation = new ClickLocation( mouseEvent );

        updateDataTracking( _clickLocation, mouseClicked );
    }

}
