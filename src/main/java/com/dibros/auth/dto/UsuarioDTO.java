package com.dibros.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
public class UsuarioDTO {

    private Long id;
    private String email;
    private String nome;
    private String imagemPath;

}
