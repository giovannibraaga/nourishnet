package backend.nourishnet.repository;

import backend.nourishnet.domain.DonationMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;

public interface MatchRepository extends JpaRepository<DonationMatch, Long> {

    @Query(value = "select count(*) from DONATION_MATCH where NGO_USER_ID = :ngoUserId", nativeQuery = true)
    long countByNgo(@org.springframework.data.repository.query.Param("ngoUserId") Long ngoUserId);

    @Query(value = """
        select MATCH_ID, DONATION_ID, NGO_USER_ID, STATUS, SCHEDULED_PICKUP_AT, CREATED_AT, UPDATED_AT
        from DONATION_MATCH
        where NGO_USER_ID = :ngoUserId
        """,
            countQuery = "select count(*) from DONATION_MATCH where NGO_USER_ID = :ngoUserId",
            nativeQuery = true)
    Page<DonationMatch> findByNgo(@org.springframework.data.repository.query.Param("ngoUserId") Long ngoUserId,
                                  Pageable pageable);

    @Query(value = "select count(*) from DONATION_MATCH where DONATION_ID = :donationId", nativeQuery = true)
    long countByDonation(@org.springframework.data.repository.query.Param("donationId") Long donationId);

    @Query(value = """
        select MATCH_ID, DONATION_ID, NGO_USER_ID, STATUS, SCHEDULED_PICKUP_AT, CREATED_AT, UPDATED_AT
        from DONATION_MATCH
        where DONATION_ID = :donationId
        """,
            countQuery = "select count(*) from DONATION_MATCH where DONATION_ID = :donationId",
            nativeQuery = true)
    Page<DonationMatch> findByDonation(@org.springframework.data.repository.query.Param("donationId") Long donationId,
                                       Pageable pageable);

    static Sort orderBy(String sort, String order) {
        var dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("status".equalsIgnoreCase(sort)) return Sort.by(dir, "STATUS").and(Sort.by(Sort.Direction.DESC, "CREATED_AT"));
        if ("pickup".equalsIgnoreCase(sort)) return Sort.by(dir, "SCHEDULED_PICKUP_AT").and(Sort.by(Sort.Direction.DESC, "CREATED_AT"));
        return Sort.by(dir, "CREATED_AT");
    }
}