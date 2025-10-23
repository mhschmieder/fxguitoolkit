/*
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
package com.mhschmieder.fxcontrols.action;

import com.mhschmieder.fxcontrols.control.LabeledControlFactory;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.PreferenceUtilities;
import org.controlsfx.control.action.Action;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * This is a struct-like container for MRU File actions.
 */
public class MruFileActions {

    public XAction[]        _mruFileActions;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties clientProperties;

    // Default constructor
    public MruFileActions( final ClientProperties pClientProperties ) {
        clientProperties = pClientProperties;

        // Make the MRU File Actions.
        final int maximumNumberOfMruFiles = PreferenceUtilities.MRU_CACHE_SIZE;
        _mruFileActions = new XAction[ maximumNumberOfMruFiles ];

        for ( int i = 0; i < maximumNumberOfMruFiles; i++ ) {
            final int mruFileNumber = i + 1;
            final XAction mruAction = LabeledActionFactory.makeFileMruAction( pClientProperties,
                                                                              mruFileNumber );
            _mruFileActions[ i ] = mruAction;
        }
    }

    // This is a helper method to generically inject the MRU File actions to
    // an actions container prepared in advance (not usually from File Actions).
    public final void injectToActions( final Collection< Action > fileActionCollection ) {
        // Make these selectively visible/invisible from the main application,
        // along with setting their actual filename references (their initial
        // text just contains the mnemonic). Since these will also be stored in
        // User Preferences between sessions, there is no need to make the
        // separator invisible as this will only add clutter to the menu on
        // first usage.
        // NOTE: We no longer add the tenth MRU action, as we must switch to
        // letters at that point and run through the alphabet, at which point we
        // really should break all the MRU's out into a sub-menu.
        final int maximumNumberOfMruFiles = PreferenceUtilities.MRU_CACHE_SIZE;
        for ( int i = 0; i < maximumNumberOfMruFiles; i++ ) {
            final XAction mruFileAction = _mruFileActions[ i ];
            fileActionCollection.add( mruFileAction );
        }
    }

    // Update the MRU File actions in the overall File actions, sans file path.
    public final void updateMruFileActions( final List< String > mruFilenames ) {
        final int mruFilenamesLastIndex = mruFilenames.size() - 1;
        for ( int i = 0; i < _mruFileActions.length; i++ ) {
            final XAction mruFileAction = _mruFileActions[ i ];

            // It is more efficient to hierarchically check for positive
            // conditions, process and continue, and then allow all condition
            // failures to fall through to the padding/hiding operation.
            // NOTE: We add the underscore cue for JavaFX to detect the MRU
            //  file number as the mnemonic for the menu item. If we don't do
            //  this, the first underscore in the filename gets stripped, as
            //  it gets detected as the mnemonic by TextBinding.parseAndSplit()
            //  and thus it overlays the next character when displaying menus.
            if ( i <= mruFilenamesLastIndex ) {
                final String mruFilename = mruFilenames.get( i );
                if ( ( mruFilename != null ) && !mruFilename.trim().isEmpty() ) {
                    final File mruFile = new File( mruFilename );
                    final String mruLabel = "_" + LabeledControlFactory
                            .getFileMruHeader( clientProperties, i + 1 ) 
                            + " " + mruFile.getName();

                    mruFileAction.setText( mruLabel );
                    mruFileAction.setDisabled( false );

                    continue;
                }
            }

            // Pad unused slots with empty names and disable, to hide.
            mruFileAction.setText( "" );
            mruFileAction.setDisabled( true );
        }
    }
}
