package br.com.dunnastecnologia.chamados.application.UserCase;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.Usuario;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface UsuarioUseCase {

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
}
