package com.dibros.auth.service;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.repository.ImagemRepository;
import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.mail.*;
import java.util.Objects;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ImagemRepository imagemRepository;
    private final TokenCreator tokenCreator;
    private final String URL = "servidor-prod/usuario/{0}/";

    public Mono<UsuarioDTO> getApplicationUserByUsername(Authentication authentication) {
        return this.usuarioRepository.getUser(authentication)
            .flatMap(u -> this.usuarioRepository.findById(u.getId()))
            .map(UsuarioMapper::toDTO);
    }

    public Flux<DataBuffer> getImagem(Long id, String imagem) {
        return this.imagemRepository.getImagem(format(URL, id)+imagem);
    }

    @Transactional
    public Mono<Long> post(UsuarioPostDTO usuarioPostDTO, FilePart imagem, Authentication authentication) {
        usuarioPostDTO.setImagem(UUID.randomUUID().toString());
        return this.usuarioRepository.getUser(authentication)
            .map(u -> UsuarioMapper.toModel(usuarioPostDTO, u))
            .flatMap(this.usuarioRepository::save)
            .flatMap(u -> this.imagemRepository.saveImagem(format(URL, u.getId())+u.getImagem(), imagem).thenReturn(u.getId()));
    }

    @Transactional
    public Mono<UsuarioDTO> put(UsuarioPostDTO usuarioPostDTO, FilePart imagem, Authentication authentication) {
        usuarioPostDTO.setImagem(imagem!=null?UUID.randomUUID().toString():"");
        return this.usuarioRepository.getUser(authentication)
            .map(u -> UsuarioMapper.toModel(usuarioPostDTO, u))
            .flatMap(this.usuarioRepository::update)
            .flatMap(u -> Objects.isNull(imagem)?Mono.just(u):this.imagemRepository.delete(format(URL, u.getId())).thenReturn(u))
            .flatMap(u -> Objects.isNull(imagem)?Mono.just(u):this.imagemRepository.saveImagem(format(URL, u.getId())+u.getImagem(), imagem).thenReturn(u))
            .map(UsuarioMapper::toDTO);
    }

    public Mono<Long> putPassword(UsuarioPostDTO usuarioPostDTO, Authentication authentication){
        return this.usuarioRepository.getUser(authentication)
            .flatMap(u -> this.usuarioRepository.updatePassword(UsuarioMapper.toModel(usuarioPostDTO, u).getPassword(), u));
    }

    public Mono<Void> emailToken(String email) {
        return this.usuarioRepository.findByEmail(email)
            .defaultIfEmpty(Usuario.builder().id(0L).email(email).build())
            .doOnNext(this.usuarioRepository::enviaEmail).then();
    }

    public Mono<Long> deleteUser(Authentication authentication) {
        return this.usuarioRepository.getUser(authentication)
            .flatMap(this.usuarioRepository::delete)
            .flatMap(usuario -> this.imagemRepository.delete(format(URL, usuario.getId())+usuario.getImagem()).thenReturn(usuario.getId()));
    }

    public Mono<ResponseEntity<Object>> login(UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioRepository.findByEmail(usuarioPostDTO.getEmail())
            .map(usuario -> new BCryptPasswordEncoder().matches(usuarioPostDTO.getPassword(), usuario.getPassword())?
                ResponseEntity.ok().header(AUTHORIZATION, String.format("Bearer %s", this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT(usuario)))).body((Object) ""):
                new ResponseEntity<Object>("Senha inválida", UNAUTHORIZED)
            )
            .defaultIfEmpty(new ResponseEntity<>(String.format("Usuário '%s' não encontrado", usuarioPostDTO.getEmail()), UNAUTHORIZED))
            .doOnError(Throwable::getLocalizedMessage);
    }

}

