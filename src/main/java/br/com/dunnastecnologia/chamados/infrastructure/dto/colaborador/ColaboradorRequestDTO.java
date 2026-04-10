package br.com.dunnastecnologia.chamados.infrastructure.dto.colaborador;

public record ColaboradorRequestDTO(
        String nome,
        String email,
        String senha
) {
}
