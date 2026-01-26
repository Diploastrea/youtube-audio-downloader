package com.yt.dl.ytaudiodownloader.dto;

/**
 * DTO for video conversion request.
 *
 * @param link YouTube video link
 * @param format output format
 * @param audioBitrate output audio bitrate
 * @param filenameStyle filename style
 * @param vCodec video codec
 */
public record ConvertPayload(
    String link, String format, String audioBitrate, String filenameStyle, String vCodec) {}
