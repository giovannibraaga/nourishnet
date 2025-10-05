package backend.nourishnet.api;

import backend.nourishnet.domain.Donation;
import backend.nourishnet.domain.DonationItem;
import backend.nourishnet.dto.CreateDonationRequest;
import backend.nourishnet.dto.CreateDonationResponse;
import backend.nourishnet.repository.FeedRow;
import backend.nourishnet.repository.FeedSort;
import backend.nourishnet.service.DonationService;
import backend.nourishnet.service.UserAccountService;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService service;
    private final UserAccountService userService;

    public DonationController(DonationService service, UserAccountService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public ResponseEntity<CreateDonationResponse> create(Authentication auth,
                                                         @Valid @RequestBody CreateDonationRequest req) {
        var donor = userService.findByFirebaseUidOrThrow(auth.getName());

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
        return ResponseEntity.status(201).body(new CreateDonationResponse(id, "OPEN"));
    }

    @GetMapping
    public ResponseEntity<PageResponse<FeedRow>> feed(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {

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
        return ResponseEntity.ok(new PageResponse<>(pagination, content));
    }
}

