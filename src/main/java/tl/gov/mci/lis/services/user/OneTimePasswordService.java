package tl.gov.mci.lis.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OneTimePasswordService {
    private static final long OTP_EXPIRATION_MINUTES = 3;

    private final StringRedisTemplate redisTemplate;
    private final Random random = new Random();

    public String generateOTP(String key) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        redisTemplate.opsForValue().set(key, "111111", OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOTP(String key, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp == null) {
            return false;
        }
        boolean isValid = storedOtp.equals(otp);
        if (isValid) {
            redisTemplate.delete(key);
        }
        return isValid;
    }

    public boolean isOtpExistAndValidByKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public String getOtpByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
