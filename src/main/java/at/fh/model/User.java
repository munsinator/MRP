package at.fh.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String userName;
    private String passwordHash;
    private LocalDateTime createdAt;

    private User(){}

    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private final User user = new User();

        public Builder id(UUID id) {
            user.id = id;
            return this;
        }

        public Builder userName(String userName) {
            user.userName = userName;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            user.passwordHash = passwordHash;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            user.createdAt = createdAt;
            return this;
        }

        public User build() {
            if (user.createdAt == null) {
                user.createdAt = LocalDateTime.now();
            }
            return user;
        }
    }
}
