package com.dibros.auth.mapper;

import com.dibros.auth.dto.UsuarioDTO;
import com.dibros.auth.dto.UsuarioPasswordDTO;
import com.dibros.auth.dto.UsuarioSaveDTO;
import com.dibros.auth.dto.UsuarioUpdateDTO;
import com.dibros.auth.records.UsuarioRecord;
import com.dibros.core.model.usuario.Usuario;
import com.dibros.core.records.ImagemRecord;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.concurrent.ThreadLocalRandom;

public final class UsuarioMapper {

    private UsuarioMapper() { throw new AssertionError(); }

    public static UsuarioRecord toRecord(Row row, RowMetadata rowMetadata) {
        return new UsuarioRecord(
                row.get("id", Long.class),
                row.get("email", String.class),
                row.get("nome", String.class),
                row.get("vulgo", String.class),
                row.get("imagem", String.class),
                row.get("password", String.class)
        );
    }

    public static Usuario toModel(UsuarioRecord record) {
        return Usuario.builder()
                .id(record.id())
                .email(record.email())
                .nome(record.nome())
                .vulgo(record.vulgo())
                .imagem(record.imagem())
                .password(record.password())
                .build();
    }

    public static Usuario toModel(String email) {
        return Usuario.builder()
                .id(0L)
                .email(email)
                .build();
    }

    public static Usuario toModel(Usuario usuario, UsuarioSaveDTO dto) {
        String vulgo = (dto.vulgo() != null && !dto.vulgo().isBlank()) ? dto.vulgo().trim().toLowerCase() : gerarVulgo(usuario.getEmail());
        return usuario.toBuilder()
                .nome(dto.nome().trim())
                .vulgo(vulgo)
                .password(dto.password())
                .build();
    }

    public static Usuario toModel(Usuario usuario, UsuarioUpdateDTO dto) {
        return usuario.toBuilder()
                .nome(dto.nome().trim())
                .vulgo(dto.vulgo().trim())
                .password(dto.password() != null && !dto.password().isBlank() ? dto.password() : null)
                .build();
    }

    public static Usuario toModel(Usuario usuario, UsuarioPasswordDTO dto) {
        return usuario.toBuilder()
                .password(dto.password())
                .build();
    }

    public static Usuario toModel(Usuario usuario, ImagemRecord img) {
        return usuario.toBuilder()
                .imagem(img.uuid() != null ? img.uuid() : "")
                .build();
    }

    public static UsuarioDTO toDTO(UsuarioRecord record) {
        return new UsuarioDTO(
                record.id(),
                record.email(),
                record.nome(),
                record.vulgo(),
                record.imagem()
        );
    }

    public static UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getVulgo(),
                usuario.getImagem()
        );
    }

    private static String gerarVulgo(String email) {
        String base = email.substring(0, email.indexOf('@'))
                .toLowerCase()
                .replaceAll("[^a-z0-9._]", "");
        base = base.length() > 20 ? base.substring(0, 20) : base;
        base = base.isBlank() ? "user" : base;
        return base + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

}
