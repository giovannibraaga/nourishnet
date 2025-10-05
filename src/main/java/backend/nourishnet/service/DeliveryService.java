package backend.nourishnet.service;

import backend.nourishnet.domain.DeliveryProof;
import backend.nourishnet.domain.Donation;
import backend.nourishnet.repository.DeliveryProofRepository;
import backend.nourishnet.repository.DonationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class DeliveryService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryProofRepository proofRepo;
    private final DonationRepository donationRepo;

    @Transactional
    public Long addProof(Long donationId, Long receiverUserId, String type, String storageRef) {
        Donation d = donationRepo.findById(donationId).orElseThrow(() -> new IllegalArgumentException("Donation not found"));
        DeliveryProof p = new DeliveryProof();
        p.setDonationId(donationId);
        p.setReceiverUserId(receiverUserId);
        p.setProofType(type);
        p.setStorageRef(storageRef);
        Long id = proofRepo.save(p).getProofId();
        log.info("delivery.proof.created donation={} proof={} type={}", donationId, id, type);
        return id;
    }
}
