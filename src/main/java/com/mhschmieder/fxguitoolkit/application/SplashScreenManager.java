/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.application;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.fxguitoolkit.stage.MainApplicationLoadTask;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Basic management of application startup splash screens, regardless of whether
 * hosted in a pop-up or a modeless window, and serving as a wrapper for the
 * layout pane that hosts the splash screen but not deriving from a layout class
 * as the duties go beyond simple GUI layout and involve app startup lifespan.
 */
public class SplashScreenManager {

    /**
     *  This interface is solely for the purpose of providing a simple mechanism
     *  for completing the display of the startup Splash Screen.
     */
    public interface InitCompletionHandler {
        void complete();
    }

    // Declare GUI components related to Splash Screen and startup.
    protected Pane                  splashScreenLayout;
    protected ImageView             splashScreenImageView;
    protected ProgressBar           loadProgress;
    protected String                progressText;
    protected Label                 progressLabel;

    public SplashScreenManager( final String jarRelativeSplashScreenFilename,
                                final ProductBranding productBranding ) {
        // Layout the Splash Screen so it is ready to go when we get a Stage.
        // NOTE: This is OK to do on the JavaFX Launcher Thread vs. the JavaFX
        //  Application Thread, as we hold off on working with Scenes or Stages,
        //  and as the image loading might take awhile so should be front-loaded.
        layoutSplashScreen( jarRelativeSplashScreenFilename, productBranding );
    }

    /**
     * Layout the Splash Screen so it is ready to go when we get a Stage.
     * 
     * @param jarRelativeSplashScreenFilename The JAR-relative path for the
     *                                        splash screen image filename
     * @param productBranding The {@link ProductBranding} for app name etc.                                    
     */
    private void layoutSplashScreen( final String jarRelativeSplashScreenFilename,
                                     final ProductBranding productBranding ) {
        // Background-load the Splash Screen Image as a JAR-resident resource,
        // then place it into an Image View container, so that it can be 
        // scaled and displayed.
        splashScreenImageView = ImageUtilities.getImageView( jarRelativeSplashScreenFilename,
                                                             true );

        // Create a Progress Bar the same width as the Splash Screen Image.
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth( GuiUtilities.SPLASH_WIDTH );
        progressText = "Loading " + productBranding.productVersion;
        progressLabel = new Label( progressText );

        splashScreenLayout = new VBox();
        splashScreenLayout.getChildren()
                .addAll( splashScreenImageView, loadProgress, progressLabel );

        progressLabel.setAlignment( Pos.CENTER );

        // Set border and colors to make the Splash Screen easier to see.
        // NOTE: This mimics the custom CSS tag #status-box for the About Box.
        final Background background = LayoutFactory.makeRegionBackground( Color.ALICEBLUE );
        splashScreenLayout.setBackground( background );
        splashScreenLayout.setPadding( new Insets( 3.0d ) );
        final BorderStroke borderStroke = new BorderStroke( Color.LIGHTSTEELBLUE,
                                                            BorderStrokeStyle.SOLID,
                                                            CornerRadii.EMPTY,
                                                            new BorderWidths( 3.0d ) );
        splashScreenLayout.setBorder( new Border( borderStroke ) );
    }

    /**
     * Create the Splash Screen and show it, using the initial stage.
     *
     * @param initialStage
     *            The initial stage created by the JavaFX engine at startup
     * @param task
     *            The task used to link the Splash Screen to the startup cycle
     * @param initCompletionHandler
     *            The handler for watching startup completion
     */
    public void showSplashScreen( final Stage initialStage,
                                  final Task< ? > task,
                                  final InitCompletionHandler initCompletionHandler ) {
        progressLabel.textProperty().bind( task.messageProperty() );
        loadProgress.progressProperty().bind( task.progressProperty() );
        task.stateProperty().addListener( ( observableValue, oldState, newState ) -> {
            if ( newState == Worker.State.SUCCEEDED ) {
                // Once the task is done, unbind the properties and set to
                // indeterminate so that there is no confusion if still visible.
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress( -1d );

                initialStage.toFront();

                final FadeTransition fadeSplash = new FadeTransition( Duration.seconds( 1.2 ),
                                                                      splashScreenLayout );
                fadeSplash.setFromValue( 1.0d );
                fadeSplash.setToValue( 0.0d );

                fadeSplash.setOnFinished( actionEvent -> initialStage.hide() );

                fadeSplash.play();

                initCompletionHandler.complete();
            }
        } );

        // Now that we are on the JavaFX Application Thread, it is safe to
        // create a Scene from the prepared layout pane.
        // TODO: Set the specific size at Scene creation time as well?
        final Scene splashScene = new Scene( splashScreenLayout, Color.TRANSPARENT );

        // Attempt to set transparency on the Splash Screen, if supported.
        initialStage.initStyle( StageStyle.TRANSPARENT );

        // Always center the Splash Screen on the screen.
        GuiUtilities.centerOnScreen( initialStage );

        // Set the Splash Screen to top, so it isn't blocked by other
        // applications. It is easy to dismiss, so shouldn't prove annoying.
        initialStage.setAlwaysOnTop( true );

        // Use the primary Stage created by the JavaFX engine to host the Scene
        // containing the Splash Screen and Progress Bar.
        initialStage.setScene( splashScene );

        // Show the Splash Screen, now that it is fully initialized.
        initialStage.show();
    }
    
    public MainApplicationLoadTask makeMainApplicationLoadTask() {
        return new MainApplicationLoadTask( progressText );
    }
}
