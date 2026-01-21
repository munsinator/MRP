package at.fh.repository;

import at.fh.dto.MediaSearchParams;
import at.fh.model.MediaEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaEntryRepository {

    boolean save(MediaEntry entry);

    Optional<MediaEntry> findById(UUID id);

    List<MediaEntry> findAll();

    boolean update(MediaEntry entry);

    boolean favoriteMedia(UUID mediaId, UUID userId);

    boolean unfavoriteMedia(UUID mediaId, UUID userId);

    boolean delete(UUID id);

    List<MediaEntry> getFavoriteMediaFrom(UUID userId);

    List<MediaEntry> findByParams(MediaSearchParams request);
}
