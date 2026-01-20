package at.fh.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
    GAME,
    MOVIE,
    SERIES;

    @JsonCreator
    public static MediaType fromString(String value) {
        return MediaType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
