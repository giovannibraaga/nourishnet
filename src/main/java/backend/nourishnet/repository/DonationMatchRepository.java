package backend.nourishnet.repository;

import backend.nourishnet.domain.DonationMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationMatchRepository extends JpaRepository<DonationMatch, Long> {
    @Query("select m from DonationMatch m where m.ngoUserId = :ngoUserId order by m.createdAt desc")
    List<DonationMatch> findByNgoUserIdOrderByCreatedAtDesc(Long ngoUserId);

    @Query("select m from DonationMatch m where m.donationId = :donationId order by m.createdAt desc")
    List<DonationMatch> findByDonationIdOrderByCreatedAtDesc(Long donationId);

    @Query("select count(m) > 0 from DonationMatch m where m.donationId = :donationId and m.status = 'ACCEPTED'")
    boolean existsAcceptedForDonation(Long donationId);
}
