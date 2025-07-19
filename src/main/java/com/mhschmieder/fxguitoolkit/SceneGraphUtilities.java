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

import java.util.ResourceBundle;

import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.fxguitoolkit.action.XAction;
import com.mhschmieder.fxguitoolkit.control.DoubleEditor;
import com.mhschmieder.fxguitoolkit.control.NumberSlider;
import com.mhschmieder.fxguitoolkit.control.XToggleButton;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.guitoolkit.component.ButtonUtilities;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

// TODO: Split this up into more specialized utilities for Buttons, etc.?
public class SceneGraphUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SceneGraphUtilities() {}

    public static Tooltip getTooltip( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName ) {
        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, false );

        final String tooltipText = ButtonUtilities
                .getButtonToolTipText( groupName, itemName, resourceBundle );

        final Tooltip tooltip = new Tooltip( tooltipText );

        return tooltip;
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

    @SuppressWarnings("nls")
    public static GridPane getLabeledTextAreaPane( final String labelText,
                                                   final TextArea textArea,
                                                   final ClientProperties clientProperties ) {
        final GridPane grid = LayoutFactory
                .makeGridPane( Pos.CENTER_LEFT, new Insets( 0.0d, 6.0d, 0.0d, 6.0d ), 6, 6 );

        // Although we put the label above the control, the size difference is
        // so huge that it won't be clear what the label is for unless we add a
        // colon as with horizontally paired controls.
        final Label labelLabel = GuiUtilities.getControlLabel( labelText );

        // TODO: Provide mnemonic and/or accelerator for this?
        labelLabel.setLabelFor( textArea );

        labelLabel.getStyleClass().add( "text-area-label" );

        grid.add( labelLabel, 0, 0 );
        grid.add( textArea, 0, 1 );

        return grid;
    }

    /**
     * This method uses resource lookup, via custom locale-sensitive text-based
     * properties files, to get a button label which is then fed into a more
     * fully qualified getter method to return a completely initialized and
     * styled button. This version does not use the Action Framework.
     *
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
        GuiUtilities.setButtonProperties( button, backColor );

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
        GuiUtilities.setButtonProperties( button, backColor );

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
        GuiUtilities.setButtonProperties( button, cssStyleClass );

        return button;
    }

    /**
     * This method uses the Action Framework to make a {@link Button} from an existing
     * Action, which it then stylizes using our custom CSS tag for buttons. The 
     * versions in {@link GuiUtilities} assume no Action basis for menus.
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
     * versions in {@link GuiUtilities} assume no Action basis for menus.
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
        GuiUtilities.setButtonProperties( button, cssStyleClass );

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
     * toggles. The versions in {@link GuiUtilities} assume no Action basis for menus.
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
     * toggles. The versions in {@link GuiUtilities} assume no Action basis for menus.
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
        GuiUtilities.setToggleButtonProperties( toggleButton, cssStyleClass );
        
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
        GuiUtilities.applyDropShadowEffect( checkBox );

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

        return GuiUtilities.getCheckBox( checkBoxLabel, false );
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
        final Label label = GuiUtilities.getControlLabel( labelLabel );

        return label;
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
        final DoubleEditor doubleEditor =
                                        getNumberSliderEditor( clientProperties,
                                                               minFractionDigitsFormat,
                                                               maxFractionDigitsFormat,
                                                               minFractionDigitsParse,
                                                               maxFractionDigitsParse,
                                                               numberSlider
                                                                       .getMeasurementUnitString(),
                                                               numberSlider.getMin(),
                                                               numberSlider.getMax(),
                                                               numberSlider.getValue(),
                                                               valueIncrement );

        return doubleEditor;
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
        GuiUtilities.applyDropShadowEffect( control );

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
}
