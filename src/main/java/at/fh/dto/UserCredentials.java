package at.fh.dto;

import java.util.UUID;

public record UserCredentials(String username, String passwordHash) {}
