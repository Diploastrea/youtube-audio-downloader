package com.yt.dl.ytaudiodownloader.dto;

/**
 * DTO for one time API key used to authorize HTTP requests to external API.
 *
 * @param key key value
 */
public record ApiKey(String key) {}
