package com.yt.dl.ytaudiodownloader.dto;

/**
 * DTO for video conversion response.
 *
 * @param status response status
 * @param url download link
 * @param filename converted filename
 */
public record ConvertResponse(String status, String url, String filename) {}
