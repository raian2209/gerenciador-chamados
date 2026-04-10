package br.com.dunnastecnologia.chamados.infrastructure.dto.bloco;

import java.util.List;
import java.util.UUID;

public record BlocoResponseDTO(
        UUID id,
        String identificacao,
        Integer quantidadeAndares,
        Integer apartamentosPorAndar,
        List<UUID> unidadeIds
) {
}
