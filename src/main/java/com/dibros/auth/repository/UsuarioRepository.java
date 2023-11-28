package com.dibros.auth.repository;

import com.dibros.core.model.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String username);
}
