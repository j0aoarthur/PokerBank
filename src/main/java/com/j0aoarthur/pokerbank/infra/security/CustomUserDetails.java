package com.j0aoarthur.pokerbank.infra.security;

import com.j0aoarthur.pokerbank.entities.User;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean isEnabled;
    private final Collection<? extends GrantedAuthority> authorities;

    // Campos personalizados
    private final Role role;
    private Long clubId = null;

    public CustomUserDetails(User user, Role role) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.isEnabled = true;
//        this.isEnabled = user.getIsVerified(); // A conta está ativa se o e-mail foi verificado
        this.role = role;

        if (role != null) {
            this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        } else {
            this.authorities = Collections.emptyList();
        }
    }

    public CustomUserDetails(User user, Role role, Long clubId) {
        this(user, role);
        this.clubId = clubId;
    }

    public CustomUserDetails(User user) {
        this(user, null);
    }

    public static CustomUserDetails create(User user) {
        return new CustomUserDetails(user, null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;

        // Se quiser usar a verificação de e-mail para ativar a conta, descomente a linha abaixo
//        return this.isEnabled;
    }

    public Long getClubId() {
        return clubId;
    }

}