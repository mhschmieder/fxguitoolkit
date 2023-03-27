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
package com.mhschmieder.fxguitoolkit.swing;

import com.mhschmieder.fxgraphicstoolkit.io.FormattedVectorGraphicsExportOptions;
import com.mhschmieder.guitoolkit.component.TitledVectorizationXPanel;

/**
 * This is an abstract parent class for Swing panels that form the main content
 * layout region for a legacy Formatted Vector Graphics presentation wrapper.
 */
public abstract class FormattedVectorGraphicsPanel extends TitledVectorizationXPanel {
    /**
     *
     */
    private static final long                      serialVersionUID = -946301966269233300L;

    // Cache the most recent Formatted Vector Graphics Export Options.
    protected FormattedVectorGraphicsExportOptions _formattedVectorGraphicsExportOptions;

    protected FormattedVectorGraphicsPanel() {
        // Always call the superclass constructor first!
        super();

        _formattedVectorGraphicsExportOptions = new FormattedVectorGraphicsExportOptions();

        try {
            initPanel();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initPanel() {}

    // NOTE: It is up to the subclasses to decide which sub-panel to hide/show.
    public void setAuxiliaryPanelVisible( final boolean visible ) {}

    /**
     * Cache the Formatted Vector Graphics Options for the next Vector Graphics
     * Export.
     *
     * @param formattedVectorGraphicsExportOptions
     *            The Formatted Vector Graphics Export Options for which
     *            components to include in the Vector Graphics File
     */
    public final void setFormattedVectorGraphicsExportOptions( final FormattedVectorGraphicsExportOptions formattedVectorGraphicsExportOptions ) {
        _formattedVectorGraphicsExportOptions = formattedVectorGraphicsExportOptions;

        // Make sure the Title is in sync with the most recent user selection.
        setTitle( formattedVectorGraphicsExportOptions.getTitle() );
    }

    // NOTE: It is up to the subclasses to decide which sub-panel to hide/show.
    public void setInformationTablesVisible( final boolean visible ) {}

    // NOTE: It is up to the subclasses to decide which sub-panel to hide/show.
    public void setOptionalItemVisible( final boolean visible ) {}

    public final void syncViewToExportOptions( final FormattedVectorGraphicsExportOptions formattedVectorGraphicsExportOptions ) {
        setAuxiliaryPanelVisible( formattedVectorGraphicsExportOptions.isExportAuxiliaryPanel() );
        setInformationTablesVisible( formattedVectorGraphicsExportOptions
                .isExportInformationTables() );
        setOptionalItemVisible( formattedVectorGraphicsExportOptions.isExportOptionalItem() );
    }
}
