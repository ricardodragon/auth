package com.dibros.auth.repository;

import com.dibros.auth.mapper.EmailMapper;
import com.dibros.auth.records.EmailRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.mail.Session;
import javax.mail.Transport;

@Repository
@RequiredArgsConstructor
public class EmailRepository {

    private final Session mailSession;

    @Value("${email.endereco}")
    private String remetente;

    public Mono<Void> enviar(EmailRecord email) {
        return Mono.fromCallable(() -> {
                Transport.send(EmailMapper.toMessage(this.mailSession, this.remetente, email));
                return null;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .then();
    }

}
