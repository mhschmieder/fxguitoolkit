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
package com.mhschmieder.fxguitoolkit.stage;

import java.net.URL;

import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewer extends Stage {

    // Declare the Web View host, so we can load it post-init.
    protected WebView webView;

    public WebViewer( final String title,
                      final String jarRelativeIconFilename,
                      final String jarRelativeHtmlFilename,
                      final double preferredWidth,
                      final double preferredHeight ) {
        // Always call the superclass constructor first!
        super();

        setTitle( title );

        try {
            initStage( jarRelativeIconFilename,
                       jarRelativeHtmlFilename,
                       preferredWidth,
                       preferredHeight );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected final void initStage( final String jarRelativeIconFilename,
                                    final String jarRelativeHtmlFilename,
                                    final double defaultWidth,
                                    final double defaultHeight ) {
        // First have the superclass initialize its content.
        // initStage( jarRelativeIconFilename, defaultWidth, defaultHeight,
        // false );

        final Parent contentPane = loadContent();

        final URL htmlUrl = WebViewer.class.getResource( jarRelativeHtmlFilename );

        final WebEngine webEngine = webView.getEngine();
        webView.autosize();

        final Scene scene = new Scene( contentPane, defaultWidth, defaultHeight );
        setScene( scene );
    }

    protected final Parent loadContent() {
        // Instantiate and return the custom Content Node.
        webView = new WebView();

        final AnchorPane contentPane = new AnchorPane();
        contentPane.getChildren().add( webView );

        final Bounds webViewBounds = webView.getBoundsInParent();
        contentPane.setMaxWidth( webViewBounds.getMaxX() );
        contentPane.setMaxHeight( webViewBounds.getMaxY() );

        return contentPane;
    }

}