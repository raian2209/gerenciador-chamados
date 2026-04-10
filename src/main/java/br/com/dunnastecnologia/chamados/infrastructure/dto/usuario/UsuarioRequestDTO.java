package br.com.dunnastecnologia.chamados.infrastructure.dto.usuario;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha
) {
}
