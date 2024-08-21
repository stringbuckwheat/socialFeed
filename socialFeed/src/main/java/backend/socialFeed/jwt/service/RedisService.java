package backend.socialFeed.jwt.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장
    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken);
    }

    // Refresh Token 조회
    public String getRefreshToken(String email) {
        return (String)redisTemplate.opsForValue().get(email);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
