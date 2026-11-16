package com.dibros.auth.mapper;

import com.dibros.auth.records.EmailRecord;
import com.dibros.core.model.usuario.Usuario;
import com.dibros.core.token.creator.TokenCreator;
import lombok.SneakyThrows;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.lang.String.format;

public final class EmailMapper {

    private EmailMapper() { throw new AssertionError(); }

    private static final String BODY_TOKEN = """
        Você está dando um dibros: <br/><br/>
        Link: <br/>
        <a style='cursor:pointer;font-weight:bolder;' href='https://dibros.com.br/{0}/{1}'>
            Clique aqui pra dibrar
        </a><br/><br/>
        """;

    public static EmailRecord toRecord(Usuario usuario, TokenCreator tokenCreator) {
        String rota = (usuario.getId() != null && usuario.getId().equals(0L)) ? "cadastro" : "nova-senha";
        String token = tokenCreator.encryptToken(tokenCreator.createSignedJWT(usuario));
        return new EmailRecord(
                usuario.getEmail(),
                "Dando dibros",
                format(BODY_TOKEN, rota, token)
        );
    }

    @SneakyThrows
    public static Message toMessage(Session session, String remetente, EmailRecord email) {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remetente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.destinatario()));
        message.setSubject(email.assunto());
        message.setContent(email.html(), "text/html; charset=utf-8");
        return message;
    }

}
