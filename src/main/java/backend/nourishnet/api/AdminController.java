package backend.nourishnet.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final JdbcTemplate jdbc;

    public AdminController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/bi:refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Msg> refreshBI() {
        jdbc.execute("begin NOURISHNET_DEV.PR_REFRESH_BI; end;");
        return ResponseEntity.ok(new Msg("ok"));
    }

    public record Msg(String status) {
    }
}