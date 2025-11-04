package com.bangvan.config;

import com.bangvan.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Configuration
@Slf4j(topic = "JWT_AUTH_FILTER")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();


    private static final List<String> PUBLIC_ENDPOINT = List.of(
            "/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/verify/**",
            "/api/webhooks/**",
            "/api/payments/vnpay-callback",
            "/api/home",
            "/hello"
    );


    private static final List<String> PUBLIC_GET_ENDPOINTS = List.of(
            "/api/products",
            "/api/products/**",
            "/api/categories",
            "/api/categories/**"
    );

    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService, UserDetailsService userDetailsService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();


        boolean isPublicEndpoint = PUBLIC_ENDPOINT.stream()
                .anyMatch(path -> antPathMatcher.match(path, requestURI));

        if (isPublicEndpoint) {
            return true;
        }


        if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {

            boolean isPublicGet = PUBLIC_GET_ENDPOINTS.stream()
                    .anyMatch(path -> antPathMatcher.match(path, requestURI));
            if (isPublicGet) {
                return true;
            }
        }


        return false;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {



        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String jwt = authHeader.substring(7);
            String username = null;
            try {
                username = jwtService.extractUsername(jwt);
            } catch (ExpiredJwtException ex) {
                log.warn("JWT Token has expired. URI: {}", request.getRequestURI());

                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            } catch (SignatureException ex) {
                log.warn("Invalid JWT signature. URI: {}", request.getRequestURI());
                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            } catch (MalformedJwtException ex) {
                log.warn("Malformed JWT token. URI: {}", request.getRequestURI());
                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            } catch (UnsupportedJwtException ex) {
                log.warn("Unsupported JWT token. URI: {}", request.getRequestURI());
                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            } catch (IllegalArgumentException ex) {
                log.warn("JWT claims string is empty or token is null. URI: {}", request.getRequestURI());
                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            } catch (JwtException ex) {
                log.error("Unhandled JWT exception: {}. URI: {}", ex.getMessage(), request.getRequestURI());
                handlerExceptionResolver.resolveException(request, response, null, ex);
                return;
            }


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("User '{}' authenticated successfully via JWT. URI: {}", username, request.getRequestURI());
                } else {
                    log.warn("JWT token validation failed for user '{}'. URI: {}", username, request.getRequestURI());
                }
            }

            filterChain.doFilter(request, response);
        }
        catch (Exception exception) {
            log.error("Unexpected error in Authentication Filter: {}. URI: {}", exception.getMessage(), request.getRequestURI(), exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}