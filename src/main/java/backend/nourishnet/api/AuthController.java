package backend.nourishnet.api;

import backend.nourishnet.service.AuthService;
import backend.nourishnet.service.AuthService.LoginResult;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResult r = auth.login(req.email(), req.password());
        return ResponseEntity.ok(new LoginResponse(r.user(), r.idToken(), r.refreshToken(), r.expiresIn()));
    }

    @PostMapping("/signup")
    public ResponseEntity<MeController.MeResponse> signup(@RequestBody SignupRequest req) {
        var u = auth.signup(req.email(), req.password(), req.name(), req.role());
        return ResponseEntity.ok(new MeController.MeResponse(
                u.getUserId(), u.getFirebaseUid(), u.getEmail(), u.getName(),
                u.getBio(), u.getRole().name(), u.getStatus()
        ));
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginResponse(
            MeController.MeResponse user,
            String idToken,
            String refreshToken,
            String expiresIn
    ) {}

    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotBlank @Size(max = 120) String name,
            backend.nourishnet.domain.Role role
    ) {}
}
