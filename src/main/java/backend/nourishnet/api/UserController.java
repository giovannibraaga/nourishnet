package backend.nourishnet.api;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.service.UserAccountService;
import backend.nourishnet.support.PageMeta;
import backend.nourishnet.support.PageResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('DONATOR','ONG','ADMIN')")
public class UserController {

    private final UserAccountService users;

    public UserController(UserAccountService users) {
        this.users = users;
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        var pg = users.page(q, page, size, sort, order);
        var content = pg.getContent().stream().map(UserResponse::from).toList();

        int p = pg.getNumber();
        int s = pg.getSize();
        long total = pg.getTotalElements();
        int totalPages = pg.getTotalPages();
        int offset = p * s;

        var meta = new PageMeta(p, s, offset, (sort == null ? "userId" : sort), (order == null ? "asc" : order), total, totalPages);
        return ResponseEntity.ok(new PageResponse<>(meta, content));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        var u = users.get(id);
        return ResponseEntity.ok(UserResponse.from(u));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest req) {
        var u = users.create(req.firebaseUid(), req.email(), req.name(), req.bio(), req.role(), req.status());
        return ResponseEntity.ok(UserResponse.from(u));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        var u = users.update(id, req.name(), req.bio(), req.role(), req.status());
        return ResponseEntity.ok(UserResponse.from(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record UserResponse(
            Long userId,
            String firebaseUid,
            String email,
            String name,
            String bio,
            String role,
            String status
    ) {
        public static UserResponse from(UserAccount u) {
            return new UserResponse(
                    u.getUserId(),
                    u.getFirebaseUid(),
                    u.getEmail(),
                    u.getName(),
                    u.getBio(),
                    u.getRole() != null ? u.getRole().name() : null,
                    u.getStatus()
            );
        }
    }

    public record CreateUserRequest(
            String firebaseUid,
            @NotBlank @Email String email,
            @NotBlank @Size(max = 120) String name,
            @Size(max = 4000) String bio,
            Role role,
            String status
    ) {}

    public record UpdateUserRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 4000) String bio,
            Role role,
            String status
    ) {}
}