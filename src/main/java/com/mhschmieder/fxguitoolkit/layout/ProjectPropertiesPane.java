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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.SceneGraphUtilities;
import com.mhschmieder.fxguitoolkit.control.TextEditor;
import com.mhschmieder.fxguitoolkit.model.ProjectProperties;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class ProjectPropertiesPane extends VBox {

    private Label projectNameLabel;
    private TextEditor projectNameEditor;

    private Label projectTypeLabel;
    private TextEditor projectTypeEditor;

    private Label projectLocationLabel;
    private TextEditor projectLocationEditor;

    private Label projectAuthorLabel;
    private TextEditor projectAuthorEditor;

    private Label projectDateLabel;
    private DatePicker projectDatePicker;

    protected TextArea projectNotes;

    // Declare a local cache of the Project Properties model.
    protected ProjectProperties projectProperties;

    // Cache the Project Category for reference during label creation.
    protected String projectCategory;

    public ProjectPropertiesPane( final String pProjectCategory,
                                  final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();
        
        projectCategory = pProjectCategory;

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties pClientProperties ) {
        createControls( pClientProperties );

        // Make a Grid Pane to host the full group of labels and controls.
        final GridPane propertiesPane = LayoutFactory.makeGridPane(
                Pos.CENTER,
                new Insets( 3.0d, 3.0d, 3.0d, 3.0d ),
                6.0d,
                12.0d );

        final Label projectPropertiesHeader = GuiUtilities.getInfoLabel(
                projectCategory + " Properties" );
        GridPane.setHalignment( projectPropertiesHeader, HPos.CENTER );

        propertiesPane.add( projectPropertiesHeader, 0, 0, 2, 1 );
        propertiesPane.add( projectNameLabel, 0, 1 );
        propertiesPane.add( projectNameEditor, 1, 1 );
        propertiesPane.add( projectTypeLabel, 0, 2 );
        propertiesPane.add( projectTypeEditor, 1, 2 );
        propertiesPane.add( projectLocationLabel, 0, 3 );
        propertiesPane.add( projectLocationEditor, 1, 3 );
        propertiesPane.add( projectAuthorLabel, 0, 4 );
        propertiesPane.add( projectAuthorEditor, 1, 4 );
        propertiesPane.add( projectDateLabel, 0, 5 );
        propertiesPane.add( projectDatePicker, 1, 5 );

        final GridPane textAreaPane = SceneGraphUtilities
                .getLabeledTextAreaPane(
                        projectCategory + " Notes",
                        projectNotes,
                        pClientProperties );

        // Create a grid to host the nested panes.
        final GridPane grid = new GridPane();
        grid.setVgap( 12.0d );

        grid.add( propertiesPane, 0, 0 );
        grid.add( textAreaPane, 0, 1 );

        getChildren().addAll( grid );

        setAlignment( Pos.CENTER );
        setSpacing( 16.0d );
        setPadding( new Insets( 16.0d, 8.0d, 16.0d, 8.0d ) );

        setPrefWidth( 460.0d );

        // This is a volatile reference that is used only for bindings and dirty
        // flag handling as the associated layout parent gets updated for each
        // animat selection, so there is no reason to pass in this reference.
        projectProperties = new ProjectProperties();

        // Now it is safe to bind the model properties to the GUI.
        bindProperties();
    }

    public void createControls( final ClientProperties pClientProperties ) {
        projectNameLabel = makePropertySheetLabel( "Name" );
        projectNameEditor = makePropertySheetEditor(
                projectCategory
                        + " Name, which May be Different from "
                        + projectCategory
                        + " Filename",
                pClientProperties );

        projectTypeLabel = makePropertySheetLabel( "Type" );
        projectTypeEditor  = makePropertySheetEditor(
                projectCategory
                        + " Type for the "
                        + projectCategory
                        + "; Often with Domain-specific Terminology",
                pClientProperties );

        projectLocationLabel = makePropertySheetLabel( "Location" );
        projectLocationEditor = makePropertySheetEditor(
                projectCategory
                        + " Location for the "
                        + projectCategory
                        + "; Often with Domain-specific Terminology",
                pClientProperties );

        projectAuthorLabel = makePropertySheetLabel( "Author" );
        projectAuthorEditor = makePropertySheetEditor(
                projectCategory
                        + " Author for the "
                        + projectCategory
                        + "; Often with Domain-specific Terminology",
                pClientProperties );

        projectDateLabel = makePropertySheetLabel( "Date" );

        // TODO: Use CSS to style this control more to our liking.
        // TODO: Start playing with options for Chronology as well, if pertinent.
        projectDatePicker = new DatePicker();
        projectDatePicker.setShowWeekNumbers( true );
        projectDatePicker.setTooltip( new Tooltip(
                "The Date that the "
                        + projectCategory
                        + "Was Created or Edited" ) );

        projectNotes = GuiUtilities.getNotesEditor( 12 );
    }
    
    private Label makePropertySheetLabel( final String pProperty ) {
        final double labelWidth = 160.0d;

        final Label label = GuiUtilities.getPropertySheetLabel(
                projectCategory + " " + pProperty );
        label.setAlignment( Pos.CENTER_LEFT );
        label.setPrefWidth( labelWidth );
        GridPane.setHalignment( label, HPos.LEFT );
        
        return label;
    }
    
    private TextEditor makePropertySheetEditor( 
            final String pTooltipText,
            final ClientProperties pClientProperties) {
        final double controlWidth = 280.0d;

        // NOTE: We give editors a bit of extra height due to font size
        //  differences on macOS and Linux, so the comma doesn't clip. This is
        //  best done via font.css, font-mac.css, etc. using "em" units.
        // TODO: Set the controls from a resource file that might be different
        //  based on locality etc. vs. hard-coding their displayed text as here.
        final double editorHeight = 26.0d;

        final TextEditor textEditor = new TextEditor(
                "",
                pTooltipText,
                true,
                pClientProperties );
        textEditor.setPrefWidth( controlWidth );
        textEditor.setPrefHeight( editorHeight );
        
        return textEditor;
    }

    private void bindProperties() {
        // Bidirectionally bind the Project Properties to their associated text
        // input controls' value properties (which reflect committed edits).
        // TODO: Do something similar for TextArea, to better handle control?
        projectNameEditor.valueProperty().bindBidirectional(
                projectProperties.projectNameProperty() );
        projectTypeEditor.valueProperty().bindBidirectional(
                projectProperties.projectTypeProperty() );
        projectLocationEditor.valueProperty().bindBidirectional(
                projectProperties.projectLocationProperty() );
        projectAuthorEditor.valueProperty().bindBidirectional(
                projectProperties.projectAuthorProperty() );
        projectDatePicker.valueProperty().bindBidirectional(
                projectProperties.projectDateProperty() );
        projectNotes.textProperty().bindBidirectional(
                projectProperties.projectNotesProperty() );
    }

    private void unbindProperties() {
        // Bidirectionally unbind the Project Properties from their
        // associated text input controls' value properties (which reflect
        // committed edits).
        // TODO: Do something similar for TextArea, to better handle control?
        projectNameEditor.valueProperty().unbindBidirectional(
                projectProperties.projectNameProperty() );
        projectTypeEditor.valueProperty().unbindBidirectional(
                projectProperties.projectTypeProperty() );
        projectLocationEditor.valueProperty().unbindBidirectional(
                projectProperties.projectLocationProperty() );
        projectAuthorEditor.valueProperty().unbindBidirectional(
                projectProperties.projectAuthorProperty() );
        projectDatePicker.valueProperty().unbindBidirectional(
                projectProperties.projectDateProperty() );
        projectNotes.textProperty().unbindBidirectional(
                projectProperties.projectNotesProperty() );
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        projectProperties.reset();

        // Update the view to match the new model.
        updateView();
    }

    public void updateView() {
        // NOTE: Project Notes (TextArea) seems to re-sync automatically.
        projectNameEditor.setValue( projectProperties.getProjectName() );
        projectTypeEditor.setValue( projectProperties.getProjectType() );
        projectLocationEditor.setValue( projectProperties.getProjectLocation() );
        projectAuthorEditor.setValue( projectProperties.getProjectAuthor() );
        projectDatePicker.setValue( projectProperties.getProjectDate() );
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
        projectLocationLabel.setText( pProjectTypeLabel );
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
        projectLocationLabel.setText( pProjectLocationLabel );
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
        projectAuthorLabel.setText( pProjectAuthorLabel );
    }

    public final ProjectProperties getProjectProperties() {
        return projectProperties;
    }

    // Set and bind the Project Properties reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    // NOTE: This is probably unwise to call now that we own the data
    //  model at construction time, but we unbind first to be safe.
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        // Unbind the data model to the respective GUI components.
        unbindProperties();

        // Cache the Project Properties reference.
        projectProperties = pProjectProperties;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground(
                backColor );
        setBackground( background );
    }
}
