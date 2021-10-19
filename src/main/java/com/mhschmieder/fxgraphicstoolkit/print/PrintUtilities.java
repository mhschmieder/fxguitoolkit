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
package com.mhschmieder.fxgraphicstoolkit.print;

import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

public final class PrintUtilities {

    // NOTE: The constructor is disabled, as this is a static utilities class.
    private PrintUtilities() {}

    /**
     * Gets the Print Job Scales for a target Node, based on the current page
     * layout and attributes.
     *
     * @param printerJob
     *            The pre-established Printer Job for this print task
     * @param node
     *            The scene node to be printed
     * @return The Scale to be applied to the Node so it fits on the Page
     */
    public static Scale getPrintJobScale( final PrinterJob printerJob, final Node node ) {
        final JobSettings jobSettings = printerJob.getJobSettings();
        final PageLayout pageLayout = jobSettings.getPageLayout();
        final double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        final double scaleY =
                            pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        final double minimumScale = Math.min( scaleX, scaleY );
        final Scale printJobScale = new Scale( minimumScale, minimumScale );

        return printJobScale;
    }

}
