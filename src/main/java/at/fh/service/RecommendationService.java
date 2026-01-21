package at.fh.service;

import at.fh.constants.MediaType;
import at.fh.model.MediaEntry;
import at.fh.repository.JDBCRecommendationRepository;

import java.util.List;
import java.util.UUID;

public class RecommendationService {
    private final JDBCRecommendationRepository repo;

    public RecommendationService(JDBCRecommendationRepository repo) {
        this.repo = repo;
    }

    public List<MediaEntry> getRecommendations(UUID userId, String type, MediaType mediaType, int limit) {
        int ratingCount = repo.countRatingsOfUser(userId);
        if (ratingCount == 0) {
            return repo.fallbackTopRated(mediaType, limit);
        }

        if ("content".equalsIgnoreCase(type)) {
            // content similarity (top-3)
            return repo.recommendByContentSimilarity(userId, mediaType, limit);
        }

        // default: genre
        return repo.recommendByGenre(userId, mediaType, limit);
    }
}
