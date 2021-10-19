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
package com.mhschmieder.fxcharttoolkit.chart;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A Legend Item to be displayed on a Legend.
 * <p>
 * NOTE: This class is pulled in from Sun's private API (
 * com.sun.javafx.charts.Legend) as that goes away starting with Java 11.
 */
public class ChartLegendItem {

    /** Label used to represent the Legend Item */
    public final Label                   label;

    /**
     * The symbol to use next to the item text, set to null for no symbol.
     * <p>
     * The default is a simple square of symbolFill.
     */
    private final ObjectProperty< Node > symbol;

    /** The item text */
    private final StringProperty         text;

    @SuppressWarnings("nls")
    public ChartLegendItem( final String textValue ) {
        label = new Label();

        symbol = new ObjectPropertyBase< Node >( new Region() ) {
            @Override
            public Object getBean() {
                return ChartLegendItem.this;
            }

            @Override
            public String getName() {
                return "symbol";
            }

            @Override
            protected void invalidated() {
                final Node symbolValue = get();
                if ( symbolValue != null ) {
                    symbolValue.getStyleClass().setAll( "chart-legend-item-symbol" );
                }
                label.setGraphic( symbolValue );
            }
        };

        text = new StringPropertyBase() {
            @Override
            public Object getBean() {
                return ChartLegendItem.this;
            }

            @Override
            public String getName() {
                return "text";
            }

            @Override
            protected void invalidated() {
                label.setText( get() );
            }
        };

        final Node symbolValue = getSymbol();
        symbolValue.getStyleClass().setAll( "chart-legend-item-symbol" );

        setText( textValue );

        label.getStyleClass().add( "chart-legend-item" );
        label.setAlignment( Pos.CENTER_LEFT );
        label.setContentDisplay( ContentDisplay.LEFT );
        label.setGraphic( symbolValue );
    }

    public ChartLegendItem( final String textValue, final Node symbolValue ) {
        this( textValue );

        setSymbol( symbolValue );
    }

    public final Node getSymbol() {
        return symbol.getValue();
    }

    public final String getText() {
        return text.getValue();
    }

    public final void setSymbol( final Node symbolValue ) {
        symbol.setValue( symbolValue );
    }

    public final void setText( final String textValue ) {
        text.setValue( textValue );
    }

    public final ObjectProperty< Node > symbolProperty() {
        return symbol;
    }

    public final StringProperty textProperty() {
        return text;
    }

}
