package at.fh.model;

import java.util.List;

public class UserProfile {
    private String email;
    private String favoriteGenre;
    private int totalRatings;
    private double averageRating;
    private List<MediaEntry> favorites;

    private UserProfile() {}

    public static class Builder {
        private final UserProfile profile = new UserProfile();

        public Builder email(String email) {
            profile.email = email;
            return this;
        }

        public Builder favoriteGenre(String favoriteGenre) {
            profile.favoriteGenre = favoriteGenre;
            return this;
        }

        public Builder totalRatings(int totalRatings) {
            profile.totalRatings = totalRatings;
            return this;
        }

        public Builder averageRating(double averageRating) {
            profile.averageRating = averageRating;
            return this;
        }

        public Builder favorites(List<MediaEntry> favorites) {
            profile.favorites = favorites;
            return this;
        }

        public UserProfile build() {
            return profile;
        }
    }
}
