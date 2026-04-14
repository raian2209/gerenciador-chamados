package br.com.dunnastecnologia.chamados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GerenciadorChamadosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciadorChamadosApplication.class, args);
	}

}
