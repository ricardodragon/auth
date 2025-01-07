package com.dibros.auth.service;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TokenCreator tokenCreator;

    public Mono<Usuario> getUser(){
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication).map(Authentication::getPrincipal).cast(Usuario.class);
    }

    public Mono<UsuarioDTO> getApplicationUserByUsername() {
        return getUser()
            .flatMap(usuario -> this.usuarioRepository.findById(usuario.getId()).map(UsuarioMapper::toDTO));
    }

    @Transactional
    public Mono<UsuarioDTO> post(UsuarioPostDTO usuarioPostDTO) {
        return getUser().flatMap(usuario -> this.usuarioRepository.findByEmail(usuario.getEmail()).defaultIfEmpty(usuario))
            .flatMap(entity -> this.usuarioRepository.save(UsuarioMapper.toModel(usuarioPostDTO, entity)).map(UsuarioMapper::toDTO));
    }

    public Mono<String> emailToken(String email) {
        return this.usuarioRepository.findByEmail(email)
            .defaultIfEmpty(Usuario.builder().id(0L).email(email).build())
            .map(u -> {this.envia(u); return "Oi filho"; });
    }

    public Mono<String> deleteUser(Long id) {
        return Mono.fromRunnable(()-> this.usuarioRepository.deleteById(id).then()).map(o -> "Ok");
    }

    public Mono<ResponseEntity<Object>> login(UsuarioPostDTO usuarioPostDTO) {
        return this.usuarioRepository.findByEmail(usuarioPostDTO.getEmail())
            .map(usuario -> new BCryptPasswordEncoder().matches(usuarioPostDTO.getPassword(), usuario.getPassword())?
                ResponseEntity.ok().header(AUTHORIZATION, String.format("Bearer %s", this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT(usuario)))).body((Object) ""):
                new ResponseEntity<Object>("Senha inválida", UNAUTHORIZED))
            .defaultIfEmpty(new ResponseEntity<>(String.format("Usuário '%s' não encontrado", usuarioPostDTO.getEmail()), UNAUTHORIZED))
            .doOnError(Throwable::getLocalizedMessage);
    }

    private static Properties properties(){
        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.ssl.checkServerIdentity", "true");
        p.put("mail.smtp.auth", "true");p.put("mail.smtp.port", "465");
        p.put("mail.smtp.ssl.checkserveridentity", true);
        return p;
    }

    @SneakyThrows
    private void envia(Usuario u){
        Session session = Session.getInstance(properties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ricardoelfuego@gmail.com", "cupbmuntnfklludh");
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ricardoelfuego@gmail.com"));
        //Remetente
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
        message.setSubject("Dando dibros");
        message.setContent("Você está dando um dibros: \n<br/><br/> Link: <br/><a style='cursor:pointer;font-weigth:bolder;' href='https://dibros.com.br/nova-senha/"+this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT(Usuario.builder().id(u.getId()).email(u.getEmail()).build()))+"?esqueci="+(!u.getId().equals(0L))+"'>Clique aqui pra dibrar</a><br/><br/><br/><br/>", "text/html");
        Transport.send(message);
    }
}

