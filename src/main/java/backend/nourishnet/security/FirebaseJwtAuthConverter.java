package backend.nourishnet.security;

import backend.nourishnet.service.UserAccountService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class FirebaseJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserAccountService userAccountService;

    public FirebaseJwtAuthConverter(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String uid = jwt.getClaimAsString("user_id");
        if (uid == null || uid.isBlank()) {
            uid = jwt.getSubject();
        }
        String email = jwt.getClaimAsString("email");

        Collection<SimpleGrantedAuthority> authorities =
                userAccountService.ensureAndGetAuthorities(uid, email);

        return new JwtAuthenticationToken(jwt, authorities, uid);
    }
}
