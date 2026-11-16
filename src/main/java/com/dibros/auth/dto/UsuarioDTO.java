package com.dibros.auth.dto;

public record UsuarioDTO(
        Long id,
        String email,
        String nome,
        String vulgo,
        String imagem
) {}
