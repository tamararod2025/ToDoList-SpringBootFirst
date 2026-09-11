package com.tamararodrigues.todolist.filter;


import com.tamararodrigues.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    //Declaração e Injeção do Repositório
    @Autowired
    private IUserRepository userRepository;

    // Instancia o verificador nativo do Spring Security
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override //sobrescricao de um metodo ja existente(pai)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Filtrar apenas as rotas que precisam de autenticação
        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks")) {

            //pegar autenticacao(user e password)
            var authorization = request.getHeader("Authorization");//pega o retorno basic64 e coloca em uma variavel

            if (authorization == null || !authorization.startsWith("Basic ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Cabeçalho Authorization ausente ou inválido");
                return; // Interrompe a execução da cadeia de filtros
            }


            try {
                //substring remover o 'BASIC',pois n e necessario.Length o tamanho do Basic. trim remove os espacos que sobrou
                var authEncoded = authorization.substring("Basic".length()).trim();


                //decode do BASIC64
                byte[] authDecode = Base64.getDecoder().decode(authEncoded);

                var authString = new String(authDecode);

                //divisao de usuario e senha
                String[] credentials = authString.split(":", 2);

                if (credentials.length < 2) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Credenciais não fornecidas");
                    return;
                }

                String username = credentials[0];
                String password = credentials[1];

                // Busca usuário no banco
                //findByUsername pega oq o usuario digitou e faz a pesquisa/comparacao se existe na BD
                var user = this.userRepository.findByUsername(username);

                //validando user
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Usuário não encontrado");//401 sem autorizacao
                    return;//interrompe o user n existente
                }

                // Validação usando o Spring Security nativo (.matches)
                //this.passwordEncoder.matches- É o método responsavel por pegar a senha em texto puro, aplicar o mesmo algoritmo de hash e verificar se ela bate com o hash salvo no banco.
                //matches-comparação matemática segura dos hashes sem precisar expor a senha real.
                boolean passwordMatches = this.passwordEncoder.matches(password, user.getPassword());

                //se for falso
                if (!passwordMatches) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Senha incorreta");
                    return;//interrompe se a senha for incorreta
                }

                // pega o ID do usuário na memoria da  requisição para resgatar no TaskController
                //resumo:adciona o ID no request para mandar para outra class e assim saber qual o user exato do pedido , sem precisar buscar de novo na BD.
                request.setAttribute("idUser", user.getId());

                // Notificação de Sucesso ao Spring Security (Solução do 403)
                //resumo:Aqui notifico p/ o spring security, que ja validei o user e pass, e que agr ele pode fazer a segunranca nativa/padrao. mando o usuario, credentials(pass) null, e uma Collections/lista em branco
                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList()
                        );
                //com tudo ja verificado agr e preciso "armazenar" a informacao para o spring processar,
                //SecurityContextHolder.createEmptyContext() cria um objeto em branco
                // context.setAuthentication(authentication)-coloca a autenticacao dentro
                //curityContextHolder.setContext(context) ativa e salva para mandar pro spring analisar e depois ir pro controllerTask
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);//guarda na memoria temporaria do servidor Java

                //salva no HttpServletRequest
                //pq? o HttpServletRequest, e a unica coisa que 'viaja' do inicio ao fim do chamado HTTP, se pelo caminho for preciso processar algo em segundo plano ou redirecionar a requisicao, o spring nao 'esquece', pois esta salvo no HttpServletRequest, e ele so 'morre' depois que chegar na TaskController e tiver a resposta 'ResponseEntity'
                new RequestAttributeSecurityContextRepository().saveContext(context, request, response);

                //filtro de corrente, diz: fiz a minha parte, agr passe pro pocesso seguinte -> Security e depois TaskController
                filterChain.doFilter(request, response);

            } catch (IllegalArgumentException e) {

                // Se o Base64 enviado estiver inválido,
                // entramos aqui.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Usuário não autorizado");
            }
        } else {

            filterChain.doFilter(request, response);
        }
    }
}