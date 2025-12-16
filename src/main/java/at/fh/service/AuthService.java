package at.fh.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private final Map<String, UUID> sessions = new HashMap<>();

    public String createSession(UUID userId) {
        String token = UUID.randomUUID().toString();

        sessions.put(token, userId);

        return token;
    }

    public UUID authenticate(String token) {
        if (token == null) return null;

        return sessions.get(token);
    }
}
