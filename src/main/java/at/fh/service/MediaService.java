package at.fh.service;
import at.fh.dto.MediaInput;
import at.fh.model.Genre;
import at.fh.model.MediaEntry;
import at.fh.repository.GenreRepository;
import at.fh.repository.MediaEntryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaService {
    private final MediaEntryRepository mediaRepository;
    private final GenreRepository genreRepository;

    public MediaService(MediaEntryRepository mediaRepository, GenreRepository genreRepository) {
        this.mediaRepository = mediaRepository;
        this.genreRepository = genreRepository;
    }

    public List<MediaEntry> findAll() {
        return mediaRepository.findAll();
    }

    public Optional<MediaEntry> findById(UUID id) {
        Optional<MediaEntry> opt = mediaRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        MediaEntry base = opt.get();
        List<Genre> genres = genreRepository.findByMediaId(id);

        MediaEntry full = new MediaEntry.Builder()
                .id(base.getId())
                .createdBy(base.getCreatedBy())
                .title(base.getTitle())
                .description(base.getDescription())
                .releaseYear(base.getReleaseYear())
                .ageRestriction(base.getAgeRestriction())
                .mediaType(base.getMediaType())
                .createdAt(base.getCreatedAt())
                .genres(genres)
                .build();

        return Optional.of(full);
    }

    public boolean create(MediaInput request, UUID userId) {
        UUID mediaId = UUID.randomUUID();

        List<Genre> genreList = new ArrayList<>();

        for (String genreName : request.genres()) {
            Optional<Genre> existing = genreRepository.findByName(genreName);

            if (existing.isPresent()) {
                genreList.add(existing.get());
            } else {
                Genre newGenre = new Genre.Builder()
                        .id(UUID.randomUUID())
                        .name(genreName)
                        .build();

                genreRepository.save(newGenre);
                genreList.add(newGenre);
            }
        }

        MediaEntry entry = new MediaEntry.Builder()
                .id(mediaId)
                .createdBy(userId)
                .title(request.title())
                .description(request.description())
                .releaseYear(request.releaseYear() == null ? 0 : request.releaseYear())
                .ageRestriction(request.ageRestriction() == null ? 0 : request.ageRestriction())
                .genres(genreList)
                .mediaType(request.mediaType())
                .build();

        return mediaRepository.save(entry);
    }

    public boolean update(MediaInput request, UUID mediaId) {
        Optional<MediaEntry> existing = mediaRepository.findById(mediaId);

        if (existing.isEmpty())
            return false;

        MediaEntry updated = new MediaEntry.Builder()
                .id(mediaId)
                .createdBy(existing.get().getCreatedBy())
                .createdAt(existing.get().getCreatedAt())
                .title(request.title())
                .description(request.description())
                .releaseYear(request.releaseYear() == null ? 0 : request.releaseYear())
                .ageRestriction(request.ageRestriction() == null ? 0 : request.ageRestriction())
                .build();

        return mediaRepository.update(updated);
    }

    public boolean delete(UUID id) {
        return mediaRepository.delete(id);
    }

    public List<MediaEntry> getFavoriteMediaFrom(UUID userId) {
        return mediaRepository.getFavoriteMediaFrom(userId);
    }

    public boolean likeMedia(UUID mediaId, UUID userId){
        return mediaRepository.favoriteMedia(userId, mediaId);
    }

    public boolean unlikeMedia(UUID mediaId, UUID userId){
        return mediaRepository.unfavoriteMedia(userId, mediaId);
    }
}