package at.fh.service;
import at.fh.dto.MediaInput;
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

    public boolean create(MediaInput request, UUID userId) {
        UUID mediaId = UUID.randomUUID();

        MediaEntry entry = new MediaEntry.Builder()
                .id(mediaId)
                .createdBy(userId)
                .title(request.title())
                .description(request.description())
                .releaseYear(request.releaseYear() == null ? 0 : request.releaseYear())
                .ageRestriction(request.ageRestriction() == null ? 0 : request.ageRestriction())
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
}