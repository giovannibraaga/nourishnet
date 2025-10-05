package backend.nourishnet.api;

import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import backend.nourishnet.domain.Alert;
import backend.nourishnet.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);
    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DONATOR','ONG')")
    public ResponseEntity<PageResponse<Alert>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long donationId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        List<Alert> content = service.search(status, severity, donationId, page, size, sort, order);
        long total = service.count(status, severity, donationId);

        int p = (page == null) ? 0 : Math.max(page, 0);
        int s = (size == null) ? content.size() : Math.max(size, 1);
        int totalPages = (page == null || size == null) ? 1 : (int) Math.ceil(total / (double) s);
        int offset = (page == null || size == null) ? 0 : p * s;

        String sortProp = (sort == null) ? "created_at" : sort;
        String sortDir = (order == null) ? "desc" : order;

        var meta = new PageMeta(p, s, offset, sortProp, sortDir, total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }

    @PostMapping("/donations/{id}:close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CountResponse> close(@PathVariable("id") Long donationId) {
        log.info("Request to close alerts for donationId={} received", donationId);
        int updated = service.closeOpenForDonation(donationId);
        if (updated == 0) {
            log.warn("No open alerts were closed for donationId={}", donationId);
        } else {
            log.info("Closed {} open alert(s) for donationId={}", updated, donationId);
        }
        return ResponseEntity.ok(new CountResponse(updated));
    }

    public record CountResponse(int updated) {
    }
}
