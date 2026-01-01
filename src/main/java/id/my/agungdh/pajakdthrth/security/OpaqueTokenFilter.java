package id.my.agungdh.pajakdthrth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class OpaqueTokenFilter extends OncePerRequestFilter {

    private final RedisTokenService redisTokenService;
    private final id.my.agungdh.pajakdthrth.repository.UserRepository userRepository;

    public OpaqueTokenFilter(RedisTokenService redisTokenService,
            id.my.agungdh.pajakdthrth.repository.UserRepository userRepository) {
        this.redisTokenService = redisTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            redisTokenService.validateToken(token).ifPresent(userId -> {
                userRepository.findById(Long.valueOf(userId)).ifPresent(user -> {
                    org.springframework.security.core.authority.SimpleGrantedAuthority authority = new org.springframework.security.core.authority.SimpleGrantedAuthority(
                            "ROLE_" + user.getRole().name());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, java.util.Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            });
        }

        filterChain.doFilter(request, response);
    }
}
