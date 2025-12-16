package at.fh.repository;

import at.fh.model.MediaEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

//Genre habe noch nicht implemtiert
public class MediaEntryRepository {

    private final Connection conn;

    public MediaEntryRepository(Connection conn) {
        this.conn = conn;
    }

    public void save(MediaEntry entry) {
        String sql = "INSERT INTO media_entry (id, created_by, title, description, release_year, age_restriction) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("[Error] saving MediaEntry", e);
        }
    }

    public Optional<MediaEntry> findById(UUID id) {
        String sql = "SELECT id, created_by, created_at, title, description, release_year, age_restriction  FROM media_entry WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToMediaEntry(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry findById failed", e);
        }
    }

    public List<MediaEntry> findAll() {
        String sql = "SELECT id, created_by, created_at, title, description, release_year, age_restriction FROM media_entry ORDER BY created_at DESC";

        List<MediaEntry> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToMediaEntry(rs));
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

    private MediaEntry mapToMediaEntry(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new MediaEntry.Builder()
                .id((UUID) rs.getObject("id"))
                .createdBy((UUID) rs.getObject("created_by"))
                .createdAt(createdAt)
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseYear(rs.getInt("release_year"))
                .ageRestriction(rs.getInt("age_restriction"))
                .build();
    }
}
