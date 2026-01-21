package at.fh.service;

import at.fh.dto.RatingInput;
import at.fh.model.MediaEntry;
import at.fh.model.Rating;
import at.fh.model.User;
import at.fh.repository.MediaEntryRepository;
import at.fh.repository.RatingRepository;
import at.fh.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public boolean createRating(RatingInput ratingInput, UUID mediaId, UUID userId) {
        Rating newRating = new Rating.Builder()
                .id(UUID.randomUUID())
                .createdBy(userId)
                .mediaId(mediaId)
                .stars(ratingInput.stars())
                .comment((ratingInput.comment() == null || ratingInput.comment().isBlank()) ? null : ratingInput.comment())
                .isPublic(false)
                .build();

        return ratingRepository.save(newRating);
    }

    public List<Rating> getRatingHistoryOfUser(UUID userId) { return ratingRepository.findByUserId(userId); }

    public List<User> findLikedUsersOfRating(UUID ratingId){
        return ratingRepository.findLikedUsersOfRating(ratingId);
    }

    public boolean updateRating(RatingInput ratingInput, UUID ratingId, UUID userId) {
        Optional<Rating> existing = ratingRepository.findById(ratingId);

        if (existing.isEmpty())
            return false;

        Rating updatedRating = new Rating.Builder()
                .id(ratingId)
                .createdBy(userId)
                .mediaId(existing.get().getMediaId())
                .stars(ratingInput.stars())
                .comment((ratingInput.comment() == null || ratingInput.comment().isBlank()) ? null : ratingInput.comment())
                .isPublic(existing.get().isPublic())
                .build();

        return ratingRepository.update(updatedRating);
    }

    public boolean deleteRating(UUID ratingId) {
        return ratingRepository.delete(ratingId);
    }

    public List<Rating> findAllRatings() {
        return ratingRepository.findAll();
    }

    //Toggle public flag
    public boolean confirmRating(UUID ratingId) {
        Optional<Rating> existing = ratingRepository.findById(ratingId);

        if (existing.isEmpty())
            return false;

        Rating updatedRating = new Rating.Builder()
                .id(ratingId)
                .createdBy(existing.get().getCreatedBy())
                .mediaId(existing.get().getMediaId())
                .stars(existing.get().getStars())
                .comment(existing.get().getComment())
                .isPublic(!existing.get().isPublic())
                .build();

        return ratingRepository.update(updatedRating);
    }

    public boolean likeRating(UUID ratingId) {
        Optional<Rating> existing = ratingRepository.findById(ratingId);

        if (existing.isEmpty())
            return false;

        return ratingRepository.saveLike(ratingId,existing.get().getCreatedBy());
    }
}
