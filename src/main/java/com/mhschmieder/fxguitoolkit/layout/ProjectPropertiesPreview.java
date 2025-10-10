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
import com.mhschmieder.fxguitoolkit.model.ProjectProperties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class ProjectPropertiesPreview extends GridPane {

    // Declare the basic UI components.
    // TODO: Match the revised names (and labels, with overrides) in the Editor.
    protected Label projectName;
    protected Label projectType;
    protected Label projectVenue;
    protected Label projectDesigner;
    protected Label projectDate;
    protected Label projectNotes;

    // Cache the current project properties.
    private ProjectProperties projectProperties;

    public ProjectPropertiesPreview( final ClientProperties clientProperties,
                                     final boolean useProjectNotes ) {
        super();

        initPane( clientProperties, useProjectNotes );
    }

    private void bindProperties() {
        // Bind the text labels to their associated Project Properties.
        projectName.textProperty().bind( projectProperties.projectNameProperty() );
        projectType.textProperty().bind( projectProperties.projectTypeProperty() );
        projectVenue.textProperty().bind( projectProperties.projectLocationProperty() );
        projectDesigner.textProperty().bind( projectProperties.projectAuthorProperty() );
        projectDate.textProperty().bind( projectProperties.projectDateProperty().asString() );
        projectNotes.textProperty().bind( projectProperties.projectNotesProperty() );
    }

    private void initPane( final ClientProperties clientProperties,
                           final boolean useProjectNotes ) {
        setAlignment( Pos.CENTER );
        setHgap( 6.0d );
        setVgap( 6.0d );
        setPadding( new Insets( 0.0d, 4.0d, 0.0d, 4.0d ) );

        projectName = new Label();
        final HBox projectNamePane = GuiUtilities.getPropertySheetLabelPane( "Project",
                                                                             projectName );

        projectType = new Label();
        final HBox typePane = GuiUtilities.getPropertySheetLabelPane( "Type",
                                                                       projectType );

        projectVenue = new Label();
        final HBox venuePane = GuiUtilities.getPropertySheetLabelPane( "Venue",
                                                                       projectVenue );

        projectDesigner = new Label();
        final HBox designerPane = GuiUtilities.getPropertySheetLabelPane( "Designer",
                                                                          projectDesigner );

        projectDate = new Label();
        final HBox datePane = GuiUtilities.getPropertySheetLabelPane( "Date",
                                                                      projectDate );

        projectNotes = new Label();
        final HBox notesPane = GuiUtilities.getPropertySheetLabelPane( "Project Notes: ",
                                                                       projectNotes );

        add( projectNamePane, 0, 0 );
        add( typePane, 0, 1 );
        add( venuePane, 0, 2 );
        add( designerPane, 0, 3 );
        add( datePane, 0, 4 );
        if ( useProjectNotes ) {
            add( notesPane, 0, 5 );
        }
    }

    // Set and bind the Project Properties reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        // Cache the Project Properties reference.
        projectProperties = pProjectProperties;

        // Bind the data model to the respective GUI components.
        bindProperties();
    }
}
