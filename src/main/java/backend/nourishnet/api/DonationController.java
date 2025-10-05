package backend.nourishnet.api;

import backend.nourishnet.domain.Donation;
import backend.nourishnet.domain.DonationItem;
import backend.nourishnet.domain.DonationMatch;
import backend.nourishnet.dto.CreateDonationRequest;
import backend.nourishnet.dto.CreateDonationResponse;
import backend.nourishnet.service.MatchService;
import backend.nourishnet.support.FeedRow;
import backend.nourishnet.support.FeedSort;
import backend.nourishnet.service.DonationService;
import backend.nourishnet.service.UserAccountService;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService service;
    private final MatchService matchService;
    private final UserAccountService userService;
    private static final Logger log = LoggerFactory.getLogger(DonationController.class);

    public DonationController(DonationService service, MatchService matchService, UserAccountService userService) {
        this.service = service;
        this.matchService = matchService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public ResponseEntity<CreateDonationResponse> create(Authentication auth,
                                                         @Valid @RequestBody CreateDonationRequest req) {
        log.info("api.donation.create uid={} items_count={}", auth.getName(), req.items().size());
        var donor = userService.findByFirebaseUidOrThrow(auth.getName());
        log.debug("Found donor with userId={}", donor.getUserId());
        Donation d = new Donation();
        d.setAddressText(req.addressText());
        d.setGeoLat(req.geoLat());
        d.setGeoLng(req.geoLng());
        d.setPreferredPickupStart(req.preferredPickupStart());
        d.setPreferredPickupEnd(req.preferredPickupEnd());
        d.setNotes(req.notes());
        d.setExpiresAt(req.expiresAt());

        List<DonationItem> items = req.items().stream().map(r -> {
            DonationItem it = new DonationItem();
            it.setName(r.name());
            it.setUnit(r.unit());
            it.setQty(r.qty());
            it.setCategory(r.category());
            it.setBestBeforeDate(r.bestBeforeDate());
            it.setNotes(r.notes());
            return it;
        }).toList();

        Long id = service.createDonation(donor.getUserId(), d, items);
        log.info("api.donation.create success id={} uid={}", id, auth.getName());
        return ResponseEntity.status(201).body(new CreateDonationResponse(id, "OPEN"));
    }

    @GetMapping
    public ResponseEntity<PageResponse<FeedRow>> feed(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {

        log.info("api.donation.feed page={} size={} sort={} order={}", page, size, sort, order);
        var sortEnum = FeedSort.of(sort);
        Boolean asc = (order == null) ? null : !order.equalsIgnoreCase("desc");

        var content = service.listOpenFeed(page, size, sortEnum, asc);

        long total = (page == null || size == null) ? content.size() : service.countOpen();
        int sz = (size == null) ? content.size() : Math.max(size, 1);
        int pg = (page == null) ? 0 : Math.max(page, 0);
        int totalPages = (page == null || size == null) ? 1 : (int) Math.ceil(total / (double) sz);
        int offset = (page == null || size == null) ? 0 : pg * sz;
        String ord = (order == null) ? "asc" : (order.equalsIgnoreCase("desc") ? "desc" : "asc");

        var pagination = new PageMeta(pg, sz, offset, sortEnum.name().toLowerCase(), ord, total, totalPages);
        log.info("api.donation.feed success items_count={} total={} pages={}", content.size(), total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(pagination, content));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DONATOR','ADMIN')")
    public org.springframework.http.ResponseEntity<Void> update(org.springframework.security.core.Authentication auth,
                                                                @PathVariable("id") Long donationId,
                                                                @Valid @RequestBody CreateDonationRequest req) {
        var requester = userService.findByFirebaseUidOrThrow(auth.getName());
        boolean isAdmin = requester.getRole().name().equals("ADMIN");
        log.info("api.donation.update id={} uid={} admin={}", donationId, auth.getName(), isAdmin);

        Donation d = new Donation();
        d.setAddressText(req.addressText());
        d.setGeoLat(req.geoLat());
        d.setGeoLng(req.geoLng());
        d.setPreferredPickupStart(req.preferredPickupStart());
        d.setPreferredPickupEnd(req.preferredPickupEnd());
        d.setNotes(req.notes());
        d.setExpiresAt(req.expiresAt());

        java.util.List<DonationItem> items = req.items().stream().map(r -> {
            var it = new DonationItem();
            it.setName(r.name());
            it.setUnit(r.unit());
            it.setQty(r.qty());
            it.setCategory(r.category());
            it.setBestBeforeDate(r.bestBeforeDate());
            it.setNotes(r.notes());
            return it;
        }).toList();

        service.updateDonation(donationId, requester.getUserId(), d, items, isAdmin);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @GetMapping("/donations/{id}/matches")
    @PreAuthorize("hasAnyRole('DONATOR','ADMIN')")
    public ResponseEntity<PageResponse<DonationMatch>> listByDonation(
            @PathVariable("id") Long donationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        var content = matchService.pageByDonation(donationId, page, size, sort, order);
        long total = (page==null||size==null) ? content.size() : matchService.countByDonation(donationId);
        int sz = (size==null)? content.size(): Math.max(size,1);
        int pg = (page==null)? 0 : Math.max(page,0);
        int totalPages = (page==null||size==null)? 1 : (int)Math.ceil(total/(double)sz);
        int offset = (page==null||size==null)? 0 : pg*sz;
        var meta = new PageMeta(pg, sz, offset, (sort==null?"created":sort), (order==null?"asc":order), total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }
}

