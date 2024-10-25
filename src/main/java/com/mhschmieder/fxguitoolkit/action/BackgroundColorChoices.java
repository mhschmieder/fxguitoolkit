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

import java.util.Arrays;
import java.util.Collection;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;

import javafx.scene.paint.Color;

/**
 * This is a struct-like container for generic Background Color choices.
 * <p>
 * NOTE: At this time, slate gray hues are not exposed anymore, so they are
 * removed from the factory methods and are aliased to default choices in the
 * mapping methods that go between cached values and list-based choices.
 */
public final class BackgroundColorChoices {

    public static final String DEFAULT_BACKGROUND_COLOR_NAME =
                                                             getBackgroundColorName( ColorConstants.DEFAULT_BACKGROUND_COLOR );

    @SuppressWarnings("nls")
    public static Color getBackgroundColor( final String backgroundColorName ) {
        Color backgroundColor = ColorConstants.DEFAULT_BACKGROUND_COLOR;
        
        switch ( backgroundColorName ) {
        case "black":
            backgroundColor = Color.BLACK;
            break;
        case "night":
            backgroundColor = ColorConstants.NIGHT_MODE;
            break;
        case "dark charcoal":
            backgroundColor = ColorConstants.DARK_CHARCOAL;
            break;
        case "davy's gray":
            backgroundColor = ColorConstants.DAVYS_GRAY;
            break;
        case "dim gray":
            backgroundColor = Color.DIMGRAY;
            break;
        case "spanish gray":
            backgroundColor = ColorConstants.SPANISH_GRAY;
            break;
        case "dark gray":
            backgroundColor = Color.DARKGRAY;
            break;
        case "medium gray":
            backgroundColor = ColorConstants.MEDIUM_GRAY;
            break;
        case "light gray":
            backgroundColor = Color.LIGHTGRAY;
            break;
        case "gainsboro":
            backgroundColor = Color.GAINSBORO;
            break;
        case "day":
            backgroundColor = ColorConstants.DAY_MODE;
            break;
        case "white smoke":
            backgroundColor = Color.WHITESMOKE;
            break;
        case "white":
            backgroundColor = Color.WHITE;
            break;
        // case "dark slate gray":
        // backgroundColor = Color.DARKSLATEGRAY;
        // break;
        // case "slate gray":
        // backgroundColor = Color.SLATEGRAY;
        // break;
        // case "light slate gray":
        // backgroundColor = Color.LIGHTSLATEGRAY;
        // break;
        case "dark blue gray":
            backgroundColor = ColorConstants.DARKBLUEGRAY;
            break;
        case "blue gray":
            backgroundColor = ColorConstants.BLUEGRAY;
            break;
        case "light blue gray":
            backgroundColor = ColorConstants.LIGHTBLUEGRAY;
            break;
        default:
            break;
        }
        
        return backgroundColor;
    }

