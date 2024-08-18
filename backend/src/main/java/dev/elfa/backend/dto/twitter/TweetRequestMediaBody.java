package dev.elfa.backend.dto.twitter;

public record TweetRequestMediaBody(
        String text,
        Media media
) {
}
