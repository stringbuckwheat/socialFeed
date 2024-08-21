package backend.socialFeed.user.model;

import backend.socialFeed.user.dto.JoinValidRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String email;
    private String password;
    private boolean verified;
    private String code;

    public static User createMember(JoinValidRequestDto requestDto, PasswordEncoder passwordEncoder) {
        return User.builder().email(requestDto.getEmail()).password(passwordEncoder.encode(requestDto.getPassword())).build();
    }
}
