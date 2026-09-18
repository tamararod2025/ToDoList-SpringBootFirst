package com.tamararodrigues.todolist.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {

        //class criada para evitar o loop de dependência circular


        //instancia o objeto BCryptPasswordEncoder que e responsavel em pegar texto limpo e transformar em hash
        //assim com o @Bean injeta automaticamente onde for preciso
        return new BCryptPasswordEncoder();
    }

}
