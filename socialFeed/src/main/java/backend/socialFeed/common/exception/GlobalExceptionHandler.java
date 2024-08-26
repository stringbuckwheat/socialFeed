package backend.socialFeed.common.exception;

import backend.socialFeed.common.dto.ErrorResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public HttpEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public HttpEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public HttpEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public HttpEntity<ErrorResponseDto> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public HttpEntity<ErrorResponseDto> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public HttpEntity<ErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = e.getParameterName() + "은/는 필수 입력값입니다.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        if (fieldError != null) {
            // @NotBlank 등 어노테이션의 직접 작성된 메시지 가져옴
            String errorMessage = fieldError.getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(errorMessage));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("입력 정보를 확인해주세요"));
    }
}
