package com.yt.dl.ytaudiodownloader.service;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.yt.dl.ytaudiodownloader.client.YouTubeClient;
import com.yt.dl.ytaudiodownloader.dto.YouTubeVideo;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service class handling interaction with YouTube Data API. */
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeService {

  private static final String INVALID_URL = "Invalid playlist URL!";
  private static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";

  private final YouTubeClient youTubeClient;

  /**
   * Returns {@code List} of {@link YouTubeVideo} containing metadata for each video in the YouTube
   * playlist.
   *
   * @param playlistUrl YouTube playlist URL
   * @return {@code List} of {@link YouTubeVideo}
   * @throws IOException if an I/O error occurs
   */
  public List<YouTubeVideo> getVideoDetailsFromPlaylist(String playlistUrl) throws IOException {
    String playlistId = getPlaylistId(playlistUrl);
    PlaylistItemListResponse response = new PlaylistItemListResponse();
    List<YouTubeVideo> youTubeVideos = new ArrayList<>();
    do {
      response =
          youTubeClient
              .getInstance()
              .playlistItems()
              .list("snippet,contentDetails")
              .setPlaylistId(playlistId)
              .setMaxResults(50L)
              .setPageToken(response.getNextPageToken())
              .execute();
      youTubeVideos.addAll(
          response.getItems().stream()
              .map(
                  x ->
                      new YouTubeVideo(
                          x.getSnippet().getTitle(),
                          YOUTUBE_VIDEO_BASE_URL.concat(
                              String.valueOf(x.getContentDetails().get("videoId"))),
                          x.getId()))
              .toList());
    } while (Objects.nonNull(response.getNextPageToken()));

    return youTubeVideos;
  }

  /**
   * Removes provided list of YouTube videos from the playlist.
   *
   * @param youTubeVideos {@code Set} of {@link YouTubeVideo}
   * @throws IOException if an I/O error occurs
   */
  public void removeFromPlaylist(Set<YouTubeVideo> youTubeVideos) throws IOException {
    log.info("Removing downloaded videos from the playlist.");
    for (YouTubeVideo youTubeVideo : youTubeVideos) {
      youTubeClient.getInstance().playlistItems().delete(youTubeVideo.id()).execute();
    }
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
