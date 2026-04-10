package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.Model.Chamado;
import br.com.dunnastecnologia.chamados.domain.Model.Comentario;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface ColaboradorUserCases {
    PageResult<Chamado> buscarChamados(
            UUID statusId,
            UUID unidadeId,
            PageRequest pageRequest
    );

    Chamado buscarChamadoPorId(UUID chamadoId);

    Chamado atualizarStatusChamado(
            AuthenticatedUser colaborador,
            UUID chamadoId,
            UUID statusId
    );

    Chamado finalizarChamado(
            AuthenticatedUser colaborador,
            UUID chamadoId
    );

    Comentario comentarChamado(
            AuthenticatedUser colaborador,
            UUID chamadoId,
            String mensagem
    );
}
