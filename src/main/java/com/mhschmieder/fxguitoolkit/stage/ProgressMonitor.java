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
package com.mhschmieder.fxguitoolkit.stage;

import java.util.List;

import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.mathtoolkit.MathUtilities;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A Progress Monitor for contexts that do not have an observable Task.
 * <p>
 * If you have an observable Task, use ControlsFX TaskProgressView instead.
 * <p>
 * This class is designed to be reusable for multiple contexts, by setting
 * the text and number of steps appropriate to each context before use.
 * <p>
 * A typical use case is to watch a Task or VirtualThread, especially for
 * current step, to update the percentage of steps performed.
 * <p>
 * Register "setOnAction()" on the Cancel Button in your application code,
 * to use this Progress Monitor to support cancellation of a task or thread.
 */
public class ProgressMonitor extends Stage {
    
    private Label progressBanner;
    private Label progressRatioLabel;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    
    private Button cancelButton;
    
    private int numberOfSteps;

    /**
     * Makes a ProgressMonitor custom Stage with all parameters specified.
     * 
     * @param title The title to use in the title bar of the Stage
     * @param jarRelativeIconFilename JAR-relative path for title bar icon
     * @param bannerText The text to use for the banner atop the controls
     * @param cancelText The text to use for the Cancel Button
     * @param pNumberOfSteps The total number of steps to monitor progress
     * @param preferredWidth The preferred width of this window
     * @param preferredHeight The preferred height of this window
     * @param systemType The OS system type for the client
     */
    public ProgressMonitor( final String title,
                            final String jarRelativeIconFilename,
                            final String bannerText,
                            final String cancelText,
                            final int pNumberOfSteps,
                            final double preferredWidth,
                            final double preferredHeight,
                            final SystemType systemType ) {
        // Always call the superclass constructor first!
        super( StageStyle.DECORATED );

        // Initialize the Modality as soon as possible (API contract).
        initModality( Modality.NONE );
        
        numberOfSteps = pNumberOfSteps;

        try {
            initStage( title, 
                       jarRelativeIconFilename, 
                       bannerText, 
                       cancelText, 
                       preferredWidth, 
                       preferredHeight, 
                       systemType );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initStage( final String title,
                              final String jarRelativeIconFilename,
                              final String bannerText,
                              final String cancelText,
                              final double preferredWidth,
                              final double preferredHeight,
                              final SystemType systemType ) {
        final Image minimizeIconImage = ImageUtilities.loadImageAsJarResource(
                jarRelativeIconFilename, false );
        
        progressBanner = new Label( bannerText );
        progressBanner.getStyleClass().add( "banner-text" );
       
        final Label progressControlsLabel = new Label( "Progress: " );
        progressRatioLabel = new Label();
        progressBar = new ProgressBar( -1.0d );
        progressIndicator = new ProgressIndicator( -1.0d );
        
        GridPane.setHalignment( progressBanner, HPos.LEFT );
        
        GridPane.setValignment( progressControlsLabel, VPos.TOP );
        GridPane.setValignment( progressRatioLabel, VPos.TOP );
        GridPane.setValignment( progressBar, VPos.TOP );
        GridPane.setValignment( progressIndicator, VPos.TOP );
        
        final GridPane gridPane = new GridPane();
        gridPane.setPadding( new Insets( 6.0d ) );
        gridPane.setHgap( 8.0d );
        gridPane.setVgap( 12.0d );
        gridPane.setAlignment( Pos.CENTER );
        
        gridPane.add( progressBanner, 0, 0, 3, 1 );
        
        gridPane.add( progressControlsLabel, 0, 2 );
        gridPane.add( progressRatioLabel, 1, 2 );
        gridPane.add( progressBar, 2, 2 );
        
        gridPane.add( progressIndicator, 3, 1, 1, 2 );
        
        final ButtonBar actionButtonBar = new ButtonBar();
        actionButtonBar.setPadding( new Insets( 6.0d, 12.0d, 6.0d, 12.0d ) );
        cancelButton = GuiUtilities.getLabeledButton( 
                cancelText, null, "cancel-button" );
        ButtonBar.setButtonData( cancelButton, ButtonData.CANCEL_CLOSE );
        cancelButton.setPrefWidth( 160.0d );
        final ObservableList< Node > actionButtons = actionButtonBar.getButtons();
        actionButtons.add( cancelButton );
        
       final BorderPane borderPane = new BorderPane();
        borderPane.setCenter( gridPane );
        borderPane.setBottom( actionButtonBar );
        
        final Scene scene = new Scene( borderPane );
        
        final List< String > jarRelativeStelesheetFilenames = GuiUtilities
                .getJarRelativeStylesheetFilenames( systemType );
        GuiUtilities.addStylesheetsAsJarResource( scene, 
                                                  jarRelativeStelesheetFilenames );
        
        final Color backColor = ColorConstants.WINDOW_BACKGROUND_COLOR;
        final Background background = LayoutFactory.makeRegionBackground( 
                backColor );
        gridPane.setBackground( background );
        GuiUtilities.setStylesheetForTheme( scene, 
                                            backColor, 
                                            bannerText, 
                                            cancelText );
        
        setWidth( preferredWidth );
        setHeight( preferredHeight );
        setResizable( false );
        
        if ( minimizeIconImage != null ) {
            getIcons().add( minimizeIconImage );            
        }
        setTitle( title );
        
        setScene( scene );
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
    
    public void setProgressBannerText( final String bannerText ) {
        progressBanner.setText( bannerText );
    }
    
    public void setCancelButtonText( final String cancelText ) {
        cancelButton.setText( cancelText );
    }
    
    public void setNumberOfSteps( final int pNumberOfSteps ) {
        numberOfSteps = pNumberOfSteps;
    }
    
    public void updateProgress( final int currentStep ) {
        final double progressRatio = MathUtilities.roundDecimal( 
                ( double ) currentStep / numberOfSteps, 2 );
        
        // Show the percentage of executed steps in the Progress Bar.
        progressRatioLabel.setText( Double.toString( progressRatio ) );
        progressBar.setProgress( progressRatio );
        progressIndicator.setProgress( progressRatio );
        
        if ( !isShowing() ) {
            show();
        }
    }
}
