package backend.nourishnet.support;

public enum FeedSort {
    URGENCY, UPDATED, EXPIRES;

    public static FeedSort of(String v) {
        if (v == null) return URGENCY;
        return switch (v.toLowerCase()) {
            case "updated" -> UPDATED;
            case "expires" -> EXPIRES;
            default -> URGENCY;
        };
    }
}
