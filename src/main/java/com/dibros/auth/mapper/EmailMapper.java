package com.dibros.auth.mapper;

import com.dibros.auth.records.EmailRecord;
import com.dibros.core.model.usuario.Usuario;
import com.dibros.core.token.creator.TokenCreator;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

public final class EmailMapper {

    private EmailMapper() { throw new AssertionError(); }

    @SneakyThrows
    public static EmailRecord toRecord(Usuario usuario, TokenCreator tokenCreator) {
        boolean isCadastro = usuario.getId() != null && usuario.getId().equals(0L);
        String rota = isCadastro ? "cadastro" : "nova-senha";
        String token = tokenCreator.encryptToken(tokenCreator.createSignedJWT(usuario));

        String assunto = isCadastro ? "Crie sua conta na DIBROS" : "Redefinição de senha • DIBROS";
        String preheader = isCadastro ? "Seu link de acesso para criar sua conta na DIBROS." : "Recebemos um pedido para redefinir sua senha.";
        String titulo = isCadastro ? "Bem-vindo à DIBROS" : "Redefinir sua senha";
        String descricao = isCadastro ? "Use o botão abaixo para validar seu e-mail e criar sua conta agora:" : "Recebemos uma solicitação para alterar a senha da sua conta. Clique no botão abaixo para escolher uma nova senha:";
        String botao = isCadastro ? "Criar minha conta" : "Criar nova senha";

        String html = new ClassPathResource("templates/email-token.html")
                .getContentAsString(StandardCharsets.UTF_8)
                .replace("{{ASSUNTO}}", assunto)
                .replace("{{PREHEADER}}", preheader)
                .replace("{{TITULO}}", titulo)
                .replace("{{DESCRICAO}}", descricao)
                .replace("{{BOTAO}}", botao)
                .replace("{{ROTA}}", rota)
                .replace("{{TOKEN}}", token);;

        return new EmailRecord(usuario.getEmail(), assunto, html);
    }

    @SneakyThrows
    public static Message toMessage(Session session, String remetente, EmailRecord email) {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remetente, "DIBROS"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.destinatario()));
        message.setSubject(email.assunto());
        message.setContent(email.html(), "text/html; charset=utf-8");
        return message;
    }

}
