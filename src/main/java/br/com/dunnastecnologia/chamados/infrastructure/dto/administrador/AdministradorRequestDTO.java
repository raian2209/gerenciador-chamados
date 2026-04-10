package br.com.dunnastecnologia.chamados.infrastructure.dto.administrador;

public record AdministradorRequestDTO(
        String nome,
        String email,
        String senha
) {
}
