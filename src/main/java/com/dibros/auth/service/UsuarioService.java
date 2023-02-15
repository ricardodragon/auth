package com.dibros.auth.service;

import com.dibros.core.model.Usuario;
import com.dibros.core.repository.UsuarioRepository;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDTO getApplicationUserByUsername(Usuario usuario) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(usuario);
    }

    public UsuarioDTO post(UsuarioPostDTO usuarioPostDTO) {
        usuarioPostDTO.setRole("USER");
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public UsuarioDTO put(UsuarioPostDTO usuarioPostDTO) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public Iterable<UsuarioDTO> getAllUsuarios() {
        return UsuarioMapper.INSTANCE. toListUsuarioDTO(this.usuarioRepository.findAll());
    }

    public String deleteUser(Usuario principal, Long id) {
        this.usuarioRepository.deleteById(id);
        return "Ok";
    }

}
//	public UsuarioDTO saveApplicationUserByUsername(UsuarioDTO userDTO) {
//		applicationUserRepository.save(UsuarioMapperImpl.INSTANCE.userDTO);
//		return
//	}
