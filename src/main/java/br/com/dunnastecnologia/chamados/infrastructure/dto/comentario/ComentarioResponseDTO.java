package br.com.dunnastecnologia.chamados.infrastructure.dto.comentario;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComentarioResponseDTO(
        UUID id,
        String mensagem,
        LocalDateTime dataCriacao,
        UUID autorId,
        UUID chamadoId
) {
}
