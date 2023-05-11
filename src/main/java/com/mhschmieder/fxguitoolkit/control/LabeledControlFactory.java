/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.control;

import java.util.ResourceBundle;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.SceneGraphUtilities;
import com.mhschmieder.fxguitoolkit.action.BackgroundColorChoices;
import com.mhschmieder.fxguitoolkit.action.ExportActions;
import com.mhschmieder.fxguitoolkit.action.FileActions;
import com.mhschmieder.fxguitoolkit.action.LabeledActionFactory;
import com.mhschmieder.fxguitoolkit.action.SettingsActions;
import com.mhschmieder.fxguitoolkit.action.WindowSizeActions;
import com.mhschmieder.fxguitoolkit.action.XAction;
import com.mhschmieder.fxguitoolkit.action.XActionGroup;
import com.mhschmieder.fxguitoolkit.action.XActionUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * {@code LabeledControlFactory} is a factory class for minimizing copy/paste
 * code
 * for shared design patterns regarding controls such as action buttons.
 */
public final class LabeledControlFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private LabeledControlFactory() {}

    // TODO: Review whether this is still correct, now that we have to package
    // all non-Java resource files in a separate hierarchy from the Java package
    // for the source code.
    @SuppressWarnings("nls") public static final String BUNDLE_NAME =
                                                                    "properties.ActionLabels";

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth ) {
        final Button button = new Button( label );
        if ( font != null ) {
            button.setFont( font );
        }
        button.setAlignment( Pos.CENTER );

        button.setPrefWidth( buttonWidth );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final double buttonHeight ) {
        final Button button = getButton( label, font, buttonWidth );
        button.setPrefHeight( buttonHeight );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss ) {
        return getButton( label,
                          font,
                          buttonWidth,
                          backColorCss,
                          borderColorCss,
                          borderWidthCss,
                          "6" );
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String borderRadiusCss ) {
        final Button button = getButton( label, 
                                         font, 
                                         buttonWidth,
                                         backColorCss,
                                         "white",
                                         borderColorCss,
                                         borderWidthCss,
                                         borderRadiusCss );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String foreColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String borderRadiusCss ) {
        final Button button = getButton( label,
                                         font, 
                                         buttonWidth,
                                         backColorCss,
                                         foreColorCss,
                                         borderColorCss,
                                         borderWidthCss,
                                         borderRadiusCss,
                                         null );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String foreColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String borderRadiusCss,
                                    final String borderInsetsCss ) {
        final Button button = getButton( label, font, buttonWidth );

        GuiUtilities.applyLabeledButtonStyle( button,
                                              backColorCss,
                                              foreColorCss,
                                              borderColorCss,
                                              borderWidthCss,
                                              borderRadiusCss,
                                              borderInsetsCss );

        return button;
    }

   public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String iconFilename,
                                    final double iconWidth,
                                    final double iconHeight ) {
        final Button button = getButton( label,
                                         font,
                                         buttonWidth,
                                         backColorCss,
                                         borderColorCss,
                                         borderWidthCss );

        final ImageView icon = ImageUtilities.createIcon( iconFilename, iconWidth, iconHeight );
        button.setGraphic( icon );
        button.setGraphicTextGap( 8.0d );

        return button;
    }

    public static Button getIconButton( final String svgImage,
                                        final double buttonWidth,
                                        final double buttonHeight,
                                        final Color backgroundColor,
                                        final String tooltipText ) {
        final SVGPath buttonIcon = GuiUtilities.getSvgImage( svgImage );

        final Button button = new Button();
        button.setShape( buttonIcon );
        button.setBackground( LayoutFactory.makeRegionBackground( backgroundColor ) );

        button.setMinSize( buttonWidth, buttonHeight );
        button.setPrefSize( buttonWidth, buttonHeight );
        button.setMaxSize( buttonWidth, buttonHeight );

        if ( ( tooltipText != null ) && !tooltipText.isEmpty() ) {
            final Tooltip tooltip = new Tooltip( tooltipText );
            button.setTooltip( tooltip );
        }

        return button;
    }

    public static ToggleButton getToggleButton( final String label,
                                                final Font font,
                                                final double buttonWidth,
                                                final double buttonHeight ) {
        final ToggleButton toggleButton = new ToggleButton( label );
        if ( font != null ) {
            toggleButton.setFont( font );
        }
        toggleButton.setAlignment( Pos.CENTER );

        toggleButton.setPrefSize( buttonWidth, buttonHeight );

        return toggleButton;
    }

    public static Label getLabel( final String labelText, final Font font ) {
        return getLabel( labelText, font, Pos.CENTER );
    }

    public static Label getLabel( final String labelText, final Font font, final Paint textFill ) {
        final Label label = getLabel( labelText, font );
        label.setTextFill( textFill );

        return label;
    }

    public static Label getLabel( final String labelText,
                                  final Font font,
                                  final double prefWidth ) {
        final Label label = getLabel( labelText, font );
        label.setPrefWidth( prefWidth );

        return label;
    }

    public static Label getLabel( final String labelText,
                                  final Font font,
                                  final Paint textFill,
                                  final double prefWidth ) {
        final Label label = getLabel( labelText, font, textFill );
        label.setPrefWidth( prefWidth );

        return label;
    }

    public static Label getLabel( final String labelText, final Font font, final Pos position ) {
        final Label label = new Label( labelText );
        if ( font != null ) {
            label.setFont( font );
        }
        label.setAlignment( position );

        // Text Alignment relates specifically to word wrap alignment.
        label.setTextAlignment( TextAlignment.CENTER );
        label.setWrapText( true );

        return label;
    }

    @SuppressWarnings("nls")
    public static Button getHelpButton( final boolean needsGraphic ) {
        final Button helpButton = GuiUtilities.getLabeledButton( "Help", null, "help-button" );

        if ( needsGraphic ) {
            final ImageView helpIcon = ImageUtilities.getImageView( "/icons/led24/Help16.png",
                                                                    true );
            helpButton.setGraphic( helpIcon );
        }

        return helpButton;
    }

    @SuppressWarnings("nls")
    public static Button getPageSetupButton( final ClientProperties pClientProperties ) {
        final Button button =
                            GuiUtilities.getIconButton( "/icons/yusukeKamiyamane/Setup16.png" );

        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "file",
                                                  "pageSetup",
                                                  button,
                                                  null );

        return button;
    }

    @SuppressWarnings("nls")
    public static Button getPrintButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/everaldo/FilePrint16.png" );

        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "file",
                                                  "print",
                                                  button,
                                                  null );

        return button;
    }

    @SuppressWarnings("nls")
    public static Button getOpenButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/led24/PageWhiteZip16.png" );

        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "file",
                                                  "open",
                                                  button,
                                                  null );

        return button;
    }

    @SuppressWarnings("nls")
    public static Button getSaveAsButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/everaldo/FileSaveAs16.png" );

        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "file",
                                                  "saveAs",
                                                  button,
                                                  null );

        return button;
    }

    @SuppressWarnings("nls")
    public static Button getApplyButton( final String objectType ) {
        final String buttonLabel = "Apply";
        final String tooltipText = buttonLabel + " Changes to " + objectType;

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "apply-button" );
    }

    @SuppressWarnings("nls")
    public static Button getCancelButton( final String objectType ) {
        final String buttonLabel = "Cancel";
        final String tooltipText = buttonLabel + " Changes to " + objectType + " and Close Window";

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "cancel-button" );
    }

    @SuppressWarnings("nls")
    public static Button getCancelExportButton() {
        return GuiUtilities.getLabeledButton( "Cancel Export", null, "cancel-button" );
    }

    @SuppressWarnings("nls")
    public static Button getCancelImportButton() {
        return GuiUtilities.getLabeledButton( "Cancel Import", null, "cancel-button" );
    }

    @SuppressWarnings("nls")
    public static Button getCancelReportButton( final String tooltipText ) {
        return GuiUtilities.getLabeledButton( "Cancel Report", tooltipText, "cancel-button" );
    }

    @SuppressWarnings("nls")
    public static Button getDoneButton( final String objectType, final boolean modal ) {
        final String buttonLabel = modal ? "OK" : "Apply and Close";
        final String tooltipText = "Apply Changes to " + objectType + " and Close Window";

        final String cssStyleId = modal ? "ok-button" : "apply-and-close-button";

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, cssStyleId );
    }

    @SuppressWarnings("nls")
    public static Button getExportGraphicsButton( final String tooltipText ) {
        return GuiUtilities.getLabeledButton( "Export Graphics", tooltipText, "export-button" );
    }

    @SuppressWarnings("nls")
    public static Button getGenerateReportButton( final String tooltipText ) {
        return GuiUtilities
                .getLabeledButton( "Generate Report", tooltipText, "generate-report-button" );
    }

    @SuppressWarnings("nls")
    public static Button getGraphicsImportButton( final String tooltipText ) {
        return GuiUtilities.getLabeledButton( "Import Graphics", tooltipText, "import-button" );
    }

    @SuppressWarnings("nls")
    public static Button getHelpAlternateButton() {
        return GuiUtilities.getLabeledButton( "Additional Help", null, "help-alternate-button" );
    }

    @SuppressWarnings("nls")
    public static Button getInsertButton( final String objectType ) {
        final String buttonLabel = "Insert";
        final String tooltipText = buttonLabel + " New " + objectType;

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "insert-button" );
    }

    @SuppressWarnings("nls")
    public static String getFileMruHeader( final ClientProperties pClientProperties,
                                           final int mruFileNumber ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( pClientProperties, BUNDLE_NAME, false );
        final String fileMruNumber = "mru" + Integer.toString( mruFileNumber );
        final String fileMruHeader = GuiUtilities
                .getButtonText( "file", fileMruNumber, resourceBundle );
        return fileMruHeader;
    }

    @SuppressWarnings("nls")
    public static Button getResetButton( final String propertiesCategory ) {
        final String buttonLabel = "Reset";
        final String tooltipText = "Set Default " + propertiesCategory;

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "reset-button" );
    }

    @SuppressWarnings("nls")
    public static Button getRevertButton( final String objectType ) {
        final String buttonLabel = "Revert";
        final String tooltipText = buttonLabel + " Changes to " + objectType;

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "revert-button" );
    }

    @SuppressWarnings("nls")
    public static Button getSaveButton( final String propertiesCategory ) {
        final String buttonLabel = "Save";
        final String tooltipText = "Save Current " + propertiesCategory;

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "save-button" );
    }

    @SuppressWarnings("nls")
    public static ToggleButton getTextWrapToggleButton() {
        final ToggleButton toggleButton = GuiUtilities
                .getIconToggleButton( "/icons/everaldo/MultiRow16.png" );

        final String tooltipText = "Toggle Wrap Text Mode";
        toggleButton.setTooltip( new Tooltip( tooltipText ) );

        return toggleButton;
    }

    @SuppressWarnings("nls")
    public static Button getRefreshButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/deviantArt/Update16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "view",
                                                  "refresh",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getResetButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities
                .getIconButton( "/icons/nineteenEightySeven/FormReset16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "view",
                                                  "reset",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getResetButton( final ClientProperties pClientProperties,
                                         final XAction resetAction ) {
        return SceneGraphUtilities.getLabeledButton( resetAction, "reset-button" );
    }

    @SuppressWarnings("nls")
    public static String getEditPropertiesLabel( final ClientProperties pClientProperties ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( pClientProperties, BUNDLE_NAME, false );
        final String editPropertiesLabel = GuiUtilities
                .getButtonText( "edit", "properties", resourceBundle );
        return editPropertiesLabel;
    }

    @SuppressWarnings("nls")
    public static int getEditPropertiesMnemonicIndex( final ClientProperties pClientProperties ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( pClientProperties, BUNDLE_NAME, false );
        final int editPropertiesMnemonicIndex = GuiUtilities
                .getMnemonicIndex( "edit", "properties", resourceBundle );
        return editPropertiesMnemonicIndex;
    }

    @SuppressWarnings("nls")
    public static Button getNavigateBackButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/ahaSoft/Back16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "navigate",
                                                  "back",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getNavigateForwardButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/ahaSoft/Forward16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "navigate",
                                                  "forward",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getSessionLogNewButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities
                .getIconButton( "/icons/deviantArt/PowerRestartInvert16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "sessionLog",
                                                  "new",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getSessionLogUpdateButton( final ClientProperties pClientProperties ) {
        final Button button = GuiUtilities.getIconButton( "/icons/deviantArt/Update16.png" );
        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "sessionLog",
                                                  "update",
                                                  button,
                                                  null );
        return button;
    }

    @SuppressWarnings("nls")
    public static Button getExportSessionLogButton( final ClientProperties pClientProperties ) {
        final Button button =
                            GuiUtilities.getIconButton( "/icons/everaldo/Txt2Mimetype16.png" );

        SceneGraphUtilities.setControlProperties( pClientProperties,
                                                  BUNDLE_NAME,
                                                  "export",
                                                  "sessionLog",
                                                  button,
                                                  null );

        return button;
    }

    @SuppressWarnings("nls")
    public static Button getEditNotesButton( final String tooltipText ) {
        final Button editNotesButton = GuiUtilities
                .getLabeledButton( "Edit Notes", tooltipText, "edit-notes-button" );
        final ImageView editNotesIcon = ImageUtilities
                .getImageView( "/icons/visualIdiot/Notes16.png", true );
        editNotesButton.setGraphic( editNotesIcon );
        return editNotesButton;
    }

    @SuppressWarnings("nls")
    public static XToggleButton getDisplayToggleButton( final String targetName,
                                                        final boolean applyAspectRatio,
                                                        final boolean selected ) {
        final String selectedText = "Visible";
        final String deselectedText = "Hidden";
        final String tooltipText = "Click to Toggle " + targetName
                + " Display Between Visible and Hidden";

        // NOTE: JavaFX CSS automatically darkens unselected buttons, and
        // auto-selects the foreground for text fill, but we mimic Compass.
        final XToggleButton toggleButton = new XToggleButton( selectedText,
                                                              deselectedText,
                                                              tooltipText,
                                                              "visible-toggle",
                                                              applyAspectRatio,
                                                              3.0d,
                                                              false,
                                                              selected );

        return toggleButton;
    }

    @SuppressWarnings("nls")
    public static Button getPredictButton( final ClientProperties pClientProperties ) {
        return SceneGraphUtilities.getLabeledButton( pClientProperties,
                                                     BUNDLE_NAME,
                                                     "tools",
                                                     "predict",
                                                     ColorConstants.PREDICT_BACKGROUND_COLOR );
    }

    public static Button getPredictButton( final ClientProperties pClientProperties,
                                           final XAction predictAction ) {
        return SceneGraphUtilities.getLabeledButton( predictAction,
                                                     ColorConstants.PREDICT_BACKGROUND_COLOR );
    }

    @SuppressWarnings("nls")
    public static Button getCancelButton( final ClientProperties pClientProperties ) {
        return SceneGraphUtilities.getLabeledButton( pClientProperties,
                                                     BUNDLE_NAME,
                                                     "tools",
                                                     "cancel",
                                                     ColorConstants.CANCEL_BACKGROUND_COLOR );
    }

    @SuppressWarnings("nls")
    public static Button getClearButton( final ClientProperties pClientProperties ) {
        return SceneGraphUtilities.getLabeledButton( pClientProperties,
                                                     BUNDLE_NAME,
                                                     "tools",
                                                     "clear",
                                                     ColorConstants.CLEAR_BACKGROUND_COLOR );
    }

    public static Button getClearButton( final ClientProperties pClientProperties,
                                         final XAction clearAction ) {
        return SceneGraphUtilities.getLabeledButton( clearAction,
                                                     ColorConstants.CLEAR_BACKGROUND_COLOR );
    }

    public static Menu getWindowSizeMenu( final ClientProperties pClientProperties,
                                          final WindowSizeActions windowSizeActions,
                                          final boolean maximumSizeSupported ) {
        final XActionGroup windowSizeActionGroup = LabeledActionFactory
                .makeWindowSizeActionGroup( pClientProperties,
                                            windowSizeActions,
                                            maximumSizeSupported );
        final Menu windowSizeMenu = XActionUtilities.createMenu( windowSizeActionGroup );
        return windowSizeMenu;
    }

    public static Menu getBackgroundColorMenu( final ClientProperties pClientProperties,
                                               final BackgroundColorChoices backgroundColorChoices ) {
        final XActionGroup backgroundColorChoiceGroup = LabeledActionFactory
                .getBackgroundColorChoiceGroup( pClientProperties, backgroundColorChoices );
        final Menu backgroundColorMenu = XActionUtilities.createMenu( backgroundColorChoiceGroup );
        return backgroundColorMenu;
    }
    
    public static Menu getExportMenu( final ClientProperties pClientProperties,
                                      final ExportActions exportActions,
                                      final boolean vectorGraphicsSupported,
                                      final boolean formattedVectorGraphicsSupported ) {
        final XActionGroup exportActionGroup = LabeledActionFactory
                .getExportActionGroup( pClientProperties,
                                       exportActions,
                                       vectorGraphicsSupported,
                                       formattedVectorGraphicsSupported );
        final Menu exportMenu = XActionUtilities.createMenu( exportActionGroup );
        return exportMenu;
    }

    public static Menu getFileMenu( final ClientProperties pClientProperties,
                                    final FileActions fileActions,
                                    final boolean vectorGraphicsSupported,
                                    final boolean formattedVectorGraphicsSupported ) {
        final XActionGroup fileActionGroup = LabeledActionFactory
                .getFileActionGroup( pClientProperties,
                                     fileActions,
                                     vectorGraphicsSupported,
                                     formattedVectorGraphicsSupported );
        final Menu fileMenu = XActionUtilities.createMenu( fileActionGroup );
        return fileMenu;
    }

    public static Menu getSettingsMenu( final ClientProperties pClientProperties,
                                        final SettingsActions settingsActions,
                                        final boolean maximumSizeSupported ) {
        final XActionGroup settingsActionGroup = LabeledActionFactory
                .getSettingsActionGroup( pClientProperties, settingsActions, maximumSizeSupported );
        final Menu settingsMenu = XActionUtilities.createMenu( settingsActionGroup );
        return settingsMenu;
    }

    public static Button getCreateLayerButton() {
        final String buttonLabel = "Create"; //$NON-NLS-1$
        final String tooltipText = "Create New Layer after Selected Row"; //$NON-NLS-1$

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "create-button" ); //$NON-NLS-1$
    }

    public static Button getDeleteLayerButton() {
        final String buttonLabel = "Delete"; //$NON-NLS-1$
        final String tooltipText = "Delete Selected Layer(s) from Table"; //$NON-NLS-1$

        return GuiUtilities.getLabeledButton( buttonLabel, tooltipText, "delete-button" ); //$NON-NLS-1$
    }
}
