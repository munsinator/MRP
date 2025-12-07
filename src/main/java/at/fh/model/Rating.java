package at.fh.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rating {
    private UUID id;
    private UUID createdBy;
    private UUID mediaId;
    private boolean isPublic;
    private int stars;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Rating() {}

    public static class Builder {
        private final Rating rating = new Rating();

        public Builder id(UUID id) {
            rating.id = id;
            return this;
        }

        public Builder createdBy(UUID createdBy) {
            rating.createdBy = createdBy;
            return this;
        }

        public Builder mediaId(UUID mediaId) {
            rating.mediaId = mediaId;
            return this;
        }

        public Builder stars(int stars) {
            rating.stars = stars;
            return this;
        }

        public Builder comment(String comment) {
            rating.comment = comment;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            rating.isPublic = isPublic;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            rating.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            rating.updatedAt = updatedAt;
            return this;
        }

        public Rating build() {
            return rating;
        }
    }
}
