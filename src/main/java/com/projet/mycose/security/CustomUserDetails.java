package com.projet.mycose.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    public CustomUserDetails(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return null; // Not used in this filter, as password is not needed for requests after authentication
    }
}