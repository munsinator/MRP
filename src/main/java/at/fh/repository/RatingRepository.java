package at.fh.repository;

import at.fh.model.Rating;
import at.fh.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository {

    boolean save(Rating rating);

    boolean update(Rating rating);

    Optional<Rating> findById(UUID id);

    List<Rating> findAll();

    boolean delete(UUID id);

    List<Rating> findByUserId(UUID userId);

    List<User> findLikedUsersOfRating(UUID ratingId);

    boolean saveLike(UUID userId, UUID ratingId);

    List<Rating> findVisibleRatingsForMedia(UUID mediaId, UUID userId);

    double getAveragePublicScoreForMedia(UUID mediaId);
}
