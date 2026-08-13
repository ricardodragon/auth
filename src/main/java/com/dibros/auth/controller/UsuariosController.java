package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Mono<UsuarioDTO> getUsuario(@AuthenticationPrincipal Authentication authentication) {
        return this.usuarioService.getApplicationUserByUsername(authentication);
    }

    @GetMapping("/imagem/{id}/{imagem}")
    public Flux<DataBuffer> getByLoja(@PathVariable Long id, @PathVariable String imagem) {
        return this.usuarioService.getImagem(id, imagem);
    }

    @PostMapping
    public Mono<Long> cadUsuario(@AuthenticationPrincipal Authentication authentication, @RequestPart UsuarioPostDTO usuarioPostDTO, @RequestPart FilePart imagem) {
        return this.usuarioService.post(usuarioPostDTO, imagem, authentication);
    }

    @PutMapping
    public Mono<UsuarioDTO> update(@AuthenticationPrincipal Authentication authentication, @RequestPart UsuarioPostDTO usuarioPostDTO, @RequestPart(required = false) FilePart imagem) {
        return this.usuarioService.put(usuarioPostDTO, imagem, authentication);
    }

    @PutMapping("/password")
    public Mono<Long> update(@AuthenticationPrincipal Authentication authentication, @RequestBody UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioService.putPassword(usuarioPostDTO, authentication);
    }

    @PostMapping("/public/email-token")
    public Mono<Void> emailToken(@RequestParam String email) {
        return this.usuarioService.emailToken(email);
    }

    @DeleteMapping
    public Mono<Long> deleteUsuario(@AuthenticationPrincipal Authentication authentication) {
        return this.usuarioService.deleteUser(authentication);
    }
}
