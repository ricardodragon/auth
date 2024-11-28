package com.dibros.auth.dto;

import lombok.*;

@Data
@Builder
public class UsuarioDTO {

    private Long id;
    private String email;
    private String nome;
    private String imagemPath;

}
