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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.tools.Duplicatable;

/**
 * Convenience class for users of the {@link Action} API. Primarily this class
 * is used to conveniently create UI controls from a given Action (this is
 * necessary for now as there is no built-in support for Action in JavaFX
 * UI controls at present).
 * <p>
 * Some of the methods in this class take a {@link Collection} of
 * {@link Action actions}. In these cases, it is likely they are designed to
 * work with {@link ActionGroup action groups}. For examples on how to work with
 * these methods, refer to the {@link ActionGroup} class documentation.
 *
 * @see Action
 * @see ActionGroup
 */
public final class ActionUtils {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ActionUtils() {}

    /**
     * Action text behavior.
     * Defines uniform action's text behavior for multi-action controls such as
     * toolbars and menus
     */
    public enum ActionTextBehavior {
        /**
         * Text is shown as usual on related control
         */
        SHOW,

        /**
         * Text is not shown on the related control
         */
        HIDE,
    }

    public static final class ButtonPropertiesMapChangeListener< T extends ButtonBase >
            implements MapChangeListener< Object, Object > {
        private final WeakReference< T > _buttonWeakReference;
        private final Action             _action;

        public ButtonPropertiesMapChangeListener( final T pButton, final Action pAction ) {
            _buttonWeakReference = new WeakReference<>( pButton );
            _action = pAction;
        }

        @Override
        public boolean equals( final Object otherObject ) {
            if ( this == otherObject ) {
                return true;
            }
            if ( ( otherObject == null ) || ( getClass() != otherObject.getClass() ) ) {
                return false;
            }

            final ButtonPropertiesMapChangeListener< ? > otherListener =
                                                                       ( ButtonPropertiesMapChangeListener< ? > ) otherObject;

            final T button = _buttonWeakReference.get();
            final ButtonBase otherButton = otherListener._buttonWeakReference.get();
            if ( button != null ? !button.equals( otherButton ) : otherButton != null ) {
                return false;
            }
            return _action.equals( otherListener._action );
        }

        @Override
        public int hashCode() {
            final T button = _buttonWeakReference.get();
            int result = button != null ? button.hashCode() : 0;
            result = ( 31 * result ) + _action.hashCode();
            return result;
        }

        @Override
        public void onChanged( final MapChangeListener.Change< ?, ? > change ) {
            final T button = _buttonWeakReference.get();
            if ( button == null ) {
                _action.getProperties().removeListener( this );
            }
            else {
                button.getProperties().clear();
                button.getProperties().putAll( _action.getProperties() );
            }
        }
    }

    public static final class MenuItemPropertiesMapChangeListener< T extends MenuItem >
            implements MapChangeListener< Object, Object > {
        private final WeakReference< T > _menuItemWeakReference;
        private final Action             _action;

        public MenuItemPropertiesMapChangeListener( final T pMenuItem, final Action pAction ) {
            _menuItemWeakReference = new WeakReference<>( pMenuItem );
            _action = pAction;
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

            final T menuItem = _menuItemWeakReference.get();
            final MenuItem otherMenuItem = otherListener._menuItemWeakReference.get();
            return menuItem != null
                ? menuItem.equals( otherMenuItem )
                : ( otherMenuItem == null ) && _action.equals( otherListener._action );
        }

        @Override
        public int hashCode() {
            final T menuItem = _menuItemWeakReference.get();
            int result = menuItem != null ? menuItem.hashCode() : 0;
            result = ( 31 * result ) + _action.hashCode();
            return result;
        }

        @Override
        public void onChanged( final MapChangeListener.Change< ?, ? > change ) {
            final T menuItem = _menuItemWeakReference.get();
            if ( menuItem == null ) {
                _action.getProperties().removeListener( this );
            }
            else {
                menuItem.getProperties().clear();
                menuItem.getProperties().putAll( _action.getProperties() );
            }
        }
    }

    /**
     * Action representation of the generic separator. Adding this action
     * anywhere in the action tree serves as indication that separator has be
     * created in its place.
     *
     * See {@link ActionGroup} for example of action tree creation
     */
    public static Action ACTION_SEPARATOR = new Action( null, null ) {
                                              @Override
                                              public String toString() {
                                                  return "Separator";              //$NON-NLS-1$
                                              }
                                          };

    public static Action ACTION_SPAN      = new Action( null, null ) {
                                              @Override
                                              public String toString() {
                                                  return "Span";                   //$NON-NLS-1$
                                              }
                                          };

