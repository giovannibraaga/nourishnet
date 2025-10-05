package backend.nourishnet.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PageResponse<T>(
        @JsonProperty("pagination") PageMeta pagination,
        List<T> content
) {
}
