package at.fh.model;

import at.fh.constants.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MediaEntry {
    private UUID id;
    private String title;
    private String description;
    private int releaseYear;
    private int ageRestriction;
    private List<Genre> genres;
    private MediaType mediaType;
    private LocalDateTime createdAt;
    private UUID createdBy;

    private MediaEntry() {}

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }


    public static class Builder {
        private final MediaEntry mediaEntry = new MediaEntry();

        public Builder id(UUID id) {
            mediaEntry.id = id;
            return this;
        }
        public Builder title(String title) {
            mediaEntry.title = title;
            return this;
        }
        public Builder description(String description) {
            mediaEntry.description = description;
            return this;
        }
        public Builder releaseYear(int releaseYear) {
            mediaEntry.releaseYear = releaseYear;
            return this;
        }
        public Builder ageRestriction(int ageRestriction) {
            mediaEntry.ageRestriction = ageRestriction;
            return this;
        }
        public Builder genres(List<Genre> genres) {
            mediaEntry.genres = genres;
            return this;
        }
        public Builder mediaType(MediaType mediaType) {
            mediaEntry.mediaType = mediaType;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            mediaEntry.createdAt = createdAt;
            return this;
        }
        public Builder createdBy(UUID userId) {
            mediaEntry.createdBy = userId;
            return this;
        }
        public MediaEntry build() {
            if (mediaEntry.createdAt == null) {
                mediaEntry.createdAt = LocalDateTime.now();
            }
            return mediaEntry;
        }
    }
}
