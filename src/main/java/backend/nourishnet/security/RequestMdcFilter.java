package backend.nourishnet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class RequestMdcFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String rid = UUID.randomUUID().toString();
        MDC.put("rid", rid);
        MDC.put("path", request.getRequestURI());
        MDC.put("method", request.getMethod());
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null) MDC.put("uid", a.getName());
        try { chain.doFilter(request, response); }
        finally { MDC.clear(); }
    }
}
