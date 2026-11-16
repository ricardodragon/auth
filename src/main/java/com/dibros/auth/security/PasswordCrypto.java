package com.dibros.auth.security;

import com.dibros.auth.dto.UsuarioPasswordDTO;
import com.dibros.auth.dto.UsuarioSaveDTO;
import com.dibros.auth.dto.UsuarioUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class PasswordCrypto {

    private final PasswordEncoder passwordEncoder;

    public Mono<String> encode(String rawPassword) {
        return Mono.fromCallable(() -> this.passwordEncoder.encode(rawPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UsuarioSaveDTO> encode(UsuarioSaveDTO dto) {
        return this.encode(dto.password()).map(dto::withPassword);
    }

    public Mono<UsuarioUpdateDTO> encode(UsuarioUpdateDTO dto) {
        return Mono.justOrEmpty(dto.password())
                .filter(p -> !p.isBlank())
                .flatMap(this::encode)
                .map(dto::withPassword)
                .defaultIfEmpty(dto);
    }

    public Mono<UsuarioPasswordDTO> encode(UsuarioPasswordDTO dto) {
        return this.encode(dto.password()).map(dto::withPassword);
    }

    public Mono<Boolean> matches(String rawPassword, String encodedPassword) {
        return Mono.fromCallable(() -> this.passwordEncoder.matches(rawPassword, encodedPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }

}