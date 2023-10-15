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
package com.mhschmieder.fxguitoolkit.stage;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.action.ProjectPropertiesActions;
import com.mhschmieder.fxguitoolkit.control.ProjectPropertiesToolBar;
import com.mhschmieder.fxguitoolkit.layout.ProjectPropertiesPane;
import com.mhschmieder.fxguitoolkit.model.ProjectProperties;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class ProjectPropertiesEditor extends XStage {

    public static final String      PROJECT_PROPERTIES_EDITOR_TITLE_DEFAULT  = "Project Properties"; //$NON-NLS-1$

    // Default window locations and dimensions.
    private static final int        PROJECT_PROPERTIES_EDITOR_WIDTH_DEFAULT  = 540;
    private static final int        PROJECT_PROPERTIES_EDITOR_HEIGHT_DEFAULT = 380;

    // Declare the actions.
    public ProjectPropertiesActions _actions;

    // Declare the main tool bar.
    public ProjectPropertiesToolBar _toolBar;

    // Declare the main content pane.
    public ProjectPropertiesPane    _projectPropertiesPane;

    @SuppressWarnings("nls")
    public ProjectPropertiesEditor( final ProductBranding productBranding,
                                    final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( PROJECT_PROPERTIES_EDITOR_TITLE_DEFAULT,
               "projectProperties",
               true,
               true,
               productBranding,
               pClientProperties );

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add all of the relevant action handlers.
    @Override
    protected void addActionHandlers() {
        // Load the action handler for the "Reset" action.
        _actions._resetAction.setEventHandler( evt -> doReset() );
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Detect the ENTER key while the Reset Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _toolBar._resetButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Reset action.
                doReset();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    protected void doReset() {
        reset();
    }

    @SuppressWarnings("nls")
    protected void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/everaldo/PackageEditors16.png",
                   PROJECT_PROPERTIES_EDITOR_WIDTH_DEFAULT,
                   PROJECT_PROPERTIES_EDITOR_HEIGHT_DEFAULT,
                   false );
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        _actions = new ProjectPropertiesActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _projectPropertiesPane = new ProjectPropertiesPane( clientProperties );
        return _projectPropertiesPane;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new ProjectPropertiesToolBar( clientProperties, _actions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    // Reset all fields to the default values, regardless of state.
    @Override
    protected void reset() {
        // Forward this method to the Project Properties Pane.
        _projectPropertiesPane.reset();
    }

    // Set and propagate the Project Properties reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        // Forward this reference to the Project Properties Pane.
        _projectPropertiesPane.setProjectProperties( pProjectProperties );
    }

    @Override
    public void updateView() {
        // Forward this reference to the Project Properties Pane.
        _projectPropertiesPane.updateView();
    }
}
