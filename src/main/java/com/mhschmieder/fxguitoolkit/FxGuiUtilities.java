/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;
import org.controlsfx.tools.Borders;

import com.mhschmieder.fxguitoolkit.dialog.DialogUtilities;
import com.mhschmieder.fxguitoolkit.image.ImageUtilities;

import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.print.PrinterJob;
import javafx.print.PrinterJob.JobStatus;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 * {@code FxGuiUtilities} is a utility class for methods related to top-level
 * JavaFX GUI functionality.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class FxGuiUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private FxGuiUtilities() {}

    /**
     *
     */
    public static final int                             FRAME_TITLE_ICON_SIZE       = 16;

    /**
     *
     */
    public static final int                             FRAME_TITLE_ICON_INSET      = 2;

    /**
     *
     */
    public static final int                             MENU_ICON_SIZE              = 16;

    /**
     *
     */
    public static final int                             MENU_ICON_INSET             = 2;

    /**
     *
     */
    public static final int                             TOOL_BAR_HEIGHT             = 40;

    /**
     *
     */
    public static final int                             TOOLBAR_ICON_SIZE           = 24;

    /**
     *
     */
    public static final int                             TOOLBAR_ICON_INSET          = 4;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_SIZE     = 32;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_INSET    = 6;

    // Predetermined Splash Screen dimensions; image will scale to fit.
    public static final int                             SPLASH_WIDTH                = 600;
    public static final int                             SPLASH_HEIGHT               = 400;

    public static final int                             LABEL_EDITOR_WIDTH_DEFAULT  = 320;

    /**
     * Labels by default are made as small as possible to contain their text,
     * but we prefer to have sufficient horizontal and vertical gaps for
     * legibility and separation of neighboring controls.
     */
    public static final Insets                          STATUS_LABEL_INSETS_DEFAULT =
                                                                                    new Insets( 3d,
                                                                                                10d,
                                                                                                3d,
                                                                                                10d );

    /**
     * Define a label delimiter for when a label is horizontally paired with a
     * user input control.
     */
    @SuppressWarnings("nls") public static final String LABEL_DELIMITER             = ": ";

    // We have chosen the ampersand as the mnemonic marker, to be compatible
    // with Qt and other GUI toolkits, so that it is more likely that resource
    // bundles can be shared.
    public static final char                            SWING_MNEMONIC_MARKER       = '&';

    // JavaFX has its own built-in mnemonic marker.
    public static final char                            JAVAFX_MNEMONIC_MARKER      = '_';

    public static void addStylesheetAsJarResource( final ObservableList< String > stylesheetFilenames,
                                                   final String jarRelativeStylesheetFilename ) {
        // If no valid style sheet file (with extension) provided, return.
        if ( ( jarRelativeStylesheetFilename == null )
                || ( jarRelativeStylesheetFilename.length() < 5 ) ) {
            return;
        }

        final URL stylesheetUrl = FxGuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        final String stylesheetFilename = stylesheetUrl.toExternalForm();
        try {
            // :NOTE: CSS loading can be timing-sensitive to JavaFX API calls
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

    /**
     * Apply drop-shadow effects when the mouse enters the specified
     * {@link Node}
     *
     * @param node
     *            The {@link Node} to which drop-shadow animation should be
     *            applied
     */
    public static void applyDropShadowEffects( final Node node ) {
        // Apply a drop-shadow effect if a node was focus traversed via TAB, or
        // if the mouse is hovering over the node (even in passing through).
        // :NOTE: If the current background is black, we need to make sure the
        // drop-shadow color is different as otherwise it won't show up. But as
        // we can't easily query that on the node inside the callback, we take a
        // one-shoe-fits-all approach with a color that shows up against all
        // background color choices as well as colors used throughout the app.
        // :NOTE: We also must supply the blur radius, set to slightly more than
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

    // Apply standardized attributes to all number-based charts.
    @SuppressWarnings("nls")
    public static void applyNumberChartAttributes( final XYChart< Number, Number > chart,
                                                   final boolean isOverlayChart,
                                                   final boolean showLegend,
                                                   final Side legendSide ) {
        chart.setLegendVisible( showLegend );
        if ( showLegend ) {
            if ( legendSide != null ) {
                chart.setLegendSide( legendSide );
            }
        }

        // It is safer to turn off animation while the chart is initializing.
        chart.setAnimated( false );

        // :NOTE: The overlay attribute could be for dual axis charts or for
        // charts with the same y-axis data range, but the underlying assumption
        // is a shared x-axis for all charts; only the bottom chart shows all.
        chart.setAlternativeRowFillVisible( !isOverlayChart );
        chart.setAlternativeColumnFillVisible( !isOverlayChart );
        chart.setHorizontalGridLinesVisible( !isOverlayChart );
        chart.setVerticalGridLinesVisible( !isOverlayChart );

        // The overlay chart shouldn't show its x-axis tick marks or labels.
        if ( isOverlayChart ) {
            final Axis< Number > xAxis = chart.getXAxis();
            xAxis.setLabel( "" );
            xAxis.setTickLabelsVisible( false );
            xAxis.setTickMarkVisible( false );

            addStylesheetAsJarResource( chart,
                                        "/com/mhschmieder/fxguitoolkit/resources/overlay-chart.css" );
        }
    }

    /**
     * @param spinner
     * @param tooltipText
     * @param maximumSpinnerWidth
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
        // on the editor to manually handle the arrow keys there instead.
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
     * @param icon
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
        applyDropShadowEffects( labeled );
    }

    /**
     * @param labeled
     * @param iconFilename
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
        window.setX( ( bounds.getMinX() + ( bounds.getWidth() / 2d ) ) - ( SPLASH_WIDTH / 2d ) );
        window.setY( ( bounds.getMinY() + ( bounds.getHeight() / 2d ) ) - ( SPLASH_HEIGHT / 2d ) );
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
                ? ( fitWidth > 0d )
                    ? fitWidth
                    : ( fitHeight > 0d ) ? fitHeight * aspectRatio : imageHeight * aspectRatio
                : ( fitWidth > 0d ) ? fitWidth : -1d;
        final double fitHeightAdjusted = preserveRatio
            ? fitHeight
            : ( ( float ) aspectRatio != 0f )
                ? ( fitHeight > 0d )
                    ? fitHeight
                    : ( fitWidth > 0d ) ? fitWidth / aspectRatio : imageWidth / aspectRatio
                : ( fitHeight > 0d ) ? fitHeight : -1d;

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
     * @param selected
     * @return
     */
    public static CheckBox getCheckBox( final String label, final boolean selected ) {
        final CheckBox checkBox = new CheckBox( label );
        checkBox.setSelected( selected );

        // Apply drop-shadow effects when the mouse enters a check box.
        applyDropShadowEffects( checkBox );

        return checkBox;
    }

    /**
     * @param labelText
     * @return
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
     * @return
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
     * @return
     */
    public static int getIconInset( final IconContext iconContext ) {
        switch ( iconContext ) {
        case FRAME_TITLE:
            return FRAME_TITLE_ICON_INSET;
        case MENU:
            return MENU_ICON_INSET;
        case TOOLBAR:
            return TOOLBAR_ICON_INSET;
        case CONTROL_PANEL:
            return CONTROL_PANEL_ICON_INSET;
        default:
            return 0;
        }
    }

    /**
     * @param iconContext
     * @return
     */
    public static int getIconSize( final IconContext iconContext ) {
        switch ( iconContext ) {
        case FRAME_TITLE:
            return FRAME_TITLE_ICON_SIZE;
        case MENU:
            return MENU_ICON_SIZE;
        case TOOLBAR:
            return TOOLBAR_ICON_SIZE;
        case CONTROL_PANEL:
            return CONTROL_PANEL_ICON_SIZE;
        default:
            return 0;
        }
    }

    // :NOTE: Use this method when the Toggle Button isn't exclusive and can
    // be selected with other buttons active.
    public static ToggleButton getIconToggleButton( final Group group, final String iconFilename ) {
        final ToggleButton toggleButton = new ToggleButton();

        // Add the toggle button to its group.
        group.getChildren().add( toggleButton );

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    // :NOTE: Use this method when the Toggle Button isn't exclusive and can
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
        // :NOTE: SegmentedButtons subsume this task; check for null!
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
        // :NOTE: SegmentedButtons subsume this task; check for null!
        if ( toggleGroup != null ) {
            toggleButton.setToggleGroup( toggleGroup );
        }

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
    }

    @SuppressWarnings("nls")
    public static Label getInfoLabel( final String info ) {
        final String infoLabelText = info;
        final Label infoLabel = new Label( infoLabelText );

        infoLabel.getStyleClass().add( "info-text" );

        return infoLabel;
    }

    // :TODO: Pass in the minimum height as a parameter?
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
     * @return A labeled @Button adhering to MSLI style guidelines
     */
    public static Button getLabeledButton( final String buttonText,
                                           final String tooltipText,
                                           final Color backColor,
                                           final Color foreColor ) {
        // Some Action Buttons are made blank, as they may also indicate Status.
        // :NOTE: Due to internal initialization order within JavaFX, it is best
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
        applyDropShadowEffects( button );

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
     * @return A labeled @Button adhering to MSLI style guidelines
     */
    public static Button getLabeledButton( final String buttonText,
                                           final String tooltipText,
                                           final String cssStyleClass ) {
        // Some Action Buttons are made blank, as they may also indicate Status.
        // :NOTE: Due to internal initialization order within JavaFX, it is best
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
     * @param choiceBox
     * @return
     */
    public static HBox getLabeledChoiceBoxPane( final String labelText,
                                                final ChoiceBox< ? > choiceBox ) {
        final Label labelLabel = getControlLabel( labelText );

        // :TODO: Provide mnemonic and/or accelerator for this?
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
     * @param comboBox
     * @return
     */
    public static HBox getLabeledComboBoxPane( final String labelText,
                                               final ComboBox< ? > comboBox ) {
        final Label labelLabel = getControlLabel( labelText );

        // :TODO: Provide mnemonic and/or accelerator for this?
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
     * @param label
     * @return
     */
    public static HBox getLabeledLabelPane( final Label labelLabel, final Label label ) {
        final HBox labeledLabelPane = new HBox();

        // :TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     * @param label
     * @return
     */
    public static HBox getLabeledLabelPane( final String labelText, final Label label ) {
        final Label labelLabel = getControlLabel( labelText );

        final HBox labeledLabelPane = new HBox();

        // :TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( label );

        labeledLabelPane.getChildren().addAll( labelLabel, label );
        labeledLabelPane.setAlignment( Pos.CENTER_LEFT );
        labeledLabelPane.setPadding( new Insets( 12d ) );
        labeledLabelPane.setSpacing( 12d );

        return labeledLabelPane;
    }

    /**
     * @param labelText
     * @param spinner
     * @return
     */
    public static HBox getLabeledSpinnerPane( final String labelText, final Spinner< ? > spinner ) {
        final Label labelLabel = getControlLabel( labelText );

        // :TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( spinner );

        final HBox labeledSpinnerPane = new HBox();
        labeledSpinnerPane.setSpacing( 16d );
        labeledSpinnerPane.getChildren().addAll( labelLabel, spinner );

        return labeledSpinnerPane;
    }

    /**
     * @param labelText
     * @param textField
     * @return
     */
    public static HBox getLabeledTextFieldPane( final String labelText,
                                                final TextField textField ) {
        final Label labelLabel = getControlLabel( labelText );

        // :TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( textField );

        final HBox labeledTextFieldPane = new HBox();
        labeledTextFieldPane.getChildren().setAll( labelLabel, textField );
        labeledTextFieldPane.setAlignment( Pos.CENTER_LEFT );
        labeledTextFieldPane.setPadding( new Insets( 12d ) );
        labeledTextFieldPane.setSpacing( 12d );

        return labeledTextFieldPane;
    }

    // :NOTE: This method should only be used for Latin alphanumeric
    // characters. See the Mnemonics.java example for a more complex and
    // foolproof methodology for finding the key code for a mnemonic. We specify
    // US-English (vs. just "English") to be safe, until support for
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
        if ( ( buttonLabel == null ) || buttonLabel.trim().isEmpty() ) {
            return -1;
        }

        // Get the button displayed mnemonic index.
        // :NOTE: The mnemonic marker index on the original label corresponds to
        // the mnemonic index on the stripped label.
        return getMnemonicMarkerIndex( buttonLabel );
    }

    // :NOTE: The menu label property files would be very time-consuming to edit
    // safely for replacing the "&" with "_", so we use the old Swing symbol as
    // the lookup and then replace it with the JavaFX symbol downstream.
    public static int getMnemonicMarkerIndex( final String key ) {
        return key.indexOf( SWING_MNEMONIC_MARKER );
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
        labeledLabelPane.setPadding( new Insets( 6d ) );
        labeledLabelPane.setSpacing( 6d );

        return labeledLabelPane;
    }

    public static RadioButton getRadioButton( final String label,
                                              final ToggleGroup toggleGroup,
                                              final boolean selected ) {
        final RadioButton radioButton = new RadioButton( label );
        radioButton.setToggleGroup( toggleGroup );
        radioButton.setSelected( selected );

        // Apply drop-shadow effects when the mouse enters a radio button.
        applyDropShadowEffects( radioButton );

        return radioButton;
    }

    // :TODO: Handle this like regular sliders in terms of measurement units.
    public static RangeSlider getRangeSlider( final double minimumValue,
                                              final double maximumValue,
                                              final double lowValue,
                                              final double highValue,
                                              final double majorTickUnit,
                                              final double blockIncrement,
                                              final boolean snapToTicks ) {
        // :NOTE: Using the default constructor and then setting minimum,
        // maximum, low and high values later, causes infinite recursion and
        // stack overflow, so it's safer to set them all together in the fully
        // specified constructor and let the ControlsFX code take care of the
        // initialization order and logic due to chicken-or-egg problems.
        final RangeSlider rangeSlider = new RangeSlider( minimumValue,
                                                         maximumValue,
                                                         lowValue,
                                                         highValue );

        rangeSlider.setMajorTickUnit( majorTickUnit );
        rangeSlider.setMinorTickCount( ( int ) Math.floor( majorTickUnit / blockIncrement ) );
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
            FxGuiUtilities.applyDropShadowEffects( toggleButton );
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
     * @return A Label that meets the Meyer Sound style guidelines set forth by
     *         Compass software for display-only status and values
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
     * @param applyStyleId
     *            Flag for whether or not to apply the CSS Style ID
     * @param applyPadding
     *            Flag for whether or not to apply left and right side padding
     *
     * @return A Label that meets the Meyer Sound style guidelines set forth by
     *         Compass software for display-only status and values
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
     * @return A Label that meets the Meyer Sound style guidelines set forth by
     *         Compass software for display-only status and values
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
     * @param applyStyleId
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
        // :NOTE: Due to internal initialization order within JavaFX, it is best
        // to supply the initial text with the constructor rather than assign it
        // afterwards.
        final Label statusLabel = ( ( labelText != null ) && !labelText.trim().isEmpty() )
            ? new Label( labelText )
            : new Label();

        // Some Labels are meant to just blend in with their background, or have
        // complex rules for rendering, so we flag whether to apply styles.
        if ( applyCssStyleId ) {
            // Match the enhanced Look-and-Feel for non-editable Status Labels.
            statusLabel.getStyleClass().add( "enhanced-label" );
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
        FxGuiUtilities.applyDropShadowEffects( statusLabel );

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
        // :NOTE: The etched border doesn't handle radii very smoothly, so we
        // use the line border. A thin border is less obtrusive and distracting,
        // but still achieves the goal of slightly setting aside groups of
        // related GUI elements so that the user can see the workflow better.
        final Node titledBorderWrappedNode = Borders.wrap( node ).lineBorder().color( Color.WHITE )
                .thickness( 1d ).title( title ).radius( 2.5d, 2.5d, 2.5d, 2.5d ).build().build();

        return titledBorderWrappedNode;
    }

    @SuppressWarnings("nls")
    public static Label getTitleLabel( final String title ) {
        final String titleLabelText = title;
        final Label titleLabel = new Label( titleLabelText );

        titleLabel.getStyleClass().add( "title-text" );

        return titleLabel;
    }

    // :TODO: Pass in the minimum height as a parameter?
    public static HBox getTitlePane( final Label titleLabel ) {
        final HBox titlePane = new HBox();

        titlePane.setAlignment( Pos.CENTER );

        titlePane.getChildren().add( titleLabel );

        titlePane.setMinHeight( 32d );

        titleLabel.prefHeightProperty().bind( titlePane.heightProperty() );

        return titlePane;
    }

    public static String handleMnemonicMarker( final String label, final boolean replaceMnemonic ) {
        final int mnemonicMarkerIndex = getMnemonicMarkerIndex( label );
        final int mnemonicIndex = ( mnemonicMarkerIndex >= 0 ) ? mnemonicMarkerIndex + 1 : 0;
        try {
            // :NOTE: If no mnemonic marker is found, "-1" is returned, which is
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

    // :TODO: Search for the JavaFX CSS source code that analyzes HSB vs. RGB.
    // :NOTE: This function is borrowed from ColorUtilities until we make the
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

    public static boolean isNodeInHierarchy( final Node sourceNode,
                                             final Node potentialHierarchyNode ) {
        if ( potentialHierarchyNode == null ) {
            return true;
        }

        Node node = sourceNode;
        while ( node != null ) {
            if ( node.equals( potentialHierarchyNode ) ) {
                return true;
            }
            node = node.getParent();
        }

        return false;
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
        // :NOTE: This is an experiment to see if this fixes the crashes on the
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

        final URL stylesheetUrl = FxGuiUtilities.class.getResource( jarRelativeStylesheetFilename );
        final String stylesheetFilename = stylesheetUrl.toExternalForm();
        try {
            // :NOTE: CSS loading can be timing-sensitive to JavaFX API calls
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
        // :NOTE: Some Controls are meant to just blend in with their
        // background, or have complex rules for rendering, so we flag whether
        // to apply styles.
        button.getStyleClass().add( cssStyleClass );

        // Unless we explicitly make the button focus traversable (the default
        // is not focus traversable), it does no good to write key listeners for
        // the ENTER key for executing the button action.
        button.setFocusTraversable( true );

        // Apply drop-shadow effects when the mouse enters a Button.
        applyDropShadowEffects( button );
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
        // Apply the enhanced Combo Box CSS Style to this control.
        comboBox.getStyleClass().add( "enhanced-combo-box" );
        comboBox.getEditor().getStyleClass().add( "enhanced-combo-box" );

        // Apply drop-shadow effects when the mouse enters a Combo Box.
        applyDropShadowEffects( comboBox );
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
        // Apply the enhanced Spinner CSS Style to this control.
        spinner.getStyleClass().add( "enhanced-spinner" );
        spinner.getEditor().getStyleClass().add( "enhanced-spinner" );

        // Apply drop-shadow effects when the mouse enters a Spinner.
        applyDropShadowEffects( spinner );
    }

    // Try to globally change the foreground theme for elements not exposed
    // in Java API calls, using our custom dark vs. light theme CSS files.
    // :NOTE: For now, we assume dark and light themes only.
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
    // :NOTE: For now, we assume dark and light themes only.
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
        applyDropShadowEffects( textArea );
    }

    @SuppressWarnings("nls")
    public static void setTextFieldProperties( final TextField textField ) {
        // Apply the enhanced Text Field CSS Style to this control.
        textField.getStyleClass().add( "enhanced-text-input" );

        // Apply drop-shadow effects when the mouse enters a Text Field.
        applyDropShadowEffects( textField );
    }

    public static void setToggleButtonProperties( final ToggleButton toggleButton,
                                                  final String cssStyleClass ) {
        // Set the CSS Style Class in place of direct setting of colors.
        // :NOTE: It is risky to set the Style Class if null, as we might lose
        // default styles that we want to preserve.
        if ( cssStyleClass != null ) {
            toggleButton.getStyleClass().add( cssStyleClass );
        }

        // Apply drop-shadow effects when the mouse enters a Toggle Button.
        applyDropShadowEffects( toggleButton );
    }

}
