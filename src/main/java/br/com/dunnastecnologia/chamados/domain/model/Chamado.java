package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue
    private UUID id;

    private String descricao;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFinalizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id")
    private Morador morador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    private TipoChamado tipoChamado;

    @ManyToOne(fetch = FetchType.LAZY)
    private StatusChamado status;

}