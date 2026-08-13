package com.dibros.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${email.endereco}") // Puxa do application.properties
    private String endereco;

    @Value("${email.token}")    // Puxa do application.properties
    private String token;

    @Bean
    public Session mailSession() {
        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.ssl.checkServerIdentity", "true");
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.port", "465");
        p.put("mail.smtp.ssl.checkserveridentity", true);

        // Retorna a Session instanciada e autenticada
        return Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(endereco, token);
            }
        });
    }
}
