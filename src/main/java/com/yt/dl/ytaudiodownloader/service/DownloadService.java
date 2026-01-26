package com.yt.dl.ytaudiodownloader.service;

import com.yt.dl.ytaudiodownloader.dto.ApiKey;
import com.yt.dl.ytaudiodownloader.dto.ConvertPayload;
import com.yt.dl.ytaudiodownloader.dto.ConvertResponse;
import com.yt.dl.ytaudiodownloader.dto.PlaylistItem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Service class responsible for executing HTTP requests to external APIs and downloading files. */
@Slf4j
@Service
public class DownloadService {

  private static final String AUTH_TOKEN_ENDPOINT = "https://cnv.cx/v2/sanity/key";
  private static final String CONVERT_ENDPOINT = "https://cnv.cx/v2/converter";
  private static final String AUTH_HEADER = "key";
  private static final String OUTPUT_FOLDER = "download";
  private static final String MP3_EXTENSION = ".mp3";
  private static final Integer MAX_RETRIES = 5;
  private static final RestClient restClient =
      RestClient.builder().defaultHeader("Origin", "https://frame.y2meta-uk.com").build();

  /**
   * Downloads audio file from each video in the playlist.
   *
   * @param playlistItems {@code List} of {@link PlaylistItem}
   * @throws ExecutionException if any download task fails
   * @throws InterruptedException if the current thread is interrupted while waiting for all tasks
   *     to complete
   */
  public void download(List<PlaylistItem> playlistItems)
      throws ExecutionException, InterruptedException {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      List<Future<?>> futures = new ArrayList<>();
      for (PlaylistItem item : playlistItems) {
        futures.add(
            executor.submit(
                () -> {
                  try {
                    download(item);
                  } catch (Exception e) {
                    log.error("Unexpected error occurred while downloading '{}'!", item.title(), e);
                  }
                }));
      }

      for (Future<?> future : futures) {
        future.get();
      }
    }
  }

  /**
   * Downloads an audio file from a single URL.
   *
   * <p>This method converts YouTube video to mp3 file through external API and saves the file to
   * local filesystem. It will retry up to {@code MAX_RETRIES} times if a {@link
   * RestClientException} occurs.
   *
   * @param item {@link PlaylistItem} containing YouTube video title and URL
   */
  private void download(PlaylistItem item) {
    log.debug("Starting download process for '{}'.", item.title());
    int attempt = 0;
    while (true) {
      attempt++;
      RestClient restClientWithAuth = getRestClientWithAuth();
      ConvertPayload payload = buildPayload(item.url());

      try {
        ConvertResponse convertResponse =
            restClientWithAuth
                .post()
                .uri(CONVERT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(ConvertResponse.class);

        if (Objects.isNull(convertResponse)) {
          throw new RestClientException(
              String.format("Unexpected error while converting '%s'!", item.title()));
        }
        downloadFile(convertResponse.url(), item.title());
        return;
      } catch (RestClientException e) {
        if (attempt <= MAX_RETRIES) {
          log.debug(
              "Retrying download for '{}' - [{}/{}] attempts.", item.title(), attempt, MAX_RETRIES);
          continue;
        }
        throw e;
      }
    }
  }

  /**
   * Obtains one-time API key and returns {@link RestClient} with auth header.
   *
   * @return an authenticated {@link RestClient} configured with API key
   */
  private RestClient getRestClientWithAuth() {
    ApiKey apiKey = restClient.get().uri(AUTH_TOKEN_ENDPOINT).retrieve().body(ApiKey.class);
    if (Objects.isNull(apiKey)) {
      throw new RestClientException("Could not retrieve API key!");
    }
    return restClient.mutate().defaultHeader(AUTH_HEADER, apiKey.key()).build();
  }

  /**
   * Builds and returns request payload.
   *
   * @param url YouTube video URL
   * @return {@link ConvertPayload} object
   */
  private ConvertPayload buildPayload(String url) {
    return new ConvertPayload(url, "mp3", "320", "pretty", "h264");
  }

  /**
   * Downloads audio file to local filesystem. Creates output directory if it does not exist.
   *
   * @param url YouTube video URL
   * @param videoTitle YouTube video title
   */
  private void downloadFile(String url, String videoTitle) {
    try {
      Path outputPath = Files.createDirectories(Path.of(OUTPUT_FOLDER));
      Path targetFile = outputPath.resolve(videoTitle + MP3_EXTENSION);
      try (InputStream in = URI.create(url).toURL().openStream()) {
        Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
      }
      log.info("Finished downloading '{}'.", videoTitle);
    } catch (IOException e) {
      log.error("Failed to download '{}' to local filesystem!", videoTitle, e);
    }
  }
}
