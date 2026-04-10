package br.com.dunnastecnologia.chamados.domain.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

import java.util.UUID;

@Entity
@Table(name = "unidades")
public class Unidade {

    @Id
    @GeneratedValue
    private UUID id;

    private String identificacao;

    private Integer andar;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bloco bloco;

}