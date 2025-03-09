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
package com.mhschmieder.fxguitoolkit.stage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.io.FileMode;
import com.mhschmieder.commonstoolkit.io.FileStatus;
import com.mhschmieder.commonstoolkit.io.FileUtilities;
import com.mhschmieder.commonstoolkit.io.IoUtilities;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.xml.XmlUtilities;
import com.mhschmieder.fxguitoolkit.control.ProjectViewerToolBar;
import com.qoppa.pdfWriter.PDFDocument;

import javafx.geometry.Insets;
import javafx.print.Paper;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * An HTML viewer that can export to PDF. It consumes HTML or XML with an XSLT
 * Stylesheet that maps the XML to HTML.
 * <p>
 * NOTE: If you use this class in your product and you support the PDF Export
 *  facility, you are required by Qoppa to include attribution in your ABout Box.
 * <p>
 * TODO: Change the name of the class to be more generic, as it isn't just for
 *  project files, but the key point is that it can show nested structured data
 *  vs. single-table data, and this at the very least applies to project files.
 */
public class ProjectViewer extends XStage {

    /**
     * Include this attribution in your About Box and Splash Screen if you make
     * use of the PDF Export facility in this class, as this is a contractual
     * requirement for using the free jPDFWriter package from Qoppa.
     */
    public static final String JPDFWRITER_ATTRIBUTION 
            = "PDF Technology by Qoppa Software, LLC – www.qoppa.com";
    
    // Declare the main tool bar.
    public ProjectViewerToolBar       _toolBar;

    // Declare the web view component for displaying HTML and converted XML.
    protected WebView                 _webView;

    // Cache the File Chooser titles and extensions.
    protected String                  _fileOpenTitle;
    protected List< ExtensionFilter > _fileOpenExtensionFilterAdditions;
    protected ExtensionFilter         _fileOpenExtensionFilterDefault;
    protected String                  _fileSaveTitle;
    protected List< ExtensionFilter > _fileSaveExtensionFilterAdditions;

    // Cache the original pre-conversion files and contents for re-save, as well
    // as for handling frame title updates etc. on back/forward actions in the
    // tool bar buttons and the browser's context menu.
    protected List< File >            _files;
    protected List< StringBuilder >   _htmlBuffers;
    protected int                     _currentPageIndex;
    
    /**
     * The JAR relative resource lookup for the XSLT to use for transforming XML to HTML.
     */
    protected String jarRelativeXsltFilename;

    public ProjectViewer( final String fileOpenTitle,
                          final List< ExtensionFilter > fileOpenExtensionFilterAdditions,
                          final ExtensionFilter fileOpenExtensionFilterDefault,
                          final String fileSaveTitle,
                          final List< ExtensionFilter > fileSaveExtensionFilterAdditions,
                          final ProductBranding productBranding,
                          final ClientProperties pClientProperties ) {
        this( "Project Viewer", 
              "projectViewer", 
              "/icons/graphicRating/Project16.png",
              820.0d,
              640.0d,
              fileOpenTitle, 
              fileOpenExtensionFilterAdditions, 
              fileOpenExtensionFilterDefault,
              fileSaveTitle,
              fileSaveExtensionFilterAdditions,
              productBranding, 
              pClientProperties );
    }

