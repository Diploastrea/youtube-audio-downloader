package com.yt.dl.ytaudiodownloader.dto;

/**
 * DTO for YouTube video.
 *
 * @param title video title
 * @param url video URL
 * @param id video ID
 */
public record YouTubeVideo(String title, String url, String id) {}
