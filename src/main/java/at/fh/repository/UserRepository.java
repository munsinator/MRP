package at.fh.repository;

import at.fh.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    boolean save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    List<User> findAll();

    boolean delete(UUID id);

    boolean update(User user);
}
