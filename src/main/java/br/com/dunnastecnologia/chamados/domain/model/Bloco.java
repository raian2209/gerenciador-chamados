package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "blocos")
@Getter
public class Bloco {

    @Id
    @GeneratedValue
    private UUID id;

    private String identificacao;

    private Integer quantidadeAndares;

    private Integer apartamentosPorAndar;

    @OneToMany(mappedBy = "bloco")
    private List<Unidade> unidades;

}
