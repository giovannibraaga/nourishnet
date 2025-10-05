package backend.nourishnet.shared.pagination;

public record Pagination(int page, int size) {
    public static Pagination of(Integer page, Integer size) {
        return new Pagination(page == null ? 0 : page, size == null ? 20 : size);
    }
}
