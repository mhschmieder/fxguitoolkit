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
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxguitoolkit.dialog;

import java.util.Locale;

import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;

import impl.org.controlsfx.i18n.Localization;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * This is a heavily customized Login Dialog that is loosely based upon the
 * example in ControlsFX.
 */
public final class XLoginDialog extends Dialog< Pair< String, String > > {

    protected final class XLoginDialogPane extends DialogPane {

        /** Cache the locale, to use for button text lookup */
        protected final Locale locale;

        /**
         * The sole purpose of this constructor is to cache an accessible copy
         * of the dialog owner, for method overrides.
         *
         * @param pLocale
         *            The Locale to use for text localization
         */
        public XLoginDialogPane( final Locale pLocale ) {
            // Always call the superclass constructor first!
            super();

            locale = pLocale;
        }

        /**
         * This method can be overridden by subclasses to create a custom button
         * that will subsequently be inserted into the DialogPane button area
         * (created via the {@link #createButtonBar()} method, but mostly
         * commonly it is an instance of {@link ButtonBar}.
         *
         * @param buttonType
         *            The {@link ButtonType} to create a button from.
         * @return A JavaFX {@link Node} that represents the given
         *         {@link ButtonType},
         *         most commonly an instance of {@link Button}.
         */
        @SuppressWarnings("nls")
        @Override
        protected Node createButton( final ButtonType buttonType ) {
            // NOTE: We override this strictly to avoid the default button from
            // closing the dialog, as the server authorization runs on a
            // separate thread and thus needs to control the dialog dismissal.
            final ButtonData buttonData = buttonType.getButtonData();
            if ( !ButtonData.OK_DONE.equals( buttonData ) ) {
                return super.createButton( buttonType );
            }

            // Set the Login Button Type, replacing the default English text
            // from ControlsFX as it is grammatically incorrect ("Log In" is two
            // words).
            final String loginButtonText =
                                         Locale.ENGLISH.getLanguage().equals( locale.getLanguage() )
                                             ? "Log In"
                                             : Localization.getString( "login.dlg.login.button" );

            // If there is no authenticator set, we need to let ControlsFX take
            // care of the button handling or else the dialog never gets
            // dismissed, but we still need to replace the default button text.
            if ( authenticator == null ) {
                final Button loginButton = ( Button ) super.createButton( buttonType );
                loginButton.setText( loginButtonText );
                return loginButton;
            }

            final Button button = new Button( loginButtonText );
            ButtonBar.setButtonData( button, buttonData );
            button.setDefaultButton( buttonData.isDefaultButton() );
            button.setCancelButton( buttonData.isCancelButton() );

            return button;
        }

    }

    protected final String                             REQUIRED_FORMAT = "'%s' is required"; //$NON-NLS-1$

    protected Label                                    errorMessage;
    protected CustomTextField                          userNameField;
    protected CustomPasswordField                      passwordField;

    // The Authenticator is needed for determining correct handling of the
    // OK/Login Button, and whether to invoke a Callback or auto-exit.
    protected Callback< Pair< String, String >, Void > authenticator;

