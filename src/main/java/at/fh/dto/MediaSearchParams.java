package at.fh.dto;

public record MediaSearchParams(
        String title,
        String genre,
        String mediaType,
        Integer releaseYear,
        Integer ageRestriction,
        Double minRating,
        String sortBy
) {}

