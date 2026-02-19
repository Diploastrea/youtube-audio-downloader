package com.yt.dl.ytaudiodownloader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application configuration properties.
 *
 * <p>Maps properties under the {@code app.config} prefix from {@code application.yml},
 * environment variables, and command-line arguments.
 *
 * @param playlist YouTube playlist URL
 * @param output directory for downloaded files
 * @param removeAfterDownload whether to remove downloaded videos from YouTube playlist
 */
@ConfigurationProperties(prefix = "app.config")
public record ApplicationConfiguration(
    String playlist, String output, boolean removeAfterDownload) {}