    @SuppressWarnings("nls")
    public static String getBackgroundColorName( final Color backgroundColor ) {
        if ( Color.BLACK.equals( backgroundColor ) ) {
            return "black";
        }
        
        if ( ColorConstants.NIGHT_MODE.equals( backgroundColor ) ) {
            return "night";
        }
        
        if ( ColorConstants.DARK_CHARCOAL.equals( backgroundColor ) ) {
            return "dark charcoal";
        }
        
        if ( ColorConstants.DAVYS_GRAY.equals( backgroundColor ) ) {
            return "davy's gray";
        }
        
        if ( Color.DIMGRAY.equals( backgroundColor ) ) {
            return "dim gray";
        }
        
        if ( ColorConstants.SPANISH_GRAY.equals( backgroundColor ) ) {
            return "spanish gray";
        }
        
        if ( Color.DARKGRAY.equals( backgroundColor ) ) {
            return "dark gray";
        }
        
        if ( ColorConstants.MEDIUM_GRAY.equals( backgroundColor ) ) {
            return "medium gray";
        }
        
        if ( Color.LIGHTGRAY.equals( backgroundColor ) ) {
            return "light gray";
        }
        
        if ( Color.GAINSBORO.equals( backgroundColor ) ) {
            return "gainsboro";
        }
        
        if ( ColorConstants.DAY_MODE.equals( backgroundColor ) ) {
            return "day";
        }
        
        if ( Color.WHITESMOKE.equals( backgroundColor ) ) {
            return "white smoke";
        }
        
        if ( Color.WHITE.equals( backgroundColor ) ) {
            return "white";
        }
        
        // if ( Color.DARKSLATEGRAY.equals( backgroundColor ) ) {
        // return "dark slate gray";
        // }
        //
        // if ( Color.SLATEGRAY.equals( backgroundColor ) ) {
        // return "slate gray";
        // }
        //
        // if ( Color.LIGHTSLATEGRAY.equals( backgroundColor ) ) {
        // return "light slate gray";
        // }
        
        if ( ColorConstants.DARKBLUEGRAY.equals( backgroundColor ) ) {
            return "dark blue gray";
        }
        
        if ( ColorConstants.BLUEGRAY.equals( backgroundColor ) ) {
            return "blue gray";
        }
        
        if ( ColorConstants.LIGHTBLUEGRAY.equals( backgroundColor ) ) {
            return "light blue gray";
        }
        
        return DEFAULT_BACKGROUND_COLOR_NAME;
    }

    // List all of the equal weighted (achromatic) gray scale colors.
    public XAction _backgroundColorBlackChoice;
    public XAction _backgroundColorNightChoice;
    public XAction _backgroundColorDarkCharcoalChoice;
    public XAction _backgroundColorDavysGrayChoice;
    public XAction _backgroundColorDimGrayChoice;
    public XAction _backgroundColorSpanishGrayChoice;
    public XAction _backgroundColorDarkGrayChoice;
    public XAction _backgroundColorMediumGrayChoice;
    public XAction _backgroundColorLightGrayChoice;
    public XAction _backgroundColorGainsboroChoice;
    public XAction _backgroundColorDayChoice;
    public XAction _backgroundColorWhiteSmokeChoice;
    public XAction _backgroundColorWhiteChoice;

    // List all of the chromatic slate gray hues.
    public XAction _backgroundColorDarkSlateGrayChoice;
    public XAction _backgroundColorSlateGrayChoice;
    public XAction _backgroundColorLightSlateGrayChoice;

    // List all of the chromatic custom blue-gray hues.
    public XAction _backgroundColorDarkBlueGrayChoice;
    public XAction _backgroundColorBlueGrayChoice;
    public XAction _backgroundColorLightBlueGrayChoice;

    public BackgroundColorChoices( final ClientProperties pClientProperties ) {
        _backgroundColorBlackChoice = LabeledActionFactory
                .getBackgroundColorBlackChoice( pClientProperties );
        _backgroundColorNightChoice = LabeledActionFactory
                .getBackgroundColorNightChoice( pClientProperties );
        _backgroundColorDarkCharcoalChoice = LabeledActionFactory
                .getBackgroundColorVeryDarkGrayChoice( pClientProperties );
        _backgroundColorDavysGrayChoice = LabeledActionFactory
                .getBackgroundColorDavysGrayChoice( pClientProperties );
        _backgroundColorDimGrayChoice = LabeledActionFactory
                .getBackgroundColorDimGrayChoice( pClientProperties );
        
        _backgroundColorSpanishGrayChoice = LabeledActionFactory
                .getBackgroundColorSpanishGrayChoice( pClientProperties );
        _backgroundColorDarkGrayChoice = LabeledActionFactory
                .getBackgroundColorDarkGrayChoice( pClientProperties );
        _backgroundColorMediumGrayChoice = LabeledActionFactory
                .getBackgroundColorMediumGrayChoice( pClientProperties );
        _backgroundColorLightGrayChoice = LabeledActionFactory
                .getBackgroundColorLightGrayChoice( pClientProperties );
        _backgroundColorGainsboroChoice = LabeledActionFactory
                .getBackgroundColorGainsboroChoice( pClientProperties );
        _backgroundColorDayChoice = LabeledActionFactory
                .getBackgroundColorDayChoice( pClientProperties );
        _backgroundColorWhiteSmokeChoice = LabeledActionFactory
                .getBackgroundColorWhiteSmokeChoice( pClientProperties );
        _backgroundColorWhiteChoice = LabeledActionFactory
                .getBackgroundColorWhiteChoice( pClientProperties );

        _backgroundColorDarkSlateGrayChoice = LabeledActionFactory
                .getBackgroundColorDarkSlateGrayChoice( pClientProperties );
        _backgroundColorSlateGrayChoice = LabeledActionFactory
                .getBackgroundColorSlateGrayChoice( pClientProperties );
        _backgroundColorLightSlateGrayChoice = LabeledActionFactory
                .getBackgroundColorLightSlateGrayChoice( pClientProperties );

        _backgroundColorDarkBlueGrayChoice = LabeledActionFactory
                .getBackgroundColorDarkBlueGrayChoice( pClientProperties );
        _backgroundColorBlueGrayChoice = LabeledActionFactory
                .getBackgroundColorBlueGrayChoice( pClientProperties );
        _backgroundColorLightBlueGrayChoice = LabeledActionFactory
                .getBackgroundColorLightBlueGrayChoice( pClientProperties );
    }

