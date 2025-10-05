package backend.nourishnet.api;

import backend.nourishnet.domain.DonationMatch;
import backend.nourishnet.service.MatchService;
import backend.nourishnet.service.UserAccountService;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchService service;
    private final UserAccountService userService;
    private static final Logger log = LoggerFactory.getLogger(DonationController.class);

    public MatchController(MatchService service, UserAccountService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping("/matches/{id}:accept")
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<Void> accept(@PathVariable("id") Long matchId, @jakarta.validation.Valid @RequestBody AcceptReq req) {
        log.info("api.match.accept id={}", matchId);
        service.accept(matchId, req.pickupAt());
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @PostMapping("/matches/{id}:reject")
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> reject(@PathVariable("id") Long matchId) {
        service.reject(matchId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/matches/{id}:cancel")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long matchId) {
        service.cancel(matchId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/donations/{id}:deliver")
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deliver(@PathVariable("id") Long donationId) {
        service.deliver(donationId);
        return ResponseEntity.noContent().build();
    }

    public record AcceptReq(@NotNull OffsetDateTime pickupAt) {
    }

    @GetMapping("/matches/my")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<PageResponse<backend.nourishnet.domain.DonationMatch>> myPaged(
            Authentication auth,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {
        var ngo = userService.findByFirebaseUidOrThrow(auth.getName());
        var content = service.pageByNgo(ngo.getUserId(), page, size, sort, order);
        long total = (page==null||size==null) ? content.size() : service.countByNgo(ngo.getUserId());
        int sz = (size==null)? content.size(): Math.max(size,1);
        int pg = (page==null)? 0 : Math.max(page,0);
        int totalPages = (page==null||size==null)? 1 : (int)Math.ceil(total/(double)sz);
        int offset = (page==null||size==null)? 0 : pg*sz;
        var meta = new PageMeta(pg, sz, offset, (sort==null?"created":sort), (order==null?"asc":order), total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }
}