package com.CptFranck.NotificationService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;

    public JwtTokenService( @Value("${keycloak.auth.jwk-set-uri}") String jwkSetUri) {
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();;
    }

    public Authentication parseToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getClaimAsString("preferred_username");
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
