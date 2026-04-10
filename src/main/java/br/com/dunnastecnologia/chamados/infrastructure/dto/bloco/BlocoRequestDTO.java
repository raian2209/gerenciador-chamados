package br.com.dunnastecnologia.chamados.infrastructure.dto.bloco;

public record BlocoRequestDTO(
        String identificacao,
        Integer quantidadeAndares,
        Integer apartamentosPorAndar
) {
}
