package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Colaborador;
import br.com.dunnastecnologia.chamados.infrastructure.dto.colaborador.ColaboradorResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ColaboradorMapper {

    public ColaboradorResponseDTO toResponseDTO(Colaborador colaborador) {
        if (colaborador == null) {
            return null;
        }

        Set<UUID> tipoChamadoIds = colaborador.getTiposChamadoResponsaveis() == null
                ? Set.of()
                : colaborador.getTiposChamadoResponsaveis().stream()
                .filter(Objects::nonNull)
                .map(tipoChamado -> tipoChamado.getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new ColaboradorResponseDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail(),
                tipoChamadoIds
        );
    }
}
