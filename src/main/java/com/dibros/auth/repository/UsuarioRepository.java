package com.dibros.auth.repository;

import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.core.model.Usuario;
import com.dibros.core.token.creator.TokenCreator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UsuarioRepository {

    private final DatabaseClient databaseClient;
    private final Session mailSession;
    private final TokenCreator tokenCreator;
    @Value("${email.endereco}")
    private String endereco;
    private final static String BODY = "Você está dando um dibros: \n<br/><br/> Link: <br/>" +
            "<a style=''cursor:pointer;font-weigth:bolder;'' href=''https://dibros.com.br/{0}/{1}''>" +
            "Clique aqui pra dibrar</a><br/><br/><br/><br/>";

    public Mono<Usuario> getUser(Authentication authentication){
        return Mono.just(authentication).map(a -> {
            Object u = authentication.getPrincipal();
            return (Usuario) Objects.requireNonNull(u);
        });
    }

    public Mono<Usuario> findById(Long idUser){
        return this.databaseClient.sql("select * from usuario where id=:id_user")
            .bind("id_user", idUser)
            .map((row, rowMetadata) -> UsuarioMapper.toModel(row))
            .first();
    }

    public Mono<Usuario> findByEmail(String email){
        return this.databaseClient.sql("select * from usuario where email=:email")
            .bind("email", email)
            .map((row, rowMetadata) -> UsuarioMapper.toModel(row))
            .first();
    }

    public Mono<Usuario> save(Usuario usuario) {
        return this.databaseClient.sql("insert into usuario values (:id, :password, :email, '', :nome, :imagem) ")
                .bind("id", usuario.getId())
                .bind("password", usuario.getPassword())
                .bind("email", usuario.getEmail())
                .bind("nome", usuario.getNome())
                .bind("imagem", usuario.getImagem())
                .filter((statement, next) -> statement.returnGeneratedValues("id").execute())
                .map((row, rowMetadata) -> usuario.toBuilder().id(Objects.requireNonNull(row.get("id", Long.class))).build())
                .first();
    }

    public Mono<Usuario> update(Usuario usuario) {
        return this.databaseClient.sql("update usuario set password=:password, nome=:nome, imagem=if(''=:imagem, imagem, :imagem) where id=:id")
                .bind("id", usuario.getId())
                .bind("password", usuario.getPassword())
                .bind("nome", usuario.getNome())
                .bind("imagem", usuario.getImagem())
                .filter((statement, next) -> statement.returnGeneratedValues("id").execute())
                .map((row, rowMetadata) -> Objects.requireNonNull(row.get("id", Long.class)))
                .first()
                .map(id -> usuario.toBuilder().id(id).build());
    }

    public Mono<Usuario> delete(Usuario usuario){
        return this.databaseClient.sql("delete usuario where id=:id")
                .bind("id", usuario.getId()).then().thenReturn(usuario);
    }

    @SneakyThrows
    public void enviaEmail(Usuario u){
        Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(endereco));
        //Remetente
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
        message.setSubject("Dando dibros");
        message.setContent(format(BODY, u.getId().equals(0L)?"cadastro":"nova-senha", this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT(u))), "text/html");
        Transport.send(message);
    }

    public Mono<Long> updatePassword(String password, Usuario usuario) {
        return this.databaseClient.sql("update usuario set password=:password where id=:id")
            .bind("password", password)
            .bind("id", usuario.getId())
            .then().thenReturn(usuario.getId());
    }
}