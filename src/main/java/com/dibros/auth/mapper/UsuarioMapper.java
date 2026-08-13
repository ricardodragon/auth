package com.dibros.auth.mapper;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.core.model.Usuario;
import io.r2dbc.spi.Row;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UsuarioMapper {

    private UsuarioMapper(){ throw new AssertionError(); }

    public static Usuario toModel(Row row) {
        return Usuario.builder()
                .id(row.get("id", Long.class))
                .email(row.get("email", String.class))
                .nome(row.get("nome", String.class))
                .imagem(row.get("imagem", String.class))
                .password(row.get("password", String.class))
                .build();
    }

    public static Usuario toModel(UsuarioPostDTO usuarioPostDTO, Usuario usuario) {
        return Usuario.builder()
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nome(usuarioPostDTO.getNome())
            .imagem(usuarioPostDTO.getImagem())
            .password(new BCryptPasswordEncoder().encode(usuarioPostDTO.getPassword()))
            .build();
    }

    public static UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nome(usuario.getNome())
            .imagem(usuario.getImagem())
            .build();
    }
}
