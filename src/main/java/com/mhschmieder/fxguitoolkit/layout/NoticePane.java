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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.ControlFactory;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

import java.net.URL;

public class NoticePane extends VBox {

    // Cache the Title Bar so it can be used by hosting layouts to register
    // mouse clicks for dismissing the owning window (e.g.).
    public HBox _titleBar;

    public NoticePane( final String bannerText, final String noticeHtmlContent ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( bannerText, noticeHtmlContent );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticePane( final String bannerText,
                       final String noticeTextContent,
                       final int numberOfColumns,
                       final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( bannerText, noticeTextContent, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticePane( final String bannerText,
                       final StringProperty notice,
                       final int numberOfColumns,
                       final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( bannerText, notice, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticePane( final String bannerText,
                       final Text noticeText,
                       final int numberOfColumns,
                       final int numberOfRows ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( bannerText, noticeText, numberOfColumns, numberOfRows );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public NoticePane( final String bannerText, final URL noticeUrl ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( bannerText, noticeUrl );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected final void initLayout( final String bannerText, final Node contentNode ) {
        // Make sure the text area itself can receive mouse pick events.
        contentNode.setPickOnBounds( true );

        // Use the main pane to host the Banner Label and the Notice Text Area.
        _titleBar = GuiUtilities.getBanner( bannerText );

        getChildren().addAll( _titleBar, contentNode );
    }

    protected final void initPane( final String bannerText, final String noticeHtmlContent ) {
        // Declare the main Web View for the primary content.
        final WebView noticeWebView = ControlFactory.makeNoticeWebView( noticeHtmlContent );

        // Initialize the remaining layout, which is common/shared.
        initLayout( bannerText, noticeWebView );
    }

    protected final void initPane( final String bannerText,
                                   final String noticeTextContent,
                                   final int numberOfColumns,
                                   final int numberOfRows ) {
        // Declare the main Text Area for the primary content.
        final TextArea noticeTextArea = ControlFactory
                .makeNoticeTextArea( noticeTextContent, false, numberOfColumns, numberOfRows );

        // Initialize the remaining layout, which is common/shared.
        initLayout( bannerText, noticeTextArea );
    }

    protected final void initPane( final String bannerText,
                                   final StringProperty notice,
                                   final int numberOfColumns,
                                   final int numberOfRows ) {
        // Declare the main Text Area for the primary content.
        final TextArea noticeTextArea = ControlFactory
                .makeNoticeTextArea( notice.get(), true, numberOfColumns, numberOfRows );

        // Bind the textField to the referenced observable string property.
        noticeTextArea.textProperty().bindBidirectional( notice );

        // Initialize the remaining layout, which is common/shared.
        initLayout( bannerText, noticeTextArea );
    }

    protected final void initPane( final String bannerText,
                                   final Text noticeText,
                                   final int numberOfColumns,
                                   final int numberOfRows ) {
        // Declare an alternate Text Flow for Rich Text content.
        final TextFlow noticeTextFlow = ControlFactory
                .makeNoticeTextFlow( noticeText, numberOfColumns, numberOfRows );

        // Initialize the remaining layout, which is common/shared.
        initLayout( bannerText, noticeTextFlow );
    }

    protected final void initPane( final String bannerText, final URL noticeUrl ) {
        // Declare the main Web View for the primary content.
        final WebView noticeWebView = ControlFactory.makeNoticeWebView( noticeUrl );

        // NOTE: We're having problems with horizontal scroll bars showing up
        // on Windows 10, if not also on Mac OS, unless we down-scale the font.
        // NOTE: This has changed with recent Java updates, so the DPI based
        // scaling was backed out and replaced downstream with font scaling.
        noticeWebView.setFontScale( 1.0d );

        // Initialize the remaining layout, which is common/shared.
        initLayout( bannerText, noticeWebView );
    }

}// class NoticePane
