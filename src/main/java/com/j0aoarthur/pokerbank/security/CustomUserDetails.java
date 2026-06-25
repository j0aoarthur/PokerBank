package com.j0aoarthur.pokerbank.security;

import com.j0aoarthur.pokerbank.entities.Account;
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

    public CustomUserDetails(Account account, Role role) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.isEnabled = true;
//        this.isEnabled = account.getIsVerified(); // A conta está ativa se o e-mail foi verificado
        this.role = role;

        if (role != null) {
            this.authorities = List.of(new SimpleGrantedAuthority(role.getRoleName()));
        } else {
            this.authorities = Collections.emptyList();
        }
    }

    public CustomUserDetails(Account account, Role role, Long clubId) {
        this(account, role);
        this.clubId = clubId;
    }

    public CustomUserDetails(Account account) {
        this(account, null);
    }

    public static CustomUserDetails create(Account account) {
        return new CustomUserDetails(account, null);
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