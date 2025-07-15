package tl.gov.mci.lis.configs.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.models.user.User;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtSessionService {

    private static final String ACTIVE_USER_SET = "jwt:activeUsernames";
    private static final String USER_DETAILS_PREFIX = "userDetails:"; // Redis key prefix

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;
    private final ValueOperations<String, String> valueOps;
    private final SetOperations<String, String> setOps;

    public JwtSessionService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.setOps = redisTemplate.opsForSet();
    }

    /**
     * Store the latest JWT for a user, and track the username in a Redis Set.
     * You can also specify a TTL if you want sessions to expire automatically:
     * valueOps.set(username, token, Duration.ofHours(2));
     */
    public void storeActiveToken(String username, String token) {
        valueOps.set(username, token, Duration.ofHours(2));
        setOps.add(ACTIVE_USER_SET, username);
    }

    /**
     * Retrieve the currently stored token for a user.
     */
    public String getActiveToken(String username) {
        return valueOps.get(username);
    }

    /**
     * Validate that the provided token matches the stored one.
     */
    public boolean isValidSession(String username, String token) {
        String current = getActiveToken(username);
        return token != null && token.equals(current);
    }

    /**
     * Invalidate a session: remove both the key and the username from the set.
     */
    public void invalidateSession(String username) {
        redisTemplate.delete(username);
        setOps.remove(ACTIVE_USER_SET, username);
    }

    /**
     * List all active usernames by reading the Redis Set members.
     */
    public Set<String> getActiveUsernames() {
        return setOps.members(ACTIVE_USER_SET);
    }

    /**
     * Store UserDetails in Redis as JSON for stateless authentication.
     */
    public void storeUserDetails(String username, CustomUserDetails userDetails) {
        try {
            User originalUser = userDetails.getUser();

            // Create a safe, lightweight copy of the User
            User slimUser = new User();
            slimUser.setId(originalUser.getId());
            slimUser.setUsername(originalUser.getUsername());
            slimUser.setPassword(originalUser.getPassword());

            if (originalUser.getRole() != null) {
                slimUser.setRole(originalUser.getRole()); // Only if Role is simple (id + name)
            }

            // You may include empresaId only, to avoid serializing full Empresa
            if (originalUser.getEmpresa() != null) {
                Empresa empresa = new Empresa();
                empresa.setId(originalUser.getEmpresa().getId());
                slimUser.setEmpresa(empresa);
            }

            // Wrap the safe copy into a new CustomUserDetails
            CustomUserDetails safeUserDetails = new CustomUserDetails(slimUser);

            String userJson = objectMapper.writeValueAsString(safeUserDetails);
            valueOps.set(USER_DETAILS_PREFIX + username, userJson, Duration.ofHours(2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user details", e);
        }
    }

    /**
     * Retrieve UserDetails from Redis and deserialize.
     */
    public UserDetails getUserDetails(String username) {
        String userJson = valueOps.get(USER_DETAILS_PREFIX + username);
        if (userJson == null) return null;
        try {
            System.out.println("Deserializing: " + userJson);
            // Replace YourUserDetailsImpl with your implementation class
            return objectMapper.readValue(userJson, CustomUserDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize user details", e);
        }
    }

    /**
     * Invalidate UserDetails cache.
     */
    public void invalidateUserDetails(String username) {
        redisTemplate.delete(USER_DETAILS_PREFIX + username);
    }
}
