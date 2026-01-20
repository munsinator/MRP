package at.fh.service;

import at.fh.dto.RatingInput;
import at.fh.model.Rating;
import at.fh.repository.RatingRepository;
import at.fh.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class RatingService {
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final AuthService authService;

    public RatingService(UserRepository userRepository, RatingRepository ratingRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.authService = authService;
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

    public List<Rating> getRatingHistoryOfUser(UUID userId) { return ratingRepository.findRatingsByUserId(userId); }
}
