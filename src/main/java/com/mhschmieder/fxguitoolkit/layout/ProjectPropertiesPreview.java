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
import com.mhschmieder.fxguitoolkit.model.ProjectProperties;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class ProjectPropertiesPreview extends GridPane {

    // Declare the basic UI components.
    protected Label           _projectName;
    protected Label           _projectVenue;
    protected Label           _projectDesigner;
    protected Label           _projectDate;
    protected Label           _projectNotes;

    // Cache the current project properties.
    private ProjectProperties projectProperties;

    public ProjectPropertiesPreview( final ClientProperties clientProperties,
                                     final boolean useProjectNotes ) {
        super();

        initPane( clientProperties, useProjectNotes );
    }

    private void bindProperties() {
        // Bind the text labels to their associated Project Properties.
        _projectName.textProperty().bind( projectProperties.projectNameProperty() );
        _projectVenue.textProperty().bind( projectProperties.venueProperty() );
        _projectDesigner.textProperty().bind( projectProperties.designerProperty() );
        _projectDate.textProperty().bind( projectProperties.dateProperty() );
        _projectNotes.textProperty().bind( projectProperties.projectNotesProperty() );
    }

    private void initPane( final ClientProperties clientProperties,
                           final boolean useProjectNotes ) {
        setAlignment( Pos.CENTER );
        setHgap( 6.0d );
        setVgap( 6.0d );
        setPadding( new Insets( 0.0d, 4.0d, 0.0d, 4.0d ) );

        _projectName = new Label();
        final HBox projectNamePane = GuiUtilities.getPropertySheetLabelPane( "Project", //$NON-NLS-1$
                                                                             _projectName );

        _projectVenue = new Label();
        final HBox venuePane = GuiUtilities.getPropertySheetLabelPane( "Venue", //$NON-NLS-1$
                                                                       _projectVenue );

        _projectDesigner = new Label();
        final HBox designerPane = GuiUtilities.getPropertySheetLabelPane( "Designer", //$NON-NLS-1$
                                                                          _projectDesigner );

        _projectDate = new Label();
        final HBox datePane = GuiUtilities.getPropertySheetLabelPane( "Date", //$NON-NLS-1$
                                                                      _projectDate );

        _projectNotes = new Label();
        final HBox notesPane = GuiUtilities.getPropertySheetLabelPane( "Project Notes: ", //$NON-NLS-1$
                                                                       _projectNotes );

        add( projectNamePane, 0, 0 );
        add( venuePane, 0, 1 );
        add( designerPane, 0, 2 );
        add( datePane, 0, 3 );
        if ( useProjectNotes ) {
            add( notesPane, 0, 4 );
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
