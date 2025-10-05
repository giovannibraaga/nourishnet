package backend.nourishnet.repository;

import backend.nourishnet.domain.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByDonationIdAndDonorUserId(Long donationId, Long donorUserId);
}

