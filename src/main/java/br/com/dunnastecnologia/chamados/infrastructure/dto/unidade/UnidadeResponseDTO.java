package br.com.dunnastecnologia.chamados.infrastructure.dto.unidade;

import java.util.UUID;

public record UnidadeResponseDTO(
        UUID id,
        String identificacao,
        Integer andar,
        UUID blocoId
) {
}
