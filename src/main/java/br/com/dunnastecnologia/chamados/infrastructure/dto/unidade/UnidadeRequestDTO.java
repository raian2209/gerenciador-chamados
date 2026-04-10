package br.com.dunnastecnologia.chamados.infrastructure.dto.unidade;

import java.util.UUID;

public record UnidadeRequestDTO(
        String identificacao,
        Integer andar,
        UUID blocoId
) {
}
