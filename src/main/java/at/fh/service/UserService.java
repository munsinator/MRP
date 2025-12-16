package at.fh.service;

import at.fh.dto.UserCredentialsDTO;
import at.fh.dto.UserDTO;
import at.fh.model.MediaEntry;
import at.fh.model.User;
import at.fh.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepo, AuthService authService) {
        this.userRepository = userRepo;
        this.authService = authService;
    }

    //create user
    public void register(UserCredentialsDTO newUser) {
        User user = new User.Builder()
                .id(UUID.randomUUID())
                .userName(newUser.userName())
                .passwordHash(newUser.passwordHash()) // TODO: hashing später!!!
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public Optional<String> login(UserCredentialsDTO user) {
        //check user
        Optional<User> result = userRepository.findByUsername(user.userName());
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

    public boolean updateUser(UserDTO existingUser) {
        Optional<User> result = userRepository.findById(existingUser.id());

        if (result.isEmpty())
            return false;

        User tmpUser = result.get();

        User updated = new User.Builder()
                .id(tmpUser.getId())
                .userName(existingUser.userName() != null ? existingUser.userName() : tmpUser.getUserName())
                .passwordHash(existingUser.password_hash() != null ? existingUser.password_hash() : tmpUser.getPasswordHash())
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
}
