package com.yt.dl.ytaudiodownloader.dto;

/**
 * DTO for YouTube video.
 *
 * @param title video title
 * @param url video URL
 */
public record PlaylistItem(String title, String url) {}
