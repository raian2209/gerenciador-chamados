package br.com.dunnastecnologia.chamados.infrastructure.service;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.UserCase.UsuarioUseCase;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.Morador;
import br.com.dunnastecnologia.chamados.domain.model.Unidade;
import br.com.dunnastecnologia.chamados.domain.model.Usuario;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import br.com.dunnastecnologia.chamados.infrastructure.exception.ResourceNotFoundException;
import br.com.dunnastecnologia.chamados.infrastructure.repository.MoradorRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.UnidadeRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.UsuarioRepository;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.AuthenticatedUserValidator;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.PageResultMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final MoradorRepository moradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final AuthenticatedUserValidator authenticatedUserValidator;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            MoradorRepository moradorRepository,
            UnidadeRepository unidadeRepository,
            AuthenticatedUserValidator authenticatedUserValidator,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.moradorRepository = moradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.authenticatedUserValidator = authenticatedUserValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario cadastrarUsuario(AuthenticatedUser admin, Usuario usuario) {
        authenticatedUserValidator.assertAdministrador(admin);
        validateUsuario(usuario);
        validateUniqueEmail(usuario.getEmail(), null);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario atualizarUsuario(AuthenticatedUser admin, UUID usuarioId, Usuario usuario) {
        authenticatedUserValidator.assertAdministrador(admin);
        validateUsuario(usuario);

        Usuario persistedUsuario = buscarUsuarioPorId(usuarioId);
        validateUniqueEmail(usuario.getEmail(), usuarioId);

        if (!persistedUsuario.getClass().equals(usuario.getClass())) {
            throw new BusinessRuleException("Nao e permitido alterar o tipo do usuario");
        }

        persistedUsuario.setNome(usuario.getNome());
        persistedUsuario.setEmail(usuario.getEmail());
        persistedUsuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if (persistedUsuario instanceof Morador persistedMorador && usuario instanceof Morador sourceMorador) {
            Set<Unidade> unidades = sourceMorador.getUnidades() == null
                    ? new HashSet<>()
                    : new HashSet<>(sourceMorador.getUnidades());
            persistedMorador.setUnidades(unidades);
        }

        return usuarioRepository.save(persistedUsuario);
    }

    @Override
    public PageResult<Usuario> listarUsuarios(PageRequest pageRequest) {
        return PageResultMapper.fromPage(usuarioRepository.findAll(pageRequest));
    }

    @Override
    public Usuario buscarUsuarioPorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    @Override
    @Transactional
    public void removerUsuario(AuthenticatedUser admin, UUID usuarioId) {
        authenticatedUserValidator.assertAdministrador(admin);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        usuarioRepository.delete(usuario);
    }

    @Override
    @Transactional
    public void vincularMoradorUnidade(AuthenticatedUser admin, UUID moradorId, UUID unidadeId) {
        authenticatedUserValidator.assertAdministrador(admin);
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Morador nao encontrado"));
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada"));

        morador.getUnidades().add(unidade);
        moradorRepository.save(morador);
    }

    @Override
    @Transactional
    public void desvincularMoradorUnidade(AuthenticatedUser admin, UUID moradorId, UUID unidadeId) {
        authenticatedUserValidator.assertAdministrador(admin);
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Morador nao encontrado"));

        morador.getUnidades().removeIf(unidade -> unidade.getId().equals(unidadeId));
        moradorRepository.save(morador);
    }

    private void validateUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new BusinessRuleException("Usuario e obrigatorio");
        }
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new BusinessRuleException("Nome do usuario e obrigatorio");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new BusinessRuleException("Email do usuario e obrigatorio");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new BusinessRuleException("Senha do usuario e obrigatoria");
        }
    }

    private void validateUniqueEmail(String email, UUID currentUserId) {
        usuarioRepository.findByEmail(email)
                .filter(existing -> currentUserId == null || !existing.getId().equals(currentUserId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Ja existe um usuario com este email");
                });
    }
}
