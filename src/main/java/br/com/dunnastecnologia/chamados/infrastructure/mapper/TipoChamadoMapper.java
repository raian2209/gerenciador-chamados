package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.TipoChamado;
import br.com.dunnastecnologia.chamados.infrastructure.dto.tipochamado.TipoChamadoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TipoChamadoMapper {

    public TipoChamadoResponseDTO toResponseDTO(TipoChamado tipoChamado) {
        if (tipoChamado == null) {
            return null;
        }

        return new TipoChamadoResponseDTO(
                tipoChamado.getId(),
                tipoChamado.getTitulo(),
                tipoChamado.getPrazoHoras()
        );
    }
}
