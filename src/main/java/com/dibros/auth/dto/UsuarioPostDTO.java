package com.dibros.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UsuarioPostDTO {

    private Long id;
    private String email;
    private String password;
    private String nome;
    private String imagem;

}
