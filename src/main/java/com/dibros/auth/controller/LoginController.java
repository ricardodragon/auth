package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final UsuarioService usuarioService;

    @PostMapping
    public Mono<ResponseEntity<Object>> login(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioService.login(usuarioPostDTO);
    }
}
