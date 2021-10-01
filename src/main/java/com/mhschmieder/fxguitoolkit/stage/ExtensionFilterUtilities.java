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
package com.mhschmieder.fxguitoolkit.stage;

import com.mhschmieder.iotoolkit.io.FileExtensions;

import java.util.List;
import java.util.Vector;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * {@code ExtensionFilterUtilities} is a static utilities class for generating
 * file extension filters that can be used by a JavaFX File Chooser.
 * <p>
 * :NOTE: This code is copied from GraphicsToolkit on GitHub until it is
 * published
 * to Maven Central from where it can be pulled via Gradle.
 */
public final class ExtensionFilterUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ExtensionFilterUtilities() {}

    public static ExtensionFilter getExtensionFilter( final String description,
                                                      final String extension ) {
        return new ExtensionFilter( description, extension );
    }

    public static ExtensionFilter getExtensionFilter( final String description,
                                                      final String[] extensions ) {
        return new ExtensionFilter( description, extensions );
    }

    public static List< ExtensionFilter > getCsvExtendedExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.CSV_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.ZIP_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getCsvExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.CSV_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getDxfExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.DXF_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getHtmlExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.HTML_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getImageGraphicsExtensionFilters() {
        // :NOTE: TIFF requires either ImageIO-Ext or JAI 1.1.3 JAR's.
        // Both are quite large (especially the former, which also has
        // JNI support that might not include the Mac, but otherwise is
        // a more direct analog to how we do other formats currently vs.
        // the different JAI approach). Better to wait until the switch
        // to JavaFX, which has its own imaging API's.
        // :NOTE: WBMP isn't necessary anymore as most people's wireless
        // devices can now handle the bandwidth of full color images, and it
        // benefits from us doing the down-conversion to black and white.
        // :NOTE: PostScript requires Swing-based printer services so is now
        // disabled due to removing Swing and AWT dependencies.
        // :NOTE: A brief experiment with PNM failed to produce output, but no
        // real time was spent on looking into what happened or changing type.
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.IMAGE_GRAPHICS_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.PNG_EXTENSION_FILTER );
        // extensionFilterAdditions.add( ExtensionFilters.PNM_EXTENSION_FILTER
        // );
        // extensionFilterAdditions.add( ExtensionFilters.TIFF_EXTENSION_FILTER
        // );
        extensionFilterAdditions.add( ExtensionFilters.GIF_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.JPEG_EXTENSION_FILTER );
        // extensionFilterAdditions.add( ExtensionFilters.PS_EXTENSION_FILTER );
        // extensionFilterAdditions
        // .add( ExtensionFilters.WBMP_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.BMP_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getLogExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.LOG_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getPngExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.PNG_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getPptxExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.PPTX_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getPresentationExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.PRESENTATION_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.PPTX_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.PPT_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getRasterImageExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions
                .add( new ExtensionFilter( FileExtensions.RASTER_IMAGE_DESCRIPTION,
                                           FileExtensions.RASTER_IMAGE_EXTENSIONS ) );
        extensionFilterAdditions.add( new ExtensionFilter( FileExtensions.PNG_DESCRIPTION,
                                                           FileExtensions.PNG_EXTENSIONS ) );
        extensionFilterAdditions.add( new ExtensionFilter( FileExtensions.GIF_DESCRIPTION,
                                                           FileExtensions.GIF_EXTENSIONS ) );
        extensionFilterAdditions.add( new ExtensionFilter( FileExtensions.JPEG_DESCRIPTION,
                                                           FileExtensions.JPEG_EXTENSIONS ) );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getSpreadsheetExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.SPREADSHEET_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.XLSX_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.XLS_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getSvgExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.SVG_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getTxtExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.TXT_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getVectorGraphicsExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        // :NOTE: The jfxConverter library is hard-wired to work with Apache
        // POI's legacy support vs. the standard XML versions of PowerPoint
        // files. It is a lot of work to revise their drivers to accept PPTX.
        extensionFilterAdditions.add( ExtensionFilters.VECTOR_GRAPHICS_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.EPS_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.PDF_EXTENSION_FILTER );
        extensionFilterAdditions.add( ExtensionFilters.SVG_EXTENSION_FILTER );
        // extensionFilterAdditions.add( ExtensionFilters.PPTX_EXTENSION_FILTER
        // );
        extensionFilterAdditions.add( ExtensionFilters.PPT_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getXlsxExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.XLSX_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getXmlExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.XML_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

    public static List< ExtensionFilter > getZipExtensionFilters() {
        final Vector< ExtensionFilter > extensionFilterAdditions = new Vector<>();

        extensionFilterAdditions.add( ExtensionFilters.ZIP_EXTENSION_FILTER );

        return extensionFilterAdditions;
    }

}
