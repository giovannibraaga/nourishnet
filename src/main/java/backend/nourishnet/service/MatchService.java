package backend.nourishnet.service;

import backend.nourishnet.domain.Donation;
import backend.nourishnet.domain.DonationMatch;
import backend.nourishnet.repository.DonationMatchRepository;
import backend.nourishnet.repository.DonationRepository;
import backend.nourishnet.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final DonationRepository donationRepo;
    private final DonationMatchRepository matchRepo;
    private final MatchRepository matchJdbc;
    private static final Logger log = LoggerFactory.getLogger(MatchService.class);


    @Transactional
    @CacheEvict(value = { "matches_by_ngo", "matches_count_ngo", "matches_by_donation", "matches_count_donation",
            "feed_open", "feed_count" }, allEntries = true)
    public void accept(Long matchId, java.time.OffsetDateTime pickup) {
        log.info("match.accept id={}", matchId);
        DonationMatch m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        Donation d = donationRepo.findById(m.getDonationId())
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
        if (!"OPEN".equalsIgnoreCase(d.getStatus()) && !"RESERVED".equalsIgnoreCase(d.getStatus())) {
            log.warn("match.accept.blocked donation={} status={}", d.getDonationId(), d.getStatus());
            throw new IllegalArgumentException("Donation not available to accept");
        }
        m.setStatus("ACCEPTED");
        m.setScheduledPickupAt(pickup);
        try {
            matchRepo.save(m);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("match.accept.unique.violated donation={}", d.getDonationId());
            throw new IllegalArgumentException("Another match already accepted for this donation");
        }
        d.setStatus(pickup != null ? "PICKUP_SCHEDULED" : "RESERVED");
        donationRepo.save(d);
        log.info("match.accept.success id={} donation={} pickup={}", matchId, d.getDonationId(), pickup);
    }

    @Transactional
    @CacheEvict(value = { "matches_by_ngo", "matches_count_ngo", "matches_by_donation", "matches_count_donation",
            "feed_open", "feed_count" }, allEntries = true)
    public void reject(Long matchId) {
        log.info("match.reject id={}", matchId);
        DonationMatch m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        m.setStatus("REJECTED");
        matchRepo.save(m);
    }

    @Transactional
    @CacheEvict(value = { "matches_by_ngo", "matches_count_ngo", "matches_by_donation", "matches_count_donation",
            "feed_open", "feed_count" }, allEntries = true)
    public void cancel(Long matchId) {
        log.info("match.cancel id={}", matchId);
        DonationMatch m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        m.setStatus("CANCELED");
        matchRepo.save(m);
    }

    @Transactional
    @CacheEvict(value = { "feed_open", "feed_count" }, allEntries = true)
    public void deliver(Long donationId) {
        log.info("donation.deliver id={}", donationId);
        Donation d = donationRepo.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
        d.setStatus("DELIVERED");
        donationRepo.save(d);
        log.info("donation.deliver.success id={}", donationId);
    }

    @Cacheable(value = "matches_by_ngo",
            key = "T(java.util.Objects).hash(#ngoUserId,#page,#size,#sort,#order)")
    public List<DonationMatch> pageByNgo(Long ngoUserId, Integer page, Integer size, String sort, String order) {
        var sortObj = MatchRepository.orderBy(sort, order);
        if (page == null || size == null) {
            var pg = matchJdbc.findByNgo(ngoUserId, PageRequest.of(0, Integer.MAX_VALUE, sortObj));
            return pg.getContent();
        }
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        return matchJdbc.findByNgo(ngoUserId, PageRequest.of(p, s, sortObj)).getContent();
    }

    @Cacheable(value = "matches_count_ngo", key = "#ngoUserId")
    public long countByNgo(Long ngoUserId) {
        return matchJdbc.countByNgo(ngoUserId);
    }


    @Cacheable(value = "matches_by_donation",
            key = "T(java.util.Objects).hash(#donationId,#page,#size,#sort,#order)")
    public List<DonationMatch> pageByDonation(Long donationId, Integer page, Integer size, String sort, String order) {
        var sortObj = MatchRepository.orderBy(sort, order);
        if (page == null || size == null) {
            var pg = matchJdbc.findByDonation(donationId, PageRequest.of(0, Integer.MAX_VALUE, sortObj));
            return pg.getContent();
        }
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        return matchJdbc.findByDonation(donationId, PageRequest.of(p, s, sortObj)).getContent();
    }

    public long countByDonation(Long donationId) {
        return matchJdbc.countByDonation(donationId);
    }
}