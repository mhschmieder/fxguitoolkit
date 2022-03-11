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
package com.mhschmieder.fxguitoolkit.action;

import java.util.Collection;

import org.controlsfx.control.action.Action;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

public class LabeledActionFactory {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private LabeledActionFactory() {}

    // TODO: Review whether this is still correct, now that we have to package
    // all non-Java resource files in a separate hierarchy from the Java package
    // for the source code.
    @SuppressWarnings("nls") public static final String BUNDLE_NAME =
                                                                    "com.mhschmieder.fxguitoolkit.action.ActionLabels";

    // TODO: Load an icon that is a stylized representation of the MRU number.
    @SuppressWarnings("nls")
    public static XAction makeFileMruAction( final ClientProperties pClientProperties,
                                             final int mruFileNumber ) {
        // Make sure the MRU File items self-hide if empty and disabled.
        final String fileMruNumber = "mru" + Integer.toString( mruFileNumber );
        return ActionFactory
                .makeAction( pClientProperties, BUNDLE_NAME, "file", fileMruNumber, null, true );
    }

    @SuppressWarnings("nls")
    public static XActionGroup makeWindowSizeActionGroup( final ClientProperties pClientProperties,
                                                          final WindowSizeActions windowSizeActions,
                                                          final boolean maximumSizeSupported ) {
        final Collection< Action > windowSizeActionCollection = windowSizeActions
                .getWindowSizeActionCollection( maximumSizeSupported );

        final XActionGroup windowSizeActionGroup = ActionFactory
                .makeActionGroup( pClientProperties,
                                  windowSizeActionCollection,
                                  BUNDLE_NAME,
                                  "windowSize",
                                  "/icons/deviantArt/FullScreen16.png" );

        return windowSizeActionGroup;
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizeDefaultSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "defaultSize",
                                         "/icons/yusukeKamiyamane/ApplicationResizeActual16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizeMaximumSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "maximumSize",
                                         "/icons/yusukeKamiyamane/ApplicationResizeFull16.png" );
    }

    @SuppressWarnings("nls")
    public static XAction makeWindowSizePreferredSizeAction( final ClientProperties pClientProperties ) {
        return ActionFactory.makeAction( pClientProperties,
                                         BUNDLE_NAME,
                                         "windowSize",
                                         "preferredSize",
                                         "/icons/yusukeKamiyamane/ApplicationResize16.png" );
    }

}
