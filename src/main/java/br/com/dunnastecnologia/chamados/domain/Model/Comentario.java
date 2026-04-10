package br.com.dunnastecnologia.chamados.domain.Model;

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
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue
    private UUID id;

    private String mensagem;

    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id")
    private Chamado chamado;

}