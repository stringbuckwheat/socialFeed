package backend.socialFeed.user.controller;

import backend.socialFeed.user.dto.JoinValidRequestDto;
import backend.socialFeed.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@RequestBody @Valid JoinValidRequestDto validRequestDto){
        userService.join(validRequestDto);
    }
}
