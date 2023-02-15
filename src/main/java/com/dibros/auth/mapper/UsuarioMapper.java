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
        return new Usuario(usuarioPostDTO.getId(), usuarioPostDTO.getEmail(), usuarioPostDTO.getUsername(), new BCryptPasswordEncoder().encode(usuarioPostDTO.getPassword()), usuarioPostDTO.getRole());
    }

    UsuarioDTO toUsuarioDTO(Usuario usuario);
    Iterable<UsuarioDTO> toListUsuarioDTO(Iterable<Usuario> usuario);
    Usuario toUsuario(UsuarioDTO usuarioDTO);
}
