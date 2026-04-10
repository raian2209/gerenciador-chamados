package br.com.dunnastecnologia.chamados.infrastructure.dto.chamado;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChamadoResponseDTO(
        UUID id,
        String descricao,
        LocalDateTime dataAbertura,
        LocalDateTime dataFinalizacao,
        UUID moradorId,
        UUID unidadeId,
        UUID tipoChamadoId,
        UUID statusId
) {
}
