/**
 * MIT License
 *
 * Copyright (c) 2023 Mark Schmieder
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

import com.mhschmieder.commonstoolkit.lang.ListViewConverter;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ListViewUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ListViewUtilities() {}

    public static Callback< ListView< ? extends ListViewConverter >, ListCell< ? extends ListViewConverter > >
        makeListViewCellFactory( final ListViewConverter listViewConverter ) {
        final Callback< ListView < ? extends ListViewConverter >, ListCell< ? extends ListViewConverter > >
                cellFactory = new Callback< ListView< ? extends ListViewConverter >, ListCell< ? extends ListViewConverter > >() {
            @Override
            public ListCell< ? extends ListViewConverter > call( ListView< ? extends ListViewConverter > p ) {
                return new ListCell< ListViewConverter >() {
                    {
                        setContentDisplay( ContentDisplay.TEXT_ONLY );
                    }

                    @Override
                    protected void updateItem( final ListViewConverter item, boolean empty ) {
                        super.updateItem( item, empty );

                        final ListViewConverter currentThreatLevel
                                = ( ( item == null ) || empty )
                                ? getItem()
                                : item;
                        if ( currentThreatLevel != null ) {
                            setText( currentThreatLevel.toListCellText() );
                        }
                    }
                };
            } };
            
            return cellFactory;
    }
}
