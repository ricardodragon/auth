package com.dibros.auth.security.user;

import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Slf4j

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Buscando user: " + username);
        Usuario usuario = this.usuarioRepository.findByEmail(username);
        log.info("Application User found : '{}'", usuario);
        if (usuario == null)
            throw new UsernameNotFoundException(String.format("Application User '%s' not found", username));

        return new CustomUserDetails(usuario);
    }

    private static final class CustomUserDetails extends Usuario implements UserDetails {
        CustomUserDetails(@NotNull Usuario usuario) {super(usuario);}
        @Override public Collection<? extends GrantedAuthority> getAuthorities() {return emptyList();}
        @Override
        public String getUsername() {return super.getEmail();}
        @Override public boolean isAccountNonExpired() {return true;}
        @Override public boolean isAccountNonLocked() {return true;}
        @Override public boolean isCredentialsNonExpired() {return true;}
        @Override public boolean isEnabled() {return true;}
    }
}
