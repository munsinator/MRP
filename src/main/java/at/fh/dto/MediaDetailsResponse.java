package at.fh.dto;

import at.fh.model.Rating;

import java.util.List;
import java.util.UUID;

public record MediaDetailsResponse(
        UUID id,
        String title,
        String description,
        int releaseYear,
        int ageRestriction,
        String mediaType,
        double averageScore,
        List<Rating> ratings
) {}
