package backend.socialFeed.user.service;

import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.dto.JoinValidRequestDto;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void join(JoinValidRequestDto validRequestDto) {
        if (userRepository.existsByEmail(validRequestDto.getEmail())) {
            throw new IllegalStateException(Constants.EMAIL_ALREADY_EXISTS);
        }
        userRepository.save(User.createMember(validRequestDto, passwordEncoder));
    }
}
