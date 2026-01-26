package com.yt.dl.ytaudiodownloader;

import com.yt.dl.ytaudiodownloader.dto.PlaylistItem;
import com.yt.dl.ytaudiodownloader.service.DownloadService;
import com.yt.dl.ytaudiodownloader.service.YouTubeService;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class YtAudioDownloaderApplication implements CommandLineRunner {

  private final YouTubeService youTubeService;
  private final DownloadService downloadService;

  static void main(String[] args) {
    SpringApplication.run(YtAudioDownloaderApplication.class, args);
  }

  @Override
  public void run(String @NonNull ... args) throws Exception {
    IO.println("Please provide YouTube playlist URL you wish to download:");
    String playlistUrl = new Scanner(System.in).nextLine();
    List<PlaylistItem> playlistItems = youTubeService.getVideoDetailsFromPlaylist(playlistUrl);
    downloadService.download(playlistItems);
  }
}
