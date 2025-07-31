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
package com.mhschmieder.fxguitoolkit.stage;

import java.util.Locale;

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
import javafx.scene.paint.Color;

public final class ProjectPropertiesEditor extends XStage {

    // Default window locations and dimensions.
    private static final double PROJECT_PROPERTIES_EDITOR_WIDTH_DEFAULT = 590.0d;
    private static final double  PROJECT_PROPERTIES_EDITOR_HEIGHT_DEFAULT = 400.0d;

    // Cache the Project Category for reference during label creation.
    protected String projectCategory;

    // Declare the actions.
    public ProjectPropertiesActions actions;

    // Declare the main tool bar.
    public ProjectPropertiesToolBar toolBar;

    // Declare the main content pane.
    public ProjectPropertiesPane projectPropertiesPane;

    public ProjectPropertiesEditor( final ProductBranding pProductBranding,
                                    final String pProjectCategory,
                                    final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        // NOTE: The window key prefix is bumped whenever default size changes.
        super( pProjectCategory + " Properties",
               pProjectCategory.toLowerCase( Locale.US ) + "Properties3",
               true,
               true,
               pProductBranding,
               pClientProperties );

        projectCategory = pProjectCategory;

        try {
            initStage( true );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initStage( final boolean resizable ) {
        // First have the superclass initialize its content.
        initStage( "/icons/everaldo/PackageEditors16.png",
                   PROJECT_PROPERTIES_EDITOR_WIDTH_DEFAULT,
                   PROJECT_PROPERTIES_EDITOR_HEIGHT_DEFAULT,
                   resizable );
    }

    // Add all of the relevant action handlers.
    @Override
    protected void addActionHandlers() {
        // Load the action handler for the "Reset" action.
        actions.resetAction.setEventHandler( evt -> doReset() );
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    //  the toolbar buttons and their associated menu items, so that when one
    //  is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Detect the ENTER key while the Reset Button has focus, and use it to
        // trigger its action (standard expected behavior).
        toolBar.resetButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Reset action.
                doReset();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        actions = new ProjectPropertiesActions( 
                projectCategory, 
                clientProperties );
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        toolBar = new ProjectPropertiesToolBar( clientProperties, actions );

        // Return the Tool Bar so the superclass can use it.
        return toolBar;
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        projectPropertiesPane = new ProjectPropertiesPane(
                projectCategory,
                clientProperties );
        return projectPropertiesPane;
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this method to the Project Properties Pane.
        projectPropertiesPane.setForegroundFromBackground( backColor );
    }

    /**
     * Sets a custom text string for the Project Type Label.
     * <p>
     * For example, in some application domains, the terminology is "Scenario".
     *
     * @param pProjectTypeLabel the custom text to use for Project Type
     */
    public final void setProjectTypeLabel(
            final String pProjectTypeLabel ) {
        // Forward this method to the Project Properties Pane.
        projectPropertiesPane.setProjectTypeLabel( pProjectTypeLabel );
    }

    /**
     * Sets a custom text string for the Project Location Label.
     * <p>
     * For example, in some application domains, the terminology is "Venue".
     *
     * @param pProjectLocationLabel the custom text to use for Project Location
     */
    public final void setProjectLocationLabel(
            final String pProjectLocationLabel ) {
        // Forward this method to the Project Properties Pane.
        projectPropertiesPane.setProjectLocationLabel( pProjectLocationLabel );
    }

    /**
     * Sets a custom text string for the Project Author Label.
     * <p>
     * For example, in some application domains, the terminology is "Designer".
     *
     * @param pProjectAuthorLabel the custom text to use for Project Author
     */
    public final void setProjectAuthorLabel(
            final String pProjectAuthorLabel ) {
        // Forward this method to the Project Properties Pane.
        projectPropertiesPane.setProjectAuthorLabel( pProjectAuthorLabel );
    }

    public final ProjectProperties getProjectProperties() {
        // Forward this method to the Project Properties Pane.
        return projectPropertiesPane.getProjectProperties();
    }

    protected void doReset() {
        reset();
    }

    // Reset all fields to the default values, regardless of state.
    @Override
    protected void reset() {
        // Forward this method to the Project Properties Pane.
        projectPropertiesPane.reset();
    }

    // Set and propagate the Project Properties reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        // Forward this reference to the Project Properties Pane.
        projectPropertiesPane.setProjectProperties( pProjectProperties );
    }

    @Override
    public void updateView() {
        // Forward this reference to the Project Properties Pane.
        projectPropertiesPane.updateView();
    }
}
