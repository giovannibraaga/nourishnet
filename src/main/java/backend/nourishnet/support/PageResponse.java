package backend.nourishnet.support;

import java.util.List;

public record PageResponse<T>(PageMeta pagination, List<T> content) {}
