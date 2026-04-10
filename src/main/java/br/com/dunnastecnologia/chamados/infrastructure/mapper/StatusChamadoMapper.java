package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.StatusChamado;
import br.com.dunnastecnologia.chamados.infrastructure.dto.statuschamado.StatusChamadoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class StatusChamadoMapper {

    public StatusChamadoResponseDTO toResponseDTO(StatusChamado statusChamado) {
        if (statusChamado == null) {
            return null;
        }

        return new StatusChamadoResponseDTO(
                statusChamado.getId(),
                statusChamado.getNome()
        );
    }
}
