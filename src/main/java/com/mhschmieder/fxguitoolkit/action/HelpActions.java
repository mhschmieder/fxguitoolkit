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

/**
 * This is a struct-like container for common Help actions.
 * <p>
 * NOTE: This class is not final, so that it can be derived for additions.
 */
public class HelpActions {

    public XAction _helpAction;
    public XAction _releaseNotesAction;
    public XAction _checkForUpdatesAction;
    public XAction _accountManagementAction;
    public XAction _sessionLogAction;
    public XAction _teamMembersAction;
    public XAction _thirdPartyLibrariesAction;
    public XAction _aboutAction;
    public XAction _eulaAction;

    public HelpActions( final ClientProperties pClientProperties, final String applicationName ) {
        _helpAction = LabeledActionFactory.getHelpHelpAction( pClientProperties );
        _releaseNotesAction = LabeledActionFactory.getHelpReleaseNotesAction( pClientProperties );
        _checkForUpdatesAction =
                               LabeledActionFactory.getHelpCheckForUpdatesAction( pClientProperties );
        _accountManagementAction = LabeledActionFactory
                .getHelpAccountManagementAction( pClientProperties );
        _sessionLogAction = LabeledActionFactory.getHelpSessionLogAction( pClientProperties );
        _teamMembersAction = LabeledActionFactory.getHelpTeamMembersAction( pClientProperties );
        _thirdPartyLibrariesAction = LabeledActionFactory
                .getHelpThirdPartyLibrariesAction( pClientProperties );
        _aboutAction = LabeledActionFactory.getHelpAboutAction( pClientProperties, applicationName );
        _eulaAction = LabeledActionFactory.getHelpEulaAction( pClientProperties );
    }

    // NOTE: This method is not final, so that it can be derived for
    // additions.
    public Collection< Action > getHelpActionCollection() {
        final Collection< Action > helpActionCollection = new ArrayList<>();

        helpActionCollection.add( _helpAction );
        helpActionCollection.add( _releaseNotesAction );
        helpActionCollection.add( _checkForUpdatesAction );
        helpActionCollection.add( _accountManagementAction );
        helpActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        helpActionCollection.add( _sessionLogAction );
        helpActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        helpActionCollection.add( _teamMembersAction );
        helpActionCollection.add( _thirdPartyLibrariesAction );

        // NOTE: The Mac's global Application Menu has its own About Menu Item,
        // but we use the Help Menu anyway, because it's the only way we have
        // full control over the displayed text and the callback hooks.
        // if ( !SystemType.MACOS.equals( clientProperties.systemType ) ) {
        helpActionCollection.add( ActionUtils.ACTION_SEPARATOR );
        helpActionCollection.add( _aboutAction );
        // }
        helpActionCollection.add( _eulaAction );

        return helpActionCollection;
    }
}
