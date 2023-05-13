package com.dibros.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {

    private Long id;
    private String username;
    @Getter(onMethod = @__( @JsonIgnore))
    private String password;

}
