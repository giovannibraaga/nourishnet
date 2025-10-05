package backend.nourishnet.api;

import backend.nourishnet.repository.EventLogRepository;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
public class EventLogController {
    private final EventLogRepository repo;

    public EventLogController(EventLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('ADMIN','DONATOR','ONG')")
    public ResponseEntity<PageResponse<EventLogRepository.EventRow>> list(
            @PathVariable("id") Long donationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        var pageable = (page == null || size == null)
                ? PageRequest.of(0, Integer.MAX_VALUE)
                : PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        var pg = repo.pageByDonation(donationId, pageable);
        var content = pg.getContent();
        var total = pg.getTotalElements();
        var sz = (size == null) ? content.size() : Math.max(size, 1);
        var current = (page == null) ? 0 : Math.max(page, 0);
        var totalPages = (size == null) ? 1 : pg.getTotalPages();
        var offset = (size == null) ? 0 : current * sz;

        var meta = new PageMeta(current, sz, offset, "event_time", "desc", total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }
}
