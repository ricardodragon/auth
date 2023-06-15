package com.dibros.auth.service;

import com.dibros.auth.repository.UsuarioRepository;
import com.dibros.core.model.Usuario;
import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPostDTO;
import com.dibros.auth.mapper.UsuarioMapper;
import com.dibros.core.token.converter.TokenConverter;
import com.dibros.core.token.creator.TokenCreator;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private TokenConverter tokenConverter;

    public UsuarioDTO getApplicationUserByUsername(Usuario usuario) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(usuario);
    }

    public UsuarioDTO post(UsuarioPostDTO usuarioPostDTO) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    @SneakyThrows
    public UsuarioDTO postCriptografico(UsuarioPostDTO usuarioPostDTO, MultipartFile file) {
        usuarioPostDTO.setEmail(SignedJWT.parse(this.tokenConverter.decryptToken(usuarioPostDTO.getToken())).getJWTClaimsSet().getSubject());

        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public String emailToken(String email) {
        Session session = Session.getDefaultInstance(properties(), new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ricardoelfuego@gmail.com", "cupbmuntnfklludh");
            }
        });
        this.envia(session, email);
        return "Oi filho";
    }

    public UsuarioDTO put(UsuarioPostDTO usuarioPostDTO) {
        return UsuarioMapper.INSTANCE.toUsuarioDTO(this.usuarioRepository.save(UsuarioMapper.INSTANCE.toUsuarioCryp(usuarioPostDTO)));
    }

    public Iterable<UsuarioDTO> getAllUsuarios() {
        return UsuarioMapper.INSTANCE. toListUsuarioDTO(this.usuarioRepository.findAll());
    }

    public String deleteUser(Long id) {
        this.usuarioRepository.deleteById(id);
        return "Ok";
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
    private void envia(Session session, String email){
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ricardoelfuego@gmail.com"));
        //Remetente
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Dando dibros");
        message.setContent("Você está dando um dibros: \n<br/><br/> Link: <br/><a style='cursor:pointer;font-weigth:bolder;' href='http://localhost:3030/nova-senha/"+this.tokenCreator.createSignedJWT(new Usuario().builder().email(email).build()).serialize()+"'>Clique aqui pra dibrar</a><br/><br/><br/><br/>", "text/html");
        Transport.send(message);
        log.info("Feito!!!");
    }
}

