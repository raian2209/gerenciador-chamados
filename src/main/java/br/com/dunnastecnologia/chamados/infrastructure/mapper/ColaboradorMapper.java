package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Colaborador;
import br.com.dunnastecnologia.chamados.infrastructure.dto.colaborador.ColaboradorResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorMapper {

    public ColaboradorResponseDTO toResponseDTO(Colaborador colaborador) {
        if (colaborador == null) {
            return null;
        }

        return new ColaboradorResponseDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail()
        );
    }
}
