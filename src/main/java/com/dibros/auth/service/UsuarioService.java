package com.dibros.auth.service;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPasswordDTO;
import com.dibros.auth.dto.UsuarioSaveDTO;
import com.dibros.auth.dto.UsuarioUpdateDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.records.UsuarioRecord;
import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.auth.security.PasswordCrypto;
import com.dibros.core.mapper.ImagemMapper;
import com.dibros.core.model.imagem.ImagemTipoEnum;
import com.dibros.core.model.usuario.Usuario;
import com.dibros.core.repository.ImagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ImagemRepository imagemRepository;
    private final PasswordCrypto passwordCrypto;

    private Mono<Usuario> getUser(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(Authentication::getPrincipal)
                .ofType(Usuario.class);
    }

    public Mono<UsuarioDTO> getUsuario(Authentication authentication) {
        return this.getUser(authentication)
                .map(Usuario::getId)
                .flatMap(this.usuarioRepository::findById)
                .map(UsuarioMapper::toDTO);
    }

    public Flux<DataBuffer> getImagem(String imagem) {
        return this.imagemRepository.getImagem(ImagemTipoEnum.USUARIOS, imagem);
    }

    public Mono<Long> post(UsuarioSaveDTO dto, FilePart imagem, Authentication authentication) {
        return this.getUser(authentication)
                .filter(u -> u.getId() != null && u.getId().equals(0L))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Token inválido")))
                .zipWith(this.passwordCrypto.encode(dto), UsuarioMapper::toModel)
                .zipWith(this.imagemRepository.saveImagem(ImagemTipoEnum.USUARIOS, imagem), UsuarioMapper::toModel)
                .flatMap(this.usuarioRepository::save)
                .map(Usuario::getId);
    }

    public Mono<UsuarioDTO> put(UsuarioUpdateDTO dto, FilePart imagem, Authentication authentication) {
        return this.getUser(authentication)
                .zipWith(this.passwordCrypto.encode(dto), UsuarioMapper::toModel)
                .zipWith(this.imagemRepository.saveImagem(ImagemTipoEnum.USUARIOS, imagem).defaultIfEmpty(ImagemMapper.empty()), UsuarioMapper::toModel)
                .flatMap(this.usuarioRepository::update)
                .map(UsuarioMapper::toDTO);
    }

    public Mono<Long> putPassword(UsuarioPasswordDTO dto, Authentication authentication) {
        return this.getUser(authentication)
                .zipWith(this.passwordCrypto.encode(dto), UsuarioMapper::toModel)
                .flatMap(this.usuarioRepository::updatePassword);
    }

    public Mono<Long> deleteUser(Authentication authentication) {
        return this.getUser(authentication)
                .map(Usuario::getId)
                .flatMap(this.usuarioRepository::findById)
                .delayUntil(u -> this.usuarioRepository.delete(u.id()))
                .delayUntil(u -> this.imagemRepository.delete(ImagemTipoEnum.USUARIOS, u.imagem()))
                .map(UsuarioRecord::id);
    }

}

