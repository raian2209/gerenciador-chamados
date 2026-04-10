package br.com.dunnastecnologia.chamados.infrastructure.dto.morador;

import java.util.Set;
import java.util.UUID;

public record MoradorResponseDTO(
        UUID id,
        String nome,
        String email,
        Set<UUID> unidadeIds
) {
}
