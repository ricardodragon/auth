package com.dibros.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record UsuarioUpdateDTO(
        @NotBlank @Size(min = 2, max = 100) String nome,
        @NotBlank @Pattern(regexp = "^[a-z0-9._]{3,30}$") String vulgo,
        @Size(min = 6, max = 100) String password
) {}
