package com.dibros.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioPostDTO {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;
}