    // Carry over action style classes changes to the @Styleable
    //
    // Binding is not a good solution since it wipes out existing @Styleable
    // classes.
    public static void bindStyle( final Styleable styleable, final Action action ) {
        styleable.getStyleClass().addAll( action.getStyleClass() );
        action.getStyleClass()
                .addListener( ( final ListChangeListener.Change< ? extends String > change ) -> {
                    while ( change.next() ) {
                        if ( change.wasAdded() ) {
                            styleable.getStyleClass().addAll( change.getAddedSubList() );
                        }
                        else if ( change.wasRemoved() ) {
                            styleable.getStyleClass().removeAll( change.getRemoved() );
                        }
                    }
                } );
    }

    public static < T extends MenuItem > T configure( final T menuItem, final Action action ) {
        if ( action == null ) {
            throw new NullPointerException( "Action cannot be null" ); //$NON-NLS-1$
        }

        // Button bind to action properties.
        bindStyle( menuItem, action );

        menuItem.textProperty().bind( action.textProperty() );
        menuItem.disableProperty().bind( action.disabledProperty() );
        menuItem.acceleratorProperty().bind( action.acceleratorProperty() );

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

    private static < T extends ButtonBase > T configure( final T button,
                                                         final Action action,
                                                         final ActionTextBehavior textBehavior ) {
        if ( action == null ) {
            throw new NullPointerException( "Action cannot be null" ); //$NON-NLS-1$
        }

        // Button bind to action properties.
        bindStyle( button, action );

        // button.textProperty().bind(action.textProperty());
        if ( textBehavior == ActionTextBehavior.SHOW ) {
            button.textProperty().bind( action.textProperty() );
        }
        button.disableProperty().bind( action.disabledProperty() );

        button.graphicProperty().bind( new ObjectBinding< Node >() {
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
        button.getProperties().putAll( action.getProperties() );
        action.getProperties()
                .addListener( new ButtonPropertiesMapChangeListener<>( button, action ) );

        // Tooltip requires some special handling (i.e. don't have one when
        // the text property is null.
        button.tooltipProperty().bind( new ObjectBinding< Tooltip >() {
            private final Tooltip       tooltip     = new Tooltip();
            private final StringBinding textBinding =
                                                    new When( action.longTextProperty().isEmpty() )
                                                            .then( action.textProperty() )
                                                            .otherwise( action.longTextProperty() );

            {
                bind( textBinding );
                tooltip.textProperty().bind( textBinding );
            }

            @Override
            protected Tooltip computeValue() {
                final String longText = textBinding.get();
                return ( longText == null ) || textBinding.get().isEmpty() ? null : tooltip;
            }

            @Override
            public void removeListener( final InvalidationListener listener ) {
                super.removeListener( listener );
                unbind( action.longTextProperty() );
                tooltip.textProperty().unbind();
            }
        } );

        // Handle the selected state of the button if it is of the applicable
        // type.
        if ( button instanceof ToggleButton ) {
            ( ( ToggleButton ) button ).selectedProperty()
                    .bindBidirectional( action.selectedProperty() );
        }

        // Just call the execute method on the action itself when the action
        // event occurs on the button.
        button.setOnAction( action );

        return button;
    }

    /**
     * Takes the provided {@link Action} and binds the relevant properties to
     * the supplied {@link Button}. This allows for the use of Actions
     * within custom Button subclasses.
     *
     * @param action
     *            The {@link Action} that the {@link Button} should bind to.
     * @param button
     *            The {@link ButtonBase} that the {@link Action} should be bound
     *            to.
     * @return The {@link ButtonBase} that was bound to the {@link Action}.
     */
    public static ButtonBase configureButton( final Action action, final ButtonBase button ) {
        return configureButton( action, button, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and binds the relevant properties to
     * the supplied {@link Button}. This allows for the use of Actions
     * within custom Button subclasses.
     *
     * @param action
     *            The {@link Action} that the {@link Button} should bind to.
     * @param button
     *            The {@link ButtonBase} that the {@link Action} should be bound
     *            to.
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @return The {@link ButtonBase} that was bound to the {@link Action}.
     */
    public static ButtonBase configureButton( final Action action,
                                              final ButtonBase button,
                                              final ActionTextBehavior textBehavior ) {
        return configure( button, action, textBehavior );
    }

    public static MenuItem configureMenuItem( final Action action, final MenuItem menuItem ) {
        return configure( menuItem, action );
    }

    public static Node copyNode( final Node node ) {
        if ( node instanceof ImageView ) {
            final Image image = ( ( ImageView ) node ).getImage();

            if ( image == null ) {
                return null;
            }

            return new ImageView( image );
        }
        else if ( node instanceof Duplicatable< ? > ) {
            return ( Node ) ( ( Duplicatable< ? > ) node ).duplicate();
        }
        else {
            return null;
        }
    }

    /**
     * Takes the provided {@link Action} and returns a {@link Button} instance
     * with all relevant properties bound to the properties of the Action.
     *
     * @param action
     *            The {@link Action} that the {@link Button} should bind to.
     * @return A {@link Button} that is bound to the state of the provided
     *         {@link Action}
     */
    public static Button createButton( final Action action ) {
        return configure( new Button(), action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link Button} instance
     * with all relevant properties bound to the properties of the Action.
     *
     * @param action
     *            The {@link Action} that the {@link Button} should bind to.
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @return A {@link Button} that is bound to the state of the provided
     *         {@link Action}
     */
    public static Button createButton( final Action action,
                                       final ActionTextBehavior textBehavior ) {
        return configure( new Button(), action, textBehavior );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns a {@link ButtonBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}.
     *
     * @param actions
     *            The {@link Action actions} to place on the {@link ButtonBar}.
     * @return A {@link ButtonBar} that contains {@link Node nodes} which are
     *         bound
     *         to the state of the provided {@link Action}
     */
    public static ButtonBar createButtonBar( final Collection< ? extends Action > actions ) {
        return updateButtonBar( new ButtonBar(), actions );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link CheckBox} instance
     * with all relevant properties bound to the properties of the Action.
     *
     * @param action
     *            The {@link Action} that the {@link CheckBox} should bind to.
     * @return A {@link CheckBox} that is bound to the state of the provided
     *         {@link Action}
     */
    public static CheckBox createCheckBox( final Action action ) {
        return configure( new CheckBox(), action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link CheckMenuItem}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link CheckMenuItem} should bind
     *            to.
     * @return A {@link CheckMenuItem} that is bound to the state of the
     *         provided
     *         {@link Action}
     */
    public static CheckMenuItem createCheckMenuItem( final Action action ) {
        return configure( new CheckMenuItem(), action );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns a {@link ContextMenu}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}.
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
     * Takes the provided {@link Action} and returns a {@link Hyperlink}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link Hyperlink} should bind to.
     * @return A {@link Hyperlink} that is bound to the state of the provided
     *         {@link Action}
     */
    public static Hyperlink createHyperlink( final Action action ) {
        return configure( new Hyperlink(), action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link Menu} instance
     * with all relevant properties bound to the properties of the Action.
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
     * Takes the provided {@link Action} and returns a {@link MenuButton}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link MenuButton} should bind to.
     * @return A {@link MenuButton} that is bound to the state of the provided
     *         {@link Action}
     */
    public static MenuButton createMenuButton( final Action action ) {
        return configure( new MenuButton(), action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link MenuButton}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link MenuButton} should bind to.
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @return A {@link MenuButton} that is bound to the state of the provided
     *         {@link Action}
     */
    public static MenuButton createMenuButton( final Action action,
                                               final ActionTextBehavior textBehavior ) {
        return configure( new MenuButton(), action, textBehavior );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link MenuItem} instance
     * with all relevant properties bound to the properties of the Action.
     *
     * @param action
     *            The {@link Action} that the {@link MenuItem} should bind to.
     * @return A {@link MenuItem} that is bound to the state of the provided
     *         {@link Action}
     */
    public static MenuItem createMenuItem( final Action action ) {

        final MenuItem menuItem = action.getClass().isAnnotationPresent( ActionCheck.class )
            ? new CheckMenuItem()
            : new MenuItem();

        return configure( menuItem, action );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link RadioButton}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link RadioButton} should bind
     *            to.
     * @return A {@link RadioButton} that is bound to the state of the provided
     *         {@link Action}
     */
    public static RadioButton createRadioButton( final Action action ) {
        return configure( new RadioButton(), action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link RadioMenuItem}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link RadioMenuItem} should bind
     *            to.
     * @return A {@link RadioMenuItem} that is bound to the state of the
     *         provided
     *         {@link Action}
     */
    public static RadioMenuItem createRadioMenuItem( final Action action ) {
        return configure( new RadioMenuItem( action.textProperty().get() ), action );
    }

    /**
     * Takes the provided varargs array of {@link Action} and returns a
     * {@link SegmentedButton} instance with all relevant properties bound to
     * the properties of the actions.
     *
     * @param actions
     *            A varargs array of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action}s
     */
    public static SegmentedButton createSegmentedButton( final Action... actions ) {
        return createSegmentedButton( ActionTextBehavior.SHOW, Arrays.asList( actions ) );
    }

    /**
     * Takes the provided varargs array of {@link Action} and returns a
     * {@link SegmentedButton} instance with all relevant properties bound to
     * the properties of the actions.
     *
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @param actions
     *            A varargs array of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action actions}
     */
    public static SegmentedButton createSegmentedButton( final ActionTextBehavior textBehavior,
                                                         final Action... actions ) {
        return createSegmentedButton( textBehavior, Arrays.asList( actions ) );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} and returns a
     * {@link SegmentedButton} instance with all relevant properties bound to
     * the properties of the actions.
     *
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @param actions
     *            The {@link Collection} of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action actions}
     */
    public static SegmentedButton createSegmentedButton( final ActionTextBehavior textBehavior,
                                                         final Collection< ? extends Action > actions ) {
        final ObservableList< ToggleButton > buttons = FXCollections.observableArrayList();
        for ( final Action action : actions ) {
            buttons.add( createToggleButton( action, textBehavior ) );
        }
        return new SegmentedButton( buttons );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} and returns a
     * {@link SegmentedButton} instance with all relevant properties bound to
     * the properties of the actions.
     *
     * @param actions
     *            The {@link Collection} of {@link Action} that the
     *            {@link SegmentedButton} should bind to.
     * @return A {@link SegmentedButton} that is bound to the state of the
     *         provided {@link Action}s
     */
    public static SegmentedButton createSegmentedButton( final Collection< ? extends Action > actions ) {
        return createSegmentedButton( ActionTextBehavior.SHOW, actions );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link ToggleButton}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link ToggleButton} should bind
     *            to.
     * @return A {@link ToggleButton} that is bound to the state of the provided
     *         {@link Action}
     */
    public static ToggleButton createToggleButton( final Action action ) {
        return createToggleButton( action, ActionTextBehavior.SHOW );
    }

    /**
     * Takes the provided {@link Action} and returns a {@link ToggleButton}
     * instance with all relevant properties bound to the properties of the
     * Action.
     *
     * @param action
     *            The {@link Action} that the {@link ToggleButton} should bind
     *            to.
     * @param textBehavior
     *            Defines {@link ActionTextBehavior}
     * @return A {@link ToggleButton} that is bound to the state of the provided
     *         {@link Action}
     */
    public static ToggleButton createToggleButton( final Action action,
                                                   final ActionTextBehavior textBehavior ) {
        return configure( new ToggleButton(), action, textBehavior );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns a {@link ToolBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}.
     *
     * @param actions
     *            The {@link Action actions} to place on the {@link ToolBar}.
     * @param textBehavior
     *            defines {@link ActionTextBehavior}
     * @return A {@link ToolBar} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static ToolBar createToolBar( final Collection< ? extends Action > actions,
                                         final ActionTextBehavior textBehavior ) {
        return updateToolBar( new ToolBar(), actions, textBehavior );
    }

    private static Collection< MenuItem > toMenuItems( final Collection< ? extends Action > actions ) {
        final Collection< MenuItem > items = new ArrayList<>();

        for ( final Action action : actions ) {
            if ( action instanceof ActionGroup ) {
                final Menu menu = createMenu( action );
                menu.getItems().addAll( toMenuItems( ( ( ActionGroup ) action ).getActions() ) );
                items.add( menu );
            }
            else if ( action == ACTION_SEPARATOR ) {
                items.add( new SeparatorMenuItem() );
            }
            else if ( ( action == null ) || ( action == ACTION_SPAN ) ) {
                // No-op.
            }
            else {
                items.add( createMenuItem( action ) );
            }
        }

        return items;
    }

    private static void unconfigure( final ButtonBase button ) {
        if ( ( button == null ) || !( button.getOnAction() instanceof Action ) ) {
            return;
        }

        final Action action = ( Action ) button.getOnAction();

        button.styleProperty().unbind();
        button.textProperty().unbind();
        button.disableProperty().unbind();
        button.graphicProperty().unbind();

        action.getProperties()
                .removeListener( new ButtonPropertiesMapChangeListener<>( button, action ) );

        button.tooltipProperty().unbind();

        if ( button instanceof ToggleButton ) {
            ( ( ToggleButton ) button ).selectedProperty()
                    .unbindBidirectional( action.selectedProperty() );
        }

        button.setOnAction( null );
    }

    private static void unconfigure( final MenuItem menuItem ) {
        if ( ( menuItem == null ) || !( menuItem.getOnAction() instanceof Action ) ) {
            return;
        }

        final Action action = ( Action ) menuItem.getOnAction();

        menuItem.styleProperty().unbind();
        menuItem.textProperty().unbind();
        menuItem.disableProperty().unbind();
        menuItem.acceleratorProperty().unbind();
        menuItem.graphicProperty().unbind();

        action.getProperties()
                .removeListener( new MenuItemPropertiesMapChangeListener<>( menuItem, action ) );

        if ( menuItem instanceof RadioMenuItem ) {
            ( ( RadioMenuItem ) menuItem ).selectedProperty()
                    .unbindBidirectional( action.selectedProperty() );
        }
        else if ( menuItem instanceof CheckMenuItem ) {
            ( ( CheckMenuItem ) menuItem ).selectedProperty()
                    .unbindBidirectional( action.selectedProperty() );
        }

        menuItem.setOnAction( null );
    }

    /**
     * Removes all bindings and listeners which were added when the supplied
     * {@link ButtonBase} was bound to an {@link Action} via one of the methods
     * of this class.
     *
     * @param button
     *            a {@link ButtonBase} that was bound to an {@link Action}
     */
    public static void unconfigureButton( final ButtonBase button ) {
        unconfigure( button );
    }

    /**
     * Removes all bindings and listeners which were added when the supplied
     * {@link MenuItem} was bound to an {@link Action} via one of the methods
     * of this class.
     *
     * @param menuItem
     *            a {@link MenuItem} that was bound to an {@link Action}
     */
    public static void unconfigureMenuItem( final MenuItem menuItem ) {
        unconfigure( menuItem );
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and updates a {@link ButtonBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}. Previous content of button bar is removed
     *
     * @param buttonBar
     *            The {@link ButtonBar buttonBar} to update
     * @param actions
     *            The {@link Action actions} to place on the {@link ButtonBar}.
     * @return A {@link ButtonBar} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static ButtonBar updateButtonBar( final ButtonBar buttonBar,
                                             final Collection< ? extends Action > actions ) {
        buttonBar.getButtons().clear();
        for ( final Action action : actions ) {
            if ( action instanceof ActionGroup ) {
                // No-op.
            }
            else if ( ( action == ACTION_SPAN ) || ( action == ACTION_SEPARATOR )
                    || ( action == null ) ) {
                // No-op.
            }
            else {
                buttonBar.getButtons().add( createButton( action, ActionTextBehavior.SHOW ) );
            }
        }

        return buttonBar;
    }

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and updates a {@link ContextMenu}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}. Previous content of context menu is removed
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
            if ( ( action == ACTION_SEPARATOR ) || ( action == ACTION_SPAN ) ) {
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

    /**
     * Takes the provided {@link Collection} of {@link Action} (or subclasses,
     * such as {@link ActionGroup}) instances and returns provided
     * {@link ToolBar}
     * populated with appropriate {@link Node nodes} bound to the provided
     * {@link Action actions}. Previous toolbar content is removed
     *
     * @param toolbar
     *            The {@link ToolBar toolbar} to update
     * @param actions
     *            The {@link Action actions} to place on the {@link ToolBar}.
     * @param textBehavior
     *            defines {@link ActionTextBehavior}
     * @return A {@link ToolBar} that contains {@link Node nodes} which are
     *         bound to the state of the provided {@link Action}
     */
    public static ToolBar updateToolBar( final ToolBar toolbar,
                                         final Collection< ? extends Action > actions,
                                         final ActionTextBehavior textBehavior ) {
        toolbar.getItems().clear();
        for ( final Action action : actions ) {
            if ( action instanceof ActionGroup ) {
                final MenuButton menu = createMenuButton( action, textBehavior );
                menu.setFocusTraversable( false );
                menu.getItems().addAll( toMenuItems( ( ( ActionGroup ) action ).getActions() ) );
                toolbar.getItems().add( menu );
            }
            else if ( action == ACTION_SEPARATOR ) {
                toolbar.getItems().add( new Separator() );
            }
            else if ( action == ACTION_SPAN ) {
                final Pane span = new Pane();
                HBox.setHgrow( span, Priority.ALWAYS );
                VBox.setVgrow( span, Priority.ALWAYS );
                toolbar.getItems().add( span );
            }
            else if ( action == null ) {
                // No-op.
            }
            else {
                ButtonBase button;
                if ( action.getClass().getAnnotation( ActionCheck.class ) != null ) {
                    button = createToggleButton( action, textBehavior );
                }
                else {
                    button = createButton( action, textBehavior );
                }
                button.setFocusTraversable( false );
                toolbar.getItems().add( button );
            }
        }

        return toolbar;
    }

}