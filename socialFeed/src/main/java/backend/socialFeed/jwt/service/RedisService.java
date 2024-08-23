package backend.socialFeed.jwt.service;

import backend.socialFeed.jwt.entity.RefreshToken;
import backend.socialFeed.jwt.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RedisService {

    private final RefreshTokenRepository refreshTokenRepository;

    // rfr token Redis 저장
    public void saveRefreshToken(String email, String refreshToken) {
        RefreshToken token = new RefreshToken(email, refreshToken, 7*24*60*60L);//7일
        refreshTokenRepository.save(token);
    }

    //rfr token Redis에서 get
    public String getRefreshToken(String email) {
        return refreshTokenRepository.findById(email)
                                        .map(RefreshToken::getRefreshToken)
                                        .orElse(null);
    }

    //rfr token Redis에서 삭제
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteById(email);
    }
}