package at.fh.dto;

import at.fh.constants.MediaType;
import at.fh.model.Genre;

import java.util.List;

public record MediaInput(
        String title,
        String description,
        Integer releaseYear,
        Integer ageRestriction,
        List<String> genres,
        MediaType mediaType
){}
