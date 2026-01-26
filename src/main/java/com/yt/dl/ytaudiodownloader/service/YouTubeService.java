package com.yt.dl.ytaudiodownloader.service;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.yt.dl.ytaudiodownloader.client.YouTubeClient;
import com.yt.dl.ytaudiodownloader.dto.PlaylistItem;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service class handling interaction with YouTube Data API. */
@Service
@RequiredArgsConstructor
public class YouTubeService {

  private static final String INVALID_URL = "Invalid playlist URL!";
  private static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";
  private final YouTubeClient client;

  /**
   * Returns URL for each video in provided YouTube playlist.
   *
   * @param playlistUrl YouTube playlist URL
   * @return {@code List} of YouTube video URLs
   * @throws GeneralSecurityException if an authorization error occurs
   * @throws IOException if an I/O error occurs
   */
  public List<PlaylistItem> getVideoDetailsFromPlaylist(String playlistUrl)
      throws GeneralSecurityException, IOException {
    String playlistId = getPlaylistId(playlistUrl);
    PlaylistItemListResponse response = new PlaylistItemListResponse();
    List<PlaylistItem> playlistItems = new ArrayList<>();
    do {
      response =
          client
              .getService()
              .playlistItems()
              .list("snippet,contentDetails")
              .setPlaylistId(playlistId)
              .setMaxResults(50L)
              .setPageToken(response.getNextPageToken())
              .execute();
      playlistItems.addAll(
          response.getItems().stream()
              .map(
                  x ->
                      new PlaylistItem(
                          x.getSnippet().getTitle(),
                          YOUTUBE_VIDEO_BASE_URL.concat(
                              String.valueOf(x.getContentDetails().get("videoId")))))
              .toList());
    } while (Objects.nonNull(response.getNextPageToken()));

    return playlistItems;
  }

  /**
   * Returns YouTube playlist ID from provided URL.
   *
   * @param playlistUrl YouTube playlist URL
   * @return YouTube playlist ID
   * @throws IllegalArgumentException if no playlist ID could be found
   */
  private String getPlaylistId(String playlistUrl) {
    URI uri = URI.create(playlistUrl);
    String queryString = uri.getQuery();
    if (Objects.isNull(queryString)) {
      throw new IllegalArgumentException(INVALID_URL);
    }

    String[] params = queryString.split("&");
    for (String param : params) {
      String[] keyValuePair = param.split("=");
      if (keyValuePair.length == 2 && keyValuePair[0].equals("list")) {
        return keyValuePair[1];
      }
    }

    throw new IllegalArgumentException(INVALID_URL);
  }
}
