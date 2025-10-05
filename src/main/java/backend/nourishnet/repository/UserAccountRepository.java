package backend.nourishnet.repository;

import backend.nourishnet.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByFirebaseUid(String firebaseUid);
    boolean existsByEmail(String email);
}
