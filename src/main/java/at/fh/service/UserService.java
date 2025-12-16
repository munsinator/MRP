package at.fh.service;

import at.fh.model.User;
import at.fh.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    public void register(String username, String password) {
        User user = new User.Builder()
                .id(UUID.randomUUID())
                .userName(username)
                .passwordHash(password) // TODO: hashing!!!
                .build();

        userRepository.save(user);
    }

    public void login(String username, String password) {

    }
}
