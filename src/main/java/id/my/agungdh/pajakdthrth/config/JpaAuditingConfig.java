package id.my.agungdh.pajakdthrth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.empty();
            }

            // Principal is now Long (userId)
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return Optional.of((Long) principal);
            } else if (principal instanceof String) {
                // Fallback attempt if something weird happened, though we expect Long
                try {
                    return Optional.of(Long.valueOf((String) principal));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }

            return Optional.empty();
        };
    }
}
