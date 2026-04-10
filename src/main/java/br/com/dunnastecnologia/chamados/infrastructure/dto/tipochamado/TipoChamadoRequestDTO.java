package br.com.dunnastecnologia.chamados.infrastructure.dto.tipochamado;

public record TipoChamadoRequestDTO(
        String titulo,
        Integer prazoHoras
) {
}
