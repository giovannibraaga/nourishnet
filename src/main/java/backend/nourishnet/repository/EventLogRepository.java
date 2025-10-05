package backend.nourishnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EventLogRepository extends JpaRepository<backend.nourishnet.domain.Donation, Long> {

    @Query(value = "select count(*) from DONATION_EVENT_LOG where DONATION_ID = :donationId", nativeQuery = true)
    long countByDonation(@Param("donationId") Long donationId);

    interface EventRow {
        Long getEventId();
        Long getDonationId();
        String getEventType();
        Long getActorUserId();
        String getPayloadJson();
        java.time.OffsetDateTime getEventTime();
    }

    @Query(value = """
        select 
            EVENT_ID           as eventId,
            DONATION_ID        as donationId,
            EVENT_TYPE         as eventType,
            ACTOR_USER_ID      as actorUserId,
            PAYLOAD_JSON       as payloadJson,
            EVENT_TIME         as eventTime
        from DONATION_EVENT_LOG
        where DONATION_ID = :donationId
        order by EVENT_TIME desc
        """,
            countQuery = "select count(*) from DONATION_EVENT_LOG where DONATION_ID = :donationId",
            nativeQuery = true)
    Page<EventRow> pageByDonation(@Param("donationId") Long donationId, Pageable pageable);
}