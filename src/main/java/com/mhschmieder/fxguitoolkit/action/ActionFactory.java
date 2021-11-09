/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.action;

import java.util.Collection;
import java.util.ResourceBundle;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.commonstoolkit.util.SystemType;
import com.mhschmieder.fxgraphicstoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.SceneGraphUtilities;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;

/**
 * This is a utility class for dealing with common attributes of actions.
 */
public final class ActionFactory {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ActionFactory() {}

    public static void applyActionAttributes( final ClientProperties clientProperties,
                                              final Action action,
                                              final String bundleName,
                                              final String groupName,
                                              final String itemName ) {
        // Get the action's label from the resource bundle, if applicable.
        final String menuItemLabel = SceneGraphUtilities
                .getLabeledControlLabel( clientProperties, bundleName, groupName, itemName, true );

        if ( ( menuItemLabel != null ) && !menuItemLabel.trim().isEmpty() ) {
            // Set the standard action label, with the adjusted mnemonic.
            action.setText( menuItemLabel );

            // Set the action's accelerator, if it exists.
            setActionAccelerator( clientProperties, action, groupName, itemName, bundleName );

            // Set the action's long text (used as tool tip), if it exists.
            setActionLongText( clientProperties, action, groupName, itemName, bundleName );
        }
    }

    public static void applyActionAttributes( final ClientProperties clientProperties,
                                              final Action action,
                                              final String bundleName,
                                              final String groupName,
                                              final String itemName,
                                              final String jarRelativeIconFilename ) {
        // Apply the common action attributes first.
        applyActionAttributes( clientProperties, action, bundleName, groupName, itemName );

        // Set the action's associated graphic, with or without an icon
        // reference, using direct loading as we trust the icon dimensions.
        if ( ( jarRelativeIconFilename != null ) && !jarRelativeIconFilename.trim().isEmpty() ) {
            final ImageView actionGraphic = ImageUtilities.createIcon( jarRelativeIconFilename );
            action.setGraphic( actionGraphic );
        }
    }

    public static void applyActionGroupAttributes( final ClientProperties clientProperties,
                                                   final ActionGroup actionGroup,
                                                   final String bundleName,
                                                   final String groupName,
                                                   final String jarRelativeIconFilename ) {
        applyActionAttributes( clientProperties,
                               actionGroup,
                               bundleName,
                               groupName,
                               null,
                               jarRelativeIconFilename );
    }

    // If an accelerator is assigned, get it from a resource bundle.
    @SuppressWarnings("nls")
    public static KeyCombination makeAcceleratorKeyCombination( final ClientProperties clientProperties,
                                                                final String groupName,
                                                                final String itemName,
                                                                final ResourceBundle resourceBundle ) {
        // There must always be both a group name and an item name for each
        // action, in order to find its accelerator from the resource bundle.
        if ( ( groupName == null ) || groupName.isEmpty() || ( itemName == null )
                || itemName.isEmpty() ) {
            return null;
        }

        // Composite the action name from the group and item names.
        final String actionName = groupName + "." + itemName;

        // Generate the resource lookup key for the action accelerator.
        final String resourceKey = actionName + ".accelerator"
                + ( SystemType.MACOS.equals( clientProperties.systemType ) ? ".mac" : "" );

        try {
            // :NOTE: Not all actions have Accelerators, so we have to check
            // first to see if one is present, to avoid unnecessary exceptions.
            if ( !resourceBundle.containsKey( resourceKey ) ) {
                return null;
            }

            String acceleratorText = resourceBundle.getString( resourceKey );
            acceleratorText = acceleratorText.replace( "alt", "Alt" );
            acceleratorText = acceleratorText.replace( "control", "Ctrl" );
            acceleratorText = acceleratorText.replace( "meta", "Meta" );
            acceleratorText = acceleratorText.replace( "shift", "Shift" );
            acceleratorText = acceleratorText.replace( " ", "+" );

            return KeyCombination.keyCombination( acceleratorText );
        }
        catch ( final Exception e ) {
            // :NOTE: It is OK to be missing an Accelerator, but as we first
            // check for a key entry, this exception indicates a structural
            // problem that shouldn't be allowed to confuse the end users, but
            // which might benefit the developers or indicate file corruption.
            return null;
        }
    }

