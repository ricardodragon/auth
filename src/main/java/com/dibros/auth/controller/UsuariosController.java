package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Mono<UsuarioDTO> getUsuario() {
        return this.usuarioService.getApplicationUserByUsername();
    }

    @PostMapping
    public Mono<UsuarioDTO> cadUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioService.post(usuarioPostDTO);
    }

    @PostMapping("/email-token")
    public Mono<String> emailToken(@RequestParam String email) {
        return this.usuarioService.emailToken(email);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteUsuario(@PathVariable Long id) {
        return this.usuarioService.deleteUser(id);
    }
}
