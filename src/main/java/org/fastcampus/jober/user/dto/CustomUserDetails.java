package org.fastcampus.jober.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(this.username, that.username);
    }
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}

