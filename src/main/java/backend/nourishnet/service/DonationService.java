package backend.nourishnet.service;

import backend.nourishnet.domain.Donation;
import backend.nourishnet.domain.DonationItem;
import backend.nourishnet.repository.DonationRepository;
import backend.nourishnet.repository.FeedJdbcRepository;
import backend.nourishnet.repository.FeedRow;
import backend.nourishnet.repository.FeedSort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepo;
    private final FeedJdbcRepository feedRepo;

    @Transactional
    public Long createDonation(Long donorUserId, Donation donation, List<DonationItem> items) {
        donation.setDonorUserId(donorUserId);
        donation.setStatus("OPEN");
        for (DonationItem it : items) {
            it.setDonation(donation);
            donation.getItems().add(it);
        }
        return donationRepo.save(donation).getDonationId();
    }

    public List<FeedRow> listOpenFeed(Integer page, Integer size, FeedSort sort, Boolean asc) {
        if (page == null || size == null) {
            return feedRepo.findOpen(null, null, sort, asc);
        }
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        int offset = p * s;
        return feedRepo.findOpen(offset, s, sort, asc);
    }

    public long countOpen() { return feedRepo.countOpen(); }
}
