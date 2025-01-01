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
package com.mhschmieder.fxguitoolkit.model;

import com.mhschmieder.fxgraphicstoolkit.beans.BeanFactory;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ProjectProperties {

    // Declare default constants, where appropriate, for all fields.
    private static final String  PROJECT_NAME_DEFAULT  = ""; //$NON-NLS-1$
    private static final String  VENUE_DEFAULT         = ""; //$NON-NLS-1$
    private static final String  DESIGNER_DEFAULT      = ""; //$NON-NLS-1$
    private static final String  DATE_DEFAULT          = ""; //$NON-NLS-1$
    private static final String  PROJECT_NOTES_DEFAULT = ""; //$NON-NLS-1$

    private final StringProperty projectName;
    private final StringProperty venue;
    private final StringProperty designer;
    private final StringProperty date;
    private final StringProperty projectNotes;

    // NOTE: This field has to follow JavaFX Property Beans conventions.
    private BooleanBinding        projectPropertiesChanged;

    /**
     * This is the default constructor; it sets all instance variables to
     * default values, initializing anything that requires memory allocation.
     */
    public ProjectProperties() {
        this( PROJECT_NAME_DEFAULT,
              VENUE_DEFAULT,
              DESIGNER_DEFAULT,
              DATE_DEFAULT,
              PROJECT_NOTES_DEFAULT );
    }

    /*
     * This is the fully qualified constructor. 
     */
    public ProjectProperties( final String pProjectName,
                              final String pVenue,
                              final String pDesigner,
                              final String pDate,
                              final String pProjectNotes ) {
        projectName = new SimpleStringProperty( pProjectName );
        venue = new SimpleStringProperty( pVenue );
        designer = new SimpleStringProperty( pDesigner );
        date = new SimpleStringProperty( pDate );
        projectNotes = new SimpleStringProperty( pProjectNotes );

        // Bind all of the properties to the associated dirty flag.
        // NOTE: This is done during initialization, as it is best to make
        //  singleton objects and just update their values vs. reconstructing.
        projectPropertiesChanged = BeanFactory.makeBooleanBinding(
               projectNameProperty(),
               venueProperty(),
               designerProperty(),
               dateProperty(),
               projectNotesProperty());
    }

    /**
     * This is the copy constructor, and is offered in place of clone() to
     * guarantee that the source object is never modified by the new target
     * object created here.
     *
     * @param pProjectProperties
     *            The Project Properties reference for the copy
     */
    public ProjectProperties( final ProjectProperties pProjectProperties ) {
        this( pProjectProperties.getProjectName(),
              pProjectProperties.getVenue(),
              pProjectProperties.getDesigner(),
              pProjectProperties.getDate(),
              pProjectProperties.getProjectNotes() );
    }

    // NOTE: Cloning is disabled as it is dangerous; use the copy constructor
    // instead.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }


    /** 
     * Default pseudo-constructor. 
     */
    public void reset() {
        setProjectProperties( PROJECT_NAME_DEFAULT,
                              VENUE_DEFAULT,
                              DESIGNER_DEFAULT,
                              DATE_DEFAULT,
                              PROJECT_NOTES_DEFAULT );
    }

    /*
     * Fully qualified pseudo-constructor. 
     */
    public void setProjectProperties( final String pProjectName,
                                      final String pVenue,
                                      final String pDesigner,
                                      final String pDate,
                                      final String pProjectNotes ) {
        setProjectName( pProjectName );
        setVenue( pVenue );
        setDesigner( pDesigner );
        setDate( pDate );

        if ( pProjectNotes != null ) {
            setProjectNotes( pProjectNotes );
        }
    }

    /**
     * Copy pseudo-constructor. 
     *
     * @param pProjectProperties
     *            The Project Properties reference for the copy
     */
    public void setProjectProperties( final ProjectProperties pProjectProperties ) {
        setProjectProperties( pProjectProperties.getProjectName(),
                              pProjectProperties.getVenue(),
                              pProjectProperties.getDesigner(),
                              pProjectProperties.getDate(),
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

    public StringProperty designerProperty() {
        return designer;
    }

    public String getDesigner() {
        return designer.get();
    }

    public void setDesigner( final String pDesigner ) {
        designer.set( pDesigner );
    }

    public StringProperty venueProperty() {
        return venue;
    }

    public String getVenue() {
        return venue.get();
    }

    public void setVenue( final String pVenue ) {
        venue.set( pVenue );
    }

    public StringProperty dateProperty() {
        return date;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate( final String pDate ) {
        date.set( pDate );
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
