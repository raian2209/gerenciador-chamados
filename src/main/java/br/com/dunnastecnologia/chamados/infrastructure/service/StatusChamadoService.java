package br.com.dunnastecnologia.chamados.infrastructure.service;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.UserCase.StatusChamadoUseCase;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.StatusChamado;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import br.com.dunnastecnologia.chamados.infrastructure.exception.ResourceNotFoundException;
import br.com.dunnastecnologia.chamados.infrastructure.repository.StatusChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.AuthenticatedUserValidator;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.PageResultMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StatusChamadoService implements StatusChamadoUseCase {

    private final StatusChamadoRepository statusChamadoRepository;
    private final AuthenticatedUserValidator authenticatedUserValidator;

    public StatusChamadoService(
            StatusChamadoRepository statusChamadoRepository,
            AuthenticatedUserValidator authenticatedUserValidator
    ) {
        this.statusChamadoRepository = statusChamadoRepository;
        this.authenticatedUserValidator = authenticatedUserValidator;
    }

    @Override
    @Transactional
    public StatusChamado cadastrarStatus(AuthenticatedUser admin, String nome) {
        authenticatedUserValidator.assertAdministrador(admin);
        validateNome(nome);

        statusChamadoRepository.findByNome(nome)
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Ja existe um status com este nome");
                });

        StatusChamado status = new StatusChamado();
        status.setNome(nome);
        status.setInicialPadrao(Boolean.FALSE);
        return statusChamadoRepository.save(status);
    }

    @Override
    public PageResult<StatusChamado> listarStatus(PageRequest pageRequest) {
        return PageResultMapper.fromPage(statusChamadoRepository.findAll(pageRequest));
    }

    @Override
    public StatusChamado buscarStatusPorId(UUID statusId) {
        return statusChamadoRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status de chamado nao encontrado"));
    }

    @Override
    @Transactional
    public StatusChamado atualizarStatus(AuthenticatedUser admin, UUID statusId, String nome) {
        authenticatedUserValidator.assertAdministrador(admin);
        validateNome(nome);

        StatusChamado status = buscarStatusPorId(statusId);
        statusChamadoRepository.findByNome(nome)
                .filter(existing -> !existing.getId().equals(statusId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Ja existe um status com este nome");
                });

        status.setNome(nome);
        return statusChamadoRepository.save(status);
    }

    @Override
    @Transactional
    public void definirStatusInicialPadrao(AuthenticatedUser admin, UUID statusId) {
        authenticatedUserValidator.assertAdministrador(admin);

        StatusChamado status = buscarStatusPorId(statusId);
        statusChamadoRepository.clearStatusInicialPadrao();
        status.setInicialPadrao(Boolean.TRUE);
        statusChamadoRepository.save(status);
    }

    private void validateNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessRuleException("Nome do status e obrigatorio");
        }
    }
}