    public XLoginDialog( final String pTitle,
                         final String pHeaderText,
                         final Locale pLocale,
                         final Pair< String, String > pInitialUserInfo,
                         final Callback< Pair< String, String >, Void > pAuthenticator ) {
        // Always call the superclass constructor first!
        super();

        // Force application-blocking modality.
        initModality( Modality.APPLICATION_MODAL );

        // Cache the Authenticator, even if null.
        authenticator = pAuthenticator;

        try {
            initDialog( pTitle, pHeaderText, pLocale, pInitialUserInfo );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * This method commits the user's edits, by setting the result and hiding
     * the dialog. This compensates for overriding the OK Button's callback
     * behavior, so that we can invoke the equivalent sequence here, from the
     * authorization service, once it has a valid server response.
     */
    public void commit() {
        final Pair< String, String > loginInformation = getLoginInformation();
        setResult( loginInformation );
        hide();
    }

    public Pair< String, String > getLoginInformation() {
        final String userName = getUserName();
        final String password = getPassword();
        final Pair< String, String > loginInformation = new Pair<>( userName, password );
        return loginInformation;
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getUserName() {
        return userNameField.getText();
    }

    @SuppressWarnings("nls")
    private void initDialog( final String title,
                             final String headerText,
                             final Locale locale,
                             final Pair< String, String > initialUserInfo ) {
        setTitle( title );

        final DialogPane dialogPane = new XLoginDialogPane( locale );
        setDialogPane( dialogPane );

        // TODO: Pull in this missing image resource after deciding where it
        // goes in the file system such that it works in IntelliJ and Eclipse.
        final Background background = LayoutFactory.makeRegionBackground( ColorConstants.GRAY86 );
        final ImageView loginIcon = new ImageView( XLoginDialog.class
                .getResource( "/icons/everaldo/Password48.png" ).toExternalForm() );

        dialogPane.setHeaderText( headerText );
        dialogPane.setBackground( background );
        dialogPane.setGraphic( loginIcon );

        // Make the main Login content pane elements.
        final ImageView userIcon = new ImageView( XLoginDialog.class
                .getResource( "/org/controlsfx/dialog/user.png" ).toExternalForm() );
        userNameField = ( CustomTextField ) TextFields.createClearableTextField();
        userNameField.setEditable( true );
        userNameField.setLeft( userIcon );
        userNameField.setPrefWidth( 300d );

        final ImageView lockIcon = new ImageView( XLoginDialog.class
                .getResource( "/org/controlsfx/dialog/lock.png" ).toExternalForm() );
        passwordField = ( CustomPasswordField ) TextFields.createClearablePasswordField();
        passwordField.setEditable( true );
        passwordField.setLeft( lockIcon );
        passwordField.setPrefWidth( 300d );

        // Make a placeholder label to hold an error message.
        errorMessage = new Label();
        errorMessage.getStyleClass().addAll( "message-banner" );
        errorMessage.setAlignment( Pos.CENTER );
        errorMessage.setVisible( false );
        errorMessage.setManaged( false );

        final VBox content = new VBox( 10.0d );
        content.getChildren().addAll( errorMessage, userNameField, passwordField );
        dialogPane.setContent( content );

        // Set the Login Button Types, depending on our override of the Dialog
        // Pane to take care of custom text for the OK Button.
        dialogPane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

        // Enable/Disable Login Button depending on whether a non-empty User
        // Name and/or Password was entered.
        final Button loginButton = ( Button ) dialogPane.lookupButton( ButtonType.OK );
        loginButton.setDisable( true );

        loginButton.setOnAction( actionEvent -> {
            try {
                if ( authenticator != null ) {
                    final Pair< String, String > loginInformation = getLoginInformation();
                    authenticator.call( loginInformation );
                }

                errorMessage.setVisible( false );
                errorMessage.setManaged( false );

                // NOTE: The authorization is run on a separate thread, so we
                // need for it to be in full control of this window hiding.
                // hide();
            }
            catch ( final Throwable throwable ) {
                errorMessage.setVisible( true );
                errorMessage.setManaged( true );
                errorMessage.setText( throwable.getMessage() );

                throwable.printStackTrace();
            }
        } );

        final String userNameCaption = Localization.getString( "login.dlg.user.caption" );
        userNameField.setPromptText( userNameCaption );

        final String passwordCaption = Localization.getString( "login.dlg.pswd.caption" );
        passwordField.setPromptText( passwordCaption );

        setLoginInformation( initialUserInfo );

        final ValidationSupport validationSupport = new ValidationSupport();
        Platform.runLater( () -> {
            validationSupport
                    .registerValidator( userNameField,
                                        Validator.createEmptyValidator( String
                                                .format( REQUIRED_FORMAT, userNameCaption ) ) );
            validationSupport
                    .registerValidator( passwordField,
                                        Validator.createEmptyValidator( String
                                                .format( REQUIRED_FORMAT, passwordCaption ) ) );

            // NOTE: The API changed so now we have to do manual binding.
            // loginButton.disabledProperty().bind(validationSupport.invalidProperty());

            // Request immediate focus on the User Name field by default.
            userNameField.requestFocus();
        } );

        // Do some User Name validation to ensure a non-empty value.
        userNameField.textProperty().addListener( ( observable, oldValue, newValue ) -> {
            final boolean loginValid =
                                     !newValue.trim().isEmpty() && !getPassword().trim().isEmpty();
            loginButton.setDisable( !loginValid );
        } );

        // Do some Password validation to ensure a non-empty value.
        passwordField.textProperty().addListener( ( observable, oldValue, newValue ) -> {
            final boolean loginValid =
                                     !newValue.trim().isEmpty() && !getUserName().trim().isEmpty();
            loginButton.setDisable( !loginValid );
        } );

        // Convert the result to a User Name and Password Pair when the OK
        // Button is clicked. We make this agnostic towards the displayed text
        // on the button, as well as fail-safe against nulls.
        setResultConverter( dialogButton -> {
            final ButtonData buttonData =
                                        dialogButton == null ? null : dialogButton.getButtonData();
            return ButtonData.OK_DONE.equals( buttonData ) ? getLoginInformation() : null;
        } );
    }

    @SuppressWarnings("nls")
    public void setLoginInformation( final Pair< String, String > loginInformation ) {
        final String userName = loginInformation == null ? "" : loginInformation.getKey();
        setUserName( userName );

        final String password = loginInformation == null ? "" : loginInformation.getValue();
        setPassword( password );
    }

    public void setPassword( final String password ) {
        passwordField.setText( password );
    }

    public void setUserName( final String userName ) {
        userNameField.setText( userName );
    }

}
