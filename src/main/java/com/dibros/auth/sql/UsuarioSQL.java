package com.dibros.auth.sql;

public final class UsuarioSQL {

    private UsuarioSQL() { throw new AssertionError(); }

    // language=MySQL
    public static final String FIND_BY_LOGIN = """
        SELECT * FROM usuario
        WHERE email = :login OR vulgo = :login
        LIMIT 1
        """;

    // language=MySQL
    public static final String FIND_BY_ID = """
        SELECT * FROM usuario WHERE id = :id_user
        """;

    // language=MySQL
    public static final String FIND_BY_EMAIL = """
        SELECT * FROM usuario WHERE email = :email
        """;

    // language=MySQL
    public static final String INSERT = """
        INSERT INTO usuario (password, email, nome, vulgo, imagem)
        VALUES (:password, :email, :nome, :vulgo, :imagem)
        """;

    // language=MySQL
    public static final String UPDATE = """
        UPDATE usuario
        SET nome = :nome,
            vulgo = COALESCE(:vulgo, vulgo),
            password = COALESCE(:password, password),
            imagem = IF(:imagem = '', imagem, :imagem)
        WHERE id = :id
        """;

    // language=MySQL
    public static final String UPDATE_PASSWORD = """
        UPDATE usuario SET password = :password WHERE id = :id
        """;

    // language=MySQL
    public static final String DELETE = """
        DELETE FROM usuario WHERE id = :id
        """;

}
