package at.fh.repository;

import at.fh.config.DatabaseConfig;
import at.fh.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void save(User user){
        String sql = "INSERT INTO users (id, username, password_hash, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, user.getId());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPasswordHash());
            ps.setObject(4, user.getCreatedAt());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("[Error] saving User: ", e);
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

    public void delete(UUID id){
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("[Error] User delete() failed: ", e);
        }

    }

    private User mapToUser(ResultSet rs) throws SQLException {
        return new User.Builder()
                .id((UUID) rs.getObject("id"))
                .userName(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}

