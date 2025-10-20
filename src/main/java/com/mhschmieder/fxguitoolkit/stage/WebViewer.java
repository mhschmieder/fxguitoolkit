/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

public class WebViewer extends XStage {

    // Declare the Web View host, so we can load it post-init.
    protected WebView webView;

    public WebViewer( final String title,
                      final String windowKeyPrefix,
                      final String jarRelativeIconFilename,
                      final String jarRelativeHtmlFilename,
                      final double preferredWidth,
                      final double preferredHeight,
                      final ProductBranding productBranding,
                      final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, productBranding, pClientProperties );

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
        initStage( jarRelativeIconFilename, defaultWidth, defaultHeight, false );

        final URL htmlUrl = WebViewer.class.getResource( jarRelativeHtmlFilename );

        final WebEngine webEngine = webView.getEngine();
        webEngine.load( htmlUrl.toString() );
        webView.autosize();
    }

    @Override
    protected final Node loadContent() {
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