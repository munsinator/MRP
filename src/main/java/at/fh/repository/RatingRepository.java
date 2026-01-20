package at.fh.repository;

import at.fh.model.Genre;
import at.fh.model.Rating;
import at.fh.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingRepository {
    private final Connection conn;

    public RatingRepository(Connection conn) {
        this.conn = conn;
    }

    public boolean save(Rating rating) {
        String sql = "INSERT INTO rating (id, created_by, media_id, is_public, stars, comment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, rating.getId());
                ps.setObject(2, rating.getCreatedBy());
                ps.setObject(3, rating.getMediaId());
                ps.setBoolean(4, rating.isPublic());
                ps.setInt(5, rating.getStars());

                if (rating.getComment() != null) {
                    ps.setString(6, rating.getComment());
                } else {
                    ps.setNull(6, Types.VARCHAR);
                }

                ps.setObject(7, rating.getCreatedAt());
                ps.setObject(8, rating.getUpdatedAt());

                int created = ps.executeUpdate();
                return created == 1;

            } catch (SQLException e) {
                throw new RuntimeException("[Error] saving Rating: ", e);
            }
    }

    public Optional<Rating> findRatingById(UUID id) {
        String sql = "SELECT id, created_by, media_id, is_public, stars, comment, created_at, updated_at FROM rating WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapToRating(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] finding rating by id", e);
        }
    }

    public List<Rating> findAllRatings() {
        String sql = "SELECT id, created_by, media_id, is_public, stars, comment, created_at FROM rating  ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Rating> ratings = new ArrayList<>();
            while (rs.next()) {
                ratings.add(mapToRating(rs));
            }
            return ratings;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] finding all ratings", e);
        }
    }

    public boolean delete(UUID id) {
        String sql = "DELETE FROM rating WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] deleting rating", e);
        }
    }

    public List<Rating> findRatingsByUserId(UUID userId) {
        String sql = "SELECT id, created_by, media_id, is_public, stars, comment, created_at FROM rating  WHERE created_by = ?  ORDER BY created_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Rating> ratings = new ArrayList<>();
                while (rs.next()) {
                    ratings.add(mapToRating(rs));
                }
                return ratings;
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] finding ratings by user id", e);
        }
    }

    public List<User> findLikedUsersOfRating(UUID ratingId){
        String sql = "SELECT u.id, u.email, u.username, u.password_hash, u.created_at FROM users u JOIN rating_like rl ON rl.user_id = u.id WHERE rl.rating_id = ? ORDER BY u.username";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ratingId);

            try (ResultSet rs = ps.executeQuery()) {
                List<User> users = new ArrayList<>();

                while (rs.next()) {
                    UUID id = (UUID) rs.getObject("id");
                    String email = rs.getString("email");
                    String username = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");

                    Timestamp createdAtTs = rs.getTimestamp("created_at");
                    LocalDateTime createdAt =
                            createdAtTs != null ? createdAtTs.toLocalDateTime() : null;

                    users.add(new User.Builder()
                            .id(id)
                            .email(email)
                            .username(username)
                            .passwordHash(passwordHash)
                            .createdAt(createdAt)
                            .build());
                }

                return users;
            }

        } catch (SQLException e) {
            throw new RuntimeException("[Error] finding liked users of rating", e);
        }
    }

    private Rating mapToRating(ResultSet rs) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (createdAtTs != null) ? createdAtTs.toLocalDateTime() : null;
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        LocalDateTime updatedAt = (updatedAtTs != null) ? updatedAtTs.toLocalDateTime() : null;

        return new Rating.Builder()
                .id((UUID) rs.getObject("id"))
                .createdBy((UUID) rs.getObject("created_by"))
                .mediaId((UUID) rs.getObject("media_id"))
                .isPublic(rs.getBoolean("is_public"))
                .stars(rs.getInt("stars"))
                .comment(rs.getString("comment"))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
