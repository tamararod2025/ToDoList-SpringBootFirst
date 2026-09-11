package com.tamararodrigues.todolist.security;



import com.tamararodrigues.todolist.filter.FilterTaskAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration //anotacao nativa do spring framework. serve para avisar o springboot que a classe contem metodos de conf.
@EnableWebSecurity//conf, do springboot, liga o sistema de seguranca p/ requisicoes web(HTTP)
public class SecurityConfig {

    // Injeta o seu filtro customizado de tarefas
    @Autowired
    private FilterTaskAuth filterTaskAuth;

    @Bean //security criado com minhas "regrar", apenas para o spring aceitar e fazer o codigo rodar em cima
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())//desativa a protecao csfr(padrao de APIs REST)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users/", "/users").permitAll() //Permite POST em /users sem autenticação (para poder criar a conta)

                        // Todas as outras rotas (incluindo /tasks) exigem autenticação
                        .anyRequest().authenticated()
                )
                //.httpBasic(Customizer.withDefaults())
                .addFilterBefore(filterTaskAuth, UsernamePasswordAuthenticationFilter.class);
        //addfilterbefore insere o filtro customizado antes do nativo
        // filterTaskAuth meu filtro intercepta a request, decodifica o base64 e busca o no bando de dados para validar

        return http.build();//constroi e entrega uma cadeia de filtro customizada para o spring
    }

}
