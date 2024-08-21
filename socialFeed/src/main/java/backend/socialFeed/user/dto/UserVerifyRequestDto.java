package backend.socialFeed.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifyRequestDto {
    private String email;
    private String password;
    private String code;
}
