package com.yt.dl.ytaudiodownloader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class YtAudioDownloaderApplication {

  static void main(String[] args) {
    SpringApplication.run(YtAudioDownloaderApplication.class, args);
  }
}
