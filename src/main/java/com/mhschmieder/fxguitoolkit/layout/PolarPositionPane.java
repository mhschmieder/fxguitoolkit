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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.layout;

import com.mhschmieder.commonstoolkit.net.ClientProperties;
import com.mhschmieder.commonstoolkit.physics.AngleUnit;
import com.mhschmieder.commonstoolkit.physics.DistanceUnit;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.ScrollingSensitivity;
import com.mhschmieder.fxguitoolkit.control.DistanceEditor;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class PolarPositionPane extends BorderPane {

    public AnglePane      _anglePane;
    public DistanceEditor _distanceEditor;

    public PolarPositionPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final double getDistance() {
        return _distanceEditor.getDistanceMeters();
    }

    public final double getRotationAngle() {
        return _anglePane.getAngleDegrees();
    }

    private final void initPane( final ClientProperties clientProperties ) {
        _anglePane = new AnglePane( clientProperties, "Angle", true ); //$NON-NLS-1$

        _distanceEditor = new DistanceEditor( clientProperties, "0", null ); //$NON-NLS-1$

        final HBox distanceEditorPane = GuiUtilities.getLabeledTextFieldPane( "Radial Distance", //$NON-NLS-1$
                                                                              _distanceEditor );

        setTop( _anglePane );
        setBottom( distanceEditorPane );
    }

    public final void saveEdits() {
        _distanceEditor.saveEdits();
        _anglePane.saveEdits();
    }

    public final void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Angle Pane.
        _anglePane.setGesturesEnabled( gesturesEnabled );
    }

    public final void setPolarPosition( final double rotationAngle, final double distance ) {
        // Forward this method to the subsidiary components.
        _anglePane.setAngleDegrees( rotationAngle );
        _distanceEditor.setDistanceMeters( distance );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public final void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Angle Pane.
        _anglePane.setScrollingSensitivity( scrollingSensitivity );
    }

    public final void toggleGestures() {
        // Forward this method to the Angle Pane.
        _anglePane.toggleGestures();
    }

    public final void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the subcomponents.
        _anglePane.updateAngleUnit( angleUnit );
    }

    public final void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the subcomponents.
        _distanceEditor.updateDistanceUnit( distanceUnit );
    }

}
