package com.dibros.auth.controller;

import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Api(value = "Endpoints to manage usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @GetMapping
    @ApiOperation(value = "List all available courses", response = Usuario.class)
    public ResponseEntity<UsuarioDTO> getUsuario(Principal principal) {
        return new ResponseEntity<>(usuarioService.getApplicationUserByUsername((Usuario) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<UsuarioDTO> postUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return new ResponseEntity<>(this.usuarioService.post(usuarioPostDTO), HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<UsuarioDTO> putUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        return new ResponseEntity<>(this.usuarioService.put(usuarioPostDTO), HttpStatus.OK);
    }

    @GetMapping("/all")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<Iterable<UsuarioDTO>> getUsuarios() {
        return new ResponseEntity<>(this.usuarioService.getAllUsuarios(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<String> deleteUsuario(Principal principal, @PathVariable Long id) {
        try{
            return new ResponseEntity<>(this.usuarioService.deleteUser((Usuario) ((UsernamePasswordAuthenticationToken) principal).getPrincipal(), id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}