    public Collection< Action > getBackgroundColorChoiceCollection() {
        final Collection< Action > backgroundColorChoiceCollection = Arrays
                .asList( _backgroundColorBlackChoice,
                         _backgroundColorNightChoice,
                         _backgroundColorDarkCharcoalChoice,
                         _backgroundColorDavysGrayChoice,
                         _backgroundColorDimGrayChoice,
                         ActionUtils.ACTION_SEPARATOR,
                         _backgroundColorSpanishGrayChoice,
                         _backgroundColorDarkGrayChoice,
                         _backgroundColorMediumGrayChoice,
                         _backgroundColorLightGrayChoice,
                         _backgroundColorGainsboroChoice,
                         _backgroundColorDayChoice,
                         _backgroundColorWhiteSmokeChoice,
                         _backgroundColorWhiteChoice,
                         // ActionUtils.ACTION_SEPARATOR,
                         // _backgroundColorDarkSlateGrayChoice,
                         // _backgroundColorSlateGrayChoice,
                         // _backgroundColorLightSlateGrayChoice,
                         ActionUtils.ACTION_SEPARATOR,
                         _backgroundColorDarkBlueGrayChoice,
                         _backgroundColorBlueGrayChoice,
                         _backgroundColorLightBlueGrayChoice );
        return backgroundColorChoiceCollection;
    }

    public Color getSelectedBackgroundColor() {
        final String backgroundColorName = getSelectedBackgroundColorName();
        return getBackgroundColor( backgroundColorName );
    }

    @SuppressWarnings("nls")
    public String getSelectedBackgroundColorName() {
        if ( _backgroundColorBlackChoice.isSelected() ) {
            return "black";
        }
        
        if ( _backgroundColorNightChoice.isSelected() ) {
            return "night";
        }
        
        if ( _backgroundColorDarkCharcoalChoice.isSelected() ) {
            return "dark Charcoal";
        }
        
        if ( _backgroundColorDavysGrayChoice.isSelected() ) {
            return "davy's gray";
        }
        
        if ( _backgroundColorDimGrayChoice.isSelected() ) {
            return "dim gray";
        }
        
        if ( _backgroundColorSpanishGrayChoice.isSelected() ) {
            return "spanish gray";
        }
        
       if ( _backgroundColorDarkGrayChoice.isSelected() ) {
            return "dark gray";
        }
        
        if ( _backgroundColorMediumGrayChoice.isSelected() ) {
            return "medium gray";
        }
        
        if ( _backgroundColorLightGrayChoice.isSelected() ) {
            return "light gray";
        }
        
        if ( _backgroundColorGainsboroChoice.isSelected() ) {
            return "gainsboro";
        }
        
        if ( _backgroundColorDayChoice.isSelected() ) {
            return "day";
        }
        
        if ( _backgroundColorWhiteSmokeChoice.isSelected() ) {
            return "white smoke";
        }
        
        if ( _backgroundColorWhiteChoice.isSelected() ) {
            return "white";
        }
        
        // if ( _backgroundColorDarkSlateGrayChoice.isSelected() ) {
        // return "dark slate gray";
        // }
        //
        // if ( _backgroundColorSlateGrayChoice.isSelected() ) {
        // return "slate gray";
        // }
        //
        // if ( _backgroundColorLightSlateGrayChoice.isSelected() ) {
        // return "light slate gray";
        // }
        
        if ( _backgroundColorDarkBlueGrayChoice.isSelected() ) {
            return "dark blue gray";
        }
        
        if ( _backgroundColorBlueGrayChoice.isSelected() ) {
            return "blue gray";
        }
        
        if ( _backgroundColorLightBlueGrayChoice.isSelected() ) {
            return "light blue gray";
        }
        
        return "white";
    }

