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
package com.mhschmieder.fxcontrols.stage;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.control.LabeledControlFactory;
import com.mhschmieder.fxcontrols.layout.RenderedGraphicsExportPreviewPane;
import com.mhschmieder.fxcontrols.swing.RenderedGraphicsPanel;
import com.mhschmieder.fxgraphics.io.RenderedGraphicsExportOptions;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.io.FileMode;
import com.mhschmieder.jcommons.io.FileStatus;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jvectorexport.eps.EpsExportUtilities;
import com.mhschmieder.jvectorexport.pdf.PdfExportUtilities;
import com.mhschmieder.jvectorexport.svg.SvgExportUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.io.File;

/**
 * This Preview window is for use with Rendered Graphics Export actions, mostly
 * as a sanity check as we currently must produce Java 2D API based content to
 * take advantage of existing Graphics2D Vector Graphics Export toolkits.
 * <p>
 * In some cases, JFXConverter is used to transcode JavaFX layouts to AWT, but
 * in other cases AWT and Swing must be generated directly due to having
 * different layout needs for output targets than within a GUI application.
 */
public final class RenderedGraphicsExportPreview extends ExportPreview {

    public static final String                      RENDERED_GRAPHICS_EXPORT_PREVIEW_TITLE_DEFAULT 
            = "Rendered Graphics Export Preview";

    // Declare the main content pane.
    public RenderedGraphicsExportPreviewPane       _renderedGraphicsExportPreviewPane;

    // Cache the Rendered Graphics Export Options.
    protected RenderedGraphicsExportOptions         _renderedGraphicsExportOptions;

    // Cache the option button labels.
    protected String                                _auxiliaryLabel;
    protected String                                _informationTablesLabel;
    protected String                                _optionalItemLabel;

    // Maintain a Swing Component reference for Rendered Graphics Export actions.
    protected RenderedGraphicsPanel                 _renderedGraphicsExportSource;

