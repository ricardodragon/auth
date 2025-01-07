package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
//@Api(value = "Endpoints to manage usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
//    @ApiOperation(value = "List all available courses", response = UsuarioDTO.class)
    public Mono<UsuarioDTO> getUsuario() {
        return this.usuarioService.getApplicationUserByUsername();
    }

    @PostMapping
//    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public Mono<UsuarioDTO> cadUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioService.post(usuarioPostDTO);
    }

    @PostMapping("/email-token")
//    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public Mono<String> emailToken(@RequestParam String email) {
        return this.usuarioService.emailToken(email);
    }

    @DeleteMapping("/{id}")
//    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public Mono<String> deleteUsuario(@PathVariable Long id) {
        return this.usuarioService.deleteUser(id);
    }
}
