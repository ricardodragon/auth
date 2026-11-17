package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPasswordDTO;
import com.dibros.auth.dto.UsuarioSaveDTO;
import com.dibros.auth.dto.UsuarioUpdateDTO;
import com.dibros.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Mono<UsuarioDTO> getUsuario(Authentication authentication) {
        return this.usuarioService.getUsuario(authentication);
    }

    @GetMapping(value = "/imagem/{imagem}", produces = "image/*")
    public Flux<DataBuffer> getImagem(@PathVariable String imagem) {
        return this.usuarioService.getImagem(imagem);
    }

    @PostMapping
    public Mono<Long> cadUsuario(Authentication authentication, @RequestPart @Valid UsuarioSaveDTO usuario, @RequestPart FilePart imagem) {
        return this.usuarioService.post(usuario, imagem, authentication);
    }

    @PutMapping
    public Mono<Void> update(Authentication authentication, @RequestPart @Valid UsuarioUpdateDTO usuario, @RequestPart(required = false) FilePart imagem) {
        return this.usuarioService.put(usuario, imagem, authentication);
    }

    @PatchMapping("/password")
    public Mono<Void> updatePassword(Authentication authentication, @RequestBody @Valid UsuarioPasswordDTO usuario) {
        return this.usuarioService.putPassword(usuario, authentication);
    }

    @DeleteMapping
    public Mono<Void> deleteUsuario(Authentication authentication) {
        return this.usuarioService.deleteUser(authentication);
    }

}
