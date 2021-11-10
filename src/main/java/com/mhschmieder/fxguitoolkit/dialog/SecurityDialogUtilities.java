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

import java.util.Locale;
import java.util.Optional;

import com.mhschmieder.commonstoolkit.concurrent.AuthorizationRequestService;
import com.mhschmieder.commonstoolkit.security.LoginCredentials;
import com.mhschmieder.commonstoolkit.security.LoginType;
import com.mhschmieder.fxguitoolkit.MessageFactory;

import javafx.util.Callback;
import javafx.util.Pair;

/**
 * This is a utility class for showing dialogs related to security features such
 * as proxies and standard logins.
 */
public final class SecurityDialogUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SecurityDialogUtilities() {}

    public static boolean showLoginDialog( final LoginType loginType,
                                           final LoginCredentials loginCredentials,
                                           final String productName,
                                           final Locale locale,
                                           final AuthorizationRequestService authorizationRequestService,
                                           final Callback< Pair< String, String >, Void > authenticator ) {
        // Block on the Login, passing in any cached Login Credentials.
        final String title = MessageFactory.getLoginCredentialsTitle( loginType );
        final String headerText = MessageFactory.getLoginCredentialsMasthead( loginType,
                                                                              productName );
        final Pair< String, String > initialUserInfo = loginCredentials.getLogin();

        final XLoginDialog loginDialog = new XLoginDialog( title,
                                                           headerText,
                                                           locale,
                                                           initialUserInfo,
                                                           authenticator );
        if ( authorizationRequestService != null ) {
            authorizationRequestService.setLoginDialog( loginDialog );
        }
        final Optional< Pair< String, String > > loginCredentialsResult = loginDialog.showAndWait();

        // Cache the new Login Credentials, unless the user canceled or didn't
        // supply non-empty strings for one or the other field.
        loginCredentialsResult.ifPresent( loginCredentialsCandidate -> {
            loginCredentials.setLogin( loginCredentialsCandidate );
        } );

        // NOTE: We distinguish between a user canceling a Login and a user
        // typing in invalid input, as we need different post-processing after
        // we exit the Login Dialog, to avoid incorrect status messages.
        return loginCredentialsResult.isPresent() && loginCredentials.isValid();
    }

    // TODO: Embed this instead in the LoginDialog pop-up window, in red.
    public static void showLoginWarningDialog( final String message ) {
        final String masthead = MessageFactory.getLoginErrorMasthead();
        final String title = MessageFactory.getLoginErrorTitle();
        DialogUtilities.showWarningAlert( message, masthead, title );
    }

}
