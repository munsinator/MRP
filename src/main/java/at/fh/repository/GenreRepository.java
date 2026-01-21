package at.fh.repository;

import at.fh.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenreRepository {

    void save(Genre genre);

    Optional<Genre> findById(UUID id);

    Optional<Genre> findByName(String genre);

    List<Genre> findByMediaId(UUID mediaId);

    boolean delete(UUID id);
}
