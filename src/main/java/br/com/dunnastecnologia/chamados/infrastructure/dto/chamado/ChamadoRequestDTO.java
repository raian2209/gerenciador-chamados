package br.com.dunnastecnologia.chamados.infrastructure.dto.chamado;

import java.util.UUID;

public record ChamadoRequestDTO(
        String descricao,
        UUID moradorId,
        UUID unidadeId,
        UUID tipoChamadoId,
        UUID statusId
) {
}
