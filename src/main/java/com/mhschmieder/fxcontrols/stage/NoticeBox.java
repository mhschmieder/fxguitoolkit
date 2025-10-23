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
package com.mhschmieder.fxcontrols.stage;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.layout.NoticePane;
import com.mhschmieder.jcommons.util.SystemType;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.net.URL;

public class NoticeBox extends Popup {

    // Declare the main content pane for the primary notice layout.
    protected NoticePane _noticePane;

    // Cache the system type, as we may need it during show and hide actions.
    protected SystemType _systemType;

    public NoticeBox( final SystemType systemType,
                      final String bannerText,
                      final String noticeHtml ) {
        // Always call the superclass constructor first!
        super();

        _systemType = systemType;

        try {
            initPopup( bannerText, noticeHtml );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticeBox( final SystemType systemType,
                      final String bannerText,
                      final String noticeText,
                      final int numberOfColumns,
                      final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        _systemType = systemType;

        try {
            initPopup( bannerText, noticeText, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticeBox( final SystemType systemType,
                      final String bannerText,
                      final StringProperty notice,
                      final int numberOfColumns,
                      final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        _systemType = systemType;

        try {
            initPopup( bannerText, notice, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticeBox( final SystemType systemType,
                      final String bannerText,
                      final Text noticeText,
                      final int numberOfColumns,
                      final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        _systemType = systemType;

        try {
            initPopup( bannerText, noticeText, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticeBox( final SystemType systemType, final String bannerText, final URL noticeUrl ) {
        // Always call the superclass constructor first!
        super();

        _systemType = systemType;

        try {
            initPopup( bannerText, noticeUrl );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    protected final void initLayout() {
        final VBox root = new VBox();
        root.getChildren().add( _noticePane );

        root.getStyleClass().add( "notice-box" );

        GuiUtilities.applyDropShadowEffect( root );

        // Pop-ups add content vs. setting a scene (hidden in implementation).
        getContent().add( root );

        // Make the Notice Box just translucent enough to see what's behind it.
        // NOTE: Translucency throws exceptions on Linux and Windows 8.1, but
        // we don't want to penalize all Windows users and Windows 8.1 has other
        // issues such as incorrect image width, so we only disable for Linux.
        // NOTE: Apparently, some Macs also crash when transparency is used
        // along with mouse focus listeners, as we show against a Swing window.
        if ( !SystemType.LINUX.equals( _systemType ) ) {
            setOpacity( 0.92125d );
        }

        // Enforce the Notice Box to be 100% on-screen in all circumstances.
        setAutoFix( true );

        // Allow the ESC key to be used to hide this pop-up window.
        // NOTE: This is not working on any OS platform; possibly due to Swing
        // intercepting the ESC key before it gets to JavaFX?
        setHideOnEscape( true );

        // Hide this pop-up window if it loses focus.
        setAutoHide( true );

        // Do not prevent additional event handling by the pop-up owner.
        // TODO: Re-evaluate this after switching to a Window vs. Node owner,
        // as currently this behaves the same way, true or false.
        // NOTE: Switched back to the default, because it seems to be a cause
        // of crashes on retina displays and dual monitor systems (Mac OS X) if
        // the mouse event is allowed to be forwarded to the AWT/Swing window
        // underneath (e.g. Sound Field) or by the Tool Bar (probably triggering
        // the Predict Button and likely running on the wrong thread).
        setConsumeAutoHidingEvents( true );

        // Hide the Notice Box if the user clicks the mouse anywhere on the
        // Title Bar. Clicking within the content pane could break scroll bars.
        // TODO: Also hide it if it is no longer in front -- although there
        // don't seem to be any callbacks to register for that change of status.
        _noticePane._titleBar.setOnMouseClicked( mouseEvent -> {
            // If the mouse exited, hide this pop-up window.
            hide();
        } );
    }

    protected final void initPopup( final String bannerText, final String noticeHtml ) {
        // Make the main content pane, which is everything but the buttons.
        _noticePane = new NoticePane( bannerText, noticeHtml );

        // Initialize the remaining layout, which is common/shared.
        initLayout();
    }

    protected final void initPopup( final String bannerText,
                                    final String noticeText,
                                    final int numberOfColumns,
                                    final int numberOfRows ) {
        // Make the main content pane, which is everything but the buttons.
        _noticePane = new NoticePane( bannerText, noticeText, numberOfColumns, numberOfRows );

        // Initialize the remaining layout, which is common/shared.
        initLayout();
    }

    protected final void initPopup( final String bannerText,
                                    final StringProperty notice,
                                    final int numberOfColumns,
                                    final int numberOfRows ) {
        // Make the main content pane, which is everything but the buttons.
        _noticePane = new NoticePane( bannerText, notice, numberOfColumns, numberOfRows );

        // Initialize the remaining layout, which is common/shared.
        initLayout();
    }

    protected final void initPopup( final String bannerText,
                                    final Text noticeText,
                                    final int numberOfColumns,
                                    final int numberOfRows ) {
        // Make the main content pane, which is everything but the buttons.
        _noticePane = new NoticePane( bannerText, noticeText, numberOfColumns, numberOfRows );

        // Initialize the remaining layout, which is common/shared.
        initLayout();
    }

    protected final void initPopup( final String bannerText, final URL noticeUrl ) {
        // Make the main content pane, which is everything but the buttons.
        _noticePane = new NoticePane( bannerText, noticeUrl );

        // Initialize the remaining layout, which is common/shared.
        initLayout();
    }

    public final void show( final Node ownerNode ) {
        // Center this Notice Box on the screen so it catches the attention of
        // the end user, and then show it until hidden by mouse focus lost.
        // NOTE: The Node itself must have a valid non-null Window owner.
        // NOTE: It turns out that JFXPanel contains an internal Window, so we
        // shouldn't get IllegalArgumentException while running in Swing.
        centerOnScreen();
        show( ownerNode, getX(), getY() );
    }

    @Override
    public final void show( final Window ownerWindow ) {
        // Center this Notice Box on the screen so it catches the attention of
        // the end user, and then show it until hidden by mouse focus lost.
        centerOnScreen();
        show( ownerWindow, getX(), getY() );
    }

}
