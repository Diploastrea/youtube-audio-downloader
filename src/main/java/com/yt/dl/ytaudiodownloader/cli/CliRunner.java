package com.yt.dl.ytaudiodownloader.cli;

import com.yt.dl.ytaudiodownloader.config.ApplicationConfiguration;
import com.yt.dl.ytaudiodownloader.dto.YouTubeVideo;
import com.yt.dl.ytaudiodownloader.service.DownloadService;
import com.yt.dl.ytaudiodownloader.service.YouTubeService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Entry point for the YouTube audio downloader application. */
@Slf4j
@Component
@RequiredArgsConstructor
public class CliRunner implements ApplicationRunner {

  private final ApplicationConfiguration config;
  private final YouTubeService youTubeService;
  private final DownloadService downloadService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String playlistUrl = config.playlist();
    if (Objects.isNull(playlistUrl) || playlistUrl.isEmpty()) {
      throw new IllegalArgumentException("Missing required config option app.config.playlist!");
    }

    List<YouTubeVideo> youTubeVideos = youTubeService.getVideoDetailsFromPlaylist(playlistUrl);
    Set<YouTubeVideo> downloaded = downloadService.download(youTubeVideos);

    if (config.removeAfterDownload()) {
      youTubeService.removeFromPlaylist(downloaded);
    }
    log.info("Successfully finished downloading files.");
  }
}
