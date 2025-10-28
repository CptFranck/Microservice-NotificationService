package com.CptFranck.NotificationService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;

    private final String resourceId;

    private final String principalAttribute;

    public JwtTokenService(@Value("${keycloak.auth.jwk-set-uri}") String jwkSetUri,
                           @Value("${keycloak.auth.converter.ressource-id}") String ressourceId_,
                           @Value("${keycloak.auth.converter.principal-attribute}") String principalAttribute_) {
        this.resourceId = ressourceId_;
        this.principalAttribute = principalAttribute_;
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    public Authentication parseToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = getPrinciplesClaimName(jwt);
        Collection<GrantedAuthority> authorities = extractRessourceRoles(jwt);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private Collection<GrantedAuthority> extractRessourceRoles(Jwt jwt) {
        if(jwt.getClaim("resource_access") == null) return Set.of();

        Map<String, Object> ressourceAccess = jwt.getClaim("resource_access");
        if(ressourceAccess.get(resourceId) == null) return Set.of();

        Map<String, Object> ressource = (Map<String, Object>) ressourceAccess.get(resourceId);

        Collection<String> roles = Optional.ofNullable((Collection<String>) ressource.get("roles"))
                .orElse(Collections.emptyList());

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private String getPrinciplesClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if(principalAttribute != null)
            claimName = principalAttribute;
        return jwt.getClaimAsString(claimName);
    }
}
