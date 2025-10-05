package backend.nourishnet.api;

import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.service.DeliveryService;
import backend.nourishnet.service.UserAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
public class DeliveryController {
    private final DeliveryService service;
    private final UserAccountService userService;
    private static final Logger log = LoggerFactory.getLogger(DeliveryController.class);

    public DeliveryController(DeliveryService service, UserAccountService userService) {
        this.service = service; this.userService = userService;
    }

    @PostMapping("/{id}/proofs")
    @PreAuthorize("hasAnyRole('ONG','ADMIN')")
    public ResponseEntity<IdResponse> addProof(Authentication auth, @PathVariable("id") Long donationId, @Valid @RequestBody ProofReq req) {
        UserAccount receiver = userService.findByFirebaseUidOrThrow(auth.getName());
        log.info("Add proof request received: donationId={}, userId={}, type={}", donationId, receiver.getUserId(), req.type());
        Long id = service.addProof(donationId, receiver.getUserId(), req.type(), req.storageRef());
        log.info("Proof created successfully: donationId={}, proofId={}", donationId, id);
        return ResponseEntity.status(201).body(new IdResponse(id));
    }

    public record ProofReq(@NotBlank String type, @NotBlank String storageRef) {}
    public record IdResponse(Long id) {}
}