    @SuppressWarnings("nls")
    public Color selectBackgroundColor( final String backgroundColorName ) {
        Color backgroundColor = Color.TRANSPARENT;
        
        switch ( backgroundColorName ) {
        case "black":
            _backgroundColorBlackChoice.setSelected( true );
            backgroundColor = Color.BLACK;
            break;
        case "night":
            _backgroundColorNightChoice.setSelected( true );
            backgroundColor = ColorConstants.NIGHT_MODE;
            break;
        case "dark charcoal":
            _backgroundColorDarkCharcoalChoice.setSelected( true );
            backgroundColor = ColorConstants.DARK_CHARCOAL;
            break;
        case "davy's gray":
            _backgroundColorDavysGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.DAVYS_GRAY;
            break;
        case "dim gray":
            _backgroundColorDimGrayChoice.setSelected( true );
            backgroundColor = Color.DIMGRAY;
            break;
        case "spanish gray":
            _backgroundColorSpanishGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.SPANISH_GRAY;
            break;
        case "dark gray":
            _backgroundColorDarkGrayChoice.setSelected( true );
            backgroundColor = Color.DARKGRAY;
            break;
        case "medium gray":
            _backgroundColorMediumGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.MEDIUM_GRAY;
            break;
        case "light gray":
            _backgroundColorLightGrayChoice.setSelected( true );
            backgroundColor = Color.LIGHTGRAY;
            break;
        case "gainsboro":
            _backgroundColorGainsboroChoice.setSelected( true );
            backgroundColor = Color.GAINSBORO;
            break;
        case "day":
            _backgroundColorDayChoice.setSelected( true );
            backgroundColor = ColorConstants.DAY_MODE;
            break;
        case "white smoke":
            _backgroundColorWhiteSmokeChoice.setSelected( true );
            backgroundColor = Color.WHITESMOKE;
            break;
        case "white":
            _backgroundColorWhiteChoice.setSelected( true );
            backgroundColor = Color.WHITE;
            break;
        // case "dark slate gray":
        // _backgroundColorDarkSlateGrayChoice.setSelected( true );
        // backgroundColor = Color.DARKSLATEGRAY;
        // break;
        // case "slate gray":
        // _backgroundColorSlateGrayChoice.setSelected( true );
        // backgroundColor = Color.SLATEGRAY;
        // break;
        // case "light slate gray":
        // _backgroundColorLightSlateGrayChoice.setSelected( true );
        // backgroundColor = Color.LIGHTSLATEGRAY;
        // break;
        case "dark blue gray":
            _backgroundColorDarkBlueGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.DARKBLUEGRAY;
            break;
        case "blue gray":
            _backgroundColorBlueGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.BLUEGRAY;
            break;
        case "light blue gray":
            _backgroundColorLightBlueGrayChoice.setSelected( true );
            backgroundColor = ColorConstants.LIGHTBLUEGRAY;
            break;
        default:
            _backgroundColorWhiteChoice.setSelected( true );
            backgroundColor = Color.WHITE;
        }
        
        return backgroundColor;
    }
}
