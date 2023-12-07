package com.dibros.auth.service;

import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final TokenCreator tokenCreator;
    private final UsuarioMapper mapper;
    private Usuario getUser(){return ((Usuario) (SecurityContextHolder.getContext().getAuthentication().getPrincipal()));}

    public ResponseEntity<UsuarioDTO> getApplicationUserByUsername() {
        return ResponseEntity.ok(this.mapper.toUsuarioDTO(this.usuarioRepository.findById(getUser().getId()).get()));
    }

    public ResponseEntity<UsuarioDTO> post(UsuarioPostDTO usuarioPostDTO) {
        Usuario u = this.usuarioRepository.findByEmail(getUser().getEmail()).orElse(new Usuario());
        this.mapper.mergeToUsuario(usuarioPostDTO, u);
        u.setEmail(getUser().getEmail());
        return ResponseEntity.ok(this.mapper.toUsuarioDTO(this.usuarioRepository.save(u)));
    }

    public ResponseEntity<String> emailToken(String email, String host) {
        Usuario u = this.usuarioRepository.findByEmail(email).orElse(Usuario.builder().id(0L).email(email).build());
        Session session = Session.getDefaultInstance(properties(), new Authenticator() {@Override protected PasswordAuthentication getPasswordAuthentication() {return new PasswordAuthentication("ricardoelfuego@gmail.com", "cupbmuntnfklludh");}});
        this.envia(session, u, host);
        return ResponseEntity.ok("Oi filho");
    }

    public ResponseEntity<Iterable<UsuarioDTO>> getAllUsuarios() {
        return ResponseEntity.ok(this.mapper.toListUsuarioDTO(this.usuarioRepository.findAll()));
    }

    public ResponseEntity<String> deleteUser(Long id) {
        this.usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Ok");
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
    private void envia(Session session, Usuario u, String host){
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ricardoelfuego@gmail.com"));
        //Remetente
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
        message.setSubject("Dando dibros");
        message.setContent("Você está dando um dibros: \n<br/><br/> Link: <br/><a style='cursor:pointer;font-weigth:bolder;' href='"+host+"/nova-senha/"+this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT(Usuario.builder().id(u.getId()).email(u.getEmail()).build()))+"?esqueci="+(!u.getId().equals(0L))+"'>Clique aqui pra dibrar</a><br/><br/><br/><br/>", "text/html");
        Transport.send(message);
    }
}

