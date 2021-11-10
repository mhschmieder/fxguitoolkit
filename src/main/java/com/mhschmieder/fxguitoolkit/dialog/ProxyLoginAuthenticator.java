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
package com.mhschmieder.fxguitoolkit.dialog;

import java.net.PasswordAuthentication;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.net.ProxyAuthenticator;
import com.mhschmieder.commonstoolkit.security.LoginCredentials;
import com.mhschmieder.commonstoolkit.security.LoginType;
import com.mhschmieder.commonstoolkit.util.ClientProperties;

import javafx.application.Platform;

public final class ProxyLoginAuthenticator extends ProxyAuthenticator {

    public static final String PROXY_LOGIN_CONTEXT = "Proxy"; //$NON-NLS-1$

    /** Declare a structure to hold the most recent Proxy Login credentials. */
    public LoginCredentials    _proxyLoginCredentials;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties    clientProperties;

    /** Cache a reference to the product branding information. */
    public ProductBranding     _productBranding;

    public ProxyLoginAuthenticator( final ClientProperties pClientProperties,
                                    final ProductBranding productBranding ) {
        // Always call the superclass constructor first!
        super();

        clientProperties = pClientProperties;
        _productBranding = productBranding;

        _proxyLoginCredentials = new LoginCredentials();
    }

    // Force the user to log in to the active proxy if they are not already
    // authorized.
    @Override
    public void requestProxyCredentials() {
        // Display the modal Proxy Login Dialog until the user dismisses it.
        // TODO: Reuse the RequestorType enum (java.net) vs. the LoginType?
        // TODO: Make a StringUtilities method to take an enum value and
        // make it all lower-case except the first letter; search Apache!
        // This would normally be thought of as Headline Capitalization.
        // NOTE: I may have already written such a method myself earlier.
        // TODO: Check whether we're already on the FX Application thread?
        Platform.runLater( () -> {
            final boolean loginCredentialsCaptured = SecurityDialogUtilities
                    .showLoginDialog( LoginType.PROXY,
                                      _proxyLoginCredentials,
                                      _productBranding.productName,
                                      clientProperties.locale,
                                      null, // authorizationRequestService
                                      null ); // authenticator );

            // Convert (forward) Login Credentials to a Proxy Authenticator.
            if ( loginCredentialsCaptured ) {
                _passwordAuthentication = new PasswordAuthentication( _proxyLoginCredentials
                        .getUserName(), _proxyLoginCredentials.getPassword().toCharArray() );
            }
        } );
    }

}
