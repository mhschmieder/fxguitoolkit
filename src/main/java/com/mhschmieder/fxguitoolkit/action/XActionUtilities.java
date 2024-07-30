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
package com.mhschmieder.fxguitoolkit.action;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.tools.Duplicatable;

import com.mhschmieder.fxguitoolkit.control.MenuUtilities;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * These are wrappers around the ControlsFX ActionUtils methods, taking care of
 * any extended behavior unique to xAction and XActionGroup while delegating the
 * normal handling to the existing methods in ControlsFX, when possible.
 * 
 * NOTE: Most of this code is identical as there is very little behavior to add
 * for XAction and XActionGroup, but unfortunately much of the ActionUtil class
 * is Private API, so there was no way to extend behavior via nested invocation.
 */
public final class XActionUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private XActionUtilities() {}

    /**
     * Takes the provided {@link XAction} and returns a {@link MenuItem}
     * instance with all relevant properties bound to the properties of the
     * Action.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, as
     * we need to extend coverage for RadioMenuItem needs.
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
        final MenuItem menuItem = ( action.getClass().isAnnotationPresent( ActionCheck.class )
                || action.isCheck() || action.isToggle() )
                    ? new CheckMenuItem()
                    : action.isChoice() ? new RadioMenuItem() : new MenuItem();

        return configure( menuItem, action );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link Menu} instance
     * with all relevant properties bound to the properties of the Action.
     * <p>
     * NOTE: This is a revision of the method in ControlsFX ActionUtils, to
     *  extend coverage for ActionGroup needs in the enhanced configure() call.
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

            if ( action == null ) {
                // Nothing to do here, but we want to avoid errors.
            }
            else if ( action instanceof ActionGroup ) {
                menu.getItems().addAll( toMenuItems( ( ( ActionGroup ) action ).getActions() ) );
            }
            else {
                // Nothing to do here, but we want to avoid errors.
            }

            menuBar.getMenus().add( menu );
        }

        return menuBar;
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

    // NOTE: This is a revision of the method in ControlsFX ActionUtils, to
    //  extend coverage for RadioMenuItem needs, including Menu Item ToggleGroups.
    private static Collection< MenuItem > toMenuItems( final Collection< ? extends Action > actions ) {
        final Collection< MenuItem > items = new ArrayList<>( actions.size() );

        for ( final Action action : actions ) {
            // Due to class derivation, it is important to check for class
            // instanceof in descending order of the class hierarchy.
            if ( action == null ) {
                // Nothing to do here, but we want to avoid errors.
            }
            else if ( action instanceof XActionGroup ) {
                final Menu menu = createMenu( action );
                final XActionGroup actionGroup = ( XActionGroup ) action;
                final Collection< MenuItem > menuItems = toMenuItems( actionGroup.getActions() );
                if ( actionGroup.isChoiceGroup() ) {
                    // Set the Toggle Group for the Choice Group of Menu Items.
                    MenuUtilities.setToggleGroup( menuItems );
                }
                menu.getItems().addAll( menuItems );
                items.add( menu );
            }
            else if ( action instanceof ActionGroup ) {
                final Menu menu = ActionUtils.createMenu( action );

                // Make sure the mnemonic is used to underline a character vs. printing
                // as a separate literal character.
                menu.setMnemonicParsing( true );

                final Collection< MenuItem > menuItems = toMenuItems( ( ( ActionGroup ) action )
                        .getActions() );
                menu.getItems().addAll( menuItems );
                items.add( menu );
            }
            else if ( action instanceof XAction ) {
                final MenuItem menuItem = createMenuItem( ( XAction ) action );
                items.add( menuItem );
            }
            else if ( action instanceof Action ) {
                final MenuItem menuItem = ActionUtils.createMenuItem( action );

                // Make sure the mnemonic is used to underline a character vs. printing
                // as a separate literal character.
                menuItem.setMnemonicParsing( true );

                items.add( menuItem );
            }
            else if ( ActionUtils.ACTION_SEPARATOR.equals( action ) ) {
                items.add( new SeparatorMenuItem() );
            }
            else if ( ActionUtils.ACTION_SPAN.equals( action ) ) {
                // Nothing to do here, but we want to avoid errors.
            }
            else {
                // Nothing to do here, but we want to avoid errors.
            }
        }

        return items;
    }

    protected static Node copyNode( final Node node ) {
        if ( node instanceof ImageView ) {
            final Image image = ( ( ImageView ) node ).getImage();
            return new ImageView( image );
        }
        else if ( node instanceof Duplicatable< ? > ) {
            return ( Node ) ( ( Duplicatable< ? > ) node ).duplicate();
        }
        else {
            return null;
        }
    }

    // Carry over action style classes changes to the @Styleable
    //
    // Binding is not a good solution since it wipes out existing @Styleable
    // classes.
    private static void bindStyle( final Styleable styleable, final Action action ) {
        styleable.getStyleClass().addAll( action.getStyleClass() );
        action.getStyleClass()
                .addListener( ( final ListChangeListener.Change< ? extends String > change ) -> {
                    while ( change.next() ) {
                        if ( change.wasRemoved() ) {
                            styleable.getStyleClass().removeAll( change.getRemoved() );
                        }
                        if ( change.wasAdded() ) {
                            styleable.getStyleClass().addAll( change.getAddedSubList() );
                        }
                    }
                } );
    }

    // NOTE: This is a revision of the method in ControlsFX ActionUtils, to
    //  extend coverage for RadioMenuItem needs.
    private static < T extends MenuItem > T configure( final T menuItem, final Action action ) {
        if ( action == null ) {
            throw new NullPointerException( "Action cannot be null" ); //$NON-NLS-1$
        }

        // Button bind to action properties.
        bindStyle( menuItem, action );

        // Make sure the mnemonic is used to underline a character vs. printing
        // as a separate literal character.
        menuItem.setMnemonicParsing( true );

        menuItem.textProperty().bind( action.textProperty() );
        menuItem.disableProperty().bind( action.disabledProperty() );
        menuItem.acceleratorProperty().bind( action.acceleratorProperty() );

        // NOTE: This is the only setting unique to XAction and XActionGroup,
        // but we can't make a nested call to ActionUtils.configure() as it is
        // Private API, so we copy/paste and extend here instead.
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
                return copyNode( action.graphicProperty().get() );
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
                .addListener( new MenuItemPropertiesMapChangeListener<>( menuItem, action ) );

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

    private static final class MenuItemPropertiesMapChangeListener< T extends MenuItem >
            implements MapChangeListener< Object, Object > {

        private final WeakReference< T > menuItemWeakReference;
        private final Action             action;

        protected MenuItemPropertiesMapChangeListener( final T pMenuItem, final Action pAction ) {
            menuItemWeakReference = new WeakReference<>( pMenuItem );
            action = pAction;
        }

        @Override
        public void onChanged( final MapChangeListener.Change< ?, ? > change ) {
            final T menuItem = menuItemWeakReference.get();
            if ( menuItem == null ) {
                action.getProperties().removeListener( this );
            }
            else {
                menuItem.getProperties().clear();
                menuItem.getProperties().putAll( action.getProperties() );
            }
        }

        @Override
        public boolean equals( final Object otherObject ) {
            if ( this == otherObject ) {
                return true;
            }
            if ( ( otherObject == null ) || ( getClass() != otherObject.getClass() ) ) {
                return false;
            }

            final MenuItemPropertiesMapChangeListener< ? > otherListener =
                                                                         ( MenuItemPropertiesMapChangeListener< ? > ) otherObject;

            final T menuItem = menuItemWeakReference.get();
            final MenuItem otherMenuItem = otherListener.menuItemWeakReference.get();
            return menuItem != null
                ? menuItem.equals( otherMenuItem )
                : ( otherMenuItem == null ) && action.equals( otherListener.action );
        }

        @Override
        public int hashCode() {
            final T menuItem = menuItemWeakReference.get();
            int result = menuItem != null ? menuItem.hashCode() : 0;
            result = ( 31 * result ) + action.hashCode();
            return result;
        }
    }

}
