package backend.nourishnet.service;

import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserAccountService users;
    private final FirebaseAuth firebaseAuth;

    @Transactional
    public UserAccount signup(String email, String password, String name, Role desiredRole) {
        try {
            UserRecord.CreateRequest req = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(name)
                    .setEmailVerified(false)
                    .setDisabled(false);

            UserRecord rec = firebaseAuth.createUser(req);

            Role role = (desiredRole == Role.ONG || desiredRole == Role.DONATOR)
                    ? desiredRole : Role.DONATOR;

            return users.create(rec.getUid(), email, name, null, role, "ACTIVE");

        } catch (FirebaseAuthException e) {
            log.warn("auth.signup.firebase.error email={} code={} msg={}",
                    email, e.getErrorCode(), e.getMessage());
            throw new IllegalArgumentException("Unable to create account: " + e.getMessage());
        }
    }
}
