package br.com.dunnastecnologia.chamados.infrastructure.dto.statuschamado;

import java.util.UUID;

public record StatusChamadoResponseDTO(
        UUID id,
        String nome
) {
}
