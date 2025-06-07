package tl.gov.mci.lis.configs.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;
    private final JwtSessionService jwtSessionService;
    private static final List<RequestMatcher> publicEndpoints = List.of(
            new AntPathRequestMatcher("/api/v1/users", "POST"),
            new AntPathRequestMatcher("/api/v1/users/authenticate", "POST"),
            new AntPathRequestMatcher("/api/v1/users/otp/**", "POST"),
            new AntPathRequestMatcher("/api/v1/users/otp/**", "PUT"),
            new AntPathRequestMatcher("/api/v1/users/activate/**", "POST"),
            new AntPathRequestMatcher("/roles", "GET")
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("Requested Path : {}", request.getServletPath());

        // 1) Skip any public endpoint
        if (publicEndpoints.stream().anyMatch(matcher -> matcher.matches(request))) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extract JWT from HttpOnly cookie
        String jwtToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Validate token structure
        String username;
        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            reject(response, "Token expired. Please login again.", e);
            return;
        } catch (JwtException | IllegalArgumentException e) {
            reject(response, "Invalid token. Please login again.", e);
            return;
        }


        // 4) Check session validity
        if (!jwtSessionService.isValidSession(username, jwtToken)) {
            reject(response, "Session expired. Please login again.", null);
            return;
        }

        // 5) Validate the token and populate SecurityContext
        if (SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.validateToken(jwtToken, username)) {

            String role = jwtUtil.extractRole(jwtToken);
            var authority = new SimpleGrantedAuthority(role);
            var auth = new UsernamePasswordAuthenticationToken(
                    username, null, List.of(authority)
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.debug("Authenticated user {}", username);
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String message, Exception ex)
            throws IOException {
        if (ex != null) logger.warn(message, ex);
        else logger.warn(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }
}
