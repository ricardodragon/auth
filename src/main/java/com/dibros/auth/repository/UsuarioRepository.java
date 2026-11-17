package com.dibros.auth.repository;

import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.records.UsuarioRecord;
import com.dibros.core.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.dibros.auth.sql.UsuarioSQL.*;
import static io.r2dbc.spi.Parameters.in;
import static io.r2dbc.spi.R2dbcType.VARCHAR;

@RequiredArgsConstructor
@Repository
public class UsuarioRepository {

    private final DatabaseClient databaseClient;

    public Mono<UsuarioRecord> findByLogin(String login) {
        String normalizado = login.startsWith("@") ? login.substring(1).trim().toLowerCase() : login.trim().toLowerCase();
        return this.databaseClient.sql(FIND_BY_LOGIN)
                .bind("login", normalizado)
                .map(UsuarioMapper::toRecord)
                .first();
    }

    public Mono<UsuarioRecord> findById(Long idUser) {
        return this.databaseClient.sql(FIND_BY_ID)
                .bind("id_user", idUser)
                .map(UsuarioMapper::toRecord)
                .first();
    }

    public Mono<UsuarioRecord> findByEmail(String email) {
        return this.databaseClient.sql(FIND_BY_EMAIL)
                .bind("email", email)
                .map(UsuarioMapper::toRecord)
                .first();
    }

    public Mono<Usuario> save(Usuario usuario) {
        return this.databaseClient.sql(INSERT)
                .bind("password", usuario.getPassword())
                .bind("email", usuario.getEmail())
                .bind("nome", usuario.getNome())
                .bind("vulgo", usuario.getVulgo())
                .bind("imagem", usuario.getImagem())
                .filter((statement, next) -> statement.returnGeneratedValues("id").execute())
                .map((row, rowMetadata) -> usuario.toBuilder().id(Objects.requireNonNull(row.get("id", Long.class))).build())
                .first();
    }

    public Mono<Usuario> update(Usuario usuario) {
        return this.databaseClient.sql(UPDATE)
                .bind("id", usuario.getId())
                .bind("nome", usuario.getNome())
                .bind("imagem", usuario.getImagem())
                .bind("vulgo", usuario.getVulgo())
                .bind("password", in(VARCHAR, usuario.getPassword()))
                .fetch()
                .rowsUpdated()
                .filter(rows -> rows > 0)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .thenReturn(usuario);
    }

    public Mono<Void> updatePassword(Usuario usuario) {
        return this.databaseClient.sql(UPDATE_PASSWORD)
                .bind("password", usuario.getPassword())
                .bind("id", usuario.getId())
                .fetch()
                .rowsUpdated()
                .filter(rows -> rows > 0)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .then();
    }

    public Mono<String> delete(Long idUser) {
        return this.databaseClient.sql(FIND_IMAGEM_BY_ID)
            .bind("id", idUser)
            .map((row, metadata) -> Objects.requireNonNull(row.get("imagem", String.class)))
            .first()
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
            .delayUntil(imagem -> this.databaseClient.sql(DELETE)
                .bind("id", idUser)
                .fetch()
                .rowsUpdated()
                .filter(rows -> rows > 0)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao deletar usuário")))
            );
    }
}