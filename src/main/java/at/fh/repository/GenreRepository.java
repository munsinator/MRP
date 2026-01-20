package at.fh.repository;

import at.fh.model.Genre;
import at.fh.model.MediaEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GenreRepository {
    private final Connection conn;

    public GenreRepository(Connection conn) {
        this.conn = conn;
    }

    public void save(Genre genre) {
        String sql = "INSERT INTO genre (id, name) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, genre.getId());
            ps.setObject(2, genre.getName());
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("[Error] creating Genre!", e);
        }
    }

    public Optional<Genre> findById(UUID id) {
        String sql = "SELECT id, name  FROM genre WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToGenre(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] MediaEntry findById failed", e);
        }
    }

    public Optional<Genre> findByName(String genre) {
        String sql = "SELECT id, name FROM genre WHERE name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, genre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToGenre(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] Genre findById failed", e);
        }
    }

    public List<Genre> findByMediaId(UUID mediaId) {
        String sql = "SELECT g.id, g.name FROM genre g JOIN media_genre mg ON mg.genre_id = g.id WHERE mg.media_id = ? ";

        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    genres.add(mapToGenre(rs));
                }
            }
            return genres;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] loading genres for media", e);
        }
    }


    public boolean delete(UUID id) {
        String sql = "DELETE FROM genre WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int deleted = ps.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] Genre delete failed", e);
        }
    }

    private Genre mapToGenre(ResultSet rs) throws SQLException {
        return new Genre.Builder()
                .id((UUID) rs.getObject("id"))
                .name(rs.getString("name"))
                .build();
    }
}
