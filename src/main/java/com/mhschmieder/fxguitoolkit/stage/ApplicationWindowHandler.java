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
package com.mhschmieder.fxguitoolkit.stage;

import javafx.geometry.Dimension2D;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Defines the contract for methods that all application windows and stages
 * must implement for handling basic application session functionality.
 */
public interface ApplicationWindowHandler { 
    
    void hideAllWindows();
    
    void loadAllPreferences();
    
    void saveAllPreferences();
    
    Preferences loadPreferences();
    
    Preferences savePreferences();
    
    void restoreAllWindowLayouts( final Preferences prefs );
    
    void saveAllWindowLayouts( final Preferences prefs );

    void restoreWindowLayout( final Preferences prefs );
    
    void saveWindowLayout( final Preferences prefs );
    
    void setDefaultWindowSize( final double defaultWidth,
                               final double defaultHeight );
    
    Dimension2D getPreferredWindowSize();
    
    void setPreferredWindowSize( final double stageWidth, 
                                 final double stageHeight );
    
    void setWindowSize( final Dimension2D windowSize );
    
    void setWindowLocation( final double x, final double y );
    
    String getWindowKeyPrefix();
    
    /**
     * This method returns the window key prefix for window preferences.
     *
     * @return The string to be used as the window key prefix for window layout
     *         preferences
     */
    StringBuilder getDefaultTitle();
    
    default StringBuilder getSubtitle( final String documentFileName,
                                       final Boolean documentModified ) {
        // Append the current document file name, with an asterisk at the end if
        // the document is modified.
        final StringBuilder subtitle = new StringBuilder( " - [" );
        subtitle.append( documentFileName );
        
        // TODO: Re-enable the Mac conditionals once we figure out how to do it.
        // if ( documentModified
        // && !SystemType.MACOS.equals( clientProperties.systemType ) ) {
        if ( documentModified ) {
            subtitle.append( "*" );
        }
        
        subtitle.append( "]" );
        return subtitle;
    }
    
    void updateFrameTitle( final File documentFile, 
                           final boolean documentModified );
    
    void setIcon( final String jarRelativeIconFilename );
}
