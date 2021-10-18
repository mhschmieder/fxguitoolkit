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

import com.mhschmieder.commonstoolkit.math.AngleUnit;
import com.mhschmieder.commonstoolkit.math.MathUtilities;
import com.mhschmieder.commonstoolkit.net.SessionContext;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;

/**
 * The Angle Selector is a dynamic drop-list of formatted angle values in
 * degrees, currently restricted to be uneditable.
 * <p>
 * :TODO: Use the richer logic of this class's list updater method to inform a
 * consolidation of the two current approaches in the super-class, and note that
 * this class adds a third approach as well. All are mutually exclusive.
 */
public final class AngleSelector extends DoubleSelector {

    // Default limiting angle, for effective no-op.
    public static final int LIMIT_ANGLE_DEFAULT = 0;

    // Maintain a reference to the Angle Unit.
    protected AngleUnit     _angleUnit;

    public AngleSelector( final SessionContext sessionContext,
                          final String tooltipText,
                          final boolean toolbarContext ) {
        // Always call the superclass constructor first!
        super( sessionContext, 0, 1, 0, 1, true, tooltipText, toolbarContext, false, false );

        _angleUnit = AngleUnit.defaultValue();

        try {
            initComboBox();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final double getAngle() {
        final double angle = MathUtilities.parseAngle( getValue(), _numberFormat, _angleUnit );
        return angle;
    }

    public final int getNumberOfAllowedAngles() {
        return getItems().size();
    }

    private final void initComboBox() {
        // :NOTE: Groupings are turned off, and we force US locale for now,
        // due to specifics about the implementation of the pattern-matcher.
        // :TODO: Alternately, cast to DecimalFormat, query the decimal and
        // grouping separator chars, and pass them to pattern-matcher?
        _numberFormat.setGroupingUsed( false );

        // Make sure the list displays all items without scrolling.
        // :NOTE: Commented out due to blank lines when list changes size.
        // setVisibleRowCount( 20 );
    }

    /**
     * This method selects either the first or the last angle. This is a
     * convenience method as this logic may be used in many places;
     * particularly when pairing angle selectors that must be guaranteed to not
     * invert angle order from one Combo Box to another.
     *
     * @param defaultToLastAngle
     *            Flag for whether to default to last angle or first angle
     */
    public final void selectDefaultAngle( final boolean defaultToLastAngle ) {
        // Select the first or last angle in the list of allowed angles.
        final SingleSelectionModel< String > selectionModel = getSelectionModel();
        if ( defaultToLastAngle ) {
            selectionModel.selectLast();
        }
        else {
            selectionModel.selectFirst();
        }
    }

    /**
     * This method replaces the displayed drop-list of allowed angles, and
     * applies mutually exclusive logic for either preserving the selected value
     * (if present in the new list) or the selected index (which may have a new
     * value and/or may be beyond the range of the new list).
     *
     * There are two variants on preserving the selected index, to account for
     * cases where we want to leave it alone unless out of range (in which case
     * we select the last index), or adjust based on list changes (unless now
     * out of range, in which case we select the first index).
     *
     * @param allowedAngles
     *            The new list of angles to be formatted and presented
     * @param preserveSelectedValue
     *            Flag for preserving current selected value
     * @param preserveSelectedIndex
     *            Flag for preserving current selected index
     * @param adjustSelectedIndex
     *            Flag for adjusting current selected index
     * @param defaultToLastAngle
     *            Flag for whether to default to last angle or first angle
     */
    public final void setAllowedAngles( final double[] allowedAngles,
                                        final boolean preserveSelectedValue,
                                        final boolean preserveSelectedIndex,
                                        final boolean adjustSelectedIndex,
                                        final boolean defaultToLastAngle ) {
        // Save the selection to reinstate after replacing the drop-list.
        final String angleCurrent = getValue();

        // Save the selected index to reinstate after replacing the drop-list.
        final SingleSelectionModel< String > selectionModel = getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();
        final int oldNumberOfAllowedAngles = getNumberOfAllowedAngles();

        // Start with a clean slate, as we are effectively replacing the entire
        // list. Be careful if restoring the current selection, as there are
        // many edge cases that either do the wrong thing, result in a blank
        // selection field, or do not generate a callback.
        final ObservableList< String > allowedAnglesFormatted = FXCollections.observableArrayList();
        for ( final double allowedAngle : allowedAngles ) {
            final String allowedAngleFormatted = MathUtilities
                    .formatAngle( allowedAngle, _numberFormat, _angleUnit );
            allowedAnglesFormatted.add( allowedAngleFormatted );
        }

        // Replace the entire list, unless it hasn't changed, in which case do
        // nothing as otherwise we can cause side effects due to the sale item
        // being reselected as though it is a brand new (different) selection.
        final ObservableList< String > items = getItems();
        if ( !items.equals( allowedAnglesFormatted ) ) {
            setItems( allowedAnglesFormatted );
        }

        // Make a revised selection based on which preservation tactics were
        // chosen (select by value or select by index).
        if ( preserveSelectedValue ) {
            // If the previous selection is also in the new list, reselect it.
            // TODO: If not found, get the angle closest to the previous
            // selection (the default action is to set to the first item in the
            // list). This requires passing in the current angle, and then
            // converting back and forth between numbers and formatted strings.
            if ( allowedAnglesFormatted.contains( angleCurrent ) ) {
                setValue( angleCurrent );
            }
            else if ( angleCurrent != null ) {
                // If no match found, select the angle by default lookup index.
                final String angleDefault = allowedAnglesFormatted.get( LIMIT_ANGLE_DEFAULT );
                setValue( angleDefault );
            }
            else {
                selectDefaultAngle( defaultToLastAngle );
            }
        }
        else if ( preserveSelectedIndex ) {
            // Preserve the selected index. If this results in an invalid index,
            // simply select the last angle in the list instead.
            final int newNumberOfAllowedAngles = getNumberOfAllowedAngles();
            if ( selectedIndex < newNumberOfAllowedAngles ) {
                selectionModel.select( selectedIndex );
            }
            else {
                selectDefaultAngle( defaultToLastAngle );
            }
        }
        else if ( adjustSelectedIndex ) {
            // Adjust the selected index by the difference between the old list
            // size and the new list size. If this results in an invalid index,
            // simply select the first angle in the list instead.
            final int newNumberOfAllowedAngles = getNumberOfAllowedAngles();
            final int numberOfAllowedAnglesDifference = newNumberOfAllowedAngles
                    - oldNumberOfAllowedAngles;
            final int adjustedIndex = selectedIndex + numberOfAllowedAnglesDifference;
            if ( adjustedIndex > 0 ) {
                selectionModel.select( adjustedIndex );
            }
            else {
                selectDefaultAngle( defaultToLastAngle );
            }
        }
    }

    public final void setAngle( final double angle ) {
        final String angleFormatted = MathUtilities.formatAngle( angle, _numberFormat, _angleUnit );
        setValue( angleFormatted );
    }

    public final void updateAngleUnit( final AngleUnit angleUnit ) {
        _angleUnit = angleUnit;
    }

}