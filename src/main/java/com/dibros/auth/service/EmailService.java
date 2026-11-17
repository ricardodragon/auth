package com.dibros.auth.service;

import com.dibros.auth.mapper.EmailMapper;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.repository.EmailRepository;
import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UsuarioRepository usuarioRepository;
    private final EmailRepository emailRepository;
    private final TokenCreator tokenCreator;

    public Mono<Void> enviarToken(String email) {
        return this.usuarioRepository.findByEmail(email)
            .map(UsuarioMapper::toModel)
            .defaultIfEmpty(UsuarioMapper.toModel(email))
            .zipWith(Mono.just(this.tokenCreator), EmailMapper::toRecord)
            .flatMap(this.emailRepository::enviar);
    }

}
