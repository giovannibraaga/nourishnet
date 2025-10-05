package backend.nourishnet.repository;

import backend.nourishnet.domain.Alert;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query(value = """
        select count(*)
        from ALERT
        where (:status    is null or STATUS     = :status)
          and (:severity  is null or SEVERITY   = :severity)
          and (:donationId is null or DONATION_ID = :donationId)
        """, nativeQuery = true)
    long count(@Param("status") String status,
               @Param("severity") String severity,
               @Param("donationId") Long donationId);

    @Query(value = """
        select ALERT_ID, TYPE, SEVERITY, DONATION_ID, MESSAGE, STATUS, CREATED_AT, CLOSED_AT
        from ALERT
        where (:status    is null or STATUS     = :status)
          and (:severity  is null or SEVERITY   = :severity)
          and (:donationId is null or DONATION_ID = :donationId)
        """,
            countQuery = """
        select count(*)
        from ALERT
        where (:status    is null or STATUS     = :status)
          and (:severity  is null or SEVERITY   = :severity)
          and (:donationId is null or DONATION_ID = :donationId)
        """,
            nativeQuery = true)
    Page<Alert> search(@Param("status") String status,
                       @Param("severity") String severity,
                       @Param("donationId") Long donationId,
                       Pageable pageable);

    static Sort orderBy(String sort, String order) {
        var dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("severity".equalsIgnoreCase(sort)) return Sort.by(dir, "SEVERITY").and(Sort.by(Sort.Direction.DESC, "CREATED_AT"));
        if ("status".equalsIgnoreCase(sort))   return Sort.by(dir, "STATUS").and(Sort.by(Sort.Direction.DESC, "CREATED_AT"));
        return Sort.by(dir, "CREATED_AT");
    }
}
