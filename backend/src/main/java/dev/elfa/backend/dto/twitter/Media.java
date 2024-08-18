package dev.elfa.backend.dto.twitter;

import java.util.Arrays;
import java.util.Objects;

public record Media(String[] media_ids) {
    @Override
    public String toString() {
        return "Media{" +
                "media_ids=" + Arrays.toString(media_ids) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.deepEquals(media_ids, media.media_ids);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(media_ids);
    }
}
