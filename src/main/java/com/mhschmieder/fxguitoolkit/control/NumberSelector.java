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
package com.mhschmieder.fxguitoolkit.control;

import java.text.NumberFormat;
import java.util.Locale;

import com.mhschmieder.commonstoolkit.util.ClientProperties;

/**
 * This class formalizes aspects of list selection that are specific to
 * number value sets.
 * <p>
 * TODO: Review whether it is better to derive from TextSelector, or even
 *  to specify the template type as Number, and use NumberConverters in
 *  place of Number Formatters? Look at ControlsFX and other examples.
 */
public class NumberSelector extends XComboBox< String > {

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat _numberFormat;

    // Number format cache used for locale-specific number parsing.
    protected NumberFormat _numberParse;

    public NumberSelector( final ClientProperties clientProperties,
                           final int minFractionDigitsFormat,
                           final int maxFractionDigitsFormat,
                           final int minFractionDigitsParse,
                           final int maxFractionDigitsParse,
                           final boolean useLocale,
                           final String tooltipText,
                           final boolean toolbarContext,
                           final boolean editable,
                           final boolean searchable ) {
        // Always call the superclass constructor first!
        super( clientProperties, tooltipText, toolbarContext, editable, searchable );

        try {
            initComboBox( minFractionDigitsFormat,
                          maxFractionDigitsFormat,
                          minFractionDigitsParse,
                          maxFractionDigitsParse,
                          useLocale );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("nls")
    private final void initComboBox( final int minFractionDigitsFormat,
                                     final int maxFractionDigitsFormat,
                                     final int minFractionDigitsParse,
                                     final int maxFractionDigitsParse,
                                     final boolean useLocale ) {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number. Note, however, that in most contexts we have specific
        // formatting that is locked to the usage domain; hence the flag. In
        // such cases we are safest with US-English (vs. just "English").
        final Locale numberLocale = useLocale
            ? clientProperties.locale
            : Locale.forLanguageTag( "en-US" );
        _numberFormat = NumberFormat.getNumberInstance( numberLocale );
        _numberParse = ( NumberFormat ) _numberFormat.clone();

        // Set the precision for floating-point text formatting.
        _numberFormat.setMinimumFractionDigits( minFractionDigitsFormat );
        _numberFormat.setMaximumFractionDigits( maxFractionDigitsFormat );

        // Set the precision for floating-point text parsing.
        _numberParse.setMinimumFractionDigits( minFractionDigitsParse );
        _numberParse.setMaximumFractionDigits( maxFractionDigitsParse );
    }

}
