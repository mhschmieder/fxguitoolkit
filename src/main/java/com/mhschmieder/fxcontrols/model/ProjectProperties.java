/*
 * MIT License
 *
 * Copyright (c) 2020, 2025, Mark Schmieder. All rights reserved.
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
 * This file is part of the fxgui Library
 *
 * You should have received a copy of the MIT License along with the fxgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgui
 */
package com.mhschmieder.fxgui.model;

import com.mhschmieder.fxgraphics.beans.BeanFactory;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Observable data model for GUI elements associated with Project Properties.
 */
public class ProjectProperties {

    private final StringProperty projectName;
    private final StringProperty projectType;
    private final StringProperty projectLocation;
    private final StringProperty projectAuthor;
    private final ObjectProperty< LocalDate > projectDate;
    private final StringProperty projectNotes;

    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private final BooleanBinding projectPropertiesChanged;

    /**
     * Makes a {@code ProjectProperties} instance using default values.
     * <p>
     * This is the default constructor; it sets all instance variables to
     * default values, initializing anything that requires memory allocation.
     */
    public ProjectProperties() {
        this(
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_NAME,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_TYPE,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_LOCATION,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_AUTHOR,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_DATE,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_NOTES );
    }

    /*
     * This is the fully qualified constructor. 
     */
    public ProjectProperties( final String pProjectName,
                              final String pProjectType,
                              final String pProjectLocation,
                              final String pProjectAuthor,
                              final LocalDate pProjectDate,
                              final String pProjectNotes ) {
        projectName = new SimpleStringProperty( pProjectName );
        projectType = new SimpleStringProperty( pProjectType );
        projectLocation = new SimpleStringProperty( pProjectLocation );
        projectAuthor = new SimpleStringProperty( pProjectAuthor );
        projectDate = new SimpleObjectProperty<>( pProjectDate );
        projectNotes = new SimpleStringProperty( pProjectNotes );

        // Bind all the properties to the associated dirty flag.
        // NOTE: This is done during initialization, as it is best to make
        //  singleton objects and just update their values vs. reconstructing.
        projectPropertiesChanged = BeanFactory.makeBooleanBinding(
               projectNameProperty(),
               projectTypeProperty(),
               projectLocationProperty(),
               projectAuthorProperty(),
               projectDateProperty(),
               projectNotesProperty());
    }

    /**
     * Makes a copy of the referenced {@code projectProperties} object.
     * <p>
     * This is the copy constructor, and is offered in place of clone() to
     * guarantee that the source object is never modified by the new target
     * object created here.
     *
     * @param pProjectProperties
     *            The Project Properties reference for the copy
     */
    public ProjectProperties( final ProjectProperties pProjectProperties ) {
        this(
                pProjectProperties.getProjectName(),
                pProjectProperties.getProjectType(),
                pProjectProperties.getProjectLocation(),
                pProjectProperties.getProjectAuthor(),
                pProjectProperties.getProjectDate(),
                pProjectProperties.getProjectNotes() );
    }

    // NOTE: Cloning is disabled as it is dangerous; use the copy constructor
    //  instead.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Resets all fields to their default values, which are blank.
     * <p>
     * Serves as a default pseudo-constructor. 
     */
    public void reset() {
        setProjectProperties(
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_NAME,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_TYPE,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_LOCATION,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_AUTHOR,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_DATE,
                com.mhschmieder.jcommons.util.ProjectProperties
                        .DEFAULT_PROJECT_NOTES );
    }

    /*
     * Fully qualified pseudo-constructor. 
     */
    public void setProjectProperties( final String pProjectName,
                                      final String pProjectType,
                                      final String pProjectLocation,
                                      final String pProjectAuthor,
                                      final LocalDate pProjectDate,
                                      final String pProjectNotes ) {
        setProjectName( pProjectName );
        setProjectType( pProjectType );
        setProjectLocation( pProjectLocation );
        setProjectAuthor( pProjectAuthor );
        setProjectDate( pProjectDate );

        if ( pProjectNotes != null ) {
            setProjectNotes( pProjectNotes );
        }
    }

    /**
     * Sets all fields to match the values in the referenced instance.
     * <p>
     * This serves as a copy pseudo-constructor. 
     *
     * @param pProjectProperties
     *            The Project Properties reference for the copy
     */
    public void setProjectProperties(
            final ProjectProperties pProjectProperties ) {
        setProjectProperties(
                pProjectProperties.getProjectName(),
                pProjectProperties.getProjectType(),
                pProjectProperties.getProjectLocation(),
                pProjectProperties.getProjectAuthor(),
                pProjectProperties.getProjectDate(),
                pProjectProperties.getProjectNotes() );
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public String getProjectName() {
        return projectName.get();
    }

    public void setProjectName( final String pProjectName ) {
        projectName.set( pProjectName );
    }

    public StringProperty projectTypeProperty() {
        return projectType;
    }

    public String getProjectType() {
        return projectType.get();
    }

    public void setProjectType( final String pProjectType ) {
        projectType.set( pProjectType );
    }

    public StringProperty projectLocationProperty() {
        return projectLocation;
    }

    public String getProjectLocation() {
        return projectLocation.get();
    }

    public void setProjectLocation( final String pProjectLocation ) {
        projectLocation.set( pProjectLocation );
    }

    public StringProperty projectAuthorProperty() {
        return projectAuthor;
    }

    public String getProjectAuthor() {
        return projectAuthor.get();
    }

    public void setProjectAuthor( final String pProjectAuthor ) {
        projectAuthor.set( pProjectAuthor );
    }

    public ObjectProperty< LocalDate > projectDateProperty() {
        return projectDate;
    }

    public LocalDate getProjectDate() {
        return projectDate.get();
    }

    public void setProjectDate( final LocalDate pProjectDate ) {
        projectDate.set( pProjectDate );
    }

    public StringProperty projectNotesProperty() {
        return projectNotes;
    }

    public String getProjectNotes() {
        return projectNotes.get();
    }

    public void setProjectNotes( final String pProjectNotes ) {
        projectNotes.set( pProjectNotes );
    }
    
    public BooleanBinding projectPropertiesChangedProperty() {
        return projectPropertiesChanged;
    }
    
    public boolean isProjectPropertiesChanged() {
        return projectPropertiesChanged.get();
    }
}
