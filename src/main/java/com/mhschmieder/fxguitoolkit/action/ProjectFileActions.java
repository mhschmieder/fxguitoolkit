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

import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;

/**
 * Generalized extension of File Actions for typical File Menu items that show up
 * in applications that have a backing store of a Project File.
 */
public class ProjectFileActions extends FileActions {

    public XAction _newProjectAction;
    public XAction _openProjectAction;
    public LoadActions _loadActions;
    public XAction _saveProjectAction;
    public XAction _saveProjectAsAction;
    public MruFileActions _mruFileActions;
    public XAction _projectPropertiesAction;

    /**
     * This is the default constructor, when no customization is required.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     */
    public ProjectFileActions( final ClientProperties pClientProperties ) {
        this( pClientProperties, 
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
    public ProjectFileActions( final ClientProperties pClientProperties,
                               final LoadActions loadActions,
                               final ImportActions importActions,
                               final ExportActions exportActions ) {
        // Always call the superclass constructor first!
        super( pClientProperties, 
               importActions,
               exportActions );
        
        _loadActions = loadActions;

        _newProjectAction = LabeledActionFactory.getFileNewProjectAction( pClientProperties );
        _openProjectAction = LabeledActionFactory.getFileOpenProjectAction( pClientProperties );
        _saveProjectAction = LabeledActionFactory.getFileSaveProjectAction( pClientProperties );
        _saveProjectAsAction = LabeledActionFactory.getFileSaveProjectAsAction( pClientProperties );
        _mruFileActions = new MruFileActions( pClientProperties );
        _projectPropertiesAction = LabeledActionFactory
                .getFileProjectPropertiesAction( pClientProperties );
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    @Override
    public Collection< Action > getFileActionCollection( final ClientProperties clientProperties,
                                                         final boolean vectorGraphicsSupported,
                                                         final boolean renderedGraphicsSupported ) {
        final XActionGroup loadActionGroup = LabeledActionFactory
                .getLoadActionGroup( clientProperties, _loadActions );
        final XActionGroup importActionGroup = LabeledActionFactory
                .getImportActionGroup( clientProperties, _importActions );
        final XActionGroup exportActionGroup = LabeledActionFactory
                .getExportActionGroup( clientProperties,
                                       _exportActions,
                                       vectorGraphicsSupported,
                                       renderedGraphicsSupported );

        // TODO: Find a way to consolidate with the super-class method.
        final Collection< Action > fileActionCollection = new ArrayList<>();

        fileActionCollection.add( _newProjectAction );
        fileActionCollection.add( _openProjectAction );
        fileActionCollection.add( loadActionGroup );
        // fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        // fileActionCollection.add( _closeAction );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( _saveProjectAction );
        fileActionCollection.add( _saveProjectAsAction );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( importActionGroup );
        fileActionCollection.add( exportActionGroup );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( _pageSetupAction );
        fileActionCollection.add( _printAction );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( _projectPropertiesAction );

        // Inject the MRU File Menu Items to this File Menu.
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        _mruFileActions.injectToActions( fileActionCollection );

        // NOTE: The Mac's global Application Menu has its own Quit Menu Item.
        //  If we also include one with the File Menu, then the menu shortcut
        //  gets triggered twice, which causes the File Save confirmation dialog
        //  to pop up a second time -- especially if the user cancels the exit.
        if ( !SystemType.MACOS.equals( clientProperties.systemType ) ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            fileActionCollection.add( _exitAction );
        }

        return fileActionCollection;
    }
}
