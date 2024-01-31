package com.dibros.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dibros.core.model.Usuario;
import com.dibros.core.token.property.JwtConfiguration;
import com.dibros.core.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;
    private final TokenCreator tokenCreator;


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Autenticando ...");
        Usuario usuario = null;
        try (ServletInputStream body = request.getInputStream()){
            usuario = new ObjectMapper().readValue(body, Usuario.class);
            if (usuario == null)
                throw new UsernameNotFoundException("Unable to retrieve the username or password ");
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getPassword(), emptyList());

        usernamePasswordAuthenticationToken.setDetails(usuario);
        try{
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }catch (Exception e){
            response.setStatus(401);
            response.setHeader("Content-Type", "text/plain; charset=UTF-8");
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
            return null;
        }
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {
        //response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN" + jwtConfiguration.getHeader().getName());
        response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + this.tokenCreator.encryptToken(this.tokenCreator.createSignedJWT((Usuario) auth.getPrincipal())));
    }
}
