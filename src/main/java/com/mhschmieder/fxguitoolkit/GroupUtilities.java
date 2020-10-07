/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is a utility class for dealing with common group functionality.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class GroupUtilities {

    // :NOTE: The constructor is disabled, as this is a static utilities class.
    private GroupUtilities() {}

    public static Group getBackgroundColorIcon( final Color backgroundColor ) {
        final Group group = new Group();

        // First, get the icon size and insets for the menu context.
        final int inset = FxGuiUtilities.getIconInset( IconContext.MENU );
        final int boxSideLength = FxGuiUtilities.MENU_ICON_SIZE - ( inset * 2 );
        final int startX = inset;
        final int startY = FxGuiUtilities.MENU_ICON_SIZE - inset;

        // Fill the icon with the specified background color.
        final Rectangle box = new Rectangle( startX, startY, boxSideLength, boxSideLength );
        box.setFill( backgroundColor );

        // Add the box to the Node Group.
        group.getChildren().addAll( box );

        return group;
    }

}
