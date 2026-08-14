package com.nearlog.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtTokenProvider
            jwtTokenProvider;

    private final CustomUserDetailsService
            userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token =
                resolveToken(request);

        if (token != null) {

            try {

                Long userId =
                        jwtTokenProvider
                                .getUserIdFromAccessToken(
                                        token
                                );

                UserPrincipal principal =
                        userDetailsService
                                .loadUserById(
                                        userId
                                );

                UsernamePasswordAuthenticationToken
                        authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContext context =
                        SecurityContextHolder
                                .createEmptyContext();

                context.setAuthentication(
                        authentication
                );

                SecurityContextHolder
                        .setContext(context);

            } catch (
                    JwtException
                    | IllegalArgumentException e
            ) {

                SecurityContextHolder
                        .clearContext();
            }
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    private String resolveToken(
            HttpServletRequest request
    ) {

        String authorization =
                request.getHeader(
                        "Authorization"
                );

        if (
                authorization != null
                        && authorization.startsWith(
                        "Bearer "
                )
        ) {

            return authorization
                    .substring(7);
        }

        return null;
    }
}