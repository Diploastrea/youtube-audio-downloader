package com.yt.dl.ytaudiodownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YtAudioDownloaderApplication {

  static void main(String[] args) {
    SpringApplication.run(YtAudioDownloaderApplication.class, args);
  }
}
