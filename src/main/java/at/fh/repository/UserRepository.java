package at.fh.repository;

import at.fh.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository
{
    private final Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
    }

    public boolean save(User user){
        String sql = "INSERT INTO users (id, username, password_hash, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPasswordHash());
            ps.setObject(4, user.getCreatedAt());

            int created = ps.executeUpdate();
            return created == 1;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] saving User: ", e);
        }

    }

    public Optional<User> findByUsername(String username){
        String sql = "SELECT id, username, password_hash, created_at FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUser(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User findId() failed: ", e);
        }
    }

    public Optional<User> findById(UUID id){
        String sql = "SELECT id, username, password_hash, created_at FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUser(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User findId() failed: ", e);
        }
    }

    public List<User> findAll(){
        String sql = "SELECT id, username, password_hash, created_at FROM users";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            List<User> userList = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userList.add(mapToUser(rs));
                }
            }
            return userList;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User findAll() failed: ", e);
        }

    }

    public boolean delete(UUID id){
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int deleted = ps.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User delete() failed: ", e);
        }

    }

    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setObject(4, user.getId());

            int updated = ps.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User update failed", e);
        }
    }

    private User mapToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .id((UUID) rs.getObject("id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}

