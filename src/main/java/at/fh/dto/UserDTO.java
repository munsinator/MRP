package at.fh.dto;

import java.util.UUID;

public record UserDTO(UUID id, String username, String passwordHash) {}
