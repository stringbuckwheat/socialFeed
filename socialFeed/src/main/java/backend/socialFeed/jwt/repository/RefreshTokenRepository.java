package backend.socialFeed.jwt.repository;

import backend.socialFeed.jwt.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}