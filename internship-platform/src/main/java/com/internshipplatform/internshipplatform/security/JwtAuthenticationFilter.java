package com.internshipplatform.internshipplatform.security;

import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = jwtUtil.getUserIdFromRequest(request); // now returns null if missing

            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);

                if (user != null) {
                    String authorityName = "ROLE_" + user.getRole().name();
                    List<GrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority(authorityName));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ignored) {
            // invalid token -> stay anonymous, let SecurityConfig decide (401/403)
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // swagger
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.equals("/swagger-ui.html")) {
            return true;
        }

        // auth
        if (path.startsWith("/api/auth")) {
            return true;
        }

        // public GET internships endpoints
        if ("GET".equals(request.getMethod())) {
            if (path.equals("/api/internships")
                    || path.equals("/api/internships/search")
                    || path.matches("^/api/internships/\\d+$")) {
                return true;
            }
        }

        return false;
    }

}
