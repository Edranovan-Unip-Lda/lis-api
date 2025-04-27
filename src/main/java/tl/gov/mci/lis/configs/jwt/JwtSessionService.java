package tl.gov.mci.lis.configs.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class JwtSessionService {

    private static final String ACTIVE_USER_SET = "jwt:activeUsernames";

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
        valueOps.set(username, token);
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
}
