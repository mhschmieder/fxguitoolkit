/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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

import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.SystemType;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a struct-like container for common File actions, with optional
 * actions relevant to applications that have a Project File backing store.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class FileActions {
    
    public static final String PROJECT_CATEGORY_DEFAULT = "Project";
    
    
    public static void modifyActionLabel( final Action action,
                                          final String projectCategory ) {
        final String oldLabel = action.getText();
        final String newLabel = oldLabel.replace( PROJECT_CATEGORY_DEFAULT, 
                                                  projectCategory );
        action.setText( newLabel );
    }

    /**
     * Flag for whether project actions are supported; used when making File
     * Action collections to feed to menus and/or toolbars.
     */
    public boolean _projectActionsSupported;

    public XAction _newProjectAction;
    public XAction _openProjectAction;
    public LoadActions _loadActions;
    
    public XAction _closeWindowAction;
    
    public XAction _saveProjectAction;
    public XAction _saveProjectAsAction;
    
    public ImportActions _importActions;
    public ExportActions _exportActions;
    
    public XAction _pageSetupAction;
    public XAction _printAction;
    
    public XAction _projectPropertiesAction;
    
    public MruFileActions _mruFileActions;
    
    public XAction _exitAction;
    
    /**
     * This is the default constructor, when no customization is required and
     * project actions are not supported. Retained for backward compatibility.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     */
    public FileActions( final ClientProperties pClientProperties ) {
        this( pClientProperties, false );
    }

    /**
     * This is the default constructor, when no customization is required, but
     * with additional information about whether project actions are supported.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     * @param pProjectActionsSupported
     *            {@code true} if project actions are supported
     */
    public FileActions( final ClientProperties pClientProperties,
                        final boolean pProjectActionsSupported ) {
        this( pClientProperties, 
              pProjectActionsSupported,
              PROJECT_CATEGORY_DEFAULT );
    }

    /**
     * This is the default constructor, when no customization is required, but
     * with additional information about whether project actions are supported.
     * <p>
     * NOTE: If the custom project category is left null or blank, we use the
     *  default project category of "Project" in all the menu item labels.
     *
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     * @param pProjectActionsSupported
     *            {@code true} if project actions are supported
     * @param projectCategory
     *            Optional custom category to use in place of default "Project"
     */
    public FileActions( final ClientProperties pClientProperties,
                        final boolean pProjectActionsSupported,
                        final String projectCategory ) {
        this( pClientProperties, 
              pProjectActionsSupported,
              projectCategory,
              new LoadActions( pClientProperties ),
              new ImportActions( pClientProperties ),
              new ExportActions( pClientProperties ) );
    }

    /**
     * This is a special constructor that takes pre-constructed Load, Import and
     * Export Actions containers. Use this when you need customization of the 
     * Load, Import and/or Export Actions, to avoid cut/paste of the basic File
     * Actions and to allow for method override in helper methods and thus avoid
     * potential code divergence.
     * 
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     * @param pProjectActionsSupported
     *            {@code true} if project actions are supported
     * @param loadActions
     *            A pre-constructed custom Load Actions container that derives
     *            from the basic functionality and adds more capabilities
     * @param importActions
     *            A pre-constructed custom Import Actions container that derives
     *            from the basic functionality and adds more capabilities
     * @param exportActions
     *            A pre-constructed custom Export Actions container that derives
     *            from the basic functionality and adds more capabilities
     */
    public FileActions( final ClientProperties pClientProperties,
                        final boolean pProjectActionsSupported,
                        final LoadActions loadActions,
                        final ImportActions importActions,
                        final ExportActions exportActions ) {
        this( pClientProperties,
              pProjectActionsSupported,
              PROJECT_CATEGORY_DEFAULT,
              loadActions,
              importActions,
              exportActions );
    }

    /**
     * This is a special constructor that takes pre-constructed Load, Import and
     * Export Actions containers. Use this when you need customization of the 
     * Load, Import and/or Export Actions, to avoid cut/paste of the basic File
     * Actions and to allow for method override in helper methods and thus avoid
     * potential code divergence.
     * <p>
     * NOTE: If the custom project category is left null or blank, we use the
     *  default project category of "Project" in all the menu item labels.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     * @param pProjectActionsSupported
     *            {@code true} if project actions are supported
     * @param projectCategory
     *            Optional custom category to use in place of default "Project"
     * @param loadActions
     *            A pre-constructed custom Load Actions container that derives
     *            from the basic functionality and adds more capabilities
     * @param importActions
     *            A pre-constructed custom Import Actions container that derives
     *            from the basic functionality and adds more capabilities
     * @param exportActions
     *            A pre-constructed custom Export Actions container that derives
     *            from the basic functionality and adds more capabilities
     */
    public FileActions( final ClientProperties pClientProperties,
                        final boolean pProjectActionsSupported,
                        final String projectCategory,
                        final LoadActions loadActions,
                        final ImportActions importActions,
                        final ExportActions exportActions ) {
        _projectActionsSupported = pProjectActionsSupported;
        
        _newProjectAction = LabeledActionFactory.getFileNewProjectAction( pClientProperties );
        _openProjectAction = LabeledActionFactory.getFileOpenProjectAction( pClientProperties );
        _loadActions = loadActions;
        
        _closeWindowAction = LabeledActionFactory.getCloseWindowAction( pClientProperties );

        _saveProjectAction = LabeledActionFactory.getFileSaveProjectAction( pClientProperties );
        _saveProjectAsAction = LabeledActionFactory.getFileSaveProjectAsAction( pClientProperties );
       
        _importActions = importActions;
        _exportActions = exportActions;
        
        _pageSetupAction = LabeledActionFactory.getPageSetupAction( pClientProperties );
        _printAction = LabeledActionFactory.getPrintAction( pClientProperties );
        
        _projectPropertiesAction = LabeledActionFactory
                .getFileProjectPropertiesAction( pClientProperties );
        
        _mruFileActions = new MruFileActions( pClientProperties );
        
        _exitAction = LabeledActionFactory.getExitAction( pClientProperties );
        
        // If project actions are support and the project category is provided
        // and is different from the default category of "Project", modify labels.
        if ( pProjectActionsSupported && ( projectCategory != null ) 
                && !projectCategory.isEmpty() 
                && !PROJECT_CATEGORY_DEFAULT.equals( projectCategory ) ) {
            modifyActionLabels( projectCategory );
        }
    }

    private void modifyActionLabels( final String projectCategory ) {
        modifyActionLabel( _newProjectAction, projectCategory );
        modifyActionLabel( _openProjectAction, projectCategory );
        
        modifyActionLabel( _loadActions._projectSettingsAction, projectCategory );
        
        modifyActionLabel( _saveProjectAction, projectCategory );
        modifyActionLabel( _saveProjectAsAction, projectCategory );
       
        modifyActionLabel( _projectPropertiesAction, projectCategory );
    }

    public final Collection< Action > getLoadActionCollection() {
        // Forward this method to the Load actions container.
        return _loadActions.getLoadActionCollection();
    }

    public final Collection< Action > getImportActionCollection( final boolean imageGraphicsSupported,
                                                                 final boolean vectorGraphicsSupported,
                                                                 final boolean cadGraphicsSupported ) {
        // Forward this method to the Import actions container.
        return _importActions.getImportActionCollection( imageGraphicsSupported,
                                                         vectorGraphicsSupported,
                                                         cadGraphicsSupported );
    }

    public final Collection< Action > getExportActionCollection( final boolean vectorGraphicsSupported,
                                                                 final boolean renderedGraphicsSupported ) {
        // Forward this method to the Export actions container.
        return _exportActions.getExportActionCollection( vectorGraphicsSupported,
                                                         renderedGraphicsSupported );
    }

    // NOTE: This is an under-specified version for backward-compatibility
    //  with legacy downstream calls (so that code doesn't have to be modified)
    //  and as a catch-all when import actions are not supported at all.
    public Collection< Action > getFileActionCollection( final ClientProperties pClientProperties,
                                                         final boolean vectorGraphicsExportSupported,
                                                         final boolean renderedGraphicsExportSupported ) {
        return getFileActionCollection( pClientProperties,
                                        true,
                                        false,
                                        false,
                                        false,
                                        vectorGraphicsExportSupported,
                                        renderedGraphicsExportSupported );
    }
    
    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getFileActionCollection( final ClientProperties pClientProperties,
                                                         final boolean supportCloseWindow,
                                                         final boolean imageGraphicsImportSupported,
                                                         final boolean vectorGraphicsImportSupported,
                                                         final boolean cadGraphicsImportSupported,
                                                         final boolean vectorGraphicsExportSupported,
                                                         final boolean renderedGraphicsExportSupported ) {
        final XActionGroup loadActionGroup = LabeledActionFactory
                .getLoadActionGroup( pClientProperties, _loadActions );
        final XActionGroup importActionGroup = LabeledActionFactory
                .getImportActionGroup( pClientProperties,
                                       _importActions,
                                       imageGraphicsImportSupported,
                                       vectorGraphicsImportSupported,
                                       cadGraphicsImportSupported );
        final XActionGroup exportActionGroup = LabeledActionFactory
                .getExportActionGroup( pClientProperties,
                                       _exportActions,
                                       vectorGraphicsExportSupported,
                                       renderedGraphicsExportSupported );
        
        return getFileActionCollection( pClientProperties,
                                        supportCloseWindow,
                                        loadActionGroup,
                                        importActionGroup,
                                        exportActionGroup );
    }
        
    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getFileActionCollection( final ClientProperties pClientProperties,
                                                         final boolean supportCloseWindow,
                                                         final XActionGroup loadActionGroup,
                                                         final XActionGroup importActionGroup,
                                                         final XActionGroup exportActionGroup ) {
        final Collection< Action > fileActionCollection = new ArrayList<>();
        
        if ( _projectActionsSupported ) {
            fileActionCollection.add( _newProjectAction );
            fileActionCollection.add( _openProjectAction );
            if ( !loadActionGroup.getActions().isEmpty() ) {
                fileActionCollection.add( loadActionGroup );
            }
        }
        
        if ( supportCloseWindow ) {
            fileActionCollection.add( _closeWindowAction );           
        }

        if ( _projectActionsSupported ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            fileActionCollection.add( _saveProjectAction );
            fileActionCollection.add( _saveProjectAsAction );
        }
        
        if ( !importActionGroup.getActions().isEmpty() 
                || !exportActionGroup.getActions().isEmpty() ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            if ( !importActionGroup.getActions().isEmpty() ) {
                fileActionCollection.add( importActionGroup );
            }
            if ( !exportActionGroup.getActions().isEmpty() ) {
                fileActionCollection.add( exportActionGroup );
            }
        }
        
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );        
        fileActionCollection.add( _pageSetupAction );
        fileActionCollection.add( _printAction );

        if ( _projectActionsSupported ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            fileActionCollection.add( _projectPropertiesAction );
    
            // Inject the MRU File Menu Items to this File Menu.
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            _mruFileActions.injectToActions( fileActionCollection );
        }

        // NOTE: The Mac's global Application Menu has its own Quit Menu Item.
        //  If we also include one with the File Menu, then the menu shortcut
        //  gets triggered twice, which causes the File Save confirmation dialog
        //  to pop up a second time -- especially if the user canceled the exit.
        if ( !SystemType.MACOS.equals( pClientProperties.systemType ) ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            fileActionCollection.add( _exitAction );
        }

        return fileActionCollection;
    }
}
