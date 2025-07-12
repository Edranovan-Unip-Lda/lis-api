package tl.gov.mci.lis.configs.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import tl.gov.mci.lis.models.dadosmestre.Role;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "aGIpirZikZXNMTmIJUJNFycoFayRWjKH8BeXkHvsl9Jpzvkh5dCxjHqtBxrPvihw";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));


    /**
     * Generates a JWT token with the given username as the subject.
     *
     * @param username the username to be included as the subject of the token
     * @return the generated JWT token
     */
    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) // Defaults to HS512
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw e; // Let caller handle expired tokens separately
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    /**
     * Validates the given JWT token by checking if the username inside the token
     * matches with the given username and if the token has not expired.
     *
     * @param token    the JWT token to be validated
     * @param username the expected username
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            return username.equals(extractUsername(token)) && isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the given JWT token has expired.
     *
     * @param token the JWT token
     * @return true if the token has expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return !Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
}
