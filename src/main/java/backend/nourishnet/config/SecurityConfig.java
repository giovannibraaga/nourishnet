package backend.nourishnet.config;

import backend.nourishnet.security.FirebaseJwtAuthConverter;
import backend.nourishnet.security.RestAccessDeniedHandler;
import backend.nourishnet.security.RestAuthEntryPoint;
import backend.nourishnet.service.UserAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new RestAuthEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/actuator/health", "/error",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers("/api/feed/open").hasAnyRole("ADMIN", "DONATOR", "ONG")
                        .requestMatchers("/api/alerts").hasAnyRole("ADMIN", "DONATOR", "ONG")
                        .requestMatchers("/api/donations/*/events").hasAnyRole("ADMIN", "DONATOR", "ONG")
                        .requestMatchers("/api/matches/my").hasAnyRole("ADMIN", "ONG")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/signup").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    FirebaseJwtAuthConverter firebaseJwtAuthConverter(UserAccountService userAccountService) {
        return new FirebaseJwtAuthConverter(userAccountService);
    }

    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<backend.nourishnet.security.RequestMdcFilter> mdcFilter() {
        var bean = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(new backend.nourishnet.security.RequestMdcFilter());
        bean.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
