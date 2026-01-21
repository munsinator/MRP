package at.fh.repository.mapper;

import at.fh.model.MediaEntry;
import at.fh.constants.MediaType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public final class MediaEntryMapper {

    private MediaEntryMapper() {}

    public static MediaEntry map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new MediaEntry.Builder()
                .id((UUID) rs.getObject("id"))
                .createdBy((UUID) rs.getObject("created_by"))
                .createdAt(createdAt)
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseYear(rs.getInt("release_year"))
                .ageRestriction(rs.getInt("age_restriction"))
                .mediaType(MediaType.valueOf(rs.getString("media_type")))
                .build();
    }
}

