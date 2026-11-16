package com.dibros.auth.records;

public record EmailRecord(
        String destinatario,
        String assunto,
        String html
) {}