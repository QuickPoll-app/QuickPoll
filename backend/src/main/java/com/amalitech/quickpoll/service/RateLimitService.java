package com.amalitech.quickpoll.service;

import com.amalitech.quickpoll.exceptionHandler.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_ATTEMPT_PREFIX = "login_attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 15;

    public void checkLoginRateLimit(String email) {
        String key = LOGIN_ATTEMPT_PREFIX + email;
        String attempts = redisTemplate.opsForValue().get(key);

        if (attempts != null && Integer.parseInt(attempts) >= MAX_ATTEMPTS) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Try again in " + BLOCK_DURATION_MINUTES + " minutes."
            );
        }
    }

    public void recordFailedAttempt(String email) {
        String key = LOGIN_ATTEMPT_PREFIX + email;
        String attempts = redisTemplate.opsForValue().get(key);

        if (attempts == null) {
            redisTemplate.opsForValue().set(
                    key, "1",
                    BLOCK_DURATION_MINUTES, TimeUnit.MINUTES
            );
        } else {
            redisTemplate.opsForValue().increment(key);
        }
    }

    public void clearFailedAttempts(String email) {
        redisTemplate.delete(LOGIN_ATTEMPT_PREFIX + email);
    }

    public long getRemainingAttempts(String email) {
        String key = LOGIN_ATTEMPT_PREFIX + email;
        String attempts = redisTemplate.opsForValue().get(key);
        if (attempts == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - Integer.parseInt(attempts));
    }
}
