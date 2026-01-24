package com.yt.dl.ytaudiodownloader.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * This class handles application OAuth 2.0 authorization and creating authenticated connection to
 * the YouTube Data API.
 */
@Component
public class YouTubeClient {

  private static final String CLIENT_SECRETS = "client_secret.json";
  private static final Collection<String> SCOPES =
      List.of("https://www.googleapis.com/auth/youtube.readonly");
  private static final String APPLICATION_NAME = "YouTube audio downloader";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /**
   * Creates an authorized Credential object.
   *
   * @param httpTransport HTTP client
   * @return an authorized Credential object
   * @throws IOException if an I/O error occurs
   */
  private static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
    InputStream in = YouTubeClient.class.getClassLoader().getResourceAsStream(CLIENT_SECRETS);
    if (Objects.isNull(in)) {
      throw new FileNotFoundException(String.format("Resource file %s not found!", CLIENT_SECRETS));
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Builds and returns an authorized API client service.
   *
   * @return an authorized API client service
   * @throws GeneralSecurityException if an authorization error occurs
   * @throws IOException if an I/O error occurs
   */
  public YouTube getService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    Credential credential = authorize(httpTransport);
    return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }
}
