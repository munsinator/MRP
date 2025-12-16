package at.fh.service;

public record MediaDTO(
        String title,
        String description,
        Integer releaseYear,
        Integer ageRestriction
) {}
