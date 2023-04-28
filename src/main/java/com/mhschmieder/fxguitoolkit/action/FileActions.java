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
package com.mhschmieder.fxguitoolkit.action;

import java.util.ArrayList;
import java.util.Collection;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;

/**
 * This is a struct-like container for common File actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class FileActions {

    public XAction       _closeWindowAction;
    public ExportActions _exportActions;
    public XAction       _pageSetupAction;
    public XAction       _printAction;
    public XAction       _exitAction;

    /**
     * This is the default constructor, when no customization is required.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     */
    public FileActions( final ClientProperties pClientProperties ) {
        this( pClientProperties, new ExportActions( pClientProperties ) );
    }

    /**
     * This is a special constructor that takes a pre-constructed Export Actions
     * container. Use this when you need customization of the Export Actions, to
     * avoid cut/paste of the basic File Actions and to allow for method
     * override in helper methods and thus avoid potential code divergence.
     *
     * @param pClientProperties
     *            The Client Properties, including Client Type and OS Name
     * @param exportActions
     *            A pre-constructed custom Export Actions container that derives
     *            from the basic functionality and adds more capabilities
     */
    public FileActions( final ClientProperties pClientProperties,
                        final ExportActions exportActions ) {
        _closeWindowAction = LabeledActionFactory.getCloseWindowAction( pClientProperties );
        _exportActions = exportActions;
        _pageSetupAction = LabeledActionFactory.getPageSetupAction( pClientProperties );
        _printAction = LabeledActionFactory.getPrintAction( pClientProperties );
        _exitAction = LabeledActionFactory.getExitAction( pClientProperties );
    }

    public final Collection< Action > getExportActionCollection( final boolean vectorGraphicsSupported,
                                                                 final boolean formattedVectorGraphicsSupported ) {
        // Forward this method to the Export actions container.
        return _exportActions.getExportActionCollection( vectorGraphicsSupported,
                                                         formattedVectorGraphicsSupported );
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getFileActionCollection( final ClientProperties pClientProperties,
                                                         final boolean vectorGraphicsSupported,
                                                         final boolean formattedVectorGraphicsSupported ) {
        final XActionGroup exportActionGroup = LabeledActionFactory
                .getExportActionGroup( pClientProperties,
                                       _exportActions,
                                       vectorGraphicsSupported,
                                       formattedVectorGraphicsSupported );

        final Collection< Action > fileActionCollection = new ArrayList<>();

        fileActionCollection.add( _closeWindowAction );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( exportActionGroup );
        fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        fileActionCollection.add( _pageSetupAction );
        fileActionCollection.add( _printAction );

        // NOTE: The Mac's global Application Menu has its own Quit Menu Item.
        // If we also include one with the File Menu, then the menu shortcut
        // gets triggered twice, which causes the File Save confirmation dialog
        // to pop up a second time -- especially if the user canceled the exit.
        if ( !SystemType.MACOS.equals( pClientProperties.systemType ) ) {
            fileActionCollection.add( ActionUtils.ACTION_SEPARATOR );
            fileActionCollection.add( _exitAction );
        }

        return fileActionCollection;
    }
}
