package backend.nourishnet.api;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.service.UserAccountService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MeController {
    private final UserAccountService userService;

    public MeController(UserAccountService userService) {
        this.userService = userService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication auth) {
        String uid = auth.getName();
        UserAccount u = userService.findByFirebaseUidOrThrow(uid);
        return ResponseEntity.ok(new MeResponse(
                u.getUserId(),
                u.getFirebaseUid(),
                u.getEmail(),
                u.getName(),
                u.getBio(),
                u.getRole().name(),
                u.getStatus()
        ));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('DONATOR','ONG','ADMIN')")
    public ResponseEntity<MeResponse> update(Authentication auth, @RequestBody MeUpdateRequest req) {
        String uid = auth.getName();
        UserAccount u = userService.updateProfile(uid, req.name(), req.bio(), req.role());
        return ResponseEntity.ok(new MeResponse(
                u.getUserId(),
                u.getFirebaseUid(),
                u.getEmail(),
                u.getName(),
                u.getBio(),
                u.getRole().name(),
                u.getStatus()
        ));
    }

    public record MeResponse(
            Long userId,
            String firebaseUid,
            String email,
            String name,
            String bio,
            String role,
            String status
    ) {}

    public record MeUpdateRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 4000) String bio,
            Role role
    ) {}
}
