package backend.nourishnet.support;

public record PageMeta(
        int page,
        int size,
        int offset,
        String sort,
        String order,
        long totalElements,
        int totalPages
) {}
