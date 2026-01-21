package at.fh.repository;

import at.fh.dto.MediaSearchParams;
import at.fh.model.Genre;
import at.fh.model.MediaEntry;
import at.fh.repository.mapper.MediaEntryMapper;

import java.sql.*;
import java.util.*;

public class JDBCMediaEntryRepository implements MediaEntryRepository {

    private final Connection conn;

    public JDBCMediaEntryRepository(Connection conn) {
        this.conn = conn;
    }

    public boolean save(MediaEntry entry) {
        String mediaSql = "INSERT INTO media_entry (id, created_by, title, description, release_year, age_restriction, media_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String genreSql = "INSERT INTO media_genre (media_id, genre_id) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(mediaSql)) {
                ps.setObject(1, entry.getId());
                ps.setObject(2, entry.getCreatedBy());
                ps.setString(3, entry.getTitle());

                if (entry.getDescription() != null) {
                    ps.setString(4, entry.getDescription());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }

                ps.setInt(5, entry.getReleaseYear());
                ps.setInt(6, entry.getAgeRestriction());
                ps.setObject(7, entry.getMediaType().name());

                int created = ps.executeUpdate();
                if (created != 1) {
                    conn.rollback();
                    return false;
                }
            }

            if (entry.getGenres() != null) {
                try (PreparedStatement ps = conn.prepareStatement(genreSql)) {
                    for (Genre genre : entry.getGenres()) {
                        ps.setObject(1, entry.getId());
                        ps.setObject(2, genre.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {}
            throw new RuntimeException("[Error] saving MediaEntry", e);

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    public Optional<MediaEntry> findById(UUID id) {
        String sql = "SELECT id, created_by, created_at, title, description, release_year, age_restriction, media_type  FROM media_entry WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(MediaEntryMapper.map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry findById failed", e);
        }
    }

    public List<MediaEntry> findAll() {
        String sql = "SELECT id, created_by, created_at, title, description, release_year, age_restriction, media_type FROM media_entry ORDER BY created_at DESC";

        List<MediaEntry> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(MediaEntryMapper.map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry findAll failed", e);
        }
    }

    public boolean update(MediaEntry entry) {
        String sql = "UPDATE media_entry SET title = ?, description = ?, release_year = ?, age_restriction = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entry.getTitle());

            if (entry.getDescription() != null) {
                ps.setString(2, entry.getDescription());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setInt(3, entry.getReleaseYear());
            ps.setInt(4, entry.getAgeRestriction());
            ps.setObject(5, entry.getId());

            int updated = ps.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry update failed", e);
        }
    }

    public boolean favoriteMedia(UUID mediaId, UUID userId) {
        String sql = "INSERT INTO media_favorite (user_id, media_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, mediaId);

            int favorited = ps.executeUpdate();
            return favorited > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry favorite failed", e);
        }
    }

    public boolean unfavoriteMedia(UUID mediaId, UUID userId) {
        String sql = "DELETE FROM media_favorite WHERE user_id = ? AND  media_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, mediaId);

            int unfavorited = ps.executeUpdate();
            return unfavorited > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry unfavorite failed", e);
        }
    }

    public boolean delete(UUID id) {
        String sql = "DELETE FROM media_entry WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int deleted = ps.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry delete failed", e);
        }
    }

    public List<MediaEntry> getFavoriteMediaFrom(UUID userId){
        String sql = "SELECT m.id, m.title, m.description, m.release_year, m.age_restriction, m.media_type, m.created_at, m.created_by FROM media_entry m JOIN media_favorite mf ON mf.media_id = m.id WHERE mf.user_id = ? ORDER BY m.created_at DESC ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<MediaEntry> mediaEntries = new ArrayList<>();

                while (rs.next()) {
                    mediaEntries.add(MediaEntryMapper.map(rs));
                }

                return mediaEntries;
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] loading favorite media for user", e);
        }
    }

    public List<MediaEntry> findByParams(MediaSearchParams request) {
        StringBuilder sql = new StringBuilder("""
        SELECT m.*, COALESCE(AVG(r.score),0) AS rating
        FROM media m
        LEFT JOIN rating r ON r.media_id = m.id
        LEFT JOIN media_genre mg ON mg.media_id = m.id
        LEFT JOIN genre g ON g.id = mg.genre_id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (request.title() != null) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + request.title().toLowerCase() + "%");
        }
        if (request.genre() != null) {
            sql.append(" AND g.name = ?");
            params.add(request.genre());
        }
        if (request.mediaType() != null) {
            sql.append(" AND m.media_type = ?");
            params.add(request.mediaType());
        }
        if (request.releaseYear() != null) {
            sql.append(" AND m.release_year = ?");
            params.add(request.releaseYear());
        }
        if (request.ageRestriction() != null) {
            sql.append(" AND m.age_restriction <= ?");
            params.add(request.ageRestriction());
        }

        sql.append(" GROUP BY m.id ");

        if (request.minRating() != null) {
            sql.append(" HAVING AVG(r.score) >= ?");
            params.add(request.minRating());
        }

        sql.append(" ORDER BY ");
        sql.append(
                switch (request.sortBy()) {
                    case "year" -> "m.release_year";
                    case "score" -> "rating DESC";
                    default -> "m.title";
        });

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<MediaEntry> mediaEntries = new ArrayList<>();

            while (rs.next())
                mediaEntries.add(MediaEntryMapper.map(rs));

            return mediaEntries;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
