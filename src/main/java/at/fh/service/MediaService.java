package at.fh.service;
import at.fh.model.MediaEntry;
import at.fh.repository.MediaEntryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaService {

    private final MediaEntryRepository mediaRepository;

    public MediaService(MediaEntryRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<MediaEntry> findAll() {
        return mediaRepository.findAll();
    }

    public Optional<MediaEntry> findById(UUID id) {
        return mediaRepository.findById(id);
    }

    public UUID create(MediaDTO input) {

        UUID id = UUID.randomUUID();

        MediaEntry entry = new MediaEntry.Builder()
                .id(id)
                .createdBy(UUID.fromString("11111111-1111-1111-1111-111111111111")) //system user
                .title(input.title())
                .description(input.description())
                .releaseYear(input.releaseYear() == null ? 0 : input.releaseYear())
                .ageRestriction(input.ageRestriction() == null ? 0 : input.ageRestriction())
                .build();

        mediaRepository.save(entry);
        return id;
    }

    public boolean update(UUID id, MediaDTO req) {
        Optional<MediaEntry> existing = mediaRepository.findById(id);
        if (existing.isEmpty()) return false;

        MediaEntry updated = new MediaEntry.Builder()
                .id(id)
                .createdBy(existing.get().getCreatedBy())
                .createdAt(existing.get().getCreatedAt())
                .title(req.title())
                .description(req.description())
                .releaseYear(req.releaseYear() == null ? 0 : req.releaseYear())
                .ageRestriction(req.ageRestriction() == null ? 0 : req.ageRestriction())
                .build();

        return mediaRepository.update(updated);
    }

    public boolean delete(UUID id) { return mediaRepository.delete(id); }
}