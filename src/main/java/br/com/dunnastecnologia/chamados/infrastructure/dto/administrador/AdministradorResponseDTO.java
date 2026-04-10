package br.com.dunnastecnologia.chamados.infrastructure.dto.administrador;

import java.util.UUID;

public record AdministradorResponseDTO(
        UUID id,
        String nome,
        String email
) {
}
