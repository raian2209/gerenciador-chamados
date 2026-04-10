package br.com.dunnastecnologia.chamados.infrastructure.dto.morador;

import java.util.Set;
import java.util.UUID;

public record MoradorRequestDTO(
        String nome,
        String email,
        String senha,
        Set<UUID> unidadeIds
) {
}
