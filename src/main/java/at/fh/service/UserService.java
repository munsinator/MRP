package at.fh.service;

import at.fh.dto.UserCredentials;
import at.fh.dto.UserProfileUpdate;
import at.fh.dto.UserStatistics;
import at.fh.model.User;
import at.fh.repository.JDBCLeaderboardRepository;
import at.fh.repository.JDBCUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final JDBCUserRepository userRepository;
    private final AuthService authService;
    private final JDBCLeaderboardRepository leaderboardRepository;

    public UserService(JDBCUserRepository userRepo, JDBCLeaderboardRepository leaderboardRepo, AuthService authService) {
        this.userRepository = userRepo;
        this.authService = authService;
        this.leaderboardRepository = leaderboardRepo;
    }

    //create user
    public void register(UserCredentials newUser) {
        User user = new User.Builder()
                .id(UUID.randomUUID())
                .username(newUser.username())
                .passwordHash(newUser.passwordHash())
                .build();

        userRepository.save(user);
    }

    //Returns token if login successful
    public Optional<String> login(UserCredentials user) {
        //check user
        Optional<User> result = userRepository.findByUsername(user.username());
        if (result.isEmpty()){
            return Optional.empty();
        }

        User tmpUser = result.get();

        //check pw
        if (!tmpUser.getPasswordHash().equals(user.passwordHash())) {
            return Optional.empty();
        }

        String token = authService.createSession(tmpUser.getId());
        return Optional.of(token);
    }

    public boolean updateUser(UserProfileUpdate existingUser, UUID userId) {
        Optional<User> result = userRepository.findById(userId);

        if (result.isEmpty())
            return false;

        User tmpUser = result.get();

        User updated = new User.Builder()
                .id(tmpUser.getId())
                .email(existingUser.email())
                .username(tmpUser.getUsername())
                .passwordHash(tmpUser.getPasswordHash())
                .createdAt(tmpUser.getCreatedAt())
                .build();

        return userRepository.update(updated);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public boolean deleteUser(UUID userId) {
       return userRepository.delete(userId);
    }

    public List<UserStatistics> getLeaderboard() {
        return leaderboardRepository.leaderboard();
    }
}
