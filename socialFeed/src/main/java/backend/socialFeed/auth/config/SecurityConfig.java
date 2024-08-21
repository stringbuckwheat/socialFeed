package backend.socialFeed.auth.config;

import backend.socialFeed.common.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 임시 WHITELIST
    private static final String[] PUBLIC_WHITELIST = {
            "/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        return http
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .headers((headerConfig) ->
                        headerConfig.frameOptions(frameOptionsConfig ->
                                frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(PUBLIC_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig
                                .authenticationEntryPoint(unauthorizedEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)).build();
    }

    private final AuthenticationEntryPoint unauthorizedEntryPoint = (request, response, authException) -> {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String json = new ObjectMapper().writeValueAsString(new ErrorResponseDto("Authentication is required."));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    };

    private final AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String json = new ObjectMapper().writeValueAsString(new ErrorResponseDto("Spring security forbidden"));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    };

}
