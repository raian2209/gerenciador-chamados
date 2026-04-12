package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "anexos_chamado")
@Getter
@Setter
public class AnexoChamado {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id")
    private Chamado chamado;

    private String nomeArquivo;

    private String contentType;

    private Long tamanhoBytes;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "conteudo")
    private byte[] conteudo;
}
