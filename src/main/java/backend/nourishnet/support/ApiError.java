package backend.nourishnet.support;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String path,
        int status,
        String error,
        String message,
        OffsetDateTime timestamp,
        List<FieldViolation> violations
) {
    public static record FieldViolation(String field, String message) {}
}