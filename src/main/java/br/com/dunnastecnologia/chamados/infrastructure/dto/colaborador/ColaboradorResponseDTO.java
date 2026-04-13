package br.com.dunnastecnologia.chamados.infrastructure.dto.colaborador;

import java.util.Set;
import java.util.UUID;

public record ColaboradorResponseDTO(
        UUID id,
        String nome,
        String email,
        Set<UUID> tipoChamadoIds
) {
}