    private static XAction makeAction( final ClientProperties clientProperties,
                                       final ActionVerb actionVerb,
                                       final String bundleName,
                                       final String groupName,
                                       final String itemName,
                                       final String jarRelativeIconFilename ) {
        return makeAction( clientProperties,
                           actionVerb,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename,
                           false );
    }

    private static XAction makeAction( final ClientProperties clientProperties,
                                       final ActionVerb actionVerb,
                                       final String bundleName,
                                       final String groupName,
                                       final String itemName,
                                       final String jarRelativeIconFilename,
                                       final boolean hideIfDisabled ) {
        final XAction action = new XAction( actionVerb );

        // Set the menu item attributes from the resource bundle.
        applyActionAttributes( clientProperties,
                               action,
                               bundleName,
                               groupName,
                               itemName,
                               jarRelativeIconFilename );

        // To simplify constructors, for now we set this later if non-default.
        action.setHideIfDisabled( hideIfDisabled );

        return action;
    }

    public static XAction makeAction( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName,
                                      final String jarRelativeIconFilename ) {
        return makeAction( clientProperties,
                           ActionVerb.DO,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename );
    }

    public static XAction makeAction( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName,
                                      final String jarRelativeIconFilename,
                                      final boolean hideIfDisabled ) {
        return makeAction( clientProperties,
                           ActionVerb.DO,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename,
                           hideIfDisabled );
    }

    private static XActionGroup makeActionGroup( final ClientProperties clientProperties,
                                                 final Collection< Action > actions,
                                                 final boolean choiceGroup,
                                                 final String bundleName,
                                                 final String groupName,
                                                 final String jarRelativeIconFilename ) {
        return makeActionGroup( clientProperties,
                                actions,
                                choiceGroup,
                                bundleName,
                                groupName,
                                jarRelativeIconFilename,
                                false );
    }

    @SuppressWarnings("nls")
    private static XActionGroup makeActionGroup( final ClientProperties clientProperties,
                                                 final Collection< Action > actions,
                                                 final boolean choiceGroup,
                                                 final String bundleName,
                                                 final String groupName,
                                                 final String jarRelativeIconFilename,
                                                 final boolean hideIfDisabled ) {
        final XActionGroup actionGroup = new XActionGroup( "", actions, choiceGroup );

        // Set the menu attributes from the resource bundle.
        applyActionGroupAttributes( clientProperties,
                                    actionGroup,
                                    bundleName,
                                    groupName,
                                    jarRelativeIconFilename );

        // To simplify constructors, for now we set this later if non-default.
        actionGroup.setHideIfDisabled( hideIfDisabled );

        return actionGroup;
    }

    public static XActionGroup makeActionGroup( final ClientProperties clientProperties,
                                                final Collection< Action > actions,
                                                final String bundleName,
                                                final String groupName,
                                                final String jarRelativeIconFilename ) {
        return makeActionGroup( clientProperties,
                                actions,
                                bundleName,
                                groupName,
                                jarRelativeIconFilename,
                                false );
    }

    public static XActionGroup makeActionGroup( final ClientProperties clientProperties,
                                                final Collection< Action > actions,
                                                final String bundleName,
                                                final String groupName,
                                                final String jarRelativeIconFilename,
                                                final boolean hideIfDisabled ) {
        return makeActionGroup( clientProperties,
                                actions,
                                false,
                                bundleName,
                                groupName,
                                jarRelativeIconFilename,
                                hideIfDisabled );
    }

