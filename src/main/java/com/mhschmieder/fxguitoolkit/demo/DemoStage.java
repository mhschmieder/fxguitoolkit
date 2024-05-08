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
package com.mhschmieder.fxguitoolkit.demo;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxguitoolkit.stage.MainApplicationStage;

import javafx.scene.Node;

/**
 * A bare bones demo stage just to have a non-abstract stage available for
 * the demo application class to launch.
 */
public class DemoStage extends MainApplicationStage {

    protected DemoStage( String title,
                         String windowKeyPrefix,
                         String jarRelativeSplashScreenFileName,
                         boolean supportsRenderedGraphicsExport,
                         ProductBranding productBranding,
                         ClientProperties pClientProperties ) {
        super( title,
               windowKeyPrefix,
               jarRelativeSplashScreenFileName,
               supportsRenderedGraphicsExport,
               productBranding,
               pClientProperties );
        // NOTE Auto-generated constructor stub
    }

    @Override
    protected Node loadContent() {
        // NOTE Auto-generated method stub
        return null;
    }

    @Override
    public void open( String filePath ) {
        // NOTE Auto-generated method stub
    }

    @Override
    protected void resetSessionContext() {
        // NOTE Auto-generated method stub
    }

    @Override
    protected void setDefaultProject( boolean applicationInitMode ) {
        // NOTE Auto-generated method stub
    }

    public void initStage( boolean b ) {
        // NOTE Auto-generated method stub
    }
}
