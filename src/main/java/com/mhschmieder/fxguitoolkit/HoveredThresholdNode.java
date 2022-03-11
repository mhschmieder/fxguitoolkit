/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * A node which displays a value on hover, but is otherwise empty
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class HoveredThresholdNode extends StackPane {

    /**
     * @param colorTag
     *            The CSS color tag lookup name associated with the data series.
     * @param d
     *            The previous data value, used for comparisons with current.
     * @param y
     *            The current data value, used for comparisons with previous.
     * @return The formatted data label, colored according to comparisons.
     */
    private static Label createDataThresholdLabel( final String colorTag,
                                                   final double d,
                                                   final double y ) {
        final Label label = new Label( y + "" ); //$NON-NLS-1$

        // TODO: Try to find out why this turns back on the circles at each
        // data point, even if we remove the other two Style Class settings.
        // NOTE: The chart line symbol style has been removed for now, as
        // drawing a box around the data display, with a white background
        // painting over everything in sight, obscures neighboring data points.
        label.getStyleClass().addAll( colorTag, // "chart-line-symbol" );
                                      "chart-series-line" ); //$NON-NLS-1$
        label.setStyle( "-fx-font-family: 'sans-serif'; -fx-font-size: 12; -fx-font-weight: bold;" ); //$NON-NLS-1$

        if ( d == 0 ) {
            label.setTextFill( Color.DARKGRAY );
        }
        else if ( y > d ) {
            label.setTextFill( Color.FORESTGREEN );
        }
        else {
            label.setTextFill( Color.FIREBRICK );
        }

        label.setMinSize( Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );

        return label;
    }

    /**
     * Default constructor.
     *
     * @param colorTag
     *            The CSS color tag lookup name associated with the data series.
     * @param d
     *            The previous data value, used for comparisons with current.
     * @param y
     *            The current data value, used for comparisons with previous.
     */
    public HoveredThresholdNode( final String colorTag, final double d, final double y ) {
        setPrefSize( 15, 15 );

        final Label label = createDataThresholdLabel( colorTag, d, y );

        setOnMouseEntered( mouseEvent -> {
            getChildren().setAll( label );
            setCursor( Cursor.TEXT );
            toFront();
        } );
        setOnMouseExited( mouseEvent -> { getChildren().clear(); setCursor( Cursor.CROSSHAIR ); } );
    }

}