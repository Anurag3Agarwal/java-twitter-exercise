package com.ing.javaexercise.authentication;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.ing.javaexercise.exception.AuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Authenticator {

  private static final Logger logger = LoggerFactory.getLogger(Authenticator.class);

  @Value("${consumerKey}")
  private String consumerKey;

  @Value("${consumerSecret}")
  private String consumerSecret;

  private HttpRequestFactory factory;

  private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
  private static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
  private static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";

  /**
   * Lazily initialize an HTTP request factory which embeds the OAuth tokens required by the Twitter
   * APIs
   *
   * @return The authenticated HTTP request factory
   */
  public synchronized HttpRequestFactory getAuthorizedHttpRequestFactory()
      throws AuthenticationException {
//    if (factory != null) {
//      return factory;
//    }

    factory = createRequestFactory();
    return factory;
  }

  /**
   * Create a new authenticated HTTP request factory which embeds the OAuth tokens required by the
   * Twitter APIs
   *
   * @return The authenticated HTTP request factory
   */
  private HttpRequestFactory createRequestFactory() throws AuthenticationException {
    OAuthHmacSigner signer = new OAuthHmacSigner();
    signer.clientSharedSecret = consumerSecret;

    OAuthCredentialsResponse requestTokenResponse = getTemporaryToken(signer);
    signer.tokenSharedSecret = requestTokenResponse.tokenSecret;

    OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(
        AUTHORIZE_URL);
    authorizeUrl.temporaryToken = requestTokenResponse.token;

    String providedPin = retrievePin(authorizeUrl);

    OAuthCredentialsResponse accessTokenResponse = retrieveAccessTokens(providedPin, signer,
        requestTokenResponse.token);
    signer.tokenSharedSecret = accessTokenResponse.tokenSecret;

    OAuthParameters parameters = new OAuthParameters();
    parameters.consumerKey = consumerKey;
    parameters.token = accessTokenResponse.token;
    parameters.signer = signer;

    return TRANSPORT.createRequestFactory(parameters);
  }

  /**
   * Retrieve the initial temporary tokens required to obtain the acces token
   *
   * @param signer The HMAC signer used to cryptographically sign requests to Twitter
   * @return The response containing the temporary tokens
   */
  private OAuthCredentialsResponse getTemporaryToken(final OAuthHmacSigner signer)
      throws AuthenticationException {
    OAuthGetTemporaryToken requestToken = new OAuthGetTemporaryToken(REQUEST_TOKEN_URL);
    requestToken.consumerKey = consumerKey;
    requestToken.transport = TRANSPORT;
    requestToken.signer = signer;

    OAuthCredentialsResponse requestTokenResponse;
    try {
      requestTokenResponse = requestToken.execute();
    } catch (IOException e) {
      throw new AuthenticationException("Unable to aquire temporary token: " + e.getMessage(), e);
    }

    logger.info("Aquired temporary token...");
    return requestTokenResponse;
  }

  /**
   * Guide the user to obtain a PIN from twitter to authorize the requests
   *
   * @param authorizeUrl The URL embedding the temporary tokens to be used to request the PIN
   * @return The PIN as it is entered by the user after following the Twitter OAuth wizard
   */
  private String retrievePin(final OAuthAuthorizeTemporaryTokenUrl authorizeUrl)
      throws AuthenticationException {
    String providedPin = null;


   // Scanner scanner = new Scanner(System.in);
    //scanner.useDelimiter(System.lineSeparator());
    try {
      BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Go to the following link in your browser:" + authorizeUrl.build());
      System.out.println("Please enter the retrieved PIN:");
      providedPin = buffReader.readLine();
    }
    catch (IOException exc){
      System.out.println(exc);
    }

    if (providedPin == null) {
      throw new AuthenticationException("Unable to read entered PIN");
    }

    return providedPin;
  }

  /**
   * Exchange the temporary token and the PIN for an access token that can be used to invoke Twitter
   * APIs
   *
   * @param providedPin The PIN that the user obtained when following the Twitter OAuth wizard
   * @param signer The HMAC signer used to cryptographically sign requests to Twitter
   * @param token The temporary token to be exchanged for the access token
   * @return The access token that can be used to invoke Twitter APIs
   */
  private OAuthCredentialsResponse retrieveAccessTokens(final String providedPin,
      final OAuthHmacSigner signer, final String token) throws AuthenticationException {
    OAuthGetAccessToken accessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
    accessToken.verifier = providedPin;
    accessToken.consumerKey = consumerSecret;
    accessToken.signer = signer;
    accessToken.transport = TRANSPORT;
    accessToken.temporaryToken = token;

    OAuthCredentialsResponse accessTokenResponse;
    try {
      accessTokenResponse = accessToken.execute();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new AuthenticationException("Unable to authorize access: " + e.getMessage(), e);
    }

    logger.info("\nAuthorization was successful");
    return accessTokenResponse;
  }

}
