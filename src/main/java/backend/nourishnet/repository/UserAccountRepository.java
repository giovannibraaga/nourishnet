package backend.nourishnet.repository;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByFirebaseUid(String firebaseUid);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<UserAccount> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email, String name, Pageable pageable);

    @Transactional
    default UserAccount create(UserAccount u) {
        return save(u);
    }

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
           update UserAccount u
              set u.name   = :name,
                  u.bio    = :bio,
                  u.role   = :role,
                  u.status = :status
            where u.firebaseUid = :uid
           """)
    int updateProfileByUid(@Param("uid") String firebaseUid,
                           @Param("name") String name,
                           @Param("bio") String bio,
                           @Param("role") Role role,
                           @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
           update UserAccount u
              set u.name   = :name,
                  u.bio    = :bio,
                  u.role   = :role,
                  u.status = :status
            where u.userId = :id
           """)
    int updateProfileById(@Param("id") Long userId,
                          @Param("name") String name,
                          @Param("bio") String bio,
                          @Param("role") Role role,
                          @Param("status") String status);

    @Transactional
    int deleteByFirebaseUid(String firebaseUid);

    @Transactional
    int deleteByEmail(String email);
}
