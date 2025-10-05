package backend.nourishnet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String message = "Unauthorized";

        var cause = authException.getCause();
        if (cause instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException oae
                && oae.getError() instanceof BearerTokenError bte) {
            if ("invalid_request".equals(bte.getErrorCode())) {
                message = "No token provided";
            } else if ("invalid_token".equals(bte.getErrorCode())) {
                message = "Invalid or expired token";
            } else {
                message = bte.getDescription() != null ? bte.getDescription() : message;
            }
            response.addHeader("WWW-Authenticate", bte.getHttpStatus().value() == 401
                    ? "Bearer error=\"" + bte.getErrorCode() + "\", error_description=\"" + message + "\""
                    : "Bearer");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());

        om.writeValue(response.getOutputStream(), body);
    }
}
