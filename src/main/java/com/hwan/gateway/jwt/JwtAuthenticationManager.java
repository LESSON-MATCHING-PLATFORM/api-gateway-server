package com.hwan.gateway.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String token =
                authentication.getCredentials()
                        .toString();

        if (!jwtProvider.validateToken(token)) {
            return Mono.empty();
        }

        String userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        userId,
                        token,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                )
                        )
                )
        );
    }
}
