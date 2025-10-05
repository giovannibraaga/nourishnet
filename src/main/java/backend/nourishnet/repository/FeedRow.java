package backend.nourishnet.repository;

import java.time.OffsetDateTime;

public record FeedRow(
        Long donationId, String status, String addressText,
        Double geoLat, Double geoLng, OffsetDateTime expiresAt,
        Long itemsCount, Double totalQtyKg, OffsetDateTime earliestBestBefore,
        OffsetDateTime lastUpdateAt
) {}