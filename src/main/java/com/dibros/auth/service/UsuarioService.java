package com.dibros.auth.service;

import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TokenCreator tokenCreator;

    public UsuarioDTO getApplicationUserByUsername(Usuario usuario) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(usuario);
    }

    public UsuarioDTO post(UsuarioPostDTO usuarioPostDTO) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public String emailToken(String email) {
        Properties props = new Properties(){{put("mail.smtp.host", "smtp.gmail.com");put("mail.smtp.socketFactory.port", "465");put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");put("mail.smtp.auth", "true");put("mail.smtp.port", "465");}};
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {protected PasswordAuthentication getPasswordAuthentication() {return new PasswordAuthentication("ricardoelfuego@gmail.com", "cupbmuntnfklludh");}});
        session.setDebug(true);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ricardoelfuego@gmail.com"));
            //Remetente
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Dando dibros");
            message.setText("Você está dando um dibros token: "+this.tokenCreator.createSignedJWT(new Usuario(null, email, null)).serialize());
            Transport.send(message);
            log.info("Feito!!!");
        } catch (MessagingException e) { throw new RuntimeException(e); }
        return "Oi filho";
    }

    public UsuarioDTO put(UsuarioPostDTO usuarioPostDTO) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public Iterable<UsuarioDTO> getAllUsuarios() {
        return UsuarioMapper.INSTANCE. toListUsuarioDTO(this.usuarioRepository.findAll());
    }

    public String deleteUser(Usuario principal, Long id) {
        this.usuarioRepository.deleteById(id);
        return "Ok";
    }


}
