package com.dibros.auth.mapper;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.core.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UsuarioMapper {

    private UsuarioMapper(){ throw new AssertionError(); }

    public static Usuario toModel(UsuarioPostDTO usuarioPostDTO) {
        return Usuario.builder()
            .id(usuarioPostDTO.getId())
            .nome(usuarioPostDTO.getNome())
            .imagemPath(usuarioPostDTO.getImagemPath())
            .password(new BCryptPasswordEncoder().encode(usuarioPostDTO.getPassword()))
            .build();
    }

    public static Usuario toModel(UsuarioPostDTO usuarioPostDTO, Usuario u) {
        if(u.getId().equals(0L))
            u.setId(null);
        u.setEmail(u.getEmail());
        u.setNome(usuarioPostDTO.getNome());
        u.setImagemPath(usuarioPostDTO.getImagemPath());
        return u;
    }

    public static UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nome(usuario.getNome())
            .imagemPath(usuario.getImagemPath())
            .build();
    }
}
