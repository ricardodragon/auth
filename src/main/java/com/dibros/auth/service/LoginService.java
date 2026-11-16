package com.dibros.auth.service;

import com.dibros.auth.dto.LoginDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.auth.security.PasswordCrypto;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordCrypto passwordCrypto;
    private final TokenCreator tokenCreator;

    public Mono<Void> login(LoginDTO dto, ServerHttpResponse response) {
        return this.usuarioRepository.findByLogin(dto.login())
                .filterWhen(u -> this.passwordCrypto.matches(dto.password(), u.password()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(UNAUTHORIZED, "Usuário ou senha inválidos")))
                .map(UsuarioMapper::toModel)
                .map(this.tokenCreator::createSignedJWT)
                .map(this.tokenCreator::encryptToken)
                .doOnNext(token -> response.getHeaders().add(AUTHORIZATION, "Bearer " + token))
                .then();
    }

}
