/**
 * MIT License
 *
 * Copyright (c) 2024 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.concurrent;

import com.mhschmieder.commonstoolkit.net.DataServerResponse;
import com.mhschmieder.commonstoolkit.net.ServerRequestProperties;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxcommonstoolkit.concurrent.DataRequestService;
import com.mhschmieder.fxcommonstoolkit.concurrent.DataRequestTask;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;
import com.mhschmieder.fxguitoolkit.stage.DataRequestStatusViewer;

import javafx.concurrent.Task;

/**
 * Base class for service commonality between server data requests, enhanced to
 * allow for tracking via a JavaFX ControlsFX TaskProgressViewer based component.
 */
public class TrackableDataRequestService extends DataRequestService {

    /** Cache the task status viewer so we can tell it to add new tasks. */
    protected DataRequestStatusViewer dataRequestStatusViewer;

    public TrackableDataRequestService( final ServerRequestProperties pServerRequestProperties,
                                        final ClientProperties pClientProperties,
                                        final DataRequestStatusViewer pDataRequestStatusViewer ) {
        super( pServerRequestProperties, 
               pClientProperties );

        dataRequestStatusViewer = pDataRequestStatusViewer;
        
        // Add callbacks for the Service API status tracking.
        addCallbacks();
    }

    @Override
    protected Task< DataServerResponse > createTask() {
        // Create a new task based on the current Data Request Type.
        final DataRequestTask dataRequestTask = makeDataRequestTask();

        // Add this task to the Task Progress View via its GUI host.
        dataRequestStatusViewer.addTask( dataRequestTask );

        return dataRequestTask;
    }
    
    /**
     * Returns a general DataRequestTask that holds the task parameters.
     * <p>
     * NOTE: This method should be overridden by implementing classes,
     *  to make and return a more specific task class instance.
     * 
     * @return A general DataRequestTask that holds the task parameters
     */
    protected DataRequestTask makeDataRequestTask() {
        return new DataRequestTask( serverRequestProperties,
                                    dataRequestParameters,
                                    clientProperties );
    }
    
    /**
     * Add callbacks for the Service API status tracking, to handle the most
     * useful status values such as: scheduled, cancelled, failed, succeeded.
     * <p>
     * NOTE: This is meant to cover boilerplate behavior to avoid copy/paste
     *  in downstream clients, especially regarding the Task Progress Viewer.
     */
    protected void addCallbacks() {
        // NOTE: Switched from "onRunning" to "onScheduled" as that is the
        // callback that is triggered by the task's "start()" method.
        /*
        setOnRunning( t -> {
            // Show the modal Task Status Viewer during the data request.
            dataRequestStatusViewer.show();
        } );
        */
        setOnScheduled( t -> {
            // Show the modal Task Status Viewer during the data request.
            if ( dataRequestStatusViewer.isShowing() ) {
                dataRequestStatusViewer.toFront();
            }
            else {
                dataRequestStatusViewer.show();
            }
        } );
        
        setOnCancelled( t -> {
            // Hide the Task Status Viewer after the cancellation.
            dataRequestStatusViewer.hide();
        } );
        
        setOnFailed( t -> {
            // Hide the Task Progress Viewer after a data request failure.
            dataRequestStatusViewer.hide();

            // Forward any service exceptions to the established logger.
            final Throwable exception = getException();
            exception.printStackTrace();

            // Alert the user that there were problems with the service.
            final String statusMessage = exception.toString();
            DialogUtilities.showFileReadErrorAlert( statusMessage );
        } );
        
        setOnSucceeded( t -> {
            // Hide the Task Status Box pop-up after the data request returns
            // successfully, if no errors are displayed in the Task Box.
            dataRequestStatusViewer.hide();
        } );
    }
}
