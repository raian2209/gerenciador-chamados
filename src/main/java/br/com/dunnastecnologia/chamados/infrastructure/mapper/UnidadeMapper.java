package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Unidade;
import br.com.dunnastecnologia.chamados.infrastructure.dto.unidade.UnidadeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMapper {

    public UnidadeResponseDTO toResponseDTO(Unidade unidade) {
        if (unidade == null) {
            return null;
        }

        return new UnidadeResponseDTO(
                unidade.getId(),
                unidade.getIdentificacao(),
                unidade.getAndar(),
                unidade.getBloco() == null ? null : unidade.getBloco().getId()
        );
    }
}
