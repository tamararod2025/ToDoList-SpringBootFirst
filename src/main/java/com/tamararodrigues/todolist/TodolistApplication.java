package com.tamararodrigues.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*anotacao onde inicializa, 3 principais anotacoes
* 1- @configuration(avisa ao spring que pode conter o @bean)
* 2- @EnableAutoConfiguration(conf. tudo automaticamente e so adcionar as dependencia no pom.xml)
* 3- @ComponentScan(scanear todo o pacote e registra automaticamente)
* */
public class TodolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodolistApplication.class, args);//liga o motor, cria o servidor web Tomcat,inicia o container so spring e le todas anotacoes da API para receber as requisicoes HTTP
	}

}
