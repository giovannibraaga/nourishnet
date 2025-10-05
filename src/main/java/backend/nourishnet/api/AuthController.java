package backend.nourishnet.api;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.service.AuthService;
import backend.nourishnet.api.MeController.MeResponse; // reaproveita o DTO que você já usa
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/signup")
    public ResponseEntity<MeResponse> signup(@RequestBody SignupRequest req) {
        UserAccount u = auth.signup(req.email(), req.password(), req.name(), req.role());
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

    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotBlank @Size(max = 120) String name,
            Role role // opcional (aceitamos só DONATOR/ONG; ADMIN será ignorado)
    ) {}
}
