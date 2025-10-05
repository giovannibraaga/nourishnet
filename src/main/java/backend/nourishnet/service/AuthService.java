package backend.nourishnet.service;

import backend.nourishnet.api.MeController.MeResponse;
import backend.nourishnet.domain.Role;
import backend.nourishnet.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserAccountService users;
    private final FirebaseAuth firebaseAuth;

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://identitytoolkit.googleapis.com")
                .build();
    }

    @Transactional
    public UserAccount signup(String email, String password, String name, Role desiredRole) {
        try {
            var req = new com.google.firebase.auth.UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(name)
                    .setEmailVerified(false)
                    .setDisabled(false);

            var rec = firebaseAuth.createUser(req);
            var role = (desiredRole == Role.ONG || desiredRole == Role.DONATOR) ? desiredRole : Role.DONATOR;
            return users.create(rec.getUid(), email, name, null, role, "ACTIVE");

        } catch (FirebaseAuthException e) {
            log.warn("auth.signup.firebase.error email={} code={} msg={}", email, e.getErrorCode(), e.getMessage());
            throw new IllegalArgumentException("Unable to create account: " + e.getMessage());
        }
    }

    public LoginResult login(String email, String password) {
        var path = "/v1/accounts:signInWithPassword?key=" + firebaseApiKey;
        var payload = new SignInRequest(email, password, true);

        SignInResponse res;
        try {
            res = restClient().post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(SignInResponse.class);
        } catch (RestClientResponseException ex) {
            log.warn("auth.login.firebase.rest.error email={} status={} body={}",
                    email, ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new IllegalArgumentException("Invalid credentials");
        } catch (Exception ex) {
            log.warn("auth.login.firebase.rest.error email={} msg={}", email, ex.getMessage());
            throw new IllegalArgumentException("Login failed");
        }

        if (res == null || res.idToken == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        final String uid;
        final String tokenEmail;
        final String tokenName;
        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(res.idToken);
            uid = decoded.getUid();
            tokenEmail = decoded.getEmail() != null ? decoded.getEmail() : email;
            tokenName = decoded.getName();
        } catch (FirebaseAuthException e) {
            log.warn("auth.login.verify.error email={} msg={}", email, e.getMessage());
            throw new IllegalArgumentException("Invalid token returned by provider");
        }

        UserAccount ua;
        try {
            ua = users.findByFirebaseUidOrThrow(uid);
        } catch (IllegalArgumentException notFound) {
            ua = users.create(
                    uid,
                    tokenEmail,
                    (tokenName != null ? tokenName : tokenEmail),
                    null,
                    Role.DONATOR,
                    "ACTIVE"
            );
        }

        var me = new MeResponse(
                ua.getUserId(),
                ua.getFirebaseUid(),
                ua.getEmail(),
                ua.getName(),
                ua.getBio(),
                ua.getRole() != null ? ua.getRole().name() : null,
                ua.getStatus()
        );

        return new LoginResult(me, res.idToken, res.refreshToken, res.expiresIn);
    }

    private record SignInRequest(
            String email,
            String password,
            @JsonProperty("returnSecureToken") boolean returnSecureToken
    ) {
    }

    private record SignInResponse(
            String idToken,
            String email,
            String refreshToken,
            String expiresIn,
            String localId
    ) {
    }

    public record LoginResult(
            MeResponse user,
            String idToken,
            String refreshToken,
            String expiresIn
    ) {
    }
}