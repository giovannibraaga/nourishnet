package backend.nourishnet.api;

import backend.nourishnet.service.DonationService;
import backend.nourishnet.support.FeedRow;
import backend.nourishnet.support.FeedSort;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final DonationService donationService;

    public FeedController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN','DONATOR','ONG')")
    public ResponseEntity<PageResponse<FeedRow>> listOpen(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) FeedSort sort,
            @RequestParam(required = false) Boolean asc
    ) {
        List<FeedRow> content = donationService.listOpenFeed(page, size, sort, asc);
        long total = donationService.countOpen();

        int p = (page == null) ? 0 : Math.max(page, 0);
        int s = (size == null) ? content.size() : Math.max(size, 1);
        int totalPages = (page == null || size == null) ? 1 : (int) Math.ceil(total / (double) s);
        int offset = (page == null || size == null) ? 0 : p * s;

        String sortProp = (sort == null) ? "URGENCY" : sort.name();
        String sortDir = (asc == null || asc) ? "asc" : "desc";

        var meta = new PageMeta(p, s, offset, sortProp, sortDir, total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }
}
