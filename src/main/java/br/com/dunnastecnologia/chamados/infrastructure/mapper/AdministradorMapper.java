package br.com.dunnastecnologia.chamados.infrastructure.mapper;

import br.com.dunnastecnologia.chamados.domain.model.Administrador;
import br.com.dunnastecnologia.chamados.infrastructure.dto.administrador.AdministradorResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {

    public AdministradorResponseDTO toResponseDTO(Administrador administrador) {
        if (administrador == null) {
            return null;
        }

        return new AdministradorResponseDTO(
                administrador.getId(),
                administrador.getNome(),
                administrador.getEmail()
        );
    }
}
