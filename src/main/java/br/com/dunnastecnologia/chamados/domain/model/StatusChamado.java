package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "status_chamado")
public class StatusChamado {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

}