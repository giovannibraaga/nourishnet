package backend.nourishnet.service;

import backend.nourishnet.domain.Donation;
import backend.nourishnet.domain.DonationItem;
import backend.nourishnet.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service @RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepo;
    private final FeedJdbcRepository feedRepo;
    private final DonationMatchRepository matchRepo;
    private static final Logger log = LoggerFactory.getLogger(DonationService.class);

    @Transactional
    public Long createDonation(Long donorUserId, Donation donation, List<DonationItem> items) {
        log.info("donation.create.request donor={} items={}", donorUserId, (items != null ? items.size() : null));
        donation.setDonorUserId(donorUserId);
        donation.setStatus("OPEN");
        assert items != null;
        for (DonationItem it : items) {
            it.setDonation(donation);
            donation.getItems().add(it);
            log.debug("donation.create.item linked");
        }
        Long id = donationRepo.save(donation).getDonationId();
        log.info("donation.create.success id={}", id);
        return id;
    }

    public List<FeedRow> listOpenFeed(Integer page, Integer size, FeedSort sort, Boolean asc) {
        log.info("feed.list.request page={} size={} sort={} asc={}", page, size, sort, asc);
        if (page == null || size == null) {
            log.debug("feed.list.nopagination");
            List<FeedRow> result = feedRepo.findOpen(null, null, sort, asc);
            log.info("feed.list.success count={}", result.size());
            return result;
        }
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        int offset = p * s;
        log.debug("feed.list.pagination page={} size={} offset={}", p, s, offset);
        List<FeedRow> result = feedRepo.findOpen(offset, s, sort, asc);
        log.info("feed.list.success count={}", result.size());
        return result;
    }

    public long countOpen() { return feedRepo.countOpen(); }

    @Transactional
    public void updateDonation(Long donationId, Long requesterUserId, Donation newData, List<DonationItem> newItems, boolean isAdmin) {
        log.info("donation.update.request id={} requester={} admin={}", donationId, requesterUserId, isAdmin);
        Donation d = isAdmin
                ? donationRepo.findById(donationId).orElseThrow(() -> new IllegalArgumentException("Donation not found"))
                : donationRepo.findByDonationIdAndDonorUserId(donationId, requesterUserId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found or not owned"));

        if (!"OPEN".equalsIgnoreCase(d.getStatus())) {
            log.warn("donation.update.blocked id={} status={}", donationId, d.getStatus());
            throw new IllegalStateException("Donation cannot be edited in current status");
        }

        if (matchRepo.existsAcceptedForDonation(donationId)) {
            log.warn("donation.update.blocked.accepted id={}", donationId);
            throw new IllegalStateException("Donation has an accepted match and cannot be edited");
        }

        if (newItems == null || newItems.isEmpty()) {
            log.warn("donation.update.invalid.noitems id={}", donationId);
            throw new IllegalArgumentException("At least one item is required");
        }

        for (DonationItem it : newItems) {
            if (it.getQty() == null || it.getQty() <= 0) throw new IllegalArgumentException("Item qty must be > 0");
            String u = it.getUnit();
            if (u == null || !(u.equals("KG") || u.equals("G") || u.equals("L") || u.equals("ML") || u.equals("UN")))
                throw new IllegalArgumentException("Invalid unit");
        }
        if (newData.getPreferredPickupStart() != null && newData.getPreferredPickupEnd() != null &&
                newData.getPreferredPickupStart().isAfter(newData.getPreferredPickupEnd()))
            throw new IllegalArgumentException("Pickup start must be before end");

        d.setAddressText(newData.getAddressText());
        d.setGeoLat(newData.getGeoLat());
        d.setGeoLng(newData.getGeoLng());
        d.setPreferredPickupStart(newData.getPreferredPickupStart());
        d.setPreferredPickupEnd(newData.getPreferredPickupEnd());
        d.setNotes(newData.getNotes());
        d.setExpiresAt(newData.getExpiresAt());

        d.getItems().clear();
        for (DonationItem it : newItems) {
            it.setDonation(d);
            d.getItems().add(it);
        }

        donationRepo.save(d);
        log.info("donation.update.success id={}", donationId);
    }
}
