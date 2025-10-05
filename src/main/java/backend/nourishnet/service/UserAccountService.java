package backend.nourishnet.service;


import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import backend.nourishnet.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);
    private final UserAccountRepository repo;

    public UserAccount findByFirebaseUidOrThrow(String uid) {
        return repo.findByFirebaseUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found for uid"));
    }

    @Transactional
    public List<String> ensureAndGetAuthorities(String uid, String email) {
        log.info("ensureAndGetAuthorities uid={} email={}", uid, email);

        var byUid = repo.findByFirebaseUid(uid);
        if (byUid.isPresent()) {
            return List.of("ROLE_" + byUid.get().getRole().name());
        }

        var byEmail = (email == null ? null : repo.findByEmail(email).orElse(null));
        if (byEmail != null) {
            byEmail.setFirebaseUid(uid);
            repo.create(byEmail); // create/update delega para save
            return List.of("ROLE_" + byEmail.getRole().name());
        }

        var u = new UserAccount();
        u.setFirebaseUid(uid);
        u.setEmail(email);
        u.setName(email);
        u.setRole(Role.DONATOR);
        u.setStatus("ACTIVE");
        repo.create(u);
        return List.of("ROLE_DONATOR");
    }

    @Transactional
    public UserAccount create(String firebaseUid, String email, String name, String bio, Role role, String status) {
        var u = new UserAccount();
        u.setFirebaseUid(firebaseUid);
        u.setEmail(email);
        u.setName(name);
        u.setBio(bio);
        u.setRole(role == null ? Role.DONATOR : role);
        u.setStatus(status == null ? "ACTIVE" : status);
        return repo.create(u);
    }

    @Transactional
    public UserAccount updateProfile(String uid, String name, String bio, Role desiredRole) {
        var current = repo.findByFirebaseUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found for uid"));

        var newRole = (desiredRole != null ? desiredRole : current.getRole());
        var newName = name;
        var newBio  = bio;
        var newStatus = current.getStatus(); // mantém

        int updated = repo.updateProfileByUid(uid, newName, newBio, newRole, newStatus);
        if (updated == 0) throw new IllegalStateException("No rows updated");

        return repo.findByFirebaseUid(uid).orElseThrow();
    }

    @Transactional
    public void deleteByUid(String uid) {
        int deleted = repo.deleteByFirebaseUid(uid);
        if (deleted == 0) throw new IllegalArgumentException("User not found for uid");
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public UserAccount update(Long id, String name, String bio, Role role, String status) {
        var current = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        int n = repo.updateProfileById(id,
                name != null ? name : current.getName(),
                bio,
                role != null ? role : current.getRole(),
                status != null ? status : current.getStatus());
        if (n == 0) throw new IllegalStateException("No rows updated");
        return repo.findById(id).orElseThrow();
    }


    public Page<UserAccount> page(String q, Integer page, Integer size, String sort, String order) {
        int p = Math.max(page == null ? 0 : page, 0);
        int s = Math.max(size == null ? 20 : size, 1);
        var dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var sortBy = (sort == null || sort.isBlank()) ? "userId" : sort;
        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortBy));

        if (q == null || q.isBlank()) return repo.findAll(pageable);
        return repo.findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(q, q, pageable);
    }

    public UserAccount get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
