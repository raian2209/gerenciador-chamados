package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Morador;
import br.com.dunnastecnologia.chamados.infrastructure.dto.morador.MoradorResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MoradorMapper {

    public MoradorResponseDTO toResponseDTO(Morador morador) {
        if (morador == null) {
            return null;
        }

        // Extrai apenas os IDs das unidades vinculadas ao morador para montar o DTO sem carregar as entidades completas.
        Set<UUID> unidadeIds = morador.getUnidades() == null
                ? Set.of()
                : morador.getUnidades().stream()
                .filter(Objects::nonNull)
                .map(unidade -> unidade.getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new MoradorResponseDTO(
                morador.getId(),
                morador.getNome(),
                morador.getEmail(),
                unidadeIds
        );
    }
}
