package com.dibros.auth.controller;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/usuarios")
@Api(value = "Endpoints to manage usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
    @ApiOperation(value = "List all available courses", response = UsuarioDTO.class)
    public ResponseEntity<UsuarioDTO> getUsuario() {
        return this.usuarioService.getApplicationUserByUsername();
    }

    @PostMapping
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<UsuarioDTO> cadUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioService.post(usuarioPostDTO);
    }

    @PostMapping("/email-token")
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<String> emailToken(@RequestParam String email, @RequestHeader(name="origin") final String host) {
        return this.usuarioService.emailToken(email, host);
    }

    @GetMapping("/all")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<Iterable<UsuarioDTO>> getUsuarios() {
        return this.usuarioService.getAllUsuarios();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<String> deleteUsuario(Principal principal, @PathVariable Long id) {
        return this.usuarioService.deleteUser(id);
    }


}