    @SuppressWarnings("nls")
    public RenderedGraphicsExportPreview( final String auxiliaryLabel,
                                          final String informationTablesLabel,
                                          final String optionalItemLabel,
                                          final ProductBranding productBranding,
                                          final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( Modality.WINDOW_MODAL,
               RENDERED_GRAPHICS_EXPORT_PREVIEW_TITLE_DEFAULT,
               "renderedGraphicsExportPreview",
               true,
               true,
               productBranding,
               pClientProperties );

        _renderedGraphicsExportOptions = new RenderedGraphicsExportOptions();

        _auxiliaryLabel = auxiliaryLabel;
        _informationTablesLabel = informationTablesLabel;
        _optionalItemLabel = optionalItemLabel;

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Override
    public FileStatus exportToEps( final File tempFile,
                                   final File file,
                                   final FileMode fileMode ) {
        // Avoid throwing unnecessary exceptions by filtering for no-ops.
        if ( _renderedGraphicsExportSource == null ) {
            return FileStatus.NOT_SAVED;
        }

        // Cache the current Rendered Graphics Export Options on the target
        // layout panel.
        _renderedGraphicsExportSource.setRenderedGraphicsExportOptions( 
                _renderedGraphicsExportOptions );

        // Unless we add a GUI field for creator, set this app as default
        // creator, as usually this DSC Header field refers to the app anyway.
        final StringBuilder savedFrom = new StringBuilder();
        savedFrom.append( "Saved from " );
        savedFrom.append( _productBranding.productVersionProtected );
        savedFrom.append( "; Locale = " );
        savedFrom.append( clientProperties.locale.getDisplayName() );
        final String creator = savedFrom.toString();

        // Write the EPS contents indirectly to the supplied file via
        // paintComponent(), using RGB Mode, and vectorized text (especially so
        // that rotated text is rotated in EPS).
        //
        // TODO: Query the page width and page height per export action, or
        //  grab from the current Page Setup attributes? There are no limits on
        //  allowed values; units are points (1/72 inch) but it is common to
        //  specify North American Letter paper size and then convert.
        //
        // TODO: Expose the Color Mode as a user choice in the Export Options?
        final boolean fileSaved = EpsExportUtilities
                .createDocument( tempFile,
                                 _renderedGraphicsExportSource,
                                 _renderedGraphicsExportSource.getTitle(),
                                 creator );

        return fileSaved ? FileStatus.EXPORTED : FileStatus.NOT_SAVED;
    }

    @Override
    public FileStatus exportToPdf( final File tempFile,
                                   final File file,
                                   final FileMode fileMode ) {
        // Avoid throwing unnecessary exceptions by filtering for no-ops.
        if ( _renderedGraphicsExportSource == null ) {
            return FileStatus.NOT_SAVED;
        }

        // Cache the current Rendered Graphics Export Options on the target
        // layout panel.
        _renderedGraphicsExportSource.setRenderedGraphicsExportOptions( 
                _renderedGraphicsExportOptions );

        // Until we add a GUI field for author, set this app as default author.
        final StringBuilder savedFrom = new StringBuilder();
        savedFrom.append( "Saved from " ); //$NON-NLS-1$
        savedFrom.append( _productBranding.productVersionProtected );
        savedFrom.append( "; Locale = " ); //$NON-NLS-1$
        savedFrom.append( clientProperties.locale.getDisplayName() );
        final String author = savedFrom.toString();

        // Write the PDF contents indirectly to the supplied file via
        // paintComponent(), using RGB Mode.
        //
        // TODO: Query the page width and page height per export action, or
        //  grab from the current Page Setup attributes? There are no limits on
        //  allowed values; units are points (1/72 inch) but it is common to
        //  specify North American Letter paper size and then convert.
        //
        // TODO: Expose the Color Mode as a user choice in the Export Options?
        final boolean fileSaved = PdfExportUtilities
                .createDocument( tempFile,
                                 _renderedGraphicsExportSource,
                                 _renderedGraphicsExportSource.getTitle(),
                                 author );

        return fileSaved ? FileStatus.EXPORTED : FileStatus.NOT_SAVED;
    }

    @Override
    public FileStatus exportToSvg( final File tempFile,
                                   final File file,
                                   final FileMode fileMode ) {
        // Avoid throwing unnecessary exceptions by filtering for no-ops.
        if ( _renderedGraphicsExportSource == null ) {
            return FileStatus.NOT_SAVED;
        }

        // Cache the current Rendered Graphics Export Options on the target
        // layout panel.
        _renderedGraphicsExportSource.setRenderedGraphicsExportOptions( 
                _renderedGraphicsExportOptions );

        // Write the SVG contents indirectly to the supplied file via
        // paintComponent(), using RGB Mode.
        //
        // TODO: Query the page width and page height per export action, or
        //  grab from the current Page Setup attributes? There are no limits on
        //  allowed values; units are points (1/72 inch) but it is common to
        //  specify North American Letter paper size and then convert.
        //
        // TODO: Expose the Color Mode as a user choice in the Export Options?
        final boolean fileSaved = SvgExportUtilities
                .createDocument( tempFile,
                                 _renderedGraphicsExportSource,
                                 _renderedGraphicsExportSource.getTitle() );

        return fileSaved ? FileStatus.EXPORTED : FileStatus.NOT_SAVED;
    }

    @Override
    protected Button getCancelButton() {
        final Button cancelButton = LabeledControlFactory.getCancelExportButton();
        return cancelButton;
    }

    @Override
    protected Button getExportButton() {
        final Button exportButton = LabeledControlFactory.getExportGraphicsButton( 
                "Export Rendered Graphics Using Contents of Preview" ); //$NON-NLS-1$
        return exportButton;
    }

    @Override
    protected VBox getExportOptionsBox() {
        final CheckBox auxiliaryCheckBox = GuiUtilities
                .getCheckBox( _auxiliaryLabel,
                              _renderedGraphicsExportOptions.isExportAuxiliaryPanel() );
        final CheckBox informationTablesCheckBox = GuiUtilities
                .getCheckBox( _informationTablesLabel,
                              _renderedGraphicsExportOptions.isExportInformationTables() );
        final CheckBox optionalItemCheckBox = GuiUtilities
                .getCheckBox( _optionalItemLabel,
                              _renderedGraphicsExportOptions.isExportOptionalItem() );

        // NOTE: We only show Export Options where there is at least one choice
        //  for refined scope. Otherwise the user has nothing to choose.
        final ObservableList< Node > exportOptionsNodes = FXCollections.observableArrayList();

        final boolean hasAuxiliary = ( _auxiliaryLabel != null )
                && ( _auxiliaryLabel.trim().length() > 0 );
        final boolean hasInformationTables = ( _informationTablesLabel != null )
                && ( _informationTablesLabel.trim().length() > 0 );
        final boolean hasOptionalItem = ( _optionalItemLabel != null )
                && ( _optionalItemLabel.trim().length() > 0 );
        if ( hasAuxiliary ) {
            exportOptionsNodes.add( auxiliaryCheckBox );

            // Bind the Export Auxiliary Radio Button to its associated
            // property.
            auxiliaryCheckBox.selectedProperty()
                    .bindBidirectional( _renderedGraphicsExportOptions
                            .exportAuxiliaryPanelProperty() );
        }
        if ( hasInformationTables ) {
            exportOptionsNodes.add( informationTablesCheckBox );

            // Bind the Export Information Tables Button to its associated
            // property.
            informationTablesCheckBox.selectedProperty()
                    .bindBidirectional( _renderedGraphicsExportOptions
                            .exportInformationTablesProperty() );
        }
        if ( hasOptionalItem ) {
            exportOptionsNodes.add( optionalItemCheckBox );

            // Bind the Export Optional Item Radio Button to its associated
            // property.
            optionalItemCheckBox.selectedProperty()
                    .bindBidirectional( _renderedGraphicsExportOptions
                            .exportOptionalItemProperty() );
        }

        final VBox exportOptionsBox = new VBox();
        exportOptionsBox.getChildren().setAll( exportOptionsNodes );

        exportOptionsBox.setPadding( new Insets( 10.0d, 10.0d, 10.0d, 10.0d ) );
        exportOptionsBox.setSpacing( 10.0d );

        return exportOptionsBox;
    }

    @SuppressWarnings("nls")
    private void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/fatCow/FileExtensionEps16.png", 1000d, 1000d, true, true );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _renderedGraphicsExportPreviewPane = new RenderedGraphicsExportPreviewPane( 
                clientProperties,
                _renderedGraphicsExportOptions );
        return _renderedGraphicsExportPreviewPane;
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this method to the Rendered Graphics Export Preview Pane.
        _renderedGraphicsExportPreviewPane.setForegroundFromBackground( backColor );
    }
    
    @Override 
    public boolean fileExport() {
        // Forward to the stock Rendered Graphics Export handler.
        return fileExportRenderedGraphics( this,   
                                           _defaultDirectory, 
                                           clientProperties, 
                                           getGraphicsCategory() );
    }

    @Override 
    public RenderedGraphicsExportOptions getRenderedGraphicsExportOptions() {
        return _renderedGraphicsExportOptions;
    }

    /**
     * This method sets the container reference for exported graphics.
     *
     * @param renderedGraphicsExportSource
     *            The Swing container for the layout group to be exported
     */
    public void setRenderedGraphicsExportSource( 
            final RenderedGraphicsPanel renderedGraphicsExportSource ) {
        // Cache the Graphics Export Source locally, for reference by file
        // actions.
        _renderedGraphicsExportSource = renderedGraphicsExportSource;

        // Forward this method to the Rendered Graphics Export Preview Pane.
        _renderedGraphicsExportPreviewPane
                .setRenderedGraphicsExportSource( renderedGraphicsExportSource );
    }

    @Override
    public void updateView() {
        // Forward this method to the Rendered Graphics Export Preview Pane.
        _renderedGraphicsExportPreviewPane.updateExportOptionsView();
    }
}
