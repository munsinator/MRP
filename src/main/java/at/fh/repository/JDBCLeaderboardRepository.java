package at.fh.repository;

import at.fh.dto.UserStatistics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JDBCLeaderboardRepository implements LeaderboardRepository {
    private final Connection conn;

    public JDBCLeaderboardRepository(Connection conn) {
        this.conn = conn;
    }

    public List<UserStatistics> leaderboard() {
        String sql = """
        SELECT u.id, u.username, COUNT(r.id) AS activity
        FROM users u
        LEFT JOIN rating r ON r.user_id = u.id
        GROUP BY u.id
        ORDER BY activity DESC
        LIMIT 10
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            List<UserStatistics> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(new UserStatistics((UUID) rs.getObject("id"), rs.getString("username"), rs.getInt("activity")));
            }
            return resultList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
