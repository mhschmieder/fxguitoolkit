/**
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.net.SessionContext;
import com.mhschmieder.fxgraphicstoolkit.geometry.Orientation;
import com.mhschmieder.fxguitoolkit.FxGuiUtilities;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class OrientationPane extends BorderPane {

    public ToggleGroup _orientationToggleGroup;
    public RadioButton _orientationHzRadioButton;
    public RadioButton _orientationVtRadioButton;
    public CheckBox    _orientationInvertedCheckBox;

    public OrientationPane( final SessionContext sessionContext,
                            final boolean useInvertedbutton ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( sessionContext, useInvertedbutton );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final Orientation getOrientation() {
        return _orientationHzRadioButton.isSelected()
            ? Orientation.HORIZONTAL
            : _orientationVtRadioButton.isSelected()
                ? Orientation.VERTICAL
                : Orientation.defaultValue();
    }

    private final void initPane( final SessionContext sessionContext,
                                 final boolean useInvertedbutton ) {
        _orientationToggleGroup = new ToggleGroup();
        _orientationHzRadioButton = FxGuiUtilities.getRadioButton( Orientation.HORIZONTAL
                .toPresentationString(), _orientationToggleGroup, true );
        _orientationVtRadioButton = FxGuiUtilities.getRadioButton( Orientation.VERTICAL
                .toPresentationString(), _orientationToggleGroup, false );

        _orientationInvertedCheckBox = FxGuiUtilities.getCheckBox( "Inverted", false ); //$NON-NLS-1$

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 10d );
        gridPane.setVgap( 10d );

        gridPane.add( _orientationHzRadioButton, 0, 0 );
        gridPane.add( _orientationVtRadioButton, 0, 1 );
        if ( useInvertedbutton ) {
            gridPane.add( _orientationInvertedCheckBox, 0, 2 );
        }

        gridPane.setAlignment( Pos.CENTER );
        gridPane.setPadding( new Insets( 12d ) );

        setLeft( gridPane );

        _orientationToggleGroup.selectedToggleProperty()
                .addListener( ( observable, oldToggle, newToggle ) -> {
                    // If no toggle button selected, re-select the previous
                    // button, but wrap this in a JavaFX runLater thread to
                    // ensure all FX event code precedes the custom selection.
                    if ( ( newToggle == null ) ) {
                        Platform.runLater( () -> _orientationToggleGroup
                                .selectToggle( oldToggle ) );
                        return;
                    }
                } );
    }

    public final boolean isInverted() {
        return _orientationInvertedCheckBox.isSelected();
    }

    public final void saveEdits() {
        // NOTE: Currently there is nothing to do as all the data is saved in
        // the controls themselves.
    }

    public final void setInverted( final boolean inverted ) {
        _orientationInvertedCheckBox.setSelected( inverted );
    }

    public final void setOrientation( final Orientation orientation ) {
        switch ( orientation ) {
        case HORIZONTAL:
            _orientationHzRadioButton.setSelected( true );
            break;
        case VERTICAL:
            _orientationVtRadioButton.setSelected( true );
            break;
        default:
            break;
        }
    }

}
