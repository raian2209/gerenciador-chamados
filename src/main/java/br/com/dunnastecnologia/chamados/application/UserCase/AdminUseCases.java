package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.Model.Bloco;
import br.com.dunnastecnologia.chamados.domain.Model.StatusChamado;
import br.com.dunnastecnologia.chamados.domain.Model.TipoChamado;
import br.com.dunnastecnologia.chamados.domain.Model.Usuario;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface AdminUseCases {

    // operação de Bloco
    Bloco cadastrarBloco(
            AuthenticatedUser admin,
            String identificacao,
            int quantidadeAndares,
            int apartamentosPorAndar
    );

    Bloco buscarBlocoPorId(UUID blocoId);

    PageResult<Bloco> listarBlocos(PageRequest pageRequest);

    // operação de Usuario
    Usuario cadastrarUsuario(
            AuthenticatedUser admin,
            Usuario usuario
    );

    Usuario atualizarUsuario(
            AuthenticatedUser admin,
            UUID usuarioId,
            Usuario usuario
    );

    PageResult<Usuario> listarUsuarios(PageRequest pageRequest);

    Usuario buscarUsuarioPorId(UUID usuarioId);

    void removerUsuario(
            AuthenticatedUser admin,
            UUID usuarioId
    );

    void vincularMoradorUnidade(
            AuthenticatedUser admin,
            UUID moradorId,
            UUID unidadeId
    );

    void desvincularMoradorUnidade(
            AuthenticatedUser admin,
            UUID moradorId,
            UUID unidadeId
    );

    // Operações de TipoChamado
    TipoChamado cadastrarTipoChamado(
            AuthenticatedUser admin,
            String titulo,
            int prazoHoras
    );

    PageResult<TipoChamado> listarTiposChamado(PageRequest pageRequest);

    TipoChamado buscarPorId(UUID tipoId);


    // Operações de StatusChamado
    StatusChamado cadastrarStatus(
            AuthenticatedUser admin,
            String nome
    );

    PageResult<StatusChamado> listarStatus(PageRequest pageRequest);

    StatusChamado buscarStatusPorId(UUID statusId);

}
