package com.dibros.auth.records;

public record UsuarioRecord(
        Long id,
        String email,
        String nome,
        String vulgo,
        String imagem,
        String password
) {}
