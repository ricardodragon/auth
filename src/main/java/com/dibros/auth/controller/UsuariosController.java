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
        try{
            return new ResponseEntity<>(this.usuarioService.getApplicationUserByUsername(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<UsuarioDTO> cadUsuario(@RequestBody UsuarioPostDTO usuarioPostDTO) {
        try{
            return new ResponseEntity<>(this.usuarioService.post(usuarioPostDTO), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email-token")
    @ApiOperation(value = "Create an new User", response = UsuarioDTO.class)
    public ResponseEntity<String> emailToken(@RequestParam String email, @RequestHeader(name="origin") final String host) {
        try{
            return new ResponseEntity<>(this.usuarioService.emailToken(email, host), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<Iterable<UsuarioDTO>> getUsuarios() {
        try{
            return new ResponseEntity<>(this.usuarioService.getAllUsuarios(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "List all available courses", response = UsuarioDTO[].class)
    public ResponseEntity<String> deleteUsuario(Principal principal, @PathVariable Long id) {
        try{
            return new ResponseEntity<>(this.usuarioService.deleteUser(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
