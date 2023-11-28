package com.dibros.auth.mapper;

import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = BCryptPasswordEncoder.class)
public interface UsuarioMapper {
    @Mapping(expression = "java(usuarioPostDTO.getPassword()!=null?new BCryptPasswordEncoder().encode(usuarioPostDTO.getPassword()):usuario.getPassword())", target = "password")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeToUsuario(UsuarioPostDTO usuarioPostDTO, @MappingTarget Usuario usuario);
    UsuarioDTO toUsuarioDTO(Usuario usuario);
    Iterable<UsuarioDTO> toListUsuarioDTO(Iterable<Usuario> usuario);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toMerge(UsuarioDTO usuarioDTO, @MappingTarget Usuario usuario);
}
