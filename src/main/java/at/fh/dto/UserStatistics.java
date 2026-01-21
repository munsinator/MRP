package at.fh.dto;

import java.util.UUID;

public record UserStatistics(
        UUID userId,
        String username,
        int activity
) {}
