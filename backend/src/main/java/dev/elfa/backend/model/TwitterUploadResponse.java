package dev.elfa.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TwitterUploadResponse(
        @JsonProperty("media_id") String mediaId,
        @JsonProperty("media_id_string") String mediaIdString,
        @JsonProperty("size") long size,
        @JsonProperty("expires_after_secs") long expiresAfterSecs,
        @JsonProperty("image") Image image
) {
    public static record Image(
            @JsonProperty("image_type") String imageType,
            @JsonProperty("w") int w,
            @JsonProperty("h") int h
    ) {
    }
}