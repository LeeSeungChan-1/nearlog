package com.nearlog.common.security;

import com.nearlog.user.domain.User;
import com.nearlog.user.domain.UserRole;
import com.nearlog.user.domain.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;

    private final String email;
    private final String password;

    // SNS에서 사용하는 username
    private final String profileUsername;
    private final String nickname;

    private final UserRole role;
    private final UserStatus status;

    private UserPrincipal(User user) {

        this.id = user.getId();

        this.email = user.getEmail();
        this.password = user.getPassword();

        this.profileUsername = user.getUsername();
        this.nickname = user.getNickname();

        this.role = user.getRole();
        this.status = user.getStatus();
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}