package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Bloco;
import br.com.dunnastecnologia.chamados.infrastructure.dto.bloco.BlocoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class BlocoMapper {

    public BlocoResponseDTO toResponseDTO(Bloco bloco) {
        if (bloco == null) {
            return null;
        }

        // Converte a lista de unidades do bloco para uma lista apenas com os IDs expostos no DTO.
        List<UUID> unidadeIds = bloco.getUnidades() == null
                ? List.of()
                : bloco.getUnidades().stream()
                .filter(Objects::nonNull)
                .map(unidade -> unidade.getId())
                .filter(Objects::nonNull)
                .toList();

        return new BlocoResponseDTO(
                bloco.getId(),
                bloco.getIdentificacao(),
                bloco.getQuantidadeAndares(),
                bloco.getApartamentosPorAndar(),
                unidadeIds
        );
    }
}
