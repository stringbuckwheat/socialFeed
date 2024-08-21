package backend.socialFeed.jwt.controller;

import backend.socialFeed.jwt.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password,
                                   HttpServletResponse response, HttpSession session) {
        try {
            authService.login(response, email, password);

            // 로그인 성공 시 세션에 이메일 저장
            session.setAttribute("email", email);

            return ResponseEntity.ok("Login successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        authService.logout(request, response);

        // 세션 무효화
        session.invalidate();

        return ResponseEntity.ok("Logout successful");
    }
}
