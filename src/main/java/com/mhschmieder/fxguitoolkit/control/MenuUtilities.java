/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.GlobalUtilities;
import com.mhschmieder.fxguitoolkit.action.ActionFactory;
import com.mhschmieder.fxguitoolkit.action.XAction;
import com.mhschmieder.fxguitoolkit.action.XActionGroup;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;

/**
 * This is a utility class for dealing with common menu functionality.
 */
public final class MenuUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private MenuUtilities() {}

    // NOTE: This is a revision of the method in ControlsFX ActionUtils, to
    // extend coverage for RadioMenuItem needs.
    public static < T extends MenuItem > T configure( final T menuItem, final Action action ) {
        if ( action == null ) {
            throw new NullPointerException( "Action cannot be null" ); //$NON-NLS-1$
        }

        // Button bind to action properties.
        ActionUtils.bindStyle( menuItem, action );

        menuItem.textProperty().bind( action.textProperty() );
        menuItem.disableProperty().bind( action.disabledProperty() );
        menuItem.acceleratorProperty().bind( action.acceleratorProperty() );

        if ( ( action instanceof XAction ) && ( ( XAction ) action ).isHideIfDisabled() ) {
            menuItem.visibleProperty().bind( action.disabledProperty().not() );
        }
        else if ( ( action instanceof XActionGroup )
                && ( ( XActionGroup ) action ).isHideIfDisabled() ) {
            menuItem.visibleProperty().bind( action.disabledProperty().not() );
        }

        menuItem.graphicProperty().bind( new ObjectBinding< Node >() {
            {
                bind( action.graphicProperty() );
            }

            @Override
            protected Node computeValue() {
                return ActionUtils.copyNode( action.graphicProperty().get() );
            }

            @Override
            public void removeListener( final InvalidationListener listener ) {
                super.removeListener( listener );
                unbind( action.graphicProperty() );
            }
        } );

        // Add all the properties of the action into the button, and set up
        // a listener so they are always copied across.
        menuItem.getProperties().putAll( action.getProperties() );
        action.getProperties()
                .addListener( new ActionUtils.MenuItemPropertiesMapChangeListener<>( menuItem,
                                                                                     action ) );

        // Handle the selected state of the menu item if it is a
        // CheckMenuItem or RadioMenuItem.
        if ( menuItem instanceof RadioMenuItem ) {
            ( ( RadioMenuItem ) menuItem ).selectedProperty()
                    .bindBidirectional( action.selectedProperty() );
        }
        else if ( menuItem instanceof CheckMenuItem ) {
            ( ( CheckMenuItem ) menuItem ).selectedProperty()
                    .bindBidirectional( action.selectedProperty() );
        }

        // Just call the execute method on the action itself when the action
        // event occurs on the button.
        menuItem.setOnAction( action );

        return menuItem;
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns a {@link ContextMenu}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param actions
     *            The {@link Action actions} to place on the
     *            {@link ContextMenu}.
     * @return A {@link ContextMenu} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static ContextMenu createContextMenu( final Collection< ? extends Action > actions ) {
        return updateContextMenu( new ContextMenu(), actions );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link Menu} instance
     * with all relevant properties bound to the properties of the Action.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param action
     *            The {@link Action} that the {@link Menu} should bind to.
     * @return A {@link Menu} that is bound to the state of the provided
     *         {@link Action}
     */
    public static Menu createMenu( final Action action ) {
        return configure( new Menu(), action );
    }

    /**
     * Takes the provided {@link XActionGroup} and returns a {@link Menu}
     * instance with all relevant properties bound to the properties of the
     * Action.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param action
     *            The {@link XActionGroup} that the {@link Menu} should bind
     *            to.
     * @return A {@link Menu} that is bound to the state of the provided
     *         {@link Action}
     */
    public static Menu createMenu( final XActionGroup actionGroup ) {
        final Menu menu = configure( new Menu(), actionGroup );

        final Collection< MenuItem > menuItems = toMenuItems( actionGroup.getActions() );

        if ( actionGroup.isChoiceGroup() ) {
            // Set the Toggle Group for the Choice Group of Menu Items.
            setToggleGroup( menuItems );
        }

        menu.getItems().addAll( menuItems );

        return menu;
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns a {@link MenuBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param actions
     *            The {@link Action actions} to place on the {@link MenuBar}.
     * @return A {@link MenuBar} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static MenuBar createMenuBar( final Collection< ? extends Action > actions ) {
        return updateMenuBar( new MenuBar(), actions );
    }

    /**
     * Takes the provided {@link XAction} and returns a {@link MenuItem}
     * instance
     * with all relevant properties bound to the properties of the Action.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for RadioMenuItem needs.
     *
     * @param action
     *            The {@link XAction} that the {@link MenuItem} should bind
     *            to.
     * @return A {@link MenuItem} that is bound to the state of the provided
     *         {@link Action}
     */
    public static MenuItem createMenuItem( final XAction action ) {
        // NOTE: This is messy logic because we cannot yet use annotation alone
        // in order to distinguish the required type of Menu Item to return.
        // Preferably we would use a switch statement for the Action Verb cases.
        final MenuItem menuItem = action.getClass().isAnnotationPresent( ActionCheck.class )
                || action.isCheck() || action.isToggle()
                    ? new CheckMenuItem()
                    : action.isChoice() ? new RadioMenuItem() : new MenuItem();

        return configure( menuItem, action );
    }

    // If an accelerator is assigned, set it by platform.
    public static void setMenuItemAccelerator( final ClientProperties clientProperties,
                                               final MenuItem menuItem,
                                               final String menuName,
                                               final String itemName,
                                               final String bundleName ) {
        // Fail-safe check to avoid unnecessary null pointer exceptions.
        if ( menuItem == null ) {
            return;
        }

        final ResourceBundle resourceBundle = GlobalUtilities
                .getResourceBundle( clientProperties, bundleName, false );

        // If an accelerator is assigned, get it from a resource bundle.
        final KeyCombination acceleratorKeyCombination = ActionFactory
                .makeAcceleratorKeyCombination( clientProperties,
                                                menuName,
                                                itemName,
                                                resourceBundle );
        if ( acceleratorKeyCombination != null ) {
            menuItem.setAccelerator( acceleratorKeyCombination );
        }
    }

    private static void setToggleGroup( final Collection< MenuItem > menuItems ) {
        // Declare a Toggle Group to hold the mutually exclusive choices, and
        // verify they are modeled as Radio Menu Items.
        final ToggleGroup toggleGroup = new ToggleGroup();
        for ( final MenuItem menuItem : menuItems ) {
            if ( menuItem instanceof RadioMenuItem ) {
                ( ( RadioMenuItem ) menuItem ).setToggleGroup( toggleGroup );
            }
        }
    }

    // NOTE: This is a revision of the method in ControlsFX ActionUtils, to
    // extend coverage for RadioMenuItem needs.
    private static Collection< MenuItem > toMenuItems( final Collection< ? extends Action > actions ) {
        final Collection< MenuItem > items = new ArrayList<>( actions.size() );

        for ( final Action action : actions ) {
            if ( action instanceof XActionGroup ) {
                final Menu menu = createMenu( ( XActionGroup ) action );
                items.add( menu );
            }
            else if ( action instanceof ActionGroup ) {
                final Menu menu = ActionUtils.createMenu( action );
                final Collection< MenuItem > menuItems = toMenuItems( ( ( ActionGroup ) action )
                        .getActions() );
                menu.getItems().addAll( menuItems );
                items.add( menu );
            }
            else if ( action == ActionUtils.ACTION_SEPARATOR ) {
                items.add( new SeparatorMenuItem() );
            }
            else if ( ( action == null ) || ( action == ActionUtils.ACTION_SPAN ) ) {
                // No-op.
            }
            else if ( action instanceof XAction ) {
                final MenuItem menuItem = createMenuItem( ( XAction ) action );
                items.add( menuItem );
            }
            else {
                final MenuItem menuItem = ActionUtils.createMenuItem( action );
                items.add( menuItem );
            }
        }

        return items;
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and updates a {@link ContextMenu}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}. Previous content of context menu is removed
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param menu
     *            The {@link ContextMenu menu} to update
     * @param actions
     *            The {@link Action actions} to place on the
     *            {@link ContextMenu}.
     * @return A {@link ContextMenu} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static ContextMenu updateContextMenu( final ContextMenu menu,
                                                 final Collection< ? extends Action > actions ) {
        menu.getItems().clear();
        menu.getItems().addAll( toMenuItems( actions ) );
        return menu;
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and updates a {@link MenuBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}. Previous MenuBar content is removed.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     * extend coverage for ActionGroup needs.
     *
     * @param menuBar
     *            The {@link MenuBar menuBar} to update
     * @param actions
     *            The {@link Action actions} to place on the {@link MenuBar}.
     * @return A {@link MenuBar} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static MenuBar updateMenuBar( final MenuBar menuBar,
                                         final Collection< ? extends Action > actions ) {
        menuBar.getMenus().clear();
        for ( final Action action : actions ) {
            if ( ( action == ActionUtils.ACTION_SEPARATOR )
                    || ( action == ActionUtils.ACTION_SPAN ) ) {
                continue;
            }

            final Menu menu = createMenu( action );

            if ( action instanceof ActionGroup ) {
                menu.getItems().addAll( toMenuItems( ( ( ActionGroup ) action ).getActions() ) );
            }
            else if ( action == null ) {
                // No-op.
            }

            menuBar.getMenus().add( menu );
        }

        return menuBar;
    }

}
