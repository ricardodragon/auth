package com.dibros.auth.repository;

import com.dibros.core.model.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

    Usuario findByEmail(String username);
}
