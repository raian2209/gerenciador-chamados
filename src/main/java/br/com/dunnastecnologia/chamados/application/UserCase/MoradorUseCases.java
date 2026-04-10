package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.Model.Chamado;
import br.com.dunnastecnologia.chamados.domain.Model.Comentario;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface MoradorUseCases {
    /**
     *  para permitir que o morador abra um chamado somente para uma unidade
     * que esteja vinculada a ele, registrando o tipo e a descricao inicial do problema.
     */
    Chamado abrirChamado(
            AuthenticatedUser morador,
            UUID unidadeId,
            UUID tipoChamadoId,
            String descricao
    );

    /**
     *  para listar apenas o historico de chamados que pertencem ao morador autenticado,
     * respeitando a regra de que ele nao acessa chamados de outras pessoas.
     */
    PageResult<Chamado> listarMeusChamados(
            AuthenticatedUser morador,
            PageRequest pageRequest
    );

    /**
     *  para recuperar um chamado especifico do morador quando ele precisar acompanhar
     * detalhes, status e interacoes sem expor registros fora do proprio escopo.
     */
    Chamado buscarMeuChamadoPorId(
            AuthenticatedUser morador,
            UUID chamadoId
    );

    /**
     *  para permitir comentario apenas em chamados do proprio morador e de suas unidades,
     * formando o historico de interacoes descrito no sistema.
     */
    Comentario comentarChamado(
            AuthenticatedUser morador,
            UUID chamadoId,
            String mensagem
    );
}
