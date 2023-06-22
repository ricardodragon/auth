package com.dibros.auth.mapper;

import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    default Usuario toUsuarioCryp(UsuarioPostDTO usuarioPostDTO) {
        return Usuario.builder().id(usuarioPostDTO.getId()).email(usuarioPostDTO.getEmail()).nome(usuarioPostDTO.getNome()).password(new BCryptPasswordEncoder().encode(usuarioPostDTO.getPassword())).imagemPath(usuarioPostDTO.getImagemPath()).build();
    }

    UsuarioDTO toUsuarioDTO(Usuario usuario);
    Iterable<UsuarioDTO> toListUsuarioDTO(Iterable<Usuario> usuario);
    Usuario toUsuario(UsuarioDTO usuarioDTO);
}
