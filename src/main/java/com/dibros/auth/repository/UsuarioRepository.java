package com.dibros.auth.repository;

import com.dibros.core.model.Usuario;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long> {

    Mono<Usuario> findByEmail(String username);
}