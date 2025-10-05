package backend.nourishnet.api;

import backend.nourishnet.domain.DonationMatch;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.service.MatchService;
import backend.nourishnet.service.UserAccountService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

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

    @PostMapping("/donations/{id}/matches")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<IdResponse> request(org.springframework.security.core.Authentication auth, @PathVariable("id") Long donationId) {
        var ngo = userService.findByFirebaseUidOrThrow(auth.getName());
        log.info("api.match.request donation={} uid={}", donationId, auth.getName());
        Long id = service.requestMatch(donationId, ngo.getUserId());
        return org.springframework.http.ResponseEntity.status(201).body(new IdResponse(id));
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

    @GetMapping("/matches/my")
    @PreAuthorize("hasRole('ONG') or hasRole('ADMIN')")
    public ResponseEntity<List<DonationMatch>> my(Authentication auth) {
        UserAccount ngo = userService.findByFirebaseUidOrThrow(auth.getName());
        return ResponseEntity.ok(service.listByNgo(ngo.getUserId()));
    }

    @PostMapping("/donations/{id}:deliver")
    @PreAuthorize("hasRole('DONATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deliver(@PathVariable("id") Long donationId) {
        service.deliver(donationId);
        return ResponseEntity.noContent().build();
    }

    public record IdResponse(Long id) {
    }

    public record AcceptReq(@NotNull OffsetDateTime pickupAt) {
    }
}