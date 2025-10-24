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
package com.mhschmieder.fxcontrols.control;

import com.mhschmieder.fxcontrols.action.XAction;
import com.mhschmieder.fxcontrols.util.IconContext;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxgraphics.image.ImageUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.GlobalUtilities;
import com.mhschmieder.jcontrols.control.ButtonUtilities;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.apache.commons.math3.util.FastMath;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.textfield.CustomTextField;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

// TODO: Split this up into more specialized utilities for Buttons, etc.?
public class ControlUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ControlUtilities() {}

    /**
     * Define a label delimiter for when a label is horizontally paired with a
     * user input control.
     */
    public static final String LABEL_DELIMITER = ": ";

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

    /**
     * This is the most common inset for most contexts of icon hosting, to avoid
     * clutter, but if we add menus later on, those usually use an inset of 2.
     */
    public static final double                          DEFAULT_ICON_INSET              = 4;

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
    public static final int                             FRAME_TITLE_ICON_SIZE           = 16;

    /**
     *
     */
    public static final int                             FRAME_TITLE_ICON_INSET          = 2;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_SIZE         = 32;

    /**
     *
     */
    public static final int                             CONTROL_PANEL_ICON_INSET        = 6;

    public static Tooltip getTooltip( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, false );

        final String tooltipText = ButtonUtilities
                .getButtonToolTipText( groupName, itemName, resourceBundle );

        return new Tooltip( tooltipText );
    }

    public static String getLabeledControlLabel( final ClientProperties clientProperties,
                                                 final String bundleName,
                                                 final String groupName,
                                                 final String itemName,
                                                 final boolean replaceMnemonic ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, false );

        // Get the control label from the resource bundle, if applicable.
        final String buttonLabel = ButtonUtilities
                .getButtonLabel( groupName, itemName, resourceBundle );
        if ( buttonLabel.trim().isEmpty() ) {
            return null;
        }

        // Conditionally strip the mnemonic marker from the label, or
        // replace the Swing mnemonic marker with the JavaFX version.
        final String buttonText = ButtonUtilities.handleMnemonicMarker( buttonLabel,
                                                                        replaceMnemonic );
        if ( ( buttonText == null ) || buttonText.trim().isEmpty() ) {
            return null;
        }

        return buttonText;
    }

    /**
     * This method uses resource lookup, via custom locale-sensitive text-based
     * properties files, to get a button label which is then fed into a more
     * fully qualified getter method to return a completely initialized and
     * styled button. This version does not use the Action Framework.
     * <p>
     * Use this version when needing custom background colors.
     *
     * @param clientProperties
     *            The {@link ClientProperties} grabbed at application startup
     * @param bundleName
     *            Resource Name for looking up locale-sensitive tags
     * @param groupName
     *            Group Name for resource lookup (e.g. Menu Name)
     * @param itemName
     *            Item Name for resource lookup (e.g. Menu Item Name)
     * @param backColor
     *            Custom background {@link Color} to apply to the {@link Button}
     * @return A labeled {@link Button} adhering to custom style guidelines
     */
    public static Button getLabeledButton( final ClientProperties clientProperties,
                                           final String bundleName,
                                           final String groupName,
                                           final String itemName,
                                           final Color backColor ) {
        final String buttonLabel = getLabeledControlLabel( clientProperties,
                                                           bundleName,
                                                           groupName,
                                                           itemName,
                                                           false );

        final Button button = new Button( buttonLabel );

        // Style the buttons with optional custom background.
        setButtonProperties( button, backColor );

        // Set the common button parameters that aren't part of the constructor.
        setButtonParameters( button, true );

        return button;
    }

    /**
     * This method uses the Action Framework to make a {@link Button} from an existing
     * Action, which it then stylizes. This version adds a label and an icon (if valid).
     *
     * Use this version when needing custom background colors.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the
     *            resources needed for making an associated {@link Button}
     * @param backColor
     *            Custom background {@link Color} to apply to the {@link Button}
     * @return A labeled {@link Button} adhering to custom style guidelines
     */
    public static Button getLabeledButton( final XAction action, final Color backColor ) {
        final Button button = ActionUtils.createButton( action );

        // Style the buttons with optional custom background.
        setButtonProperties( button, backColor );

        return button;
    }

    /**
     * This method uses the Action Framework to make a {@link Button} from an existing
     * Action, which it then stylizes. This version adds a label and an icon (if valid).
     *
     * Use this version when needing custom background colors.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the resources
     *            needed for making an associated {@link Button}
     * @param cssStyleClass
     *            The Style Class of the CSS attributes that customize the
     *            button
     * @return A labeled {@link Button} adhering to custom style guidelines
     */
    public static Button getLabeledButton( final XAction action, final String cssStyleClass ) {
        final Button button = ActionUtils.createButton( action );

        // Set the CSS Style ID in place of direct setting of colors.
        setButtonProperties( button, cssStyleClass );

        return button;
    }

    /**
     * This method uses the Action Framework to make a {@link Button} from an existing
     * Action, which it then stylizes using our custom CSS tag for buttons. The 
     * versions here assume no Action basis for menus.
     * <p>
     * Use this version when needing an icon and no text, such as for a toolbar.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the resources
     *            needed for making an associated {@link Button}
     * @return A labeled icon-only {@link Button} adhering to custom style guidelines
     */
    public static Button getIconButton( final XAction action ) {
        return getIconButton( action, "tool-bar-button" );
    }

    /**
     * This method uses the Action Framework to make a {@link Button} from an existing
     * Action, which it then stylizes using our custom CSS tag for buttons. The 
     * versions here assume no Action basis for menus.
     * <p>
     * Use this version when needing an icon and no text, such as for a toolbar.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the resources
     *            needed for making an associated {@link Button}
     * @param cssStyleClass
     *            The Style Class of the CSS attributes that customize the
     *            button
     * @return A labeled icon-only {@link Button} adhering to custom style guidelines
     */
    public static Button getIconButton( final XAction action, final String cssStyleClass ) {
        final Button button = ActionUtils.createButton( action, ActionUtils.ActionTextBehavior.HIDE );

        // Set the CSS Style ID in place of direct setting of colors.
        setButtonProperties( button, cssStyleClass );

        // Set the common button parameters that aren't part of the constructor.
        setButtonParameters( button, true );

        return button;
    }
    
    /**
     * Sets the common button parameters that aren't part of the constructor.
     * 
     * @param button The Button whose extra parameters are to be set
     * @param forceMnemonicParsing {@code true} if we should set mnemonic parsing;
     *        {@code false} if current mnemonic parsing should be left unaltered
     */
    public static void setButtonParameters( final ButtonBase button,
                                            final boolean forceMnemonicParsing ) {
        // Make sure the mnemonic is used to underline a character vs. printing
        // as a separate literal character.
        if ( forceMnemonicParsing ) {
            button.setMnemonicParsing( true );
        }
    }

    /**
     * This method returns a completely initialized and styled toggle button.
     *
     * Use this version when not needing custom background or foreground colors,
     * but when resource lookup is necessary for determining the label text. All
     * other parameters are optional and check for null pointers.
     *
     * @param clientProperties
     *            The {@link ClientProperties} grabbed at application startup
     * @param toggleGroup
     *            The {@link ToggleGroup} to which this {@link ToggleButton} is to be assigned
     * @param bundleName
     *            Resource Name for looking up locale-sensitive tags
     * @param groupName
     *            Group Name for resource lookup (e.g. Menu Name)
     * @param itemName
     *            Item Name for resource lookup (e.g. Menu Item Name)
     * @return A labeled {@link ToggleButton} adhering to custom style guidelines
     */
    public static ToggleButton getLabeledToggleButton( final ClientProperties clientProperties,
                                                       final ToggleGroup toggleGroup,
                                                       final String bundleName,
                                                       final String groupName,
                                                       final String itemName ) {
        final String buttonLabel = getLabeledControlLabel( clientProperties,
                                                           bundleName,
                                                           groupName,
                                                           itemName,
                                                           false );
        return getLabeledToggleButton( toggleGroup, buttonLabel, null, null );
    }

    /**
     * This method returns a completely initialized and styled toggle button.
     *
     * Use this version when needing custom background and/or foreground colors,
     * and when resource lookup is not necessary as the label text is already at
     * hand. All other parameters are optional and check for null pointers.
     *
     * @param toggleGroup
     *            The {@link ToggleGroup} to which this {@link ToggleButton} is to be assigned
     * @param buttonLabel
     *            The {@link Label} text used for both selected and unselected status
     * @param tooltipText
     *            Optional {@link Tooltip} text
     * @param cssStyleId
     *            The CSS Style ID used for all button colors in all states
     * @return A labeled {@link ToggleButton} adhering to custom style guidelines
     */
    public static ToggleButton getLabeledToggleButton( final ToggleGroup toggleGroup,
                                                       final String buttonLabel,
                                                       final String tooltipText,
                                                       final String cssStyleId ) {
        final ToggleButton toggleButton = new XToggleButton( buttonLabel,
                                                             tooltipText,
                                                             cssStyleId,
                                                             true,
                                                             3.0d,
                                                             false,
                                                             false );

        
        // Set the toggle button parameters that aren't part of the constructor.
        setToggleButtonParameters( toggleButton, toggleGroup, true );

        return toggleButton;
    }

    /**
     * This method returns a completely initialized and styled toggle button.
     *
     * Use this version when needing custom selected/deselected background
     * colors, and when resource lookup is not necessary as the label text is
     * already at hand. All other parameters are optional and check for null
     * pointers. Foreground colors are matched automatically by CSS.
     *
     * @param toggleGroup
     *            The {@link ToggleGroup} to which this {@link ToggleButton} is to be assigned
     * @param selectedText
     *            The {@link Label} text used for both selected status
     * @param deselectedText
     *            The {@link Label} text used for unselected status
     * @param tooltipText
     *            Optional {@link Tooltip} text
     * @param cssStyleId
     *            The CSS Style ID used for all button colors in all states
     * @return A labeled {@link ToggleButton} adhering to custom style guidelines
     */
    public static ToggleButton getLabeledToggleButton( final ToggleGroup toggleGroup,
                                                       final String selectedText,
                                                       final String deselectedText,
                                                       final String tooltipText,
                                                       final String cssStyleId ) {
        final ToggleButton toggleButton = new XToggleButton( selectedText,
                                                             deselectedText,
                                                             tooltipText,
                                                             cssStyleId,
                                                             true,
                                                             4.5d,
                                                             false,
                                                             false );

        
        // Set the toggle button parameters that aren't part of the constructor.
        setToggleButtonParameters( toggleButton, toggleGroup, false );

        return toggleButton;
    }

    /**
     * This method uses the Action Framework to make a Toggle Button from an 
     * existing Action, which it then stylizes using our custom CSS tag for 
     * toggles. The versions here assume no Action basis for menus.
     * <p>
     * Use this version when needing an icon and no text, such as when this is
     * part of a toggle group or a {@link SegmentedButton} or is being used in a toolbar,
     * and when there is no need to change the background/etc for selected state.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the resources
     *            needed for making an associated {@link ToggleButton}
     *  @param toggleGroup The {@link ToggleGroup} that the {@link XAction} is part of
     * @return A labeled icon-only {@link ToggleButton} adhering to custom style 
     * guidelines
     */
    public static ToggleButton getIconToggleButton( final XAction action,
                                                    final ToggleGroup toggleGroup ) {
        return getIconToggleButton( action, "tool-bar-toggle", toggleGroup );
    }

    /**
     * This method uses the Action Framework to make a Toggle Button from an 
     * existing Action, which it then stylizes using our custom CSS tag for 
     * toggles. The versions here assume no Action basis for menus.
     * <p>
     * Use this version when needing an icon and no text, such as for a toolbar,
     * and when there is no need to change the background/etc for selected state.
     *
     * @param action
     *            The {@link XAction} reference that contains most of the resources
     *            needed for making an associated {@link ToggleButton}
     * @param cssStyleClass
     *            The Style Class of the CSS attributes that customize the toggle
     *            button
     * @param toggleGroup
     *            The {@link ToggleGroup} to add the {@link ToggleButton} to
     * @return A labeled icon-only {@link ToggleButton} adhering to custom style 
     * guidelines
     */
    public static ToggleButton getIconToggleButton( final XAction action,
                                                    final String cssStyleClass,
                                                    final ToggleGroup toggleGroup ) {
        final ToggleButton toggleButton = ActionUtils
                .createToggleButton( action, ActionUtils.ActionTextBehavior.HIDE );

        // Set the CSS Style ID in place of direct setting of colors.
        setToggleButtonProperties( toggleButton, cssStyleClass );
        
        // Set the toggle button parameters that aren't part of the constructor.
        setToggleButtonParameters( toggleButton, toggleGroup, true );

        return toggleButton;
    }
    
    /**
     * Sets the toggle button parameters that aren't part of the constructor.
     * 
     * @param toggleButton The Toggle Button whose extra parameters are to be set
     * @param toggleGroup The optional Toggle Group to add the Toggle Button to
     * @param forceMnemonicParsing {@code true} if we should set mnemonic parsing;
     *        {@code false} if current mnemonic parsing should be left unaltered
     */
    public static void setToggleButtonParameters( final ToggleButton toggleButton,
                                                  final ToggleGroup toggleGroup,
                                                  final boolean forceMnemonicParsing ) {
        // Set the common button parameters that aren't part of the constructor.
        setButtonParameters( toggleButton, true );

        // Add the toggle button to its toggle group, if it exists.
        if ( toggleGroup != null ) {
            toggleButton.setToggleGroup( toggleGroup );
        }
    }

    public static CheckBox getLabeledCheckBox( final XAction action ) {
        final CheckBox checkBox = ActionUtils.createCheckBox( action );

        // Apply drop-shadow effects when the mouse enters a Check Box.
        applyDropShadowEffect( checkBox );

        return checkBox;
    }

    public static CheckBox getLabeledCheckBox( final ClientProperties clientProperties,
                                               final String bundleName,
                                               final String groupName,
                                               final String itemName ) {
        final String checkBoxLabel = getLabeledControlLabel( clientProperties,
                                                             bundleName,
                                                             groupName,
                                                             itemName,
                                                             false );

        return getCheckBox( checkBoxLabel, false );
    }

    public static Label getLabeledLabel( final ClientProperties clientProperties,
                                         final String bundleName,
                                         final String groupName,
                                         final String itemName ) {
        final String labelLabel = getLabeledControlLabel( clientProperties,
                                                          bundleName,
                                                          groupName,
                                                          itemName,
                                                          false );

        return getControlLabel( labelLabel );
    }

    // Helper method to get a number textField, stand-alone or paired.
    public static DoubleEditor getNumberSliderEditor( final ClientProperties clientProperties,
                                                      final int minFractionDigitsFormat,
                                                      final int maxFractionDigitsFormat,
                                                      final int minFractionDigitsParse,
                                                      final int maxFractionDigitsParse,
                                                      final String measurementUnit,
                                                      final double minimumValue,
                                                      final double maximumValue,
                                                      final double initialValue,
                                                      final double valueIncrement ) {
        // Get the current value and format it as initial text.
        final String initialText = Double.toString( initialValue );

        final DoubleEditor doubleEditor = new DoubleEditor( clientProperties,
                                                            initialText,
                                                            null,
                                                            true,
                                                            minFractionDigitsFormat,
                                                            maxFractionDigitsFormat,
                                                            minFractionDigitsParse,
                                                            maxFractionDigitsParse,
                                                            minimumValue,
                                                            maximumValue,
                                                            initialValue,
                                                            valueIncrement );

        doubleEditor.setMeasurementUnitString( measurementUnit );

        return doubleEditor;
    }

    // Helper method to get a number textField to pair with a slider.
    public static DoubleEditor getNumberSliderEditor( final ClientProperties clientProperties,
                                                      final NumberSlider numberSlider,
                                                      final int minFractionDigitsFormat,
                                                      final int maxFractionDigitsFormat,
                                                      final int minFractionDigitsParse,
                                                      final int maxFractionDigitsParse,
                                                      final double valueIncrement ) {
        // Use the current slider value and limits to set the number textField.
        return getNumberSliderEditor(
                clientProperties,
                minFractionDigitsFormat,
                maxFractionDigitsFormat,
                minFractionDigitsParse,
                maxFractionDigitsParse,
                numberSlider.getMeasurementUnitString(),
                numberSlider.getMin(),
                numberSlider.getMax(),
                numberSlider.getValue(),
                valueIncrement );
    }

    public static void setControlProperties( final ClientProperties clientProperties,
                                             final String bundleName,
                                             final String groupName,
                                             final String itemName,
                                             final Control control,
                                             final Object userData ) {
        final Tooltip tooltip = getTooltip( clientProperties, bundleName, groupName, itemName );
        control.setTooltip( tooltip );

        // Apply drop-shadow effects when the mouse enters this Control.
        applyDropShadowEffect( control );

        if ( userData != null ) {
            control.setUserData( userData );
        }
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
     * @param labelText
     *            The text to use for a Control label
     * @return The Label to use for a Control
     */
    public static Label getControlLabel( final String labelText ) {
        // We enforce a style of right-justified control labels using bold
        // italic text, and we add a colon and space for better comprehension of
        // context when setting a label as a control label vs. a column header.
        final String controlLabelText = labelText + LABEL_DELIMITER;
        final Label controlLabel = new Label( controlLabelText );

        controlLabel.getStyleClass().add( "control-label" );

        return controlLabel;
    }

    public static void setButtonProperties( final Button button,
                                            final String cssStyleClass ) {
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

    public static void setButtonProperties(final Button button,
                                           final Color backColor ) {
        // Style the buttons with optional custom background.
        // NOTE: CSS automatically chooses an appropriate foreground.
        if ( backColor != null ) {
            final Background background = getButtonBackground( backColor );
            button.setBackground( background );
        }

        // Apply drop-shadow effects when the mouse enters a Button.
        applyDropShadowEffect( button );
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

        return new Background( new BackgroundFill(
                backColor,
                new CornerRadii( 3 ),
                Insets.EMPTY ) );
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

        // Make sure the mnemonic is used to underline a character vs. printing
        // as a separate literal character.
        checkBox.setMnemonicParsing( true );

        checkBox.setSelected( selected );

        // Apply drop-shadow effects when the mouse enters a check box.
        applyDropShadowEffect( checkBox );

        return checkBox;
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

    public static RadioButton getRadioButton(final String label,
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
    public static RangeSlider getRangeSlider(final double minimumValue,
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
    public static void setComboBoxProperties( final ComboBox< ? > comboBox ) {
        // Apply this toolkit's custom Combo Box CSS Style to this control.
        comboBox.getStyleClass().add( "fxguitoolkit-combo-box" );
        comboBox.getEditor().getStyleClass().add( "fxguitoolkit-combo-box" );

        // Apply drop-shadow effects when the mouse enters a Combo Box.
        applyDropShadowEffect( comboBox );
    }

    @SuppressWarnings("nls")
    public static void setSpinnerProperties( final Spinner< ? > spinner ) {
        // Apply this toolkit's custom Spinner CSS Style to this control.
        spinner.getStyleClass().add( "fxguitoolkit-spinner" );
        spinner.getEditor().getStyleClass().add( "fxguitoolkit-spinner" );

        // Apply drop-shadow effects when the mouse enters a Spinner.
        applyDropShadowEffect( spinner );
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

    public static void applyLabeledButtonStyle(final Button button,
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

    public static void applyLabeledButtonStyle(final Button button,
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

    public static void applyApplicationButtonStyle(final Button button,
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

    public static void applyApplicationButtonStyle(final Button button,
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

    public static void applyTextFieldStyle(final TextField textField,
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

    public static void applyCustomTextFieldStyle(final CustomTextField customTextField,
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

    public static void applyRoundButtonStyle(final Button button,
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

    public static void applySolidRoundButtonStyle(final Button button,
                                                  final String backColorCss,
                                                  final int radiusPixels ) {
        final int borderWidthPixels = 1;
        applyRoundButtonStyle( button,
                               backColorCss,
                               radiusPixels,
                               backColorCss,
                               borderWidthPixels );
    }

    public static void applyRegionStyle(final Region region,
                                        final String backColorCss,
                                        final String borderColorCss,
                                        final String borderWidthCss ) {
        applyRegionStyle( region, backColorCss, borderColorCss, borderWidthCss, " 7.5" );
    }

    public static void applyRegionStyle(final Region region,
                                        final String backColorCss,
                                        final String borderColorCss,
                                        final String borderWidthCss,
                                        final String borderRadiusCss ) {
        region.setStyle( "-fx-content-display: center" + "; -fx-padding: 6 8 6 8"
                + "; -fx-background-color: " + backColorCss + "; -fx-border-color: "
                + borderColorCss + "; -fx-border-width: " + borderWidthCss + "; -fx-border-radius: "
                + borderRadiusCss + ";" );
    }

    /**
     * @param spinner
     *            The spinner to apply attributes to
     * @param tooltipText
     *            The tool tip text to use for the spinner
     * @param maximumSpinnerWidth
     *            Maximum spinner width in pixels
     */
    public static void applySpinnerAttributes( final Spinner< ? > spinner,
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

    public static VBox getImageBox(final Label imageLabel, final double imageSize ) {
        final VBox imageVBox = new VBox();
        imageVBox.getChildren().addAll( imageLabel );
        imageVBox.setAlignment( Pos.CENTER );
        imageVBox.setMinSize( imageSize, imageSize );
        imageVBox.setMaxSize( imageSize, imageSize );
        imageVBox.setPrefSize( imageSize, imageSize );

        return imageVBox;
    }

    public static Button getIconButton(final Group group, final Node icon ) {
        final Button button = new Button();

        // Add the button to its group.
        group.getChildren().add( button );

        // Apply the icon from a local node and set its style.
        applyToolbarIcon( button, icon );

        return button;
    }

    public static Button getIconButton(final Group group, final String iconFilename ) {
        final Button button = new Button();

        // Add the Button to its Group.
        group.getChildren().add( button );

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( button, iconFilename );

        return button;
    }

    public static Button getIconButton(final String iconFilename ) {
        final Button button = new Button();

        // Apply the icon from JAR-resident resources and set its style.
        applyToolbarIcon( button, iconFilename );

        return button;
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

    public static ToggleButton getIconToggleButton(final ToggleGroup toggleGroup,
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

    public static ToggleButton getIconToggleButton(final ToggleGroup toggleGroup,
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

    public static ToggleButton getIconToggleButton(final ToggleGroup toggleGroup,
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
        applyToolbarIcon( toggleButton, iconFilename );

        return toggleButton;
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

    public static int getMnemonicIndex(final String groupName,
                                       final String itemName,
                                       final ResourceBundle resourceBundle ) {
        // Get the button label from the resource bundle, if applicable.
        final String buttonLabel = ButtonUtilities.getButtonLabel(
                groupName, itemName, resourceBundle );
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

    public static SVGPath getSvgImage(final String svgContent ) {
        final SVGPath svgImage = new SVGPath();
        svgImage.setContent( svgContent );

        return svgImage;
    }

    public static ToggleButton getSvgToggleButton(final SVGPath svgImage,
                                                  final Color svgColor,
                                                  final double imageSize,
                                                  final String tooltipText ) {
        final ToggleButton svgToggleButton = new ToggleButton();
        if ( ( tooltipText != null ) && !tooltipText.isEmpty() ) {
            svgToggleButton.setTooltip( new Tooltip( tooltipText ) );
        }
        svgToggleButton.setBackground( RegionUtilities.makeRegionBackground( svgColor ) );
        svgToggleButton.setMinSize( imageSize, imageSize );
        svgToggleButton.setMaxSize( imageSize, imageSize );
        svgToggleButton.setPrefSize( imageSize, imageSize );
        svgToggleButton.setShape( svgImage );

        return svgToggleButton;
    }

    public static ToggleButton getSvgToggleButton(final String svgContent,
                                                  final Color svgColor,
                                                  final double imageSize,
                                                  final String tooltipText ) {
        final SVGPath svgImage = getSvgImage( svgContent );

        return getSvgToggleButton( svgImage, svgColor, imageSize, tooltipText );
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

    @SuppressWarnings("nls")
    public static TextArea getNotesEditor( final int numberOfRows ) {
        final TextArea notesEditor = new TextArea();
        notesEditor.setWrapText( true );
        notesEditor.setPrefRowCount( numberOfRows );
        notesEditor.setPrefColumnCount( 80 );

        notesEditor.getStyleClass().add( "notes-text-area" );

        return notesEditor;
    }

    /**
     * This method uses the Action Framework to make a Segmented Button from
     * an existing collection of Actions, which it then stylizes.
     *
     * @param textBehavior
     *            Defines {@link ActionUtils.ActionTextBehavior}
     * @param actions
     *            The {@link Collection} of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action actions}
     */
    public static SegmentedButton getSegmentedButton( final ActionUtils.ActionTextBehavior textBehavior,
                                                      final Collection< ? extends Action > actions ) {
        // Get the Segmented Button from a collection of mutually exclusive
        // choices modeled as actions.
        final SegmentedButton segmentedButton = ActionUtils.createSegmentedButton( textBehavior,
                                                                                   actions );

        final ObservableList< ToggleButton > toggleButtons = segmentedButton.getButtons();
        for ( final ToggleButton toggleButton : toggleButtons ) {
            // Style the icon to use consistent insets on all sides.
            toggleButton.setPadding( new Insets(
                    TOOLBAR_ICON_INSET) );

            // Apply drop-shadow effects when the mouse enters a node.
            applyDropShadowEffect( toggleButton );
        }

        // Use the dark styling so the selected button is more obvious.
        segmentedButton.getStyleClass().add( SegmentedButton.STYLE_CLASS_DARK );

        return segmentedButton;
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
}
