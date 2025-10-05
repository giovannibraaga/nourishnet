package backend.nourishnet.service;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userRepo;

    @Transactional
    public List<SimpleGrantedAuthority> ensureAndGetAuthorities(String firebaseUid, String email) {
        if (firebaseUid == null || firebaseUid.isBlank()) {
            throw new IllegalArgumentException("JWT sem UID (user_id/sub)");
        }
        UserAccount user = userRepo.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    UserAccount u = new UserAccount();
                    u.setFirebaseUid(firebaseUid);
                    u.setEmail(email != null ? email : (firebaseUid + "@unknown.local"));
                    u.setName(email != null ? email : firebaseUid);
                    u.setRole(Role.DONATOR);
                    u.setStatus("ACTIVE");
                    return userRepo.save(u);
                });

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Conta suspensa");
        }

        return List.of(new SimpleGrantedAuthority(user.getRole().asAuthority()));
    }

    public UserAccount findByFirebaseUidOrThrow(String firebaseUid) {
        return userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para UID=" + firebaseUid));
    }

    @Transactional
    public UserAccount updateProfile(String firebaseUid, String name, String bio, Role requestedRole) {
        UserAccount u = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para UID=" + firebaseUid));

        u.setName(name);
        u.setBio(bio);

        if (requestedRole != null && requestedRole != Role.ADMIN) {
            u.setRole(requestedRole);
        }
        return userRepo.save(u);
    }
}
