package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import br.com.dunnastecnologia.chamados.infrastructure.dto.chamado.ChamadoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ChamadoMapper {

    public ChamadoResponseDTO toResponseDTO(Chamado chamado) {
        if (chamado == null) {
            return null;
        }

        return new ChamadoResponseDTO(
                chamado.getId(),
                chamado.getDescricao(),
                chamado.getDataAbertura(),
                chamado.getDataFinalizacao(),
                chamado.getMorador() == null ? null : chamado.getMorador().getId(),
                chamado.getUnidade() == null ? null : chamado.getUnidade().getId(),
                chamado.getTipoChamado() == null ? null : chamado.getTipoChamado().getId(),
                chamado.getStatus() == null ? null : chamado.getStatus().getId()
        );
    }
}
