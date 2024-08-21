package backend.socialFeed.user.service;

import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.dto.JoinValidRequestDto;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.socialFeed.user.util.CodeGenerator.generateRandomMixStr;

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
        userRepository.save(User.builder().email(validRequestDto.getEmail())
                .password(passwordEncoder.encode(validRequestDto.getPassword()))
                .code(generateRandomMixStr(6)).build());
    }

    @Transactional
    public void verifyUser(UserVerifyRequestDto verifyRequestDto) {
        User user = userRepository.findByEmail(verifyRequestDto.getEmail())
                .orElseThrow(() -> new IllegalStateException(Constants.EMAIL_NOT_EXISTS));
        if (!passwordEncoder.matches(verifyRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalStateException(Constants.PASSWORD_NOT_MATCH);
        }
        if (!verifyRequestDto.getCode().equals(user.getCode())) {
            throw new IllegalStateException(Constants.CODE_NOT_MATCH);
        }
        user.setVerified();
    }

}
