package at.fh.repository;

import at.fh.constants.MediaType;
import at.fh.model.MediaEntry;
import at.fh.repository.mapper.MediaEntryMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RecommendationRepository {
    private final Connection conn;

    public RecommendationRepository(Connection conn) {
        this.conn = conn;
    }

    // ========== TYPE=GENRE (User Genre Pref) ==========
    public List<MediaEntry> recommendByGenre(UUID userId, MediaType mediaType, int limit) {
        String sql = """
            WITH user_genre_pref AS (
              SELECT mg.genre_id,
                     SUM(r.stars - 3) AS score
              FROM rating r
              JOIN media_genre mg ON mg.media_id = r.media_id
              WHERE r.created_by = ?
              GROUP BY mg.genre_id
            ),
            candidates AS (
              SELECT m.id, m.created_by, m.created_at, m.title, m.description,
                     m.release_year, m.age_restriction, m.media_type
              FROM media_entry m
              WHERE m.media_type = ?
                AND NOT EXISTS (
                  SELECT 1 FROM rating r2
                  WHERE r2.created_by = ? AND r2.media_id = m.id
                )
            )
            SELECT c.*,
                   COALESCE(SUM(ugp.score), 0) AS rec_score
            FROM candidates c
            LEFT JOIN media_genre mg ON mg.media_id = c.id
            LEFT JOIN user_genre_pref ugp ON ugp.genre_id = mg.genre_id
            GROUP BY c.id, c.created_by, c.created_at, c.title, c.description, c.release_year, c.age_restriction, c.media_type
            ORDER BY rec_score DESC, c.created_at DESC
            LIMIT ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setString(2, mediaType.name());
            ps.setObject(3, userId);
            ps.setInt(4, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<MediaEntry> out = new ArrayList<>();
                while (rs.next()) out.add(MediaEntryMapper.map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] recommendByGenre failed", e);
        }
    }

    // ========== TYPE=CONTENT (Similar to Top Rated) ==========
    public List<MediaEntry> recommendByContentSimilarity(UUID userId, MediaType mediaType, int limit) {
        String sql = """
            WITH top_media AS (
              SELECT r.media_id
              FROM rating r
              JOIN media_entry m ON m.id = r.media_id
              WHERE r.created_by = ?
                AND m.media_type = ?
              ORDER BY r.stars DESC, r.created_at DESC
              LIMIT 3
            ),
            top_genres AS (
              SELECT DISTINCT mg.genre_id
              FROM media_genre mg
              JOIN top_media tm ON tm.media_id = mg.media_id
            )
            SELECT m.id, m.created_by, m.created_at, m.title, m.description,
                   m.release_year, m.age_restriction, m.media_type,
                   COUNT(*) AS shared_genres
            FROM media_entry m
            JOIN media_genre mg ON mg.media_id = m.id
            JOIN top_genres tg ON tg.genre_id = mg.genre_id
            WHERE m.media_type = ?
              AND NOT EXISTS (
                SELECT 1 FROM rating r2
                WHERE r2.created_by = ? AND r2.media_id = m.id
              )
            GROUP BY m.id, m.created_by, m.created_at, m.title, m.description, m.release_year, m.age_restriction, m.media_type
            ORDER BY shared_genres DESC, m.created_at DESC
            LIMIT ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setString(2, mediaType.name());
            ps.setString(3, mediaType.name());
            ps.setObject(4, userId);
            ps.setInt(5, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<MediaEntry> out = new ArrayList<>();
                while (rs.next()) out.add(MediaEntryMapper.map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] recommendByContentSimilarity failed", e);
        }
    }

    // ========== FALLBACK (No ratings) ==========
    public List<MediaEntry> fallbackTopRated(MediaType mediaType, int limit) {
        String sql = """
            SELECT m.id, m.created_by, m.created_at, m.title, m.description,
                   m.release_year, m.age_restriction, m.media_type
            FROM media_entry m
            LEFT JOIN rating r ON r.media_id = m.id AND r.is_public = TRUE
            WHERE m.media_type = ?
            GROUP BY m.id, m.created_by, m.created_at, m.title, m.description, m.release_year, m.age_restriction, m.media_type
            ORDER BY COALESCE(AVG(r.stars), 0) DESC, COUNT(r.id) DESC, m.created_at DESC
            LIMIT ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mediaType.name());
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<MediaEntry> out = new ArrayList<>();
                while (rs.next()) out.add(MediaEntryMapper.map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] fallbackTopRated failed", e);
        }
    }

    public int countRatingsOfUser(UUID userId) {
        String sql = "SELECT COUNT(*) FROM rating WHERE created_by = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] countRatingsOfUser failed", e);
        }
    }
}
