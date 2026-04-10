package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Comentario;
import br.com.dunnastecnologia.chamados.infrastructure.dto.comentario.ComentarioResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {

    public ComentarioResponseDTO toResponseDTO(Comentario comentario) {
        if (comentario == null) {
            return null;
        }

        return new ComentarioResponseDTO(
                comentario.getId(),
                comentario.getMensagem(),
                comentario.getDataCriacao(),
                comentario.getAutor() == null ? null : comentario.getAutor().getId(),
                comentario.getChamado() == null ? null : comentario.getChamado().getId()
        );
    }
}
