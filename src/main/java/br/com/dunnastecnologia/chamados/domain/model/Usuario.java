package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;
}
