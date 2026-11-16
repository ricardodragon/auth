package com.dibros.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record UsuarioPasswordDTO(
        @NotBlank @Size(min = 6, max = 100) String password
) {}
