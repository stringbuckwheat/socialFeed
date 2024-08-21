package backend.socialFeed.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinValidRequestDto {

    @NotBlank
    @Email(message = "잘못된 이메일 형식입니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?!.*(123456|password|123456789|12345678|qwerty|abc123|1234567)).{10,}$",
            message = "비밀번호는 10자 이상이어야 하고, 통상적으로 자주 사용되는 비밀번호는 사용할 수 없습니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).+$",
            message = "비밀번호는 숫자, 문자, 특수문자 중 2가지 이상을 포함해야 합니다.")
    @Pattern(regexp = "^(?!.*(.)\\1\\1).+$",
            message = "비밀번호에는 3회 이상 연속된 문자가 포함될 수 없습니다.")
    private String password;
}
