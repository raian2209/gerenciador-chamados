package br.com.dunnastecnologia.chamados.infrastructure.dto.comentario;

import java.util.UUID;

public record ComentarioRequestDTO(
        String mensagem,
        UUID autorId,
        UUID chamadoId
) {
}
