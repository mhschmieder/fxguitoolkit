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
 * FxGuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.layout;

import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Derived from <a href=
 * "http://stackoverflow.com/questions/24005247/draw-cartesian-plane-graphi-with-canvas-in-javafx/24008426#24008426"
 * >
 * jewelsea's StackOverflow answer</a>.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class Axes extends Pane {

    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    public Axes( final Bounds dims, final double tickUnit ) {
        setMinSize( Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );
        setPrefSize( dims.getWidth(), dims.getHeight() );
        setMaxSize( Region.USE_PREF_SIZE, Region.USE_PREF_SIZE );

        xAxis = new NumberAxis( dims.getMinX(), dims.getMaxX(), tickUnit );
        xAxis.setSide( Side.BOTTOM );
        xAxis.setMinorTickVisible( false );
        xAxis.setPrefWidth( dims.getWidth() );
        xAxis.setLayoutY( 0.5d * dims.getHeight() );

        yAxis = new NumberAxis( dims.getMinY(), dims.getMaxY(), tickUnit );
        yAxis.setSide( Side.LEFT );
        yAxis.setMinorTickVisible( false );
        yAxis.setPrefHeight( dims.getHeight() );
        yAxis.layoutXProperty()
                .bind( Bindings.subtract( ( 0.5d * dims.getWidth() ) + 1, yAxis.widthProperty() ) );

        getChildren().setAll( xAxis, yAxis );
    }

    public NumberAxis getXAxis() {
        return xAxis;
    }

    public NumberAxis getYAxis() {
        return yAxis;
    }

}
