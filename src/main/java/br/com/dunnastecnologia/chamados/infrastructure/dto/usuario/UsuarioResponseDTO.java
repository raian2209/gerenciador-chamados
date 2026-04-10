package br.com.dunnastecnologia.chamados.infrastructure.dto.usuario;

import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email
) {
}
