package backend.nourishnet.api;

import backend.nourishnet.repository.EventLogRepository;
import backend.nourishnet.service.EventLogService;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
public class EventLogController {
    private final EventLogService service;

    public EventLogController(EventLogService service) {
        this.service = service;
    }

    @GetMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('ADMIN','DONATOR','ONG')")
    public ResponseEntity<PageResponse<EventLogRepository.EventRow>> list(
            @PathVariable("id") Long donationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        var content = service.pageByDonation(donationId, page, size);
        var total = service.countByDonation(donationId);

        int p = (page == null) ? 0 : Math.max(page, 0);
        int s = (size == null) ? content.size() : Math.max(size, 1);
        int totalPages = (page == null || size == null) ? 1 : (int) Math.ceil(total / (double) s);
        int offset = (page == null || size == null) ? 0 : p * s;

        var meta = new PageMeta(p, s, offset, "event_time", "desc", total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }
}
