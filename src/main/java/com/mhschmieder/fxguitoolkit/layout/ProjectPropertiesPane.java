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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.control.TextEditor;
import com.mhschmieder.fxguitoolkit.model.ProjectProperties;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class ProjectPropertiesPane extends VBox {

    protected TextEditor        _projectNameTextField;
    protected TextEditor        _venueTextField;
    protected TextEditor        _designerTextField;
    protected TextEditor        _dateTextField;
    protected TextArea          _projectNotes;

    // Cache a reference to the global Project Properties.
    protected ProjectProperties projectProperties;

    public ProjectPropertiesPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void bindProperties() {
        // Bidirectionally bind the Project Properties to their associated text
        // input controls' value properties (which reflect committed edits).
        // TODO: Do something similar for TextArea, to better handle control?
        _projectNameTextField.textProperty()
                .bindBidirectional( projectProperties.projectNameProperty() );
        _venueTextField.textProperty().bindBidirectional( projectProperties.venueProperty() );
        _designerTextField.textProperty().bindBidirectional( projectProperties.designerProperty() );
        _dateTextField.textProperty().bindBidirectional( projectProperties.dateProperty() );
        _projectNotes.textProperty().bindBidirectional( projectProperties.projectNotesProperty() );
    }

    private void initPane( final ClientProperties clientProperties ) {
        final double textFieldWidth = 360d;

        final Label projectNameLabel = GuiUtilities.getPropertySheetLabel( "Project" ); //$NON-NLS-1$
        _projectNameTextField = new TextEditor( true, clientProperties );
        _projectNameTextField.setPrefWidth( textFieldWidth );

        final Label venueLabel = GuiUtilities.getPropertySheetLabel( "Venue" ); //$NON-NLS-1$
        _venueTextField = new TextEditor( true, clientProperties );
        _venueTextField.setPrefWidth( textFieldWidth );

        final Label designerLabel = GuiUtilities.getPropertySheetLabel( "Designer" ); //$NON-NLS-1$
        _designerTextField = new TextEditor( true, clientProperties );
        _designerTextField.setPrefWidth( textFieldWidth );

        final Label dateLabel = GuiUtilities.getPropertySheetLabel( "Date" ); //$NON-NLS-1$
        _dateTextField = new TextEditor( true, clientProperties );
        _dateTextField.setPrefWidth( textFieldWidth );

        _projectNotes = GuiUtilities.getNotesEditor( 12 );

        final GridPane textAreaPane = com.mhschmieder.fxguitoolkit.SceneGraphUtilities
                .getLabeledTextAreaPane( "Project Notes", //$NON-NLS-1$
                                         _projectNotes,
                                         clientProperties );

        // Create a grid to host the Project Properties panes.
        final GridPane projectPropertiesPane = LayoutFactory
                .makeGridPane( Pos.CENTER, new Insets( 16d, 8.0d, 16d, 8.0d ), 6, 12 );

        projectPropertiesPane.add( projectNameLabel, 0, 0 );
        projectPropertiesPane.add( _projectNameTextField, 1, 0 );
        projectPropertiesPane.add( venueLabel, 0, 1 );
        projectPropertiesPane.add( _venueTextField, 1, 1 );
        projectPropertiesPane.add( designerLabel, 0, 2 );
        projectPropertiesPane.add( _designerTextField, 1, 2 );
        projectPropertiesPane.add( dateLabel, 0, 3 );
        projectPropertiesPane.add( _dateTextField, 1, 3 );

        // Create a grid to host the nested panes.
        final GridPane grid = new GridPane();
        grid.setVgap( 12d );

        grid.add( projectPropertiesPane, 0, 0 );
        grid.add( textAreaPane, 0, 1 );

        getChildren().addAll( grid );

        setAlignment( Pos.CENTER );
        setSpacing( 16d );
        setPadding( new Insets( 16d ) );
    }

    // Reset all fields to the default values, regardless of state.
    public void reset() {
        projectProperties.reset();

        // Update the view to match the new model.
        syncViewToModel();
    }

    // Set and bind the Project Properties reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        // Cache the Project Properties reference.
        projectProperties = pProjectProperties;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    public void syncViewToModel() {
        // NOTE: Project Notes (TextArea) seems to re-sync automatically.
        _projectNameTextField.setValue( projectProperties.getProjectName() );
        _venueTextField.setValue( projectProperties.getVenue() );
        _designerTextField.setValue( projectProperties.getDesigner() );
        _dateTextField.setValue( projectProperties.getDate() );
    }

}
