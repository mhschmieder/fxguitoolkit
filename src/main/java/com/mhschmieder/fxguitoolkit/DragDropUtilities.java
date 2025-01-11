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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.mhschmieder.commonstoolkit.io.FileMode;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.fxguitoolkit.stage.FileActionHandler;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * {@code DragDropUtilities} is a utility class for drag and drop utilities that
 * act on GUI modules and thus can't be placed in FxGraphicsToolkit.
 */
public class DragDropUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private DragDropUtilities() {}

    /**
     * Placeholder for drag/drop of images; needs File Handler class written.
     *
     * @param fileActionHandler
     *            A Window derived object that implements File Handler protocols
     *
     * @return An Event Handler for image drag events
     */
    public static EventHandler< DragEvent > getImageDragHandler( final FileActionHandler fileActionHandler ) {
        return dragEvent -> {
            final Dragboard dragboard = dragEvent.getDragboard();

            final EventType< DragEvent > eventType = dragEvent.getEventType();

            boolean dropCompleted = false;
            if ( dragboard.hasFiles() ) {
                // Block unless there is just one file and it has the proper
                // file extension for the supported image formats in Java.
                final List< File > files = dragboard.getFiles();
                if ( files.size() == 1 ) {
                    final File file = files.get( 0 );
                    final String fileExtension = FileUtilities.getExtension( file );
                    final Iterator< ImageReader > iter = ImageIO
                            .getImageReadersBySuffix( fileExtension );
                    final boolean canReadImageExtension = iter.hasNext();
                    if ( canReadImageExtension ) {
                        if ( DragEvent.DRAG_OVER.equals( eventType ) ) {
                            dragEvent.acceptTransferModes( TransferMode.ANY );
                        }
                        else if ( DragEvent.DRAG_DROPPED.equals( eventType ) ) {
                            dropCompleted = fileActionHandler.fileOpen( 
                                file,
                                FileMode.IMPORT_RASTER_GRAPHICS );
                        }
                    }
                }
            }

            // Let the drag source know whether the image was successfully
            // transferred and/or whether the source was a supported format.
            if ( DragEvent.DRAG_DROPPED.equals( eventType ) ) {
                dragEvent.setDropCompleted( dropCompleted );
            }

            dragEvent.consume();
        };
    }
}