    public static XAction makeCheck( final ClientProperties clientProperties,
                                     final String bundleName,
                                     final String groupName,
                                     final String itemName,
                                     final String jarRelativeIconFilename ) {
        return makeAction( clientProperties,
                           ActionVerb.CHECK,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename );
    }

    public static XAction makeCheck( final ClientProperties clientProperties,
                                     final String bundleName,
                                     final String groupName,
                                     final String itemName,
                                     final String jarRelativeIconFilename,
                                     final boolean hideIfDisabled ) {
        return makeAction( clientProperties,
                           ActionVerb.CHECK,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename,
                           hideIfDisabled );
    }

    // NOTE: This is also a stand-in for now, for toggle groups, as ControlsFX
    // supports annotation hints for CheckMenuItem but not for RadioMenuItem.
    public static XAction makeChoice( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName,
                                      final String jarRelativeIconFilename ) {
        return makeAction( clientProperties,
                           ActionVerb.CHOOSE,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename );
    }

    // NOTE: This is also a stand-in for now, for toggle groups, as ControlsFX
    // supports annotation hints for CheckMenuItem but not for RadioMenuItem.
    public static XAction makeChoice( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName,
                                      final String jarRelativeIconFilename,
                                      final boolean hideIfDisabled ) {
        return makeAction( clientProperties,
                           ActionVerb.CHOOSE,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename,
                           hideIfDisabled );
    }

    public static XActionGroup makeChoiceGroup( final ClientProperties clientProperties,
                                                final Collection< Action > choices,
                                                final String bundleName,
                                                final String groupName,
                                                final String jarRelativeIconFilename ) {
        return makeActionGroup( clientProperties,
                                choices,
                                true,
                                bundleName,
                                groupName,
                                jarRelativeIconFilename );
    }

    public static XActionGroup makeChoiceGroup( final ClientProperties clientProperties,
                                                final Collection< Action > choices,
                                                final String bundleName,
                                                final String groupName,
                                                final String jarRelativeIconFilename,
                                                final boolean hideIfDisabled ) {
        return makeActionGroup( clientProperties,
                                choices,
                                true,
                                bundleName,
                                groupName,
                                jarRelativeIconFilename,
                                hideIfDisabled );
    }

    public static XAction makeToggle( final ClientProperties clientProperties,
                                      final String bundleName,
                                      final String groupName,
                                      final String itemName,
                                      final String jarRelativeIconFilename ) {
        return makeAction( clientProperties,
                           ActionVerb.TOGGLE,
                           bundleName,
                           groupName,
                           itemName,
                           jarRelativeIconFilename );
    }

    // If an accelerator is assigned, set it by platform.
    public static void setActionAccelerator( final ClientProperties clientProperties,
                                             final Action action,
                                             final String groupName,
                                             final String itemName,
                                             final String bundleName ) {
        // Fail-safe check to avoid unnecessary null pointer exceptions.
        if ( action == null ) {
            return;
        }

        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, true );

        // If an accelerator is assigned, get it from a resource bundle.
        final KeyCombination acceleratorKeyCombination =
                                                       makeAcceleratorKeyCombination( clientProperties,
                                                                                      groupName,
                                                                                      itemName,
                                                                                      resourceBundle );
        if ( acceleratorKeyCombination != null ) {
            action.setAccelerator( acceleratorKeyCombination );
        }
    }

    // If long text (used as tool tip) is assigned, set it.
    public static void setActionLongText( final ClientProperties clientProperties,
                                          final Action action,
                                          final String groupName,
                                          final String itemName,
                                          final String bundleName ) {
        // Fail-safe check to avoid unnecessary null pointer exceptions.
        if ( action == null ) {
            return;
        }

        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, true );

        // If long text is assigned, get it from a resource bundle.
        final String longText = SceneGraphUtilities
                .getButtonToolTipText( groupName, itemName, resourceBundle );
        if ( ( longText != null ) && !longText.trim().isEmpty() ) {
            action.setLongText( longText );
        }
    }

}