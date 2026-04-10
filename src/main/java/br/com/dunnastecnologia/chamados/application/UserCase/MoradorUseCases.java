package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.Model.Chamado;
import br.com.dunnastecnologia.chamados.domain.Model.Comentario;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface MoradorUseCases {
    Chamado abrirChamado(
            AuthenticatedUser morador,
            UUID unidadeId,
            UUID tipoChamadoId,
            String descricao
    );

    PageResult<Chamado> listarMeusChamados(
            AuthenticatedUser morador,
            PageRequest pageRequest
    );

    Comentario comentarChamado(
            AuthenticatedUser morador,
            UUID chamadoId,
            String mensagem
    );
}
