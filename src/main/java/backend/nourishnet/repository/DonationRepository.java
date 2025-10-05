package backend.nourishnet.repository;

import backend.nourishnet.domain.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> { }

