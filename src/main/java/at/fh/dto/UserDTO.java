package at.fh.dto;

import java.util.UUID;

public record UserDTO(UUID id, String userName, String password_hash) {}
