package br.com.dunnastecnologia.chamados.infrastructure.dto.tipochamado;

import java.util.UUID;

public record TipoChamadoResponseDTO(
        UUID id,
        String titulo,
        Integer prazoHoras
) {
}
