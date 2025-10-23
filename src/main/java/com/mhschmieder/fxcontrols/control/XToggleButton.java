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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxcontrols.control;

import com.mhschmieder.fxgraphics.image.ImageUtilities;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

/**
 * {@code XToggleButton} is an enhancement to {@link ToggleButton} that takes
 * care of some of its anomalies.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class XToggleButton extends ToggleButton {

    protected String    _selectedText;
    protected String    _deselectedText;

    protected boolean   _hasGraphics;
    protected ImageView _selectedIcon;
    protected ImageView _deselectedIcon;

    // This is the constructor for cases where the displayed text is not state
    // dependent (only an icon), and where background color applies a simple
    // darkening formula to the provided selected color, and foreground colors
    // are matched automatically by CSS.
    public XToggleButton( final String tooltipText,
                          final String cssStyleClass,
                          final boolean wrapText,
                          final boolean selected ) {
        this( null, null, tooltipText, cssStyleClass, true, 1.0d, wrapText, selected );
    }

    // This is the constructor for cases where the displayed text is not state
    // dependent (only an icon), and where background color applies a simple
    // darkening formula to the provided selected color, and foreground colors
    // are matched automatically by CSS.
    public XToggleButton( final String selectedText,
                          final String tooltipText,
                          final String cssStyleClass,
                          final boolean applyAspectRatio,
                          final double aspectRatio,
                          final boolean wrapText,
                          final boolean selected ) {
        this( selectedText,
              selectedText,
              tooltipText,
              cssStyleClass,
              applyAspectRatio,
              aspectRatio,
              wrapText,
              selected );
    }

    // This is the constructor for cases where the text and background color are
    // set by custom CSS, for both the selected and deselected states.
    public XToggleButton( final String selectedText,
                          final String deselectedText,
                          final String tooltipText,
                          final String cssStyleClass,
                          final boolean applyAspectRatio,
                          final double aspectRatio,
                          final boolean wrapText,
                          final boolean selected ) {
        // Always call the superclass constructor first!
        super();

        _selectedText = selectedText;
        _deselectedText = deselectedText;

        _hasGraphics = false;
        _selectedIcon = null;
        _deselectedIcon = null;

        try {
            initToggleButton( tooltipText,
                              cssStyleClass,
                              applyAspectRatio,
                              aspectRatio,
                              wrapText,
                              selected );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final String getDeselectedText() {
        return _deselectedText;
    }

    public final String getSelectedText() {
        return _selectedText;
    }

    private final void initToggleButton( final String tooltipText,
                                         final String cssStyleClass,
                                         final boolean applyAspectRatio,
                                         final double aspectRatio,
                                         final boolean wrapText,
                                         final boolean selected ) {
        // The more important sizing attribute is whether to wrap the label.
        setWrapText( wrapText );

        // Set the Tool Tip text for this Toggle Button, whether an image is
        // used or it only has standard button text.
        if ( ( tooltipText != null ) && !tooltipText.trim().isEmpty() ) {
            setTooltip( new Tooltip( tooltipText ) );
        }

        // Set the specified CSS Style Class reference, in place of directly
        // setting the background and foreground/text colors.
        ControlUtilities.setToggleButtonProperties( this, cssStyleClass );

        // Add indentation (insets/margins), and use centering.
        setPadding( new Insets( 4.0d, 8.0d, 4.0d, 8.0d ) );
        setAlignment( Pos.CENTER );
        setTextAlignment( TextAlignment.CENTER );

        // Force the height to match the inverse aspect ratio, to preserve wrap.
        if ( applyAspectRatio ) {
            prefHeightProperty().bind( widthProperty().divide( aspectRatio ) );
        }

        // Use the toggle selected property to modify the background color and
        // the displayed text to match the toggle state, as there are no direct
        // methods in the ToggleButton API to accomplish this.
        selectedProperty().addListener( ( observableValue, oldValue, newValue ) -> {
            // Set the button label according to state. CSS does the rest.
            Platform.runLater( () -> setToggleAttributes( newValue ) );
        } );

        // Set the initial state of this toggle button.
        setToggleAttributes( selected );
        setSelected( selected );
    }

    public final void setDeselectedText( final String deselectedText ) {
        _deselectedText = deselectedText;
    }

    // In some contexts, it helps to have graphics for each state.
    public final void setIcons( final String selectedJarRelativeImageFilename,
                                final String deselectedJarRelativeImageFilename ) {
        _selectedIcon = ImageUtilities.getImageView( selectedJarRelativeImageFilename, true );
        _deselectedIcon = ImageUtilities.getImageView( deselectedJarRelativeImageFilename, true );
        _hasGraphics = true;
    }

    public final void setSelectedText( final String selectedText ) {
        _selectedText = selectedText;
    }

    public final void setToggleAttributes( final boolean selected ) {
        // Set the label according to selection toggle state.
        final String text = selected ? _selectedText : _deselectedText;
        setText( text );

        // If we are also using graphics, set the appropriate icon.
        if ( _hasGraphics ) {
            final ImageView icon = selected ? _selectedIcon : _deselectedIcon;
            setGraphic( icon );
        }
    }

}
