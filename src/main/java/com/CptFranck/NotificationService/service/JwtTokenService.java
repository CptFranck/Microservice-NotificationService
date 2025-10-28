package com.CptFranck.NotificationService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;

    static private String RESSOURCE_ID;

    static private String PRINCIPAL_ATTRIBUTE;

    public JwtTokenService(@Value("${keycloak.auth.jwk-set-uri}") String jwkSetUri,
                           @Value("${keycloak.auth.converter.ressource-id}") String ressourceId,
                           @Value("${keycloak.auth.converter.principal-attribute}") String principalAttribute) {
        RESSOURCE_ID = ressourceId;
        PRINCIPAL_ATTRIBUTE = principalAttribute;
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
        if(ressourceAccess.get(RESSOURCE_ID) == null) return Set.of();

        Map<String, Object> ressource = (Map<String, Object>) ressourceAccess.get(RESSOURCE_ID);

        Collection<String> roles = (Collection<String>) ressource.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private String getPrinciplesClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if(PRINCIPAL_ATTRIBUTE != null)
            claimName = PRINCIPAL_ATTRIBUTE;
        return jwt.getClaimAsString(claimName);
    }
}
