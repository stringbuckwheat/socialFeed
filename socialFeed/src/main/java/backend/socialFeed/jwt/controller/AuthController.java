package backend.socialFeed.jwt.controller;

import backend.socialFeed.jwt.service.AuthService;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserVerifyRequestDto user,
                                   HttpServletResponse response) {
        return authService.login(response, user);
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
