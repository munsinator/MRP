package at.fh.repository;

import at.fh.dto.UserStatistics;

import java.util.List;

public interface LeaderboardRepository {
    List<UserStatistics> leaderboard();
}
