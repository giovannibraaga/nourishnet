package backend.nourishnet.repository;

import backend.nourishnet.support.FeedSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;

public interface FeedRepository extends JpaRepository<backend.nourishnet.domain.Donation, Long> {

    @Query(value = """
        SELECT COUNT(*) 
        FROM BI_MV_DONATION_FEED_SUMMARY 
        WHERE STATUS = 'OPEN'
        """, nativeQuery = true)
    long countOpen();

    interface FeedProjection {
        Long getDonationId();
        String getStatus();
        String getAddressText();
        Double getGeoLat();
        Double getGeoLng();
        java.time.OffsetDateTime getExpiresAt();
        Long getItemsCount();
        Double getTotalQtyKg();
        java.time.OffsetDateTime getEarliestBestBefore();
        java.time.OffsetDateTime getLastUpdateAt();
    }

    @Query(value = """
        SELECT 
            DONATION_ID              AS donationId,
            STATUS                   AS status,
            ADDRESS_TEXT             AS addressText,
            GEO_LAT                  AS geoLat,
            GEO_LNG                  AS geoLng,
            EXPIRES_AT               AS expiresAt,
            ITEMS_COUNT              AS itemsCount,
            TOTAL_QTY_KG             AS totalQtyKg,
            EARLIEST_BEST_BEFORE     AS earliestBestBefore,
            LAST_UPDATE_AT           AS lastUpdateAt
        FROM BI_MV_DONATION_FEED_SUMMARY
        WHERE STATUS = 'OPEN'
        """,
            countQuery = """
        SELECT COUNT(*) 
        FROM BI_MV_DONATION_FEED_SUMMARY 
        WHERE STATUS = 'OPEN'
        """,
            nativeQuery = true)
    Page<FeedProjection> findOpenAsProjection(Pageable pageable);

    static Sort orderByFor(FeedSort sort, Boolean asc) {
        var a = (asc == null || asc);
        return switch (sort == null ? FeedSort.URGENCY : sort) {
            case UPDATED -> Sort.by(a ? Sort.Direction.ASC : Sort.Direction.DESC, "LAST_UPDATE_AT");
            case EXPIRES -> Sort.by(a ? Sort.Direction.ASC : Sort.Direction.DESC, "EXPIRES_AT").and(Sort.by(Sort.Direction.ASC, "DONATION_ID"));
            case URGENCY -> Sort.by(Sort.Direction.ASC, "EARLIEST_BEST_BEFORE")
                    .and(Sort.by(Sort.Direction.ASC, "EXPIRES_AT"))
                    .and(Sort.by(Sort.Direction.DESC, "LAST_UPDATE_AT"));
        };
    }
}
