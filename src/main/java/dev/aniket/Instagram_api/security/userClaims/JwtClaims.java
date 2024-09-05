package dev.aniket.Instagram_api.security.userClaims;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class JwtClaims {
    private String username;
    private String email;
}
