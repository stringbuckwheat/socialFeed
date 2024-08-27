package backend.socialFeed.jwt.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class UserCustom extends User {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private String email;
    private List<GrantedAuthority> authorities; // 권한을 저장할 필드 추가

    public UserCustom(String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = email;
        this.authorities = (List<GrantedAuthority>) authorities; // 초기화
    }

    public UserCustom(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String email) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = email;
        this.authorities = (List<GrantedAuthority>) authorities; // 초기화
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
