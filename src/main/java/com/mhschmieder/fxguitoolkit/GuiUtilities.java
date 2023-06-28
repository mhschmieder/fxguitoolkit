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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.tools.Borders;

import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.control.XToggleButton;
import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.PrinterJob;
import javafx.print.PrinterJob.JobStatus;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * {@code GuiUtilities} is a utility class for methods related to top-level
 * JavaFX GUI functionality.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class GuiUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private GuiUtilities() {}

    /**
     *
     */
    public static final int                             FRAME_TITLE_ICON_SIZE           = 16;

    /**
     *
     */
    public static final int                             FRAME_TITLE_ICON_INSET          = 2;

    /**
     *
     */
    public static final int                             MENU_ICON_SIZE                  = 16;

    /**
     *
     */
    public static final int                             MENU_ICON_INSET                 = 2;

    /**
     *
     */
    public static final int                             TOOL_BAR_HEIGHT                 = 40;

    /**
     *
     */
    public static final int                             TOOLBAR_ICON_SIZE               = 24;

    /**
     *
     */
    public static final int                             TOOLBAR_ICON_INSET              = 4;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_SIZE         = 32;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_INSET        = 6;

    // Predetermined Splash Screen dimensions; image will scale to fit.
    public static final int                             SPLASH_WIDTH                    = 600;
    public static final int                             SPLASH_HEIGHT                   = 400;

    public static final int                             LABEL_EDITOR_WIDTH_DEFAULT      = 320;

    // Default smallest screen size (4:3 AR), based on laptops (not netbooks).
    // :OTE: The next level up is typically 1280 x 1024, which is more useful.
    // NOTE: For retina displays, it is more commonly 1366 x 768 (native),
    // 1344 x 756 or 1280 x 720 (16:9), 1152 x 720 (16:10) or 1024 x 768 (4:3).
    public static final int                             LEGACY_SCREEN_WIDTH_DEFAULT     = 1024;
    public static final int                             LEGACY_SCREEN_HEIGHT_DEFAULT    = 768;

    // Modern screen size assumptions are based on 16:10 (in this case) or 16:9.
    public static final int                             SCREEN_WIDTH_DEFAULT            = 1440;
    public static final int                             SCREEN_HEIGHT_DEFAULT           = 900;

    // Set the minimum width and height for primary application windows.
    public static final int                             MINIMUM_WINDOW_WIDTH            = 500;
    public static final int                             MINIMUM_WINDOW_HEIGHT           = 300;

    // Toggle Buttons tend to be given bindings related to aspect ratio, and
    // must be given an initial preferred size or the bindings don't kick in on
    // the first layout round, so we experimented to find the width that is
    // least likely to make the button get taller -- on Windows 10, at least.
    public static final int                             TOGGLE_BUTTON_WIDTH_DEFAULT     = 72;

    /**
     * Labels by default are made as small as possible to contain their text,
     * but we prefer to have sufficient horizontal and vertical gaps for
     * legibility and separation of neighboring controls.
     */
    public static final Insets                          STATUS_LABEL_INSETS_DEFAULT     =
                                                                                    new Insets( 3.0d,
                                                                                                10.0d,
                                                                                                3.0d,
                                                                                                10.0d );

    /**
     * Define a label delimiter for when a label is horizontally paired with a
     * user input control.
     */
    @SuppressWarnings("nls") public static final String LABEL_DELIMITER                 = ": ";

    /**
     * This is the most common inset for most contexts of icon hosting, to avoid
     * clutter, but if we add menus later on, those usually use an inset of 2.
     */
    public static final double                          DEFAULT_ICON_INSET              = 4;

    // We have chosen the ampersand as the mnemonic marker, to be compatible
    // with Qt and other GUI toolkits, so that it is more likely that resource
    // bundles can be shared.
    public static final char                            SWING_MNEMONIC_MARKER           = '&';

    // JavaFX has its own built-in mnemonic marker.
    public static final char                            JAVAFX_MNEMONIC_MARKER          = '_';

    // TODO: Make a bunch of partial CSS string constants, to reduce
    // copy/paste.
    public static final String                          UNDECORATED_BORDERED_REGION_CSS =
                                                                                        "-fx-content-display: center; -fx-padding: 16; -fx-background-color: black; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 7.5;";
    public static final String                          UNDECORATED_LABELED_CSS         =
                                                                                "-fx-content-display: center; -fx-padding: 4 8 4 8; -fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 7.5;";

    // To avoid cut/paste errors with resource references, make global constants
    // for the CSS theme to be used for dark vs. light backgrounds.
    @SuppressWarnings("nls") public static final String DARK_BACKGROUND_CSS             =
                                                                            "/css/theme-dark.css";
    @SuppressWarnings("nls") public static final String LIGHT_BACKGROUND_CSS            =
                                                                             "/css/theme-light.css";

    public static void addStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                   final String jarRelativeStylesheetFilename ) {
        // If no valid style sheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilename == null )
                || ( jarRelativeStylesheetFilename.length() < 5 ) ) {
            return;
        }

        final URL stylesheetUrl = GuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        try {
            // If not found, the returned string is null, so we should either
            // check for null and throw an exception, or let the null string
            // throw an exception when used, which we capture below.
            final String stylesheetFilename = stylesheetUrl.toExternalForm();

            // NOTE: CSS loading can be timing-sensitive to JavaFX API calls
            // that also affect style attributes, so it might be safer to defer
            // the CSS loading so that it is applied to a more stable GUI.
            stylesheetFilenames.add( stylesheetFilename );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public static void addStylesheetAsJarResource( final Parent parent,
                                                   final String jarRelativeStylesheetFilename ) {
        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
    }

    public static void addStylesheetAsJarResource( final Scene scene,
                                                   final String jarRelativeStylesheetFilename ) {
        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
    }

    public static void addStylesheetsAsJarResource( final Parent parent,
                                                    final List< String > jarRelativeStylesheetFilenames ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilenames == null )
                || ( jarRelativeStylesheetFilenames.isEmpty() ) ) {
            return;
        }

        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        for ( final String jarRelativeStylesheetFilename : jarRelativeStylesheetFilenames ) {
            addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
        }
    }

    public static void addStylesheetsAsJarResource( final Scene scene,
                                                    final List< String > jarRelativeStylesheetFilenames ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilenames == null )
                || ( jarRelativeStylesheetFilenames.isEmpty() ) ) {
            return;
        }

        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        for ( final String jarRelativeStylesheetFilename : jarRelativeStylesheetFilenames ) {
            addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilename );
        }
    }

    @SuppressWarnings("nls")
    public static List< String > getJarRelativeStylesheetFilenames( final SystemType systemType ) {
        // NOTE: The CSS files are copied from FxGuiToolkit as a starting point
        // and thus doesn't even begin to yet match our LAF for main Desktop.
        final List< String > jarRelativeStylesheetFilenames = new ArrayList<>();
        jarRelativeStylesheetFilenames.add( "/css/skin.css" );
        final String fontStylesheet = SystemType.MACOS.equals( systemType )
            ? "/css/font-mac.css"
            : "/css/font.css";
        jarRelativeStylesheetFilenames.add( fontStylesheet );
        return jarRelativeStylesheetFilenames;
    }

    @SuppressWarnings("nls")
    public static String getButtonLabel( final String groupName,
                                         final String itemName,
                                         final ResourceBundle resourceBundle ) {
        // There must always at least be a group name for each button.
        if ( ( groupName == null ) || groupName.trim().isEmpty() ) {
            return "";
        }

        // Composite the button name from the group and item names.
        final String buttonName = ( ( itemName == null ) || itemName.trim().isEmpty() )
            ? groupName
            : groupName + "." + itemName;

        // Generate the resource lookup key for the button label.
        final String resourceKey = buttonName + ".label";

        try {
            return resourceBundle.getString( resourceKey );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return '!' + buttonName + '!';
        }
    }

    @SuppressWarnings("nls")
    public static Label getTitleLabel( final String title ) {
        final Label titleLabel = new Label( title );

        // NOTE: This is temporary until we figure out why the CSS style from
        // the main stylesheet doesn't appear to be loaded when this is invoked.
        // titleLabel.getStyleClass().add( "title-text" );
        titleLabel
                .setStyle( "-fx-font-family: 'sans-serif'; -fx-font-size: 150.0%; -fx-font-style: normal; -fx-font-weight: bold; -fx-alignment: center;" );

        return titleLabel;
    }

    // TODO: Pass in the minimum height as a parameter?
    public static HBox getTitlePane( final Label titleLabel ) {
        final HBox titlePane = LayoutFactory.makeCenteredLabeledHBox( titleLabel );
        titlePane.setMinHeight( 32d );
        titleLabel.prefHeightProperty().bind( titlePane.heightProperty() );

        return titlePane;
    }

    // NOTE: This method should only be used for Latin alphanumeric
    // characters. See the Mnemonics.java example for a more complex and
    // foolproof methodology for finding the key code for a mnemonic. We
    // specify US-English (vs. just "English") to be safe, until support for
    // locale-sensitive menus is added to the GUI (it is already implemented).
    @SuppressWarnings("nls")
    public static char getMnemonicChar( final String key ) {
        final int mnemonicMarkerIndex = getMnemonicMarkerIndex( key );
        final int mnemonicIndex = ( mnemonicMarkerIndex >= 0 ) ? mnemonicMarkerIndex + 1 : 0;
        // final char mnemonicLabel = key.toUpperCase( Locale.getDefault() )
        return key.toUpperCase( Locale.forLanguageTag( "en-US" ) ).charAt( mnemonicIndex );
    }

    public static int getMnemonicIndex( final String groupName,
                                        final String itemName,
                                        final ResourceBundle resourceBundle ) {
        // Get the button label from the resource bundle, if applicable.
        final String buttonLabel = getButtonLabel( groupName, itemName, resourceBundle );
        if ( buttonLabel.trim().isEmpty() ) {
            return -1;
        }

        // Get the button displayed mnemonic index.
        // NOTE: The mnemonic marker index on the original label corresponds to
        // the mnemonic index on the stripped label.
        return getMnemonicMarkerIndex( buttonLabel );
    }

    // NOTE: The menu label property files would be very time-consuming to edit
    // safely for replacing the "&" with "_", so we use the old Swing symbol as
    // the lookup and then replace it with the JavaFX symbol downstream.
    public static int getMnemonicMarkerIndex( final String key ) {
        return key.indexOf( SWING_MNEMONIC_MARKER );
    }

    public static String handleMnemonicMarker( final String label, final boolean replaceMnemonic ) {
        final int mnemonicMarkerIndex = getMnemonicMarkerIndex( label );
        final int mnemonicIndex = ( mnemonicMarkerIndex >= 0 ) ? mnemonicMarkerIndex + 1 : 0;
        try {
            // NOTE: If no mnemonic marker is found, "-1" is returned, which is
            // then incremented to use the first character as the mnemonic (by
            // default).
            final String labelPreMnemonic = label.substring( 0, mnemonicMarkerIndex );
            final String labelPostMnemonic = label.substring( mnemonicIndex );

            // Conditionally strip the mnemonic marker from the label, or
            // replace the Swing mnemonic marker with the one for JavaFX.
            final StringBuilder adjustedLabel = new StringBuilder();
            adjustedLabel.append( labelPreMnemonic );
            if ( replaceMnemonic ) {
                adjustedLabel.append( JAVAFX_MNEMONIC_MARKER );
            }
            adjustedLabel.append( labelPostMnemonic );
            return adjustedLabel.toString();
        }
        catch ( final IndexOutOfBoundsException ioobe ) {
            ioobe.printStackTrace();
            return label;
        }
    }

    public static void applyLabeledButtonStyle( final Button button,
                                                final String backgroundColorCss,
                                                final String foregroundColorCss,
                                                final String borderColorCss,
                                                final String borderWidthCss ) {
        applyLabeledButtonStyle( button, 
                                 backgroundColorCss, 
                                 foregroundColorCss, 
                                 borderColorCss, 
                                 borderWidthCss, 
                                 "7.5" );
    }

    public static void applyLabeledButtonStyle( final Button button,
                                                final String backgroundColorCss,
                                                final String foregroundColorCss,
                                                final String borderColorCss,
                                                final String borderWidthCss,
                                                final String borderRadiusCss ) {
        applyLabeledButtonStyle( button,
                                 backgroundColorCss,
                                 foregroundColorCss,
                                 borderColorCss,
                                 borderWidthCss,
                                 borderRadiusCss,
                                 null );
    }

    public static void applyLabeledButtonStyle( final Button button,
                                                final String backgroundColorCss,
                                                final String foregroundColorCss,
                                                final String borderColorCss,
                                                final String borderWidthCss,
                                                final String borderRadiusCss,
                                                final String borderInsetsCss ) {
        // NOTE: Do not apply the fx-content-display as centered here, as
        // that causes buttons with both text and graphics to stack them
        // both in the center of the button, thus obscuring each other.
        final String padding = ( borderInsetsCss == null ) ? "-fx-padding: 6 8 6 8" : "";
        final String backgroundInsets = ( borderInsetsCss != null )
            ? "-fx-background-insets: " + borderInsetsCss
            : "";
        final String borderInsets = ( borderInsetsCss != null )
            ? "-fx-border-insets: " + borderInsetsCss
            : "";
        button.setStyle( "-fx-background-color: " + backgroundColorCss + "; " + backgroundInsets
                + "; -fx-background-radius: " + borderRadiusCss + "; -fx-border-color: "
                + borderColorCss + "; " + borderInsets + "; -fx-border-radius: " + borderRadiusCss
                + "; -fx-border-width: " + borderWidthCss + "; " + padding
                + "; -fx-text-fill: " + foregroundColorCss + ";" );
    }

    public static void applyApplicationButtonStyle( final Button button,
                                                    final String backgroundColorCss,
                                                    final String borderColorCss,
                                                    final String borderWidthCss ) {
        // NOTE: Do not apply the fx-content-display as centered here, as
        // that causes buttons with both text and graphics to stack them
        // both in the center of the button, thus obscuring each other.
        // TODO: Move this into CSS as state-specific styles, to avoid
        // so much copy/paste code in the toggle button action handlers.
        applyApplicationButtonStyle( button, 
                                     backgroundColorCss, 
                                     "white", 
                                     borderColorCss, 
                                     borderWidthCss );
    }

    public static void applyApplicationButtonStyle( final Button button,
                                                    final String backgroundColorCss,
                                                    final String foregroundColorCss,
                                                    final String borderColorCss,
                                                    final String borderWidthCss ) {
        // NOTE: Do not apply the fx-content-display as centered here, as
        // that causes buttons with both text and graphics to stack them
        // both in the center of the button, thus obscuring each other.
        // TODO: Move this into CSS as state-specific styles, to avoid
        // so much copy/paste code in the toggle button action handlers.
        applyLabeledButtonStyle( button, 
                                 backgroundColorCss, 
                                 foregroundColorCss, 
                                 borderColorCss, 
                                 borderWidthCss, 
                                 "45" );
    }

    public static void applyTextFieldStyle( final TextField textField,
                                            final String backColorCss,
                                            final String borderColorCss,
                                            final String borderWidthCss ) {
        // NOTE: Do not apply the fx-content-display as centered here, as
        // that causes buttons with both text and graphics to stack them
        // both in the center of the button, thus obscuring each other.
        textField.setStyle( "-fx-padding: 6 8 6 8" + "; -fx-background-color: " + backColorCss
                + "; -fx-text-fill: white; -fx-border-color: " + borderColorCss
                + "; -fx-border-width: " + borderWidthCss + "; -fx-border-radius: 45;" );
    }

    public static void applyCustomTextFieldStyle( final CustomTextField customTextField,
                                                  final String backColorCss,
                                                  final String borderColorCss,
                                                  final String borderWidthCss ) {
        // NOTE: Do not apply the fx-content-display as centered here, as
        // that causes buttons with both text and graphics to stack them
        // both in the center of the button, thus obscuring each other.
        // NOTE: This variant assumes a Node is added to the right side
        // of the Custom Text Field, and thus leaves no inset padding there.
        customTextField.setStyle( "-fx-padding: 6 0 6 8" + "; -fx-background-color: " + backColorCss
                + "; -fx-text-fill: white; -fx-border-color: " + borderColorCss
                + "; -fx-border-width: " + borderWidthCss + "; -fx-border-radius: 45;" );
    }

    public static void applyRoundButtonStyle( final Button button,
                                              final String backColorCss,
                                              final int radiusPixels,
                                              final String borderColorCss,
                                              final int borderWidthPixels ) {
        final int spanPixels = 2 * radiusPixels;

        // TODO: Review whether units these are in pixels by default, and
        // remove the "px" suffix in the CSS parameters if so.
        final String radiusPixelsCss = Integer.toString( radiusPixels ) + "px";
        final String spanPixelsCss = Integer.toString( spanPixels ) + "px";
        final String borderWidthPixelsCss = Integer.toString( borderWidthPixels ) + "px";

        button.setStyle( "-fx-content-display: center" + "; -fx-base: " + backColorCss
                + "; -fx-background-color: " + backColorCss + "; -fx-background-size: "
                + spanPixelsCss + ", " + spanPixelsCss + "; -fx-background-radius: "
                + radiusPixelsCss + "; -fx-border-color: " + borderColorCss + "; -fx-border-width: "
                + borderWidthPixelsCss + "; -fx-border-radius: " + radiusPixelsCss
                + "; -fx-min-width: " + spanPixelsCss + "; -fx-min-height: " + spanPixelsCss
                + "; -fx-max-width: " + spanPixelsCss + "; -fx-max-height: " + spanPixelsCss
                + "; -fx-pref-width: " + spanPixelsCss + "; -fx-pref-height: " + spanPixelsCss
                + ";" );
    }

    public static void applySolidRoundButtonStyle( final Button button,
                                                   final String backColorCss,
                                                   final int radiusPixels ) {
        final int borderWidthPixels = 1;
        applyRoundButtonStyle( button,
                               backColorCss,
                               radiusPixels,
                               backColorCss,
                               borderWidthPixels );
    }

    public static void applyRegionStyle( final Region region,
                                         final String backColorCss,
                                         final String borderColorCss,
                                         final String borderWidthCss ) {
        applyRegionStyle( region, backColorCss, borderColorCss, borderWidthCss, " 7.5" );
    }

    public static void applyRegionStyle( final Region region,
                                         final String backColorCss,
                                         final String borderColorCss,
                                         final String borderWidthCss,
                                         final String borderRadiusCss ) {
        region.setStyle( "-fx-content-display: center" + "; -fx-padding: 6 8 6 8"
                + "; -fx-background-color: " + backColorCss + "; -fx-border-color: "
                + borderColorCss + "; -fx-border-width: " + borderWidthCss + "; -fx-border-radius: "
                + borderRadiusCss + ";" );
    }

    public static VBox getImageBox( final Label imageLabel, final double imageSize ) {
        final VBox imageVBox = new VBox();
        imageVBox.getChildren().addAll( imageLabel );
        imageVBox.setAlignment( Pos.CENTER );
        imageVBox.setMinSize( imageSize, imageSize );
        imageVBox.setMaxSize( imageSize, imageSize );
        imageVBox.setPrefSize( imageSize, imageSize );

        return imageVBox;
    }

    public static SVGPath getSvgImage( final String svgContent ) {
        final SVGPath svgImage = new SVGPath();
        svgImage.setContent( svgContent );

        return svgImage;
    }

    public static Label getSvgImageLabel( final SVGPath svgImage,
                                          final Color svgColor,
                                          final double imageSize ) {
        final Label svgImageLabel = new Label();
        svgImageLabel.setAlignment( Pos.CENTER );
        svgImageLabel.setBackground( LayoutFactory.makeRegionBackground( svgColor ) );
        svgImageLabel.setOpacity( 100d );
        svgImageLabel.setMinSize( imageSize, imageSize );
        svgImageLabel.setMaxSize( imageSize, imageSize );
        svgImageLabel.setPrefSize( imageSize, imageSize );
        svgImageLabel.setShape( svgImage );

        return svgImageLabel;
    }

    public static Label getSvgImageLabel( final String svgContent,
                                          final Color svgColor,
                                          final double imageSize ) {
        final SVGPath svgImage = getSvgImage( svgContent );

        return getSvgImageLabel( svgImage, svgColor, imageSize );
    }

    public static VBox getSvgImageBox( final String svgContent,
                                       final Color svgColor,
                                       final double imageSize ) {
        final Label svgImageLabel = getSvgImageLabel( svgContent, svgColor, imageSize );

        return getImageBox( svgImageLabel, imageSize );
    }

    public static ToggleButton getSvgToggleButton( final SVGPath svgImage,
                                                   final Color svgColor,
                                                   final double imageSize,
                                                   final String tooltipText ) {
        final ToggleButton svgToggleButton = new ToggleButton();
        if ( ( tooltipText != null ) && !tooltipText.isEmpty() ) {
            svgToggleButton.setTooltip( new Tooltip( tooltipText ) );
        }
        svgToggleButton.setBackground( LayoutFactory.makeRegionBackground( svgColor ) );
        svgToggleButton.setMinSize( imageSize, imageSize );
        svgToggleButton.setMaxSize( imageSize, imageSize );
        svgToggleButton.setPrefSize( imageSize, imageSize );
        svgToggleButton.setShape( svgImage );

        return svgToggleButton;
    }

    public static ToggleButton getSvgToggleButton( final String svgContent,
                                                   final Color svgColor,
                                                   final double imageSize,
                                                   final String tooltipText ) {
        final SVGPath svgImage = getSvgImage( svgContent );

        return getSvgToggleButton( svgImage, svgColor, imageSize, tooltipText );
    }

    /**
     * Applies an {@link ImageView} hosted icon to a supplied {@link Labeled}
     * container, and then applies a drop-shadow effect to the container.
     *
     * @param labeled
     *            The {@link Labeled} container for the supplied icon
     * @param iconOnly
     *            {@code true} if the {@link Labeled} container is to
     *            contain only the icon and no text; {@code false} otherwise
     * @param icon
     *            The {@link ImageView} that hosts the icon to apply
     */
    public static void applyIcon( final Labeled labeled, final boolean iconOnly, final Node icon ) {
        try {
            // Set the icon, if it is a unique node in the scene graph.
            labeled.setGraphic( icon );
        }
        catch ( final IllegalArgumentException iae ) {
            iae.printStackTrace();
        }

        if ( iconOnly ) {
            // An icon container sometimes has no text, such as in a toolbar
            // context.
            labeled.setContentDisplay( ContentDisplay.GRAPHIC_ONLY );
        }

        // Style the icon to use consistent insets on all sides.
        labeled.setPadding( new Insets( DEFAULT_ICON_INSET ) );

        // Apply drop-shadow effects when the mouse enters a node.
        applyDropShadowEffect( labeled );
    }

    /**
     * Applies an icon to a supplied {@link Labeled} container, using a
     * resource filename to load the icon into an {@link ImageView}.
     *
     * @param labeled
     *            The {@link Labeled} container for the supplied icon
     * @param iconOnly
     *            {@code true} if the {@link Labeled} container is to
     *            contain only the icon and no text; {@code false} otherwise
     * @param iconFilename
     *            The resource filename for the icon
     */
    public static void applyIcon( final Labeled labeled,
                                  final boolean iconOnly,
                                  final String iconFilename ) {
        // Get the button icon from JAR-resident resources and apply it.
        final ImageView icon = ImageUtilities.createIcon( iconFilename );
        applyIcon( labeled, iconOnly, icon );
    }

    /**
     * Applies an icon to a supplied {@link Labeled} container, using a
     * resource filename to load the icon into an {@link ImageView}.
     *
     * @param labeled
     *            The {@link Labeled} container for the supplied icon
     * @param iconOnly
     *            {@code true} if the {@link Labeled} container is to
     *            contain only the icon and no text; {@code false} otherwise
     * @param iconFilename
     *            The resource filename for the icon
     * @param fitWidth
     *            The desired resulting width of the loaded icon
     * @param fitHeight
     *            The desired resulting height of the loaded icon
     */
    public static void applyIcon( final Labeled labeled,
                                  final boolean iconOnly,
                                  final String iconFilename,
                                  final double fitWidth,
                                  final double fitHeight ) {
        // Get the button icon from JAR-resident resources and apply it.
        final ImageView icon = ImageUtilities.createIcon( iconFilename, fitWidth, fitHeight );
        applyIcon( labeled, iconOnly, icon );
    }

    /**
     * Apply drop-shadow effects when the mouse enters the specified
     * {@link Node}
     *
     * @param node
     *            The {@link Node} to which drop-shadow animation should be
     *            applied
     */
    public static void applyDropShadowEffect( final Node node ) {
        // Apply a drop-shadow effect if a node was focus traversed via TAB, or
        // if the mouse is hovering over the node (even in passing through).
        // NOTE: If the current background is black, we need to make sure the
        // drop-shadow color is different as otherwise it won't show up. But as
        // we can't easily query that on the node inside the callback, we take a
        // one-shoe-fits-all approach with a color that shows up against all
        // background color choices as well as colors used throughout the app.
        // NOTE: We also must supply the blur radius, set to slightly more than
        // the default of 10%, as otherwise it doesn't show up against similar
        // backgrounds, and as 20% causes the mouse to select the wrong control.
        final DropShadow dropShadow = new DropShadow( 12d, Color.NAVY );
        node.setOnMouseEntered( mouseEvent -> node.setEffect( dropShadow ) );
        node.setOnMouseExited( mouseEvent -> node.setEffect( null ) );
        node.focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue.booleanValue() ) {
                node.setEffect( dropShadow );
            }
            else {
                node.setEffect( null );
            }
        } );
    }

    /**
     * Creates a random color every time the method is called.
     *
     * @return A random Color with full opacity
     */
    public static Paint randomColor() {
        final Random random = new Random();
        final int r = random.nextInt( 255 );
        final int g = random.nextInt( 255 );
        final int b = random.nextInt( 255 );

        return Color.rgb( r, g, b );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Region region ) {
        final Insets insets = region.getInsets();

        return detectResizeTarget( mouseEvent, scene, insets );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Insets insets ) {
        final double borderWidth = 8.0d;

        return detectResizeTarget( mouseEvent, scene, insets, borderWidth );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final Insets insets,
                                                   final double borderWidth ) {
        final double resizeMarginTop = FastMath.max( borderWidth, 0.5d * insets.getTop() );
        final double resizeMarginLeft = FastMath.max( borderWidth, 0.5d * insets.getLeft() );
        final double resizeMarginBottom = FastMath.max( borderWidth, 0.5d * insets.getBottom() );
        final double resizeMarginRight = FastMath.max( borderWidth, 0.5d * insets.getRight() );

        return detectResizeTarget( mouseEvent,
                                   scene,
                                   resizeMarginTop,
                                   resizeMarginRight,
                                   resizeMarginBottom,
                                   resizeMarginLeft );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Scene scene,
                                                   final double resizeMarginTop,
                                                   final double resizeMarginRight,
                                                   final double resizeMarginBottom,
                                                   final double resizeMarginLeft ) {
        final double yMin = mouseEvent.getSceneY();
        final double xMin = mouseEvent.getSceneX();
        final double yMax = scene.getHeight() - yMin;
        final double xMax = scene.getWidth() - xMin;

        return ResizeTarget.detectResizeTarget( yMin,
                                                xMin,
                                                yMax,
                                                xMax,
                                                resizeMarginTop,
                                                resizeMarginRight,
                                                resizeMarginBottom,
                                                resizeMarginLeft );
    }

    public static ResizeTarget detectResizeTarget( final MouseEvent mouseEvent,
                                                   final Region region,
                                                   final Bounds layoutBounds,
                                                   final double borderWidth ) {
        final double mouseX = mouseEvent.getX();
        final double mouseY = mouseEvent.getY();

        final Insets insets = region.getInsets();

        final double diffMinY = FastMath.abs( ( layoutBounds.getMinY() - mouseY ) + insets.getTop() );
        final double diffMinX = FastMath.abs( ( layoutBounds.getMinX() - mouseX ) + insets.getLeft() );
        final double diffMaxY = FastMath.abs( layoutBounds.getMaxY() - mouseY - insets.getBottom() );
        final double diffMaxX = FastMath.abs( layoutBounds.getMaxX() - mouseX - insets.getRight() );

        final double resizeMarginTop = FastMath.max( borderWidth, 0.5d * insets.getTop() );
        final double resizeMarginLeft = FastMath.max( borderWidth, 0.5d * insets.getLeft() );
        final double resizeMarginBottom = FastMath.max( borderWidth, 0.5d * insets.getBottom() );
        final double resizeMarginRight = FastMath.max( borderWidth, 0.5d * insets.getRight() );

        return ResizeTarget.detectResizeTarget( diffMinY,
                                                diffMinX,
                                                diffMaxY,
                                                diffMaxX,
                                                resizeMarginTop,
                                                resizeMarginRight,
                                                resizeMarginBottom,
                                                resizeMarginLeft );
    }

    public static Cursor getCursorForResizeTarget( final ResizeTarget resizeTarget ) {
        Cursor cursor = Cursor.DEFAULT;
        
        switch ( resizeTarget ) {
        case NONE:
            cursor = Cursor.DEFAULT;
            break;
        case TOP:
            cursor = Cursor.N_RESIZE;
            break;
        case TOP_RIGHT:
            cursor = Cursor.NE_RESIZE;
            break;
        case RIGHT:
            cursor = Cursor.E_RESIZE;
            break;
        case BOTTOM_RIGHT:
            cursor = Cursor.SE_RESIZE;
            break;
        case BOTTOM:
            cursor = Cursor.S_RESIZE;
            break;
        case BOTTOM_LEFT:
            cursor = Cursor.SW_RESIZE;
            break;
        case LEFT:
            cursor = Cursor.W_RESIZE;
            break;
        case TOP_LEFT:
            cursor = Cursor.NW_RESIZE;
            break;
        default:
            break;
        }

        return cursor;
    }

    public static void clampStageSize( final Stage stage ) {
        final double width = stage.getWidth();
        final double clampedWidth = getClampedWidth( stage, width );
        if ( clampedWidth != width ) {
            stage.setWidth( clampedWidth );
        }

        final double height = stage.getHeight();
        final double clampedHeight = getClampedHeight( stage, height );
        if ( clampedHeight != height ) {
            stage.setHeight( clampedHeight );
        }

    }

    public static double getClampedWidth( final Stage stage, final double resizeWidthCandidate ) {
        final Screen activeScreen = findActiveScreen( stage );
        final Rectangle2D screenBounds = activeScreen.getVisualBounds();

        return getClampedWidth( stage, resizeWidthCandidate, screenBounds );
    }

    public static double getClampedWidth( final Stage stage,
                                          final double resizeWidthCandidate,
                                          final Rectangle2D bounds ) {
        final double allowedWidth = bounds.getWidth();

        return getClampedWidth( stage, resizeWidthCandidate, allowedWidth );
    }

    public static double getClampedWidth( final Stage stage,
                                          final double resizeWidthCandidate,
                                          final double allowedWidth ) {
        return ( resizeWidthCandidate > stage.getMaxWidth() )
            ? stage.getMaxWidth()
            : ( resizeWidthCandidate < stage.getMinWidth() )
                ? stage.getMinWidth()
                : FastMath.min( resizeWidthCandidate, allowedWidth );
    }

    public static double getClampedHeight( final Stage stage, final double resizeHeightCandidate ) {
        final Screen activeScreen = findActiveScreen( stage );
        final Rectangle2D screenBounds = activeScreen.getVisualBounds();

        return getClampedHeight( stage, resizeHeightCandidate, screenBounds );
    }

    public static double getClampedHeight( final Stage stage,
                                           final double resizeHeightCandidate,
                                           final Rectangle2D bounds ) {
        final double allowedHeight = bounds.getHeight();
        return getClampedHeight( stage, resizeHeightCandidate, allowedHeight );
    }

    public static double getClampedHeight( final Stage stage,
                                           final double resizeHeightCandidate,
                                           final double allowedHeight ) {
        return ( resizeHeightCandidate > stage.getMaxHeight() )
            ? stage.getMaxHeight()
            : ( resizeHeightCandidate < stage.getMinHeight() )
                ? stage.getMinHeight()
                : FastMath.min( resizeHeightCandidate, allowedHeight );
    }

    public static Screen findActiveScreen( final Window window ) {
        final double minX = window.getX();
        final double minY = window.getY();
        final double width = window.getWidth();
        final double height = window.getHeight();
        final Rectangle2D bounds = new Rectangle2D( minX, minY, width, height );

        final List< Screen > screens = Screen.getScreens();

        for ( final Screen screen : screens ) {
            final Rectangle2D screenRect = screen.getVisualBounds();

            // First, check for simple containment, as only one
            // screen can fully contain the supplied window.
            if ( screenRect.contains( bounds ) ) {
                return screen;
            }

            // Next, check for intersection of the interior.
            if ( screenRect.intersects( bounds ) ) {
                return screen;
            }
        }

        return Screen.getPrimary();
    }

    public static void updateToggleButtonSilently( final ToggleButton toggleButton,
                                                   final EventHandler< ActionEvent > selectionHandler,
                                                   final boolean selected ) {
        // Remove any existing selection handler so we don't get infinite
        // recursion on selection change callbacks during manual updates.
        toggleButton.setOnAction( actionEvent -> {} );
        toggleButton.setSelected( selected );
        toggleButton.setOnAction( selectionHandler );
    }

    // Converts a color to an rgba syntax that works in JavaFX 8 CSS
    // where other syntaxes don't (but should; there are bugs in Java 8).
    //
    // Primarily, this method is needed when a color is specified with
    // an alpha value; colors with no alpha work using every available
    // CSS syntax, as does the default color "name" for "transparent".
    //
    // As with some other methods here, this one actually comes from
    // FxGuiToolkit's ColorUtilities class, which we use very little of.
    public static String colorToRgba( final Color color ) {
        return "rgba(" + Double.toString( FastMath.floor( color.getRed() * 255.0d ) ) + ", "
                + Double.toString( FastMath.floor( color.getGreen() * 255.0d ) ) + ", "
                + Double.toString( FastMath.floor( color.getBlue() * 255.0d ) ) + ", "
                + Double.toString( color.getOpacity() ) + ")";
    }

    // Never speak of this code... ever again!
    public static void resizeTextAreaHeight( final TextArea textArea ) {
        final double totalWidth = textArea.getPrefWidth();
        resizeTextAreaHeight( textArea, totalWidth );
    }

    // Never speak of this code... ever again!
    public static void resizeTextAreaHeight( final TextArea textArea, final double totalWidth ) {
        final String text = textArea.getText();

        final Label l = new Label( text );
        l.setFont( textArea.getFont() );
        l.applyCss();

        final Text t = new Text( text );
        t.setFont( textArea.getFont() );
        t.setWrappingWidth( totalWidth );
        t.applyCss();

        final HBox hl = new HBox();
        hl.setMinWidth( totalWidth );
        hl.setPrefWidth( totalWidth );
        hl.setMaxWidth( totalWidth );
        hl.getChildren().add( l );
        final Scene sl = new Scene( hl );

        final HBox ht = new HBox();
        ht.setMinWidth( totalWidth );
        ht.setPrefWidth( totalWidth );
        ht.setMaxWidth( totalWidth );
        ht.getChildren().add( t );
        final Scene st = new Scene( ht );

        final double lHeight = l.prefHeight( Region.USE_COMPUTED_SIZE ) + 16d;
        final double tHeight = t.prefHeight( Region.USE_COMPUTED_SIZE ) + 16d;
        final double finalHeight = FastMath.max( lHeight, tHeight );

        textArea.setMinHeight( finalHeight );
        textArea.setPrefHeight( finalHeight );
        textArea.setMaxHeight( finalHeight );
    }

    public static void adaptDividerToRegionBounds( final Region region ) {
        region.layoutBoundsProperty().addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue == null ) {
                return;
            }

            final double x = 0.5d * newValue.getWidth();
            final double y = 0.5d * newValue.getHeight();
            final Color white0 = Color.web( "white", 0.0d );

            final Stop[] dividerStops = new Stop[] {
                                                     new Stop( 0.0d, Color.WHITE ),
                                                     new Stop( 0.5d, white0 ),
                                                     new Stop( 1.0d, white0 ) };

            final RadialGradient dividerGradient = new RadialGradient( 0.0d,
                                                                       0.0d,
                                                                       x,
                                                                       y,
                                                                       newValue.getWidth(),
                                                                       false,
                                                                       CycleMethod.NO_CYCLE,
                                                                       dividerStops );

            region.setBackground( LayoutFactory.makeRegionBackground( dividerGradient ) );
        } );
    }

    /**
     * @param spinner
     *            The spinner to apply attributes to
     * @param tooltipText
     *            The tool tip text to use for the spinner
     * @param maximumSpinnerWidth
     *            Maximum spinner width in pixels
     */
    public static void applyNumberSpinnerAttributes( final Spinner< ? extends Number > spinner,
                                                     final String tooltipText,
                                                     final double maximumSpinnerWidth ) {
        final Tooltip tooltip = new Tooltip( tooltipText );
        spinner.setTooltip( tooltip );

        // Allow the user to type in a valid number that is in the list.
        spinner.setEditable( true );

        // Try to limit the size as this control can get too wide.
        spinner.setMaxWidth( maximumSpinnerWidth );

        // Set up the key listeners for the up and down arrow keys to increment
        // and decrement, as this built-in feature of the spinner doesn't work
        // once the spinner is set to editable, so we have to register listeners
        // on the textField to manually handle the arrow keys there instead.
        spinner.getEditor().setOnKeyPressed( event -> {
            switch ( event.getCode() ) {
            case UP:
                spinner.increment( 1 );
                break;
            case DOWN:
                spinner.decrement( 1 );
                break;
            // $CASES-OMITTED$
            default:
                break;
            }
        } );
    }

    /**
     * @param labeled
     *            The Labeled component that needs an icon in a tool bar context
     * @param icon
     *            The icon to apply to the Labeled component in a tool bar
     *            context
     */
    public static void applyToolbarIcon( final Labeled labeled, final Node icon ) {
        try {
            // Set the icon, if it is a unique node in the scene graph.
            labeled.setGraphic( icon );
        }
        catch ( final IllegalArgumentException iae ) {
            iae.printStackTrace();
        }

        // An icon (usually for a toolbar) generally has no text.
        labeled.setContentDisplay( ContentDisplay.GRAPHIC_ONLY );

        // Style the icon to use consistent insets on all sides.
        labeled.setPadding( new Insets( TOOLBAR_ICON_INSET ) );

        // Apply drop-shadow effects when the mouse enters a node.
        applyDropShadowEffect( labeled );
    }

    /**
     * @param labeled
     *            The Labeled component that needs an icon in a tool bar context
     * @param iconFilename
     *            The file name of the icon to apply to the Labeled component in
     *            a tool bar context
     */
    public static void applyToolbarIcon( final Labeled labeled, final String iconFilename ) {
        // Get the button icon from JAR-resident resources and apply it.
        final ImageView icon = ImageUtilities.createIcon( iconFilename );
        applyToolbarIcon( labeled, icon );
    }

    /**
     * This method centers a window on the screen, and takes the place of
     * Window.centerOnScreen() as that method doesn't seem to account for
     * screen resolution or other factors and thus results in off-centeredness.
     *
     * @param window
     *            The window to be centered on the screen
     */
    public static void centerOnScreen( final Window window ) {
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        window.setX( ( bounds.getMinX() + ( bounds.getWidth() / 2.0d ) )
                - ( SPLASH_WIDTH / 2.0d ) );
        window.setY( ( bounds.getMinY() + ( bounds.getHeight() / 2.0d ) )
                - ( SPLASH_HEIGHT / 2.0d ) );
    }

    /**
     * Clips the children of the specified {@link Region} to its current size.
     * This requires attaching a change listener to the region’s layout bounds
     * as JavaFX does not currently provide any built-in way to clip children.
     *
     * @param region
     *            The {@link Region} whose children to clip
     * @param arc
     *            The {@link Rectangle#arcWidth} and {@link Rectangle#arcHeight}
     *            of the clipping {@link Rectangle}
     * @throws NullPointerException
     *             If {@code region} is {@code null}
     */
    public static void clipChildren( final Region region, final double arc ) {
        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth( arc );
        outputClip.setArcHeight( arc );
        region.setClip( outputClip );

        region.layoutBoundsProperty()
                .addListener( ( observableValue, oldValue, newValue ) -> {
                    outputClip.setWidth( newValue.getWidth() );
                    outputClip.setHeight( newValue.getHeight() );
                } );
    }

    public static BackgroundImage getBackgroundImage( final Image image,
                                                      final boolean preserveRatio,
                                                      final double aspectRatio,
                                                      final double fitWidth,
                                                      final double fitHeight ) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        // Determine whether the source image Aspect Ratio should be preserved.
        // If not, use the supplied fit dimensions (if valid) or the intrinsic
        // image dimensions.
        final double fitWidthAdjusted = preserveRatio
            ? fitWidth
            : ( ( float ) aspectRatio != 0f )
                ? ( fitWidth > 0.0d )
                    ? fitWidth
                    : ( fitHeight > 0.0d ) ? fitHeight * aspectRatio : imageHeight * aspectRatio
                : ( fitWidth > 0.0d ) ? fitWidth : -1d;
        final double fitHeightAdjusted = preserveRatio
            ? fitHeight
            : ( ( float ) aspectRatio != 0f )
                ? ( fitHeight > 0.0d )
                    ? fitHeight
                    : ( fitWidth > 0.0d ) ? fitWidth / aspectRatio : imageWidth / aspectRatio
                : ( fitHeight > 0.0d ) ? fitHeight : -1d;

        final BackgroundSize backgroundSize = new BackgroundSize( fitWidthAdjusted,
                                                                  fitHeightAdjusted,
                                                                  false,
                                                                  false,
                                                                  true,
                                                                  true );
        final BackgroundImage backgroundImage = new BackgroundImage( image,
                                                                     BackgroundRepeat.NO_REPEAT,
                                                                     BackgroundRepeat.NO_REPEAT,
                                                                     BackgroundPosition.CENTER,
                                                                     backgroundSize );

        return backgroundImage;
    }

    /**
     * This method constructs an @HBox to center a @Label constructed from a
     * provided @String and set to adhere to style guidelines via custom CSS.
     *
     * @param bannerText
     *            The string to use for the Banner @Label
     * @return An @HBox that centers the Banner @Label
     */
    @SuppressWarnings("nls")
    public static HBox getBanner( final String bannerText ) {
        final Label bannerLabel = new Label( bannerText );
        bannerLabel.getStyleClass().add( "banner-text" );

        final HBox banner = new HBox();
        banner.getChildren().add( bannerLabel );
        banner.setAlignment( Pos.CENTER );

        return banner;
    }

    /**
     * The purpose of this method is to produce a consistent background style
     * for use on all button controls, using a supplied custom background color
     * and a corner radius designed to give slightly rounded corners.
     *
     * @param backColor
     *            The desired background @Color for the @Button client
     * @return A @Background object targeted to setBackground()
     */
    public static Background getButtonBackground( final Color backColor ) {
        if ( backColor == null ) {
            return null;
        }

        final Background background = new Background( new BackgroundFill( backColor,
                                                                          new CornerRadii( 3 ),
                                                                          Insets.EMPTY ) );

        return background;
    }

    // Get the button text from the resource bundle, if applicable.
    public static String getButtonText( final String groupName,
                                        final String itemName,
                                        final ResourceBundle resourceBundle ) {
        // Get the button label from the resource bundle, if applicable.
        final String buttonLabel = getButtonLabel( groupName, itemName, resourceBundle );
        if ( ( buttonLabel == null ) || buttonLabel.trim().isEmpty() ) {
            return null;
        }

        // Strip the mnemonic marker from the button label.
        return handleMnemonicMarker( buttonLabel, false );
    }

    /**
     * @param label
     *            The label to apply to the Check Box
     * @param selected
     *            The initial selected status of the Check Box
     * @return The newly constructed Check Box
     */
    public static CheckBox getCheckBox( final String label, final boolean selected ) {
        final CheckBox checkBox = new CheckBox( label );
        checkBox.setSelected( selected );

        // Apply drop-shadow effects when the mouse enters a check box.
        applyDropShadowEffect( checkBox );

        return checkBox;
    }

    /**
     * @param labelText
     *            The text to use for a Column Header
     * @return The Label to use for a Column Header
     */
    @SuppressWarnings("nls")
    public static Label getColumnHeader( final String labelText ) {
        // We enforce a style of centered column headers, using bold italic
        // text.
        final Label columnHeader = new Label( labelText );

        columnHeader.getStyleClass().add( "column-header" );

        return columnHeader;
    }

    /**
     * @param labelText
     *            The text to use for a Control label
     * @return The Label to use for a Control
     */
    @SuppressWarnings("nls")
    public static Label getControlLabel( final String labelText ) {
        // We enforce a style of right-justified control labels using bold
        // italic text, and we add a colon and space for better comprehension of
        // context when setting a label as a control label vs. a column header.
        final String controlLabelText = labelText + LABEL_DELIMITER;
        final Label controlLabel = new Label( controlLabelText );

        controlLabel.getStyleClass().add( "control-label" );

        return controlLabel;
    }

    public static Button getIconButton( final Group group, final Node icon ) {
        final Button button = new Button();

        // Add the button to its group.
        group.getChildren().add( button );

        // Apply the icon from a local node and set its style.
        applyToolbarIcon( button, icon );

        return button;
    }

    public static Button getIconButton( final Group group, final String iconFilename ) {
        final Button button = new Button();

        // Add the Button to its Group.
        group.getChildren().add( button );

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( button, iconFilename );

        return button;
    }

    public static Button getIconButton( final String iconFilename ) {
        final Button button = new Button();

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( button, iconFilename );

        return button;
    }

    /**
     * @param iconContext
     *            The icon type to use as context for the inset dimension
     * @return The inset dimension to use for the supplied icon type
     */
    public static int getIconInset( final IconContext iconContext ) {
        int iconInset = 0;
        
        switch ( iconContext ) {
        case FRAME_TITLE:
            iconInset = FRAME_TITLE_ICON_INSET;
            break;
        case MENU:
            iconInset = MENU_ICON_INSET;
            break;
        case TOOLBAR:
            iconInset = TOOLBAR_ICON_INSET;
            break;
        case CONTROL_PANEL:
            iconInset = CONTROL_PANEL_ICON_INSET;
            break;
        default:
            break;
        }
        
        return iconInset;
    }

    /**
     * @param iconContext
     *            The icon type to use as context for the icon size
     * @return The icon size to use for the supplied icon type
     */
    public static int getIconSize( final IconContext iconContext ) {
        int iconSize = 0;
        
        switch ( iconContext ) {
        case FRAME_TITLE:
            iconSize = FRAME_TITLE_ICON_SIZE;
            break;
        case MENU:
            iconSize = MENU_ICON_SIZE;
            break;
        case TOOLBAR:
            iconSize = TOOLBAR_ICON_SIZE;
            break;
        case CONTROL_PANEL:
            iconSize = CONTROL_PANEL_ICON_SIZE;
            break;
        default:
            break;
        }
        
        return iconSize;
    }

    // NOTE: Use this method when the Toggle Button isn't exclusive and can
    // be selected with other buttons active.
    public static ToggleButton getIconToggleButton( final Group group, final String iconFilename ) {
        final ToggleButton toggleButton = new ToggleButton();

        // Add the toggle button to its group.
        group.getChildren().add( toggleButton );

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    // NOTE: Use this method when the Toggle Button isn't exclusive and can
    // be selected with other buttons active.
    public static ToggleButton getIconToggleButton( final String iconFilename ) {
        final ToggleButton toggleButton = new ToggleButton();

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    public static ToggleButton getIconToggleButton( final ToggleGroup toggleGroup,
                                                    final Node icon ) {
        final ToggleButton toggleButton = new ToggleButton();

        // Add the toggle button to its toggle group.
        // NOTE: SegmentedButtons subsume this task; check for null!
        if ( toggleGroup != null ) {
            toggleButton.setToggleGroup( toggleGroup );
        }

        // Apply the icon from a local canvas and set its style.
        applyToolbarIcon( toggleButton, icon );

        return toggleButton;
    }

    public static ToggleButton getIconToggleButton( final ToggleGroup toggleGroup,
                                                    final String iconFilename ) {
        final ToggleButton toggleButton = new ToggleButton();

        // Add the toggle button to its toggle group.
        // NOTE: SegmentedButtons subsume this task; check for null!
        if ( toggleGroup != null ) {
            toggleButton.setToggleGroup( toggleGroup );
        }

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    public static ToggleButton getIconToggleButton( final ToggleGroup toggleGroup,
                                                    final String iconFilename,
                                                    final String tooltipText,
                                                    final String cssStyleClass ) {
        final ToggleButton toggleButton = new XToggleButton( tooltipText,
                                                             cssStyleClass,
                                                             false,
                                                             false );

        // Add the toggle button to its toggle group.
        // NOTE: SegmentedButtons subsume this task; check for null!
        if ( toggleGroup != null ) {
            toggleButton.setToggleGroup( toggleGroup );
        }

        // Apply the icon from JAR-resident resources and set its style.
        GuiUtilities.applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    @SuppressWarnings("nls")
    public static Label getInfoLabel( final String info ) {
        final String infoLabelText = info;
        final Label infoLabel = new Label( infoLabelText );

        infoLabel.getStyleClass().add( "info-text" );

        return infoLabel;
    }

    // TODO: Pass in the minimum height as a parameter?
    public static HBox getInfoPane( final Label infoLabel ) {
        final HBox infoPane = new HBox();

        infoPane.setAlignment( Pos.CENTER );
        infoPane.getChildren().add( infoLabel );
        infoPane.setMinHeight( 40d );
        infoLabel.prefHeightProperty().bind( infoPane.heightProperty() );

        return infoPane;
    }

    /**
     * This method returns a completely initialized and styled button.
     *
     * Use this version when needing custom background and/or foreground colors,
     * and when resource lookup is not necessary as the optional label text is
     * already at hand. All parameters are optional and check for null pointers.
     *
     * @param buttonText
     *            Optional text label for the button
     * @param tooltipText
     *            Optional @Tooltip text
     * @param backColor
     *            The color to use for the background of the button
     * @param foreColor
     *            The color to use for the label text on the button
     * @return A labeled @Button adhering to custom style guidelines
     */
    public static Button getLabeledButton( final String buttonText,
                                           final String tooltipText,
                                           final Color backColor,
                                           final Color foreColor ) {
        // Some Action Buttons are made blank, as they may also indicate Status.
        // NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Button button = ( ( buttonText != null ) && !buttonText.trim().isEmpty() )
            ? new Button( buttonText )
            : new Button();

        // Optionally add tool tip text for the button, more verbose than label.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            button.setTooltip( new Tooltip( tooltipText ) );
        }

        if ( backColor != null ) {
            final Background background = getButtonBackground( backColor );
            button.setBackground( background );
        }
        if ( foreColor != null ) {
            button.setTextFill( foreColor );
        }

        // Apply drop-shadow effects when the mouse enters the Button.
        applyDropShadowEffect( button );

        return button;
    }

    /**
     * This method returns a completely initialized and styled button.
     *
     * Use this version when needing custom background and/or foreground colors,
     * and when resource lookup is not necessary as the optional label text is
     * already at hand. All parameters are optional and check for null pointers.
     *
     * @param buttonText
     *            Optional text label for the button
     * @param tooltipText
     *            Optional @Tooltip text
     * @param cssStyleClass
     *            The Style Class of the CSS attributes that customize the
     *            button
     * @return A labeled @Button adhering to custom style guidelines
     */
    public static Button getLabeledButton( final String buttonText,
                                           final String tooltipText,
                                           final String cssStyleClass ) {
        // Some Action Buttons are made blank, as they may also indicate Status.
        // NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Button button = ( ( buttonText != null ) && !buttonText.trim().isEmpty() )
            ? new Button( buttonText )
            : new Button();

        // Optionally add tool tip text for the button, more verbose than label.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            button.setTooltip( new Tooltip( tooltipText ) );
        }

        // Set the CSS Style ID in place of direct setting of colors.
        setButtonProperties( button, cssStyleClass );

        return button;
    }

    /**
     * @param labelText
     *            The text to use for the Choice Box label
     * @param choiceBox
     *            The Choice Box to apply the label to
     * @return An {@link HBox} layout pane container for the Choice Box with its
     *         Label
     */
    public static HBox getLabeledChoiceBoxPane( final String labelText,
                                                final ChoiceBox< ? > choiceBox ) {
        final Label labelLabel = getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( choiceBox );

        final HBox labeledChoiceBoxPane = new HBox();
        labeledChoiceBoxPane.getChildren().addAll( labelLabel, choiceBox );
        labeledChoiceBoxPane.setAlignment( Pos.CENTER_LEFT );
        labeledChoiceBoxPane.setPadding( new Insets( 12d ) );
        labeledChoiceBoxPane.setSpacing( 12d );

        return labeledChoiceBoxPane;
    }

    /**
     * @param labelText
     *            The text to use for the Combo Box label
     * @param comboBox
     *            The Combo Box to apply the label to
     * @return An {@link HBox} layout pane container for the Combo Box with its
     *         Label
     */
    public static HBox getLabeledComboBoxPane( final String labelText,
                                               final ComboBox< ? > comboBox ) {
        final Label labelLabel = getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( comboBox );

        final HBox labeledComboBoxPane = new HBox();
        labeledComboBoxPane.getChildren().addAll( labelLabel, comboBox );
        labeledComboBoxPane.setAlignment( Pos.CENTER_LEFT );
        labeledComboBoxPane.setPadding( new Insets( 12d ) );
        labeledComboBoxPane.setSpacing( 12d );

        return labeledComboBoxPane;
    }

    /**
     * @param labelLabel
     *            The Label to use for a labeled Label
     * @param label
     *            The Label to which to apply the supplied Label
     * @return An {@link HBox} layout pane container for the Label with its
     *         Label
     */
    public static HBox getLabeledLabelPane( final Label labelLabel, final Label label ) {
        final HBox labeledLabelPane = new HBox();

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Label
     * @param label
     *            The Label to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Label with its
     *         Label
     */
    public static HBox getLabeledLabelPane( final String labelText, final Label label ) {
        final Label labelLabel = getControlLabel( labelText );

        final HBox labeledLabelPane = new HBox();

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER_LEFT );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Spinner
     * @param spinner
     *            The Spinner to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Spinner with its
     *         Label
     */
    public static HBox getLabeledSpinnerPane( final String labelText, final Spinner< ? > spinner ) {
        final Label labelLabel = getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( spinner );

        final HBox labeledSpinnerPane = new HBox();
        labeledSpinnerPane.setSpacing( 16d );
        labeledSpinnerPane.getChildren().addAll( labelLabel, spinner );

        return labeledSpinnerPane;
    }

    /**
     * @param labelText
     *            The text to use for a labeled Text Field
     * @param textField
     *            The Text Field to which to apply the supplied text
     * @return An {@link HBox} layout pane container for the Text Field with its
     *         Label
     */
    public static HBox getLabeledTextFieldPane( final String labelText,
                                                final TextField textField ) {
        final Label labelLabel = getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( textField );

        final HBox labeledTextFieldPane = new HBox();
        labeledTextFieldPane.getChildren().setAll( labelLabel, textField );
        labeledTextFieldPane.setAlignment( Pos.CENTER_LEFT );
        labeledTextFieldPane.setPadding( new Insets( 12d ) );
        labeledTextFieldPane.setSpacing( 12d );

        return labeledTextFieldPane;
    }

    @SuppressWarnings("nls")
    public static TextArea getNotesEditor( final int numberOfRows ) {
        final TextArea notesEditor = new TextArea();
        notesEditor.setWrapText( true );
        notesEditor.setPrefRowCount( numberOfRows );
        notesEditor.setPrefColumnCount( 80 );

        notesEditor.getStyleClass().add( "notes-text-area" );

        return notesEditor;
    }

    @SuppressWarnings("nls")
    public static Label getPropertySheetLabel( final String labelText ) {
        final String propertySheetLabelText = labelText + LABEL_DELIMITER;

        final Label propertySheetLabel = new Label( propertySheetLabelText );

        propertySheetLabel.getStyleClass().add( "property-sheet-label" );

        return propertySheetLabel;
    }

    public static HBox getPropertySheetLabelPane( final String labelText, final Label label ) {
        final Label propertySheetLabel = getPropertySheetLabel( labelText );

        final HBox labeledLabelPane = new HBox();
        labeledLabelPane.getChildren().addAll( propertySheetLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER );
        labeledLabelPane.setPadding( new Insets( 6.0d ) );
        labeledLabelPane.setSpacing( 6.0d );

        return labeledLabelPane;
    }

    public static RadioButton getRadioButton( final String label,
                                              final ToggleGroup toggleGroup,
                                              final boolean selected ) {
        final RadioButton radioButton = new RadioButton( label );
        radioButton.setToggleGroup( toggleGroup );
        radioButton.setSelected( selected );

        // Apply drop-shadow effects when the mouse enters a radio button.
        applyDropShadowEffect( radioButton );

        return radioButton;
    }

    // TODO: Handle this like regular sliders in terms of measurement units.
    public static RangeSlider getRangeSlider( final double minimumValue,
                                              final double maximumValue,
                                              final double lowValue,
                                              final double highValue,
                                              final double majorTickUnit,
                                              final double blockIncrement,
                                              final boolean snapToTicks ) {
        // NOTE: Using the default constructor and then setting minimum,
        // maximum, low and high values later, causes infinite recursion and
        // stack overflow, so it's safer to set them all together in the fully
        // specified constructor and let the ControlsFX code take care of the
        // initialization order and logic due to chicken-or-egg problems.
        final RangeSlider rangeSlider = new RangeSlider( minimumValue,
                                                         maximumValue,
                                                         lowValue,
                                                         highValue );

        rangeSlider.setMajorTickUnit( majorTickUnit );
        rangeSlider.setMinorTickCount( ( int ) FastMath.floor( majorTickUnit / blockIncrement ) );
        rangeSlider.setBlockIncrement( blockIncrement );

        rangeSlider.setShowTickMarks( true );
        rangeSlider.setShowTickLabels( true );
        rangeSlider.setSnapToTicks( snapToTicks );

        return rangeSlider;
    }

    @SuppressWarnings("nls")
    public static Label getRowHeader( final String labelText ) {
        // We enforce a style of right-justified row headers using bold
        // italic text, and we add a colon and space for better comprehension of
        // context when setting a label as a row header vs. a column header.
        final String rowHeaderText = labelText + LABEL_DELIMITER;
        final Label rowHeader = new Label( rowHeaderText );

        rowHeader.getStyleClass().add( "row-header" );

        return rowHeader;
    }

    /**
     * This method uses the Action Framework to make a Segmented Button from
     * an existing collection of Actions, which it then stylizes.
     *
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @param actions
     *            The {@link Collection} of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action actions}
     */
    public static SegmentedButton getSegmentedButton( final ActionTextBehavior textBehavior,
                                                      final Collection< ? extends Action > actions ) {
        // Get the Segmented Button from a collection of mutually exclusive
        // choices modeled as actions.
        final SegmentedButton segmentedButton = ActionUtils.createSegmentedButton( textBehavior,
                                                                                   actions );

        final ObservableList< ToggleButton > toggleButtons = segmentedButton.getButtons();
        for ( final ToggleButton toggleButton : toggleButtons ) {
            // Style the icon to use consistent insets on all sides.
            toggleButton.setPadding( new Insets( TOOLBAR_ICON_INSET ) );

            // Apply drop-shadow effects when the mouse enters a node.
            GuiUtilities.applyDropShadowEffect( toggleButton );
        }

        // Use the dark styling so the selected button is more obvious.
        segmentedButton.getStyleClass().add( SegmentedButton.STYLE_CLASS_DARK );

        return segmentedButton;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It automatically
     * applies the CSS Style ID.
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel() {
        // Get a status Label with the custom CSS Style ID applied.
        final Label statusLabel = getStatusLabel( true, true );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It provides the
     * option of skipping the CSS styling, as some contexts are too complex to
     * just have one setting. It assumes an initially empty text label.
     *
     * @param applyCssStyleId
     *            Flag for whether or not to apply the CSS Style ID
     * @param applyPadding
     *            Flag for whether or not to apply left and right side padding
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel( final boolean applyCssStyleId,
                                        final boolean applyPadding ) {
        // Get a status Label without any initial text applied.
        final Label statusLabel = getStatusLabel( null, applyCssStyleId, applyPadding );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It automatically
     * skips the CSS Style ID, but does provide initial text for the label.
     *
     * @param labelText
     *            The text to apply to the Label
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    public static Label getStatusLabel( final String labelText ) {
        // Get a status Label without the custom CSS Style ID applied.
        final Label statusLabel = getStatusLabel( labelText, false, false );

        return statusLabel;
    }

    /**
     * This is a factory method to make an initially blank Label that has been
     * styled for application consistency of Look-and-Feel. It provides the
     * option of skipping the CSS styling, as some contexts are too complex to
     * just have one setting. It also optionally takes an initial text string.
     *
     * @param labelText
     *            The text to apply to the Label
     * @param applyCssStyleId
     *            Flag for whether or not to apply the CSS Style ID
     * @param applyPadding
     *            Flag for whether or not to apply left and right side padding
     *
     * @return A Label that meets the style guidelines set forth by this library
     *         for display-only status and values
     */
    @SuppressWarnings("nls")
    public static Label getStatusLabel( final String labelText,
                                        final boolean applyCssStyleId,
                                        final boolean applyPadding ) {
        // Some Status Labels are made blank, as there may be no default Status.
        // NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Label statusLabel = ( ( labelText != null ) && !labelText.trim().isEmpty() )
            ? new Label( labelText )
            : new Label();

        // Some Labels are meant to just blend in with their background, or have
        // complex rules for rendering, so we flag whether to apply styles.
        if ( applyCssStyleId ) {
            // Match this toolkit's Look-and-Feel for non-editable Status Labels.
            statusLabel.getStyleClass().add( "fxguitoolkit-label" );
        }

        // Some Labels are used in paired contexts that already account for
        // padding; whereas some are used in large grid layouts and need it.
        if ( applyPadding ) {
            // Labels by default are made as small as possible to contain their
            // text, but we prefer to have sufficient horizontal and vertical
            // gaps for legibility and separation of neighboring controls.
            statusLabel.setPadding( STATUS_LABEL_INSETS_DEFAULT );
        }

        // Apply drop-shadow effects when the mouse enters a Status Label.
        GuiUtilities.applyDropShadowEffect( statusLabel );

        // Labels do not have context menus by default, and all we'd want to do
        // anyway is copy the text, just when it's something of interest like a
        // status or a value. Fortunately, it is trivial to add our own menu.
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem copyMenuItem = new MenuItem( "Copy" );
        copyMenuItem.setOnAction( evt -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString( statusLabel.getText() );
            clipboard.setContent( content );
        } );
        contextMenu.getItems().add( copyMenuItem );

        statusLabel.setOnContextMenuRequested( evt -> contextMenu
                .show( statusLabel, evt.getScreenX(), evt.getScreenY() ) );

        return statusLabel;
    }

    public static Node getTitledBorderWrappedNode( final Node node, final String title ) {
        // NOTE: The etched border doesn't handle radii very smoothly, so we
        // use the line border. A thin border is less obtrusive and distracting,
        // but still achieves the goal of slightly setting aside groups of
        // related GUI elements so that the user can see the workflow better.
        final Node titledBorderWrappedNode = Borders.wrap( node ).lineBorder().color( Color.WHITE )
                .thickness( 1.0d ).title( title ).radius( 2.5d, 2.5d, 2.5d, 2.5d ).build().build();

        return titledBorderWrappedNode;
    }

    /**
     * This method handles errors from Print Page commands.
     *
     * @param printerJob
     *            The Printer Job that failed
     * @param printCategory
     *            The category or context for the Printer Job
     */
    public static void handlePrintJobError( final PrinterJob printerJob,
                                            final String printCategory ) {
        final String masthead = MessageFactory.getPrintServicesProblemMasthead();
        final JobStatus jobStatus = printerJob.getJobStatus();
        switch ( jobStatus ) {
        case CANCELED:
            // Print Information: Print Job Cancelled.
            final String printJobCanceledMessage = MessageFactory.getPrintJobCanceledMessage();
            DialogUtilities
                    .showInformationAlert( printJobCanceledMessage, masthead, printCategory );
            break;
        case DONE:
            // Print Warning: Cannot print due to Printer blocked by other Print
            // Job.
            final String printerBlockedMessage = MessageFactory.getPrinterBlockedMessage();
            DialogUtilities.showWarningAlert( printerBlockedMessage, masthead, printCategory );
            break;
        case ERROR:
            // Print Error: Nothing to print, or Printer not set up correctly.
            final String nullPrintJobMessage = MessageFactory.getNullPrintJobMessage();
            DialogUtilities.showErrorAlert( nullPrintJobMessage, masthead, printCategory );
            break;
        case NOT_STARTED:
            // Print Error: Unknown internal failure; Print Job not started.
            final String printJobNotStartedMessage = MessageFactory.getPrintJobNotStartedMessage();
            DialogUtilities.showErrorAlert( printJobNotStartedMessage, masthead, printCategory );
            break;
        case PRINTING:
            // This is the normal condition; nothing to do.
            break;
        default:
            break;
        }
    }

    /**
     * This method initializes the persistent shared attributes of decorator
     * node groups, which generally are application managed and non-interactive.
     *
     * @param decoratorNodeGroup
     *            The decorator node group whose persistent shared attributes
     *            are to be set at initialization time
     */
    public static void initDecoratorNodeGroup( final Group decoratorNodeGroup ) {
        // Mark the decorator node group as unmanaged, as its preferred size
        // changes should not affect our layout, and as otherwise changes to
        // Distance Unit can create interim states that we never recover from
        // due to JavaFX making layout decisions for managed nodes/groups.
        decoratorNodeGroup.setManaged( false );

        // Do not auto-size decorator node group children, as we are managing
        // the nodes ourselves, and as otherwise changes to Distance Unit can
        // create interim states that we never recover from due to JavaFX making
        // layout decisions for auto-sized children.
        decoratorNodeGroup.setAutoSizeChildren( false );

        // For now, we do not allow mouse-picking of decorator node groups.
        decoratorNodeGroup.setMouseTransparent( true );
        decoratorNodeGroup.setPickOnBounds( false );
    }

    // TODO: Search for the JavaFX CSS source code that analyzes HSB vs. RGB.
    // NOTE: This function is borrowed from ColorUtilities until we make the
    // FxGraphicsUtilities library, at which time we should also normalize the
    // logic here with what was done for EPS Export and PDF Export.
    public static boolean isColorDark( final Color color ) {
        // Use HSB Analysis to find the perceived Brightness of the supplied
        // color. We introduce a small fudge factor for floating-point
        // imprecision and rounding, equating roughly to 51% as the cutoff for
        // Dark vs. Light, so that 50% Gray (aka Mid-Gray) will trigger a white
        // foreground. This makes for better contrast and is easier on the eyes
        // than a black foreground, in such cases.
        return color.getBrightness() <= 0.51d;
    }

    // Launch the user's default browser set to the specified initial URL.
    public static void launchBrowser( final HostServices hostServices, final String url ) {
        try {
            final URI uri = new URI( url );
            hostServices.showDocument( uri.toString() );
        }
        catch ( final NullPointerException | URISyntaxException e ) {
            // In theory, we produce the URL so it can't be null or invalid.
            e.printStackTrace();

            // Alert the user that the default browser couldn't launch.
            final String browserLaunchErrorMessage = MessageFactory.getBrowserLaunchErrorMessage();
            final String browserLaunchErrorMasthead = MessageFactory.getBadUrlMasthead();
            final String browserLaunchErrorTitle = MessageFactory.getBrowserLaunchErrorTitle();
            com.mhschmieder.fxguitoolkit.dialog.DialogUtilities
                    .showWarningAlert( browserLaunchErrorMessage,
                                       browserLaunchErrorMasthead,
                                       browserLaunchErrorTitle );
        }
    }

    public static void redirectTouchEvents( final Window window ) {
        // NOTE: This is an experiment to see if this fixes the crashes on the
        // new Touch Bars that Apple added to MacBook Pros in 2017.
        window.addEventFilter( TouchEvent.ANY, touchEvent -> {
            // Consume the touch event
            touchEvent.consume();

            // Create a fake Mouse Clicked Event for the current Touch Event.
            final TouchPoint touchPoint = touchEvent.getTouchPoint();
            final int clickCount = 1;
            final MouseEvent mouseEvent = new MouseEvent( touchEvent.getSource(),
                                                          touchEvent.getTarget(),
                                                          MouseEvent.MOUSE_CLICKED,
                                                          touchPoint.getX(),
                                                          touchPoint.getY(),
                                                          touchPoint.getScreenX(),
                                                          touchPoint.getScreenY(),
                                                          MouseButton.PRIMARY,
                                                          clickCount,
                                                          false,
                                                          false,
                                                          false,
                                                          false,
                                                          true,
                                                          false,
                                                          false,
                                                          true,
                                                          false,
                                                          false,
                                                          null );

            // Fire the fake traditional Mouse Event.
            final Scene scene = window.getScene();
            Event.fireEvent( scene.getRoot(), mouseEvent );
        } );
    }

    public static void removeStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                      final String jarRelativeStylesheetFilename ) {
        // If no valid stylesheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilename == null )
                || ( jarRelativeStylesheetFilename.length() < 5 ) ) {
            return;
        }

        final URL stylesheetUrl = GuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        final String stylesheetFilename = stylesheetUrl.toExternalForm();
        try {
            // NOTE: CSS loading can be timing-sensitive to JavaFX API calls
            // that also affect style attributes, so it might be safer to defer
            // the CSS loading so that it is applied to a more stable GUI.
            stylesheetFilenames.remove( stylesheetFilename );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public static void replaceStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        removeStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilenameOld );
        addStylesheetAsJarResource( stylesheetFilenames, jarRelativeStylesheetFilenameNew );
    }

    public static void replaceStylesheetAsJarResource( final Parent parent,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        final ObservableList< String > stylesheetFilenames = parent.getStylesheets();
        replaceStylesheetAsJarResource( stylesheetFilenames,
                                        jarRelativeStylesheetFilenameOld,
                                        jarRelativeStylesheetFilenameNew );
    }

    public static void replaceStylesheetAsJarResource( final Scene scene,
                                                       final String jarRelativeStylesheetFilenameOld,
                                                       final String jarRelativeStylesheetFilenameNew ) {
        final ObservableList< String > stylesheetFilenames = scene.getStylesheets();
        replaceStylesheetAsJarResource( stylesheetFilenames,
                                        jarRelativeStylesheetFilenameOld,
                                        jarRelativeStylesheetFilenameNew );
    }

    public static void setButtonProperties( final Button button, final String cssStyleClass ) {
        // Set the CSS Style Class in place of direct setting of colors.
        // NOTE: Some Controls are meant to just blend in with their
        // background, or have complex rules for rendering, so we flag whether
        // to apply styles.
        button.getStyleClass().add( cssStyleClass );

        // Unless we explicitly make the button focus traversable (the default
        // is not focus traversable), it does no good to write key listeners for
        // the ENTER key for executing the button action.
        button.setFocusTraversable( true );

        // Apply drop-shadow effects when the mouse enters a Button.
        applyDropShadowEffect( button );
    }

    public static void setColumnHeaderLabelForeground( final GridPane gridPane,
                                                       final int firstColumn,
                                                       final int lastColumn,
                                                       final Color foregroundColor ) {
        // Set the column header label foreground.
        final ObservableList< Node > nodes = gridPane.getChildren();
        for ( int columnIndex = firstColumn; columnIndex <= lastColumn; columnIndex++ ) {
            final int columnHeaderNodeIndex = columnIndex;
            final Node node = nodes.get( columnHeaderNodeIndex );
            if ( node instanceof Labeled ) {
                final Labeled label = ( Labeled ) node;
                label.setTextFill( foregroundColor );
            }
        }
    }

    @SuppressWarnings("nls")
    public static void setComboBoxProperties( final ComboBox< ? > comboBox ) {
        // Apply this toolkit's custom Combo Box CSS Style to this control.
        comboBox.getStyleClass().add( "fxguitoolkit-combo-box" );
        comboBox.getEditor().getStyleClass().add( "fxguitoolkit-combo-box" );

        // Apply drop-shadow effects when the mouse enters a Combo Box.
        applyDropShadowEffect( comboBox );
    }

    public static void setRowHeaderLabelForeground( final GridPane gridPane,
                                                    final int firstRow,
                                                    final int lastRow,
                                                    final int columnHeaderIndexAdjustment,
                                                    final int rowHeaderIndexAdjustment,
                                                    final int numberOfColumns,
                                                    final Color foregroundColor ) {
        // Set the row header label foreground.
        final ObservableList< Node > nodes = gridPane.getChildren();
        for ( int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++ ) {
            final int rowArrayIndexAdjustment = ( rowIndex - 1 ) * numberOfColumns;
            final int rowHeaderNodeIndex = columnHeaderIndexAdjustment + rowArrayIndexAdjustment
                    + rowHeaderIndexAdjustment;
            final Node node = nodes.get( rowHeaderNodeIndex );
            if ( node instanceof Labeled ) {
                final Labeled label = ( Labeled ) node;
                label.setTextFill( foregroundColor );
            }
        }
    }

    @SuppressWarnings("nls")
    public static void setSpinnerProperties( final Spinner< ? > spinner ) {
        // Apply this toolkit's custom Spinner CSS Style to this control.
        spinner.getStyleClass().add( "fxguitoolkit-spinner" );
        spinner.getEditor().getStyleClass().add( "fxguitoolkit-spinner" );

        // Apply drop-shadow effects when the mouse enters a Spinner.
        applyDropShadowEffect( spinner );
    }

    // Try to globally change the foreground theme for elements not exposed
    // in Java API calls, using our custom dark vs. light theme CSS files.
    // NOTE: For now, we assume dark and light themes only.
    public static void setStylesheetForTheme( final Parent parent,
                                              final Color backColor,
                                              final String jarRelativeStylesheetFilenameDark,
                                              final String jarRelativeStylesheetFilenameLight ) {
        final boolean isDark = isColorDark( backColor );
        if ( isDark ) {
            replaceStylesheetAsJarResource( parent,
                                            jarRelativeStylesheetFilenameLight,
                                            jarRelativeStylesheetFilenameDark );
        }
        else {
            replaceStylesheetAsJarResource( parent,
                                            jarRelativeStylesheetFilenameDark,
                                            jarRelativeStylesheetFilenameLight );
        }
    }

    // Try to globally change the foreground theme for elements not exposed
    // in Java API calls, using our custom dark vs. light theme CSS files.
    // NOTE: For now, we assume dark and light themes only.
    public static void setStylesheetForTheme( final Scene scene,
                                              final Color backColor,
                                              final String jarRelativeStylesheetFilenameDark,
                                              final String jarRelativeStylesheetFilenameLight ) {
        final boolean isDark = isColorDark( backColor );
        if ( isDark ) {
            replaceStylesheetAsJarResource( scene,
                                            jarRelativeStylesheetFilenameLight,
                                            jarRelativeStylesheetFilenameDark );
        }
        else {
            replaceStylesheetAsJarResource( scene,
                                            jarRelativeStylesheetFilenameDark,
                                            jarRelativeStylesheetFilenameLight );
        }
    }

    public static void setTextAreaProperties( final TextArea textArea,
                                              final String cssStyleClass ) {
        // Apply a specific enhanced Text Area CSS Style to this control.
        textArea.getStyleClass().add( cssStyleClass );

        // Apply drop-shadow effects when the mouse enters a Text Area.
        applyDropShadowEffect( textArea );
    }

    @SuppressWarnings("nls")
    public static void setTextFieldProperties( final TextField textField ) {
        // Apply this toolkit's custom Text Field CSS Style to this control.
        textField.getStyleClass().add( "fxguitoolkit-text-input" );

        // Apply drop-shadow effects when the mouse enters a Text Field.
        applyDropShadowEffect( textField );
    }

    public static void setToggleButtonProperties( final ToggleButton toggleButton,
                                                  final String cssStyleClass ) {
        // Set the CSS Style Class in place of direct setting of colors.
        // NOTE: It is risky to set the Style Class if null, as we might lose
        // default styles that we want to preserve.
        if ( cssStyleClass != null ) {
            toggleButton.getStyleClass().add( cssStyleClass );
        }

        // Apply drop-shadow effects when the mouse enters a Toggle Button.
        applyDropShadowEffect( toggleButton );
    }

}
