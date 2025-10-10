/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
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
package com.mhschmieder.fxguitoolkit.stage;

import com.mhschmieder.commonstoolkit.io.FileExtensions;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * {@code ExtensionFilters} is a static constants class for JavaFX File Chooser
 * based file extensions.
 * <p>
 * NOTE: This code is copied from GraphicsToolkit on GitHub until it is
 * published to Maven Central from where it can be pulled via Gradle.
 */
public final class ExtensionFilters {

    /**
     * The default constructor is disabled, as this is a static constants class.
     */
    private ExtensionFilters() {}

    public static final ExtensionFilter ALL_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.ALL_DESCRIPTION, FileExtensions.ALL_EXTENSIONS );
    public static final ExtensionFilter BMP_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.BMP_DESCRIPTION, FileExtensions.BMP_EXTENSIONS );
    public static final ExtensionFilter CSV_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.CSV_DESCRIPTION, FileExtensions.CSV_EXTENSIONS );
    public static final ExtensionFilter DXF_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.DXF_DESCRIPTION, FileExtensions.DXF_EXTENSIONS );
    public static final ExtensionFilter EPS_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.EPS_DESCRIPTION, FileExtensions.EPS_EXTENSIONS );
    public static final ExtensionFilter FPX_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.FPX_DESCRIPTION, FileExtensions.FPX_EXTENSIONS );
    public static final ExtensionFilter GIF_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.GIF_DESCRIPTION, FileExtensions.GIF_EXTENSIONS );
    public static final ExtensionFilter HTML_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.HTML_DESCRIPTION, FileExtensions.HTML_EXTENSIONS );
    public static final ExtensionFilter IMAGE_GRAPHICS_EXTENSION_FILTER  = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.IMAGE_GRAPHICS_DESCRIPTION,
                                 FileExtensions.IMAGE_GRAPHICS_EXTENSIONS );
    public static final ExtensionFilter JAR_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.JAR_DESCRIPTION, FileExtensions.JAR_EXTENSIONS );
    public static final ExtensionFilter JPEG_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.JPEG_DESCRIPTION, FileExtensions.JPEG_EXTENSIONS );
    public static final ExtensionFilter LOG_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.LOG_DESCRIPTION, FileExtensions.LOG_EXTENSIONS );
    public static final ExtensionFilter PDF_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PDF_DESCRIPTION, FileExtensions.PDF_EXTENSIONS );
    public static final ExtensionFilter PNG_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PNG_DESCRIPTION, FileExtensions.PNG_EXTENSIONS );
    public static final ExtensionFilter PNM_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PNM_DESCRIPTION, FileExtensions.PNM_EXTENSIONS );
    public static final ExtensionFilter PPT_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PPT_DESCRIPTION, FileExtensions.PPT_EXTENSIONS );
    public static final ExtensionFilter PPTX_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PPTX_DESCRIPTION, FileExtensions.PPTX_EXTENSIONS );
    public static final ExtensionFilter PRESENTATION_EXTENSION_FILTER    = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PRESENTATION_DESCRIPTION,
                                 FileExtensions.PRESENTATION_EXTENSIONS );
    public static final ExtensionFilter RASTER_IMAGE_EXTENSION_FILTER    = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.RASTER_IMAGE_DESCRIPTION,
                                 FileExtensions.RASTER_IMAGE_EXTENSIONS );
    public static final ExtensionFilter SPREADSHEET_EXTENSION_FILTER     = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.SPREADSHEET_DESCRIPTION,
                                 FileExtensions.SPREADSHEET_EXTENSIONS );
    public static final ExtensionFilter PS_EXTENSION_FILTER              = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.PS_DESCRIPTION, FileExtensions.PS_EXTENSIONS );
    public static final ExtensionFilter SVG_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.SVG_DESCRIPTION, FileExtensions.SVG_EXTENSIONS );
    public static final ExtensionFilter TIFF_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.TIFF_DESCRIPTION, FileExtensions.TIFF_EXTENSIONS );
    public static final ExtensionFilter TXT_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.TXT_DESCRIPTION, FileExtensions.TXT_EXTENSIONS );
    public static final ExtensionFilter VECTOR_GRAPHICS_EXTENSION_FILTER = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.VECTOR_GRAPHICS_DESCRIPTION,
                                 FileExtensions.VECTOR_GRAPHICS_EXTENSIONS );
    public static final ExtensionFilter WBMP_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.WBMP_DESCRIPTION, FileExtensions.WBMP_EXTENSIONS );
    public static final ExtensionFilter XLS_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.XLS_DESCRIPTION, FileExtensions.XLS_EXTENSIONS );
    public static final ExtensionFilter XLSX_EXTENSION_FILTER            = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.XLSX_DESCRIPTION, FileExtensions.XLSX_EXTENSIONS );
    public static final ExtensionFilter XML_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.XML_DESCRIPTION, FileExtensions.XML_EXTENSIONS );
    public static final ExtensionFilter ZIP_EXTENSION_FILTER             = ExtensionFilterUtilities
            .getExtensionFilter( FileExtensions.ZIP_DESCRIPTION, FileExtensions.ZIP_EXTENSIONS );

}
