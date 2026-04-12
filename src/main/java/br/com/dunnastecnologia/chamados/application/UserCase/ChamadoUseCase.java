package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface ChamadoUseCase {

    Chamado abrirChamado(
            AuthenticatedUser morador,
            UUID unidadeId,
            UUID tipoChamadoId,
            String descricao
    );

    PageResult<Chamado> listarChamadosParaAdmin(
            AuthenticatedUser admin,
            UUID statusId,
            String moradorNome,
            PageRequest pageRequest
    );

    PageResult<Chamado> listarChamadosParaColaborador(
            AuthenticatedUser colaborador,
            UUID statusId,
            UUID tipoChamadoId,
            String unidadeIdentificacao,
            PageRequest pageRequest
    );

    PageResult<Chamado> listarChamadosDoMorador(
            AuthenticatedUser morador,
            PageRequest pageRequest
    );

    Chamado buscarChamadoParaAdmin(
            AuthenticatedUser admin,
            UUID chamadoId
    );

    Chamado buscarChamadoParaColaborador(
            AuthenticatedUser colaborador,
            UUID chamadoId
    );

    Chamado buscarChamadoDoMorador(
            AuthenticatedUser morador,
            UUID chamadoId
    );

    Chamado atualizarStatusComoAdmin(
            AuthenticatedUser admin,
            UUID chamadoId,
            UUID statusId
    );

    Chamado atualizarStatusComoColaborador(
            AuthenticatedUser colaborador,
            UUID chamadoId,
            UUID statusId
    );

    Chamado finalizarComoAdmin(
            AuthenticatedUser admin,
            UUID chamadoId
    );

    Chamado finalizarComoColaborador(
            AuthenticatedUser colaborador,
            UUID chamadoId
    );
}
