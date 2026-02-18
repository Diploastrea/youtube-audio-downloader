package com.yt.dl.ytaudiodownloader.cli;

import com.yt.dl.ytaudiodownloader.dto.YouTubeVideo;
import com.yt.dl.ytaudiodownloader.service.DownloadService;
import com.yt.dl.ytaudiodownloader.service.YouTubeService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CliRunner implements ApplicationRunner {

  private final YouTubeService youTubeService;
  private final DownloadService downloadService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    List<String> playlistOption = args.getOptionValues("playlist");
    if (Objects.isNull(playlistOption) || playlistOption.isEmpty()) {
      throw new IllegalArgumentException("Missing required option --playlist!");
    }

    List<YouTubeVideo> youTubeVideos =
        youTubeService.getVideoDetailsFromPlaylist(playlistOption.getFirst());
    Set<YouTubeVideo> downloaded = downloadService.download(youTubeVideos);

    if (args.containsOption("remove")) {
      youTubeService.removeFromPlaylist(downloaded);
    }
  }
}
