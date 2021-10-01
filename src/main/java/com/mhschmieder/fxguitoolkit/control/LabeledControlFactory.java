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
package com.mhschmieder.fxguitoolkit.control;

import com.mhschmieder.fxguitoolkit.image.ImageUtilities;
import com.mhschmieder.fxguitoolkit.FxGuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * {@code LabeledControlFactory} is a factory class for minimizing copy/paste
 * code
 * for shared design patterns regarding controls such as action buttons.
 */
public final class LabeledControlFactory {

    /**
     * The default constructor is disabled, as this is a static factory class.
     */
    private LabeledControlFactory() {}

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final double buttonHeight ) {
        final Button button = getButton( label, font, buttonWidth );
        button.setPrefHeight( buttonHeight );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth ) {
        final Button button = new Button( label );
        if ( font != null ) {
            button.setFont( font );
        }
        button.setAlignment( Pos.CENTER );

        button.setPrefWidth( buttonWidth );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss ) {
        return getButton( label,
                          font,
                          buttonWidth,
                          backColorCss,
                          borderColorCss,
                          borderWidthCss,
                          "6" );
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String borderRadiusCss ) {
        final Button button = getButton( label, font, buttonWidth );

        FxGuiUtilities.applyLabeledButtonStyle( button,
                                                backColorCss,
                                                borderColorCss,
                                                borderWidthCss,
                                                borderRadiusCss );

        return button;
    }

    public static Button getButton( final String label,
                                    final Font font,
                                    final double buttonWidth,
                                    final String backColorCss,
                                    final String borderColorCss,
                                    final String borderWidthCss,
                                    final String iconFilename,
                                    final double iconWidth,
                                    final double iconHeight ) {
        final Button button = getButton( label,
                                         font,
                                         buttonWidth,
                                         backColorCss,
                                         borderColorCss,
                                         borderWidthCss );

        final ImageView icon = ImageUtilities.createIcon( iconFilename, iconWidth, iconHeight );
        button.setGraphic( icon );
        button.setGraphicTextGap( 8d );

        return button;
    }

    public static Button getIconButton( final String svgImage,
                                        final double buttonWidth,
                                        final double buttonHeight,
                                        final Color backgroundColor,
                                        final String tooltipText ) {
        final SVGPath buttonIcon = FxGuiUtilities.getSvgImage( svgImage );

        final Button button = new Button();
        button.setShape( buttonIcon );
        button.setBackground( LayoutFactory.makeRegionBackground( backgroundColor ) );

        button.setMinSize( buttonWidth, buttonHeight );
        button.setPrefSize( buttonWidth, buttonHeight );
        button.setMaxSize( buttonWidth, buttonHeight );

        if ( ( tooltipText != null ) && !tooltipText.isEmpty() ) {
            final Tooltip tooltip = new Tooltip( tooltipText );
            button.setTooltip( tooltip );
        }

        return button;
    }

    public static ToggleButton getToggleButton( final String label,
                                                final Font font,
                                                final double buttonWidth,
                                                final double buttonHeight ) {
        final ToggleButton toggleButton = new ToggleButton( label );
        if ( font != null ) {
            toggleButton.setFont( font );
        }
        toggleButton.setAlignment( Pos.CENTER );

        toggleButton.setPrefSize( buttonWidth, buttonHeight );

        return toggleButton;
    }

    public static Label getLabel( final String labelText, final Font font ) {
        return getLabel( labelText, font, Pos.CENTER );
    }

    public static Label getLabel( final String labelText, final Font font, final Paint textFill ) {
        final Label label = getLabel( labelText, font );
        label.setTextFill( textFill );

        return label;
    }

    public static Label getLabel( final String labelText,
                                  final Font font,
                                  final double prefWidth ) {
        final Label label = getLabel( labelText, font );
        label.setPrefWidth( prefWidth );

        return label;
    }

    public static Label getLabel( final String labelText,
                                  final Font font,
                                  final Paint textFill,
                                  final double prefWidth ) {
        final Label label = getLabel( labelText, font, textFill );
        label.setPrefWidth( prefWidth );

        return label;
    }

    public static Label getLabel( final String labelText, final Font font, final Pos position ) {
        final Label label = new Label( labelText );
        if ( font != null ) {
            label.setFont( font );
        }
        label.setAlignment( position );

        // Text Alignment relates specifically to word wrap alignment.
        label.setTextAlignment( TextAlignment.CENTER );
        label.setWrapText( true );

        return label;
    }

}
