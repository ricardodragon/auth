package com.dibros.auth.controller;

import com.dibros.auth.dto.LoginDTO;
import com.dibros.auth.dto.UsuarioSaveDTO;
import com.dibros.auth.service.LoginService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    @ApiResponse(
            responseCode = "200",
            description = "Autenticado com sucesso",
            headers = @Header(name = "Authorization", description = "Bearer JWT Token"),
            content = @Content
    )
    public Mono<Void> login(@RequestBody @Valid LoginDTO dto, ServerHttpResponse response) {
        return this.loginService.login(dto, response);
    }

}
