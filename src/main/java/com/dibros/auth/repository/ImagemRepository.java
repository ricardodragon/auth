package com.dibros.auth.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ImagemRepository {

    public Mono<Void> saveImagem(String uri, FilePart imagem){
        Path path = Paths.get(uri);
        return Mono.fromCallable(() -> Files.createDirectories(path.getParent()))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.defer(() -> imagem.transferTo(path)))
                .onErrorMap(throwable -> new RuntimeException("Erro ao salvar o arquivo", throwable));
    }

    public Flux<DataBuffer> getImagem(String uri) {
        return DataBufferUtils.read(
            Paths.get(uri),
            new DefaultDataBufferFactory(),
            4096
        ).onErrorResume(IOException.class, e -> Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada")));
    }

    public Mono<Void> delete(String uri){
        return Mono.fromCallable(()-> {
                FileUtils.deleteDirectory(new File(uri));
                return null;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(throwable -> new RuntimeException("Erro ao excluir o arquivo", throwable))
            .then();
    }
}