    public ProjectViewer( final String title,
                          final String windowKeyPrefix,
                          final String jarRelativeIconFilename,
                          final double preferredWidth,
                          final double preferredHeight,
                          final String fileOpenTitle,
                          final List< ExtensionFilter > fileOpenExtensionFilterAdditions,
                          final ExtensionFilter fileOpenExtensionFilterDefault,
                          final String fileSaveTitle,
                          final List< ExtensionFilter > fileSaveExtensionFilterAdditions,
                          final ProductBranding productBranding,
                          final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( title, windowKeyPrefix, true, true, productBranding, pClientProperties );

        _fileOpenTitle = fileOpenTitle;
        _fileOpenExtensionFilterAdditions = fileOpenExtensionFilterAdditions;
        _fileOpenExtensionFilterDefault = fileOpenExtensionFilterDefault;
        _fileSaveTitle = fileSaveTitle;
        _fileSaveExtensionFilterAdditions = fileSaveExtensionFilterAdditions;

        // Avoid run-time null pointer exceptions, but make list access illegal
        // at first, until a page/file is loaded.
        _files = new ArrayList<>();
        _htmlBuffers = new ArrayList<>();
        _currentPageIndex = -1;

        try {
            initStage( jarRelativeIconFilename, preferredWidth, preferredHeight );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    //  the toolbar buttons and their associated menu items, so that when one
    //  is disabled the other is as well. Is this already true of what we do?
    @Override
    protected final void addToolBarListeners() {
        // Disable Save As, Page Setup and Print, until loading the first page.
        _toolBar._fileActionButtons._fileSaveAsButton.setDisable( true );
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( true );
        _toolBar._fileActionButtons._filePrintButton.setDisable( true );

        // Disable Back and Forward until loading the first file.
        _toolBar._navigationButtons._backButton.setDisable( true );
        _toolBar._navigationButtons._forwardButton.setDisable( true );

        // Load the event handler for the File Open Button.
        _toolBar._fileActionButtons._fileOpenButton.setOnAction( evt -> doFileOpen() );

        // Load the event handler for the File Save As Button.
        _toolBar._fileActionButtons._fileSaveAsButton.setOnAction( evt -> doFileSaveAs() );

        // Load the event handler for the File Page Setup Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPageSetup() );

        // Load the event handler for the File Print Button.
        _toolBar._fileActionButtons._filePrintButton.setOnAction( evt -> doPrint() );

        // Load the event handler for the Navigate Back Button.
        _toolBar._navigationButtons._backButton.setOnAction( evt -> doNavigateBack() );

        // Load the event handler for the Navigate Forward Button.
        _toolBar._navigationButtons._forwardButton.setOnAction( evt -> doNavigateForward() );

        // Detect the ENTER key while the File Open Button has focus, and use it
        // to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileOpenButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Open action.
                doFileOpen();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the File Save As Button has focus, and use
        // it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._fileSaveAsButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Save As action.
                doFileSaveAs();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the File Page Setup Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._filePageSetupButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Page Setup action.
                doPageSetup();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the File Print Button has focus, and use
        // it to trigger its action (standard expected behavior).
        _toolBar._fileActionButtons._filePrintButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the File Print action.
                doPrint();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Navigate Back Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._navigationButtons._backButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Navigate Back action.
                doNavigateBack();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );

        // Detect the ENTER key while the Navigate Forward Button has focus, and
        // use it to trigger its action (standard expected behavior).
        _toolBar._navigationButtons._forwardButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Navigate Forward action.
                doNavigateForward();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }
    
    /**
     * Sets the JAR-relative resource to use for transforming XML files into HTML.
     * This can be set multiple times; just set it differently for different schemas,
     * before invoking the FIle Save. You may need to derive this class to customize.
     * <p>
     * This class also supports direct-loading of HTML files, which need no XSLT.
     * <p>
     * The output from this class is a PDF file generating using Qoppa's free toolkit.
     * 
     * @param pJarRelativeXsltFilename the JAR-relative resource to use for transforming
     *        XML files into HTML
     */
    public final void setJarRelativeXsltFilename( final String pJarRelativeXsltFilename ) {
        jarRelativeXsltFilename = pJarRelativeXsltFilename;
    }

    protected final void doFileOpen() {
        // NOTE: Use the on-line example to take file load status into account.
        fileOpen( this,
                  FileMode.OPEN,
                  _fileOpenTitle,
                  _defaultDirectory,
                  _fileOpenExtensionFilterAdditions,
                  _fileOpenExtensionFilterDefault,
                  false );
    }

    protected final void doFileSaveAs() {
        // Force the user to provide a filename for converted project views.
        fileSaveAs();
    }

    protected final void doNavigateBack() {
        // Update the web view with the previous HTML content.
        if ( _currentPageIndex <= 0 ) {
            return;
        }

        --_currentPageIndex;
        final StringBuilder htmlBuffer = _htmlBuffers.get( _currentPageIndex );
        updateWebView( htmlBuffer );

        // Update the frame title with the input name.
        final File file = _files.get( _currentPageIndex );
        updateFrameTitle( file, false );

        // Disable Back and Enable Forward after loading a new file.
        _toolBar._navigationButtons._backButton.setDisable( _currentPageIndex <= 0 );
        _toolBar._navigationButtons._forwardButton.setDisable( false );
    }

    protected final void doNavigateForward() {
        // Update the web view with the current HTML content.
        if ( _currentPageIndex >= ( _htmlBuffers.size() - 1 ) ) {
            return;
        }

        _currentPageIndex++;
        final StringBuilder htmlBuffer = _htmlBuffers.get( _currentPageIndex );
        updateWebView( htmlBuffer );

        // Update the frame title with the input name.
        final File file = _files.get( _currentPageIndex );
        updateFrameTitle( file, false );

        // Enable Back and Disable Forward after loading a new file.
        _toolBar._navigationButtons._backButton.setDisable( false );
        _toolBar._navigationButtons._forwardButton
                .setDisable( _currentPageIndex >= ( _htmlBuffers.size() - 1 ) );
    }

    // This file export is a wrapper to ensure that all project view save as
    // actions are treated uniformly.
    protected final FileStatus fileSaveAs() {
        // Unconditionally replace the file suffix, so we have a string builder
        // to work with that includes the full canonical file path.
        final File referenceFile = _files.get( _currentPageIndex );
        final String pdfFileSuffix = "pdf";
        final StringBuilder pdfFileName = FileUtilities.getFileNameWithNewSuffix( referenceFile,
                                                                                  pdfFileSuffix );

        // Conditionally revision tag the target file name and re-save, so we
        // don't step on an existing file (or the original HTML file, when
        // relevant) accidentally if the user does a later re-save.
        final File pdfFile = ( pdfFileName != null )
            ? FileUtilities.getUniqueRevisionTaggedFile( pdfFileName )
            : new File( referenceFile.getAbsolutePath().concat( pdfFileSuffix ) );

        // Prompt for a file to save as, using this default file name.
        final boolean fileSaved = fileSaveAs( this,
                                              FileMode.SAVE_CONVERTED,
                                              clientProperties,
                                              _fileSaveTitle,
                                              _defaultDirectory,
                                              _fileSaveExtensionFilterAdditions,
                                              ExtensionFilters.PDF_EXTENSION_FILTER,
                                              pdfFile );

        return fileSaved ? FileStatus.EXPORTED : FileStatus.NOT_SAVED;
    }

    // Take care of any extensions specific to this sub-class.
    @Override
    public final FileStatus fileSaveExtensions( final File file,
                                                final File tempFile,
                                                final FileMode msliFileMode ) {
        // Pre-declare the File Save status in case of exceptions.
        FileStatus fileStatus = FileStatus.WRITE_ERROR;

        // TODO: Switch these and others to Apache Commons I/O library, which
        //  has a SuffixFileFilter with accept() methods.
        final String fileName = file.getName();
        final String fileNameCaseInsensitive = fileName.toLowerCase( Locale.ENGLISH );
        try {
            if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "pdf" ) ) {
                if ( FileMode.SAVE_CONVERTED.equals( msliFileMode ) ) {
                    // Chain a BufferedOutputStream to a FileOutputStream, for
                    // better performance and to guarantee platform-independence
                    // of newlines and overall system-neutrality and
                    // locale-sensitivity of text data.
                    try ( final FileOutputStream fileOutputStream =
                                                                  new FileOutputStream( tempFile );
                            final BufferedOutputStream bufferedOutputStream =
                                                                            new BufferedOutputStream( fileOutputStream ) ) {
                        // Export the Converted Project to a PDF file using an
                        // Output Stream. Overwrite it if it already exists.
                        fileStatus = saveProjectToPdf( bufferedOutputStream );
                    }
                }
            }
            else if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "html" )
                    || FilenameUtils.isExtension( fileNameCaseInsensitive, "htm" ) ) {
                if ( FileMode.SAVE_CONVERTED.equals( msliFileMode ) ) {
                    // Export the Converted Project to an HTML file using a File
                    // Writer. Overwrite it if it already exists.
                    try ( final FileWriter htmlFileWriter = new FileWriter( tempFile ) ) {
                        fileStatus = saveProjectToHtml( htmlFileWriter );
                    }
                }
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileStatus;
    }

    protected final void initStage( final String jarRelativeIconFilename,
                                    final double defaultWidth,
                                    final double defaultHeight ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename, defaultWidth, defaultHeight, true );
    }

    @Override
    protected final Node loadContent() {
        // Instantiate and return the custom Content Node.
        _webView = new WebView();
        _webView.autosize();

        final BorderPane contentPane = new BorderPane();
        contentPane.setPadding( new Insets( 5.0d ) );
        contentPane.setCenter( _webView );

        return contentPane;
    }

    // This file loader uses a specified file for the open, and is the
    // lowest-level shared call for all file open and import actions.
    @SuppressWarnings("nls")
    @Override
    public final FileStatus loadFromFile( final File file, 
                                          final FileMode msliFileMode ) {
        final StringBuilder htmlBuffer = new StringBuilder( 1024 );

        // Open the file.
        try {
            final String fileName = file.getName();
            final String fileNameCaseInsensitive = fileName.toLowerCase( Locale.ENGLISH );
            if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "xml" )
                    || FilenameUtils.isExtension( fileNameCaseInsensitive, "zip" ) ) {
                // Load the data into HTML from an XML or ZIP file.
                // TODO: Query the schema type and switch on the method?
                final boolean fileOpened = XmlUtilities.convertXmlToHtml( file, 
                                                                          htmlBuffer,
                                                                          jarRelativeXsltFilename );
                if ( !fileOpened ) {
                    return FileStatus.READ_ERROR;
                }
            }
            else if ( FilenameUtils.isExtension( fileNameCaseInsensitive, "html" )
                    || FilenameUtils.isExtension( fileNameCaseInsensitive, "htm" ) ) {
                // Load the data from an HTML file.
                final boolean fileOpened = IoUtilities.loadIntoStringBuilder( file, htmlBuffer );
                if ( !fileOpened ) {
                    return FileStatus.READ_ERROR;
                }
            }
            else {
                // Do not attempt to open unsupported file types.
                return FileStatus.READ_ERROR;
            }
        }
        catch ( final SecurityException se ) {
            se.printStackTrace();
            return FileStatus.READ_ERROR;
        }

        // Update the full cache of displayed data and context, along with tools
        // enablement criteria.
        updateCache( file, htmlBuffer );

        return FileStatus.OPENED;
    }

    // Add the Tool Bar for this Frame.
    @Override
    public final ToolBar loadToolBar() {
        // Build the Tool Bar for this Frame.
        _toolBar = new ProjectViewerToolBar( clientProperties );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    /**
     * Prints the Web View's main content via its Web Engine.
     */
    @Override
    public final void doPrint() {
        // NOTE: An application window has no insight into how to layout 
        //  WebView content, so we invoke WebKit's own printing layout engine.
        printManager.print( _webView.getEngine() );
    }

    private final FileStatus saveProjectToHtml( final FileWriter htmlFileWriter ) {
        // Avoid throwing unnecessary exceptions by filtering for bad writers.
        if ( htmlFileWriter == null ) {
            return FileStatus.WRITE_ERROR;
        }

        try {
            // Write the cached HTML to disc. This may be from the original
            // file, if the user opened a legacy Project Report or a legacy
            // Converted Project, or it may be from a newly converted Project
            // generated by the XML to HTML transformer.
            final StringBuilder htmlBuffer = _htmlBuffers.get( _currentPageIndex );
            htmlFileWriter.write( htmlBuffer.toString() );

            // Return the HTML writer's status, which subsumes exceptions.
            return FileStatus.SAVED;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return FileStatus.WRITE_ERROR;
        }
    }

    private final FileStatus saveProjectToPdf( final OutputStream outputStream ) {
        // Avoid throwing unnecessary exceptions by filtering for bad streams.
        if ( outputStream == null ) {
            return FileStatus.WRITE_ERROR;
        }

        try {
            // Use the cached HTML to convert to PDF and then write to disc.
            // This may be from the original file, if the user opened a legacy
            // Project Report or a legacy Converted Project, or it may be from a
            // newly converted Project generated by the XML to HTML transformer.
            final StringBuilder htmlBuffer = _htmlBuffers.get( _currentPageIndex );

            // Create a Page Format of standard Letter size with no margins.
            // NOTE: For now, we take the minimum for Letter and A4 in each
            //  dimension, so that the output will print properly for either one.
            // TODO: Use A4 size instead, if Locale isn't US or Canada?
            // TODO: Pass this in as a parameter instead, and present in GUI?
            final double pageWidth = FastMath.min( Paper.NA_LETTER.getWidth(), 
                                                   Paper.A4.getWidth() );
            final double pageHeight = FastMath.min( Paper.NA_LETTER.getHeight(), 
                                                    Paper.A4.getHeight() );
            final java.awt.print.Paper paper = new java.awt.print.Paper();
            paper.setSize( pageWidth, pageHeight );
            paper.setImageableArea( 0.0d, 0.0d, pageWidth, pageHeight );
            final java.awt.print.PageFormat pageFormat = new java.awt.print.PageFormat();
            pageFormat.setPaper( paper );

            // Convert the HTML to PDF using jPDFWriter.
            // TODO: Set Document Info fields (metadata) for PDF/A standard.
            final byte[] htmlByteArray = htmlBuffer.toString().getBytes();
            final PDFDocument document = PDFDocument
                    .loadHTML( htmlByteArray, null, pageFormat, true );

            // Save the PDF Document, using an OutputStream vs. a Writer due to
            // the entire file contents being stored as a byte array.
            document.saveDocument( outputStream );

            // Return the PDF document's status, which subsumes exceptions.
            return FileStatus.SAVED;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return FileStatus.WRITE_ERROR;
        }
    }

    private final void updateCache( final File file, final StringBuilder htmlBuffer ) {
        // Update the web view with the new HTML content.
        updateWebView( htmlBuffer );

        // Cache the original pre-conversion file for output defaulting.
        _files.add( file );

        // Cache the HTML buffer for reuse on file save and/or back/forward.
        _htmlBuffers.add( htmlBuffer );

        // Update the page index for the current page/file.
        _currentPageIndex = _htmlBuffers.size() - 1;

        // Update the frame title with the input name.
        updateFrameTitle( file, false );

        // Enable Back and Disable Forward after loading a new file.
        _toolBar._navigationButtons._backButton.setDisable( _currentPageIndex <= 0 );
        _toolBar._navigationButtons._forwardButton.setDisable( true );

        // Enable Save As, Page Setup and Print, after loading a new file.
        _toolBar._fileActionButtons._fileSaveAsButton.setDisable( false );
        _toolBar._fileActionButtons._filePageSetupButton.setDisable( false );
        _toolBar._fileActionButtons._filePrintButton.setDisable( false );
    }

    @SuppressWarnings("nls")
    private final void updateWebView( final StringBuilder htmlBuffer ) {
        // For some reason this control has issues with these tags: { ':'
        // '-' '?' }.
        // NOTE: Some <BR> tags contain unneeded extra attributes.
        // NOTE: Meta tags just mess things up in the HTML protocol.
        String html = htmlBuffer.toString();
        html = html.replaceAll( "(?i)<p/>", "<p></p>" );
        html = html.replaceAll( "(?i)<hr/>", "<hr>" );
        html = html.replaceAll( "(?i)<br[^/]*/>", "<br>" );
        html = html.replaceAll( "(?i)<meta .*", "" );

        _webView.getEngine().loadContent( html );
    }
}
