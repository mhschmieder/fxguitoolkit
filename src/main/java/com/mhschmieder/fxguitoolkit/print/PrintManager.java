/*
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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.print;

import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxguitoolkit.MessageFactory;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEngine;
import javafx.stage.Window;

/**
 * A manager for JavaFX PrinterJob and associated print functionality. This
 * allows for generic print handling to be accessed in any code hosting context
 * rather than solely via inheritance of {@code XStage} in fxguitoolkit.
 */
public class PrintManager {

    /**
     * Cache the {@link PrinterJob} to maintain it as always-active, as this is
     * required for Page Setup to work and also is more efficient overall.
     */
    protected PrinterJob printerJob;
    
    /**
     * Cache the Window Owner at construction time, as it can't change after that.
     * <p>
     * For most print activity, this is ignored if running on macOS.
     */
    protected final Window windowOwner;

    /**
     * Constructs a PrintManager instance with a cached Window Owner for dialogs.
     * <p>
     * For most print activity, Window Owner is ignored if running on macOS.
     * 
     * @param pWindowOwner The {@link Window} owner for any launched dialogs
     */
    public PrintManager( final Window pWindowOwner ) {
        // NOTE: Nothing else to do as the Printer Job instance is made on demand.
        windowOwner = pWindowOwner;
    }

    public final void pageSetup( final SystemType systemType ) {
        final String printCategory = "Page Setup"; 
        final boolean printJobVerified = verifyPrinterJob( printCategory );
        if ( !printJobVerified ) {
            return;
        }

        // NOTE: On macOS, setting this window as owner, causes the menu
        //  system to freeze up until another primary window is shown, and
        //  the behavior and window stacking order seem no different when
        //  setting the dialog's owner to null, so it is safer to do so.
        // NOTE: On Windows, however, there was no side effect in using
        //  this window as the dialog's owner, and not doing so causes it to
        //  go to the upper left of the screen, and thus hard to notice.
        final Window windowOwnerModified = SystemType.MACOS.equals( 
                systemType )
            ? null
            : windowOwner;

        try {
            // NOTE: This runs internally on a separate synced thread.
            printerJob.showPageSetupDialog( windowOwnerModified );
        }
        catch ( final IllegalStateException ise ) {
            ise.printStackTrace();
        }
    }

    public final void print( final Node printNode,
                             final SystemType systemType ) {
        final String printCategory = "Print";
        final boolean printJobVerified = verifyPrinterJob( printCategory );
        if ( !printJobVerified ) {
            return;
        }

        // NOTE: On macOS, setting this window as owner, causes the menu
        //  system to freeze up until another primary window is shown, and
        //  the behavior and window stacking order seem no different when
        //  setting the dialog's owner to null, so it is safer to do so.
        // NOTE: On Windows, however, there was no side effect in using
        //  this window as the dialog's owner, and not doing so causes it to
        //  go to the upper left of the screen, and thus hard to notice.
        final Window windowOwnerModified = SystemType.MACOS.equals( 
                systemType )
            ? null
            : windowOwner;

        boolean printConfirmed = false;
        try {
            // NOTE: This runs internally on a separate synced thread.
            printConfirmed = printerJob.showPrintDialog( windowOwner );
        }
        catch ( final IllegalStateException ise ) {
            ise.printStackTrace();
        }
        finally {
            if ( printConfirmed ) {
                // Print the requested page(s).
                // TODO: Find a way to indicate and generate multiple pages.
               print( printNode, printCategory );
            }
            else {
                // Make sure the printer doesn't lock out new jobs.
                renewPrintJob();
            }
        }
    }

    public final void print( final Node printNode,
                             final String printCategory ) {
        // Print the main Content Node, which excludes not only the Frame Title
        // Bar, but also the Menu Bar, Tool Bar, Status Bar, Action Button Bar.
        final Scale printJobScale = PrintUtilities.getPrintJobScale( printerJob, 
                                                                     printNode );
        try {
            // Scale the Print Job to fit the printed page.
            printNode.getTransforms().add( printJobScale );
        }
        catch ( final UnsupportedOperationException | ClassCastException | NullPointerException
                | IllegalArgumentException e ) {
            e.printStackTrace();
        }

        try {
            // Print the requested node, with scaling applied.
            final boolean pagePrinted = printerJob.printPage( printNode );
            if ( !pagePrinted ) {
                PrintUtilities.handlePrintJobError( printerJob, printCategory );
            }
        }
        catch ( final NullPointerException npe ) {
            npe.printStackTrace();
        }

        // Prepare for the next Print Job so the Printer isn't hung.
        renewPrintJob();

        try {
            // Remove the print job scale transform or the node itself gets
            // permanently scaled on the screen as well.
            printNode.getTransforms().remove( printJobScale );
        }
        catch ( final UnsupportedOperationException | ClassCastException
                | NullPointerException e ) {
            e.printStackTrace();
        }
    }

    public final void print( final WebEngine webEngine ) {
        // NOTE: An application window has no insight into how to layout 
        //  WebView content, so we invoke WebKit's own printing layout engine.
        webEngine.print( printerJob );

        // Prepare for the next Print Job so the Printer isn't hung.
        renewPrintJob();
    }

    public final void createPrintJob() {
        try {
             printerJob = PrinterJob.createPrinterJob();
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();
        }
    }

    // Prepare for the next Print Job so the Printer isn't hung.
    public final void renewPrintJob() {
        // End the job even if it failed, as this also cancels a hung job and
        // thus makes the printer available for another try.
        printerJob.endJob();

        // Make a new print job, or we can only print once per session.
        createPrintJob();
    }
    
    public final boolean verifyPrinterJob( final String printCategory ) {
        // NOTE: We need an always active Printer Job in order to support Page
        //  Setup and Printing. Note that a printer may come on-line while the
        //  application is running, and that each printer job is good once only.
        if ( printerJob == null ) {
            createPrintJob();
        }

        // If the Printer Job is still null, alert the user that there are no
        // printers available to the application.
        if ( printerJob == null ) {
            final String noPrinterAvailableErrorMessage = MessageFactory
                    .getNoPrinterAvailableMessage();
            final String masthead = MessageFactory.getPrintServicesProblemMasthead();
            DialogUtilities.showErrorAlert( noPrinterAvailableErrorMessage, 
                                            masthead, 
                                            printCategory );

            return false;
        }

        return true;
    }
}
