package at.fh.repository;

import at.fh.constants.MediaType;
import at.fh.model.MediaEntry;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository {

    List<MediaEntry> recommendByGenre(UUID userId, MediaType mediaType, int limit);

    List<MediaEntry> recommendByContentSimilarity(UUID userId, MediaType mediaType, int limit);

    List<MediaEntry> fallbackTopRated(MediaType mediaType, int limit);

    int countRatingsOfUser(UUID userId);
}
