package br.com.dunnastecnologia.chamados.infrastructure.service;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.UserCase.ChamadoUseCase;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import br.com.dunnastecnologia.chamados.domain.model.Morador;
import br.com.dunnastecnologia.chamados.domain.model.StatusChamado;
import br.com.dunnastecnologia.chamados.domain.model.TipoChamado;
import br.com.dunnastecnologia.chamados.domain.model.Unidade;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import br.com.dunnastecnologia.chamados.infrastructure.exception.ResourceNotFoundException;
import br.com.dunnastecnologia.chamados.infrastructure.repository.ChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.MoradorRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.StatusChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.TipoChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.UnidadeRepository;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.AuthenticatedUserValidator;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.PageResultMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ChamadoService implements ChamadoUseCase {

    private final ChamadoRepository chamadoRepository;
    private final MoradorRepository moradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final TipoChamadoRepository tipoChamadoRepository;
    private final StatusChamadoRepository statusChamadoRepository;
    private final AuthenticatedUserValidator authenticatedUserValidator;

    public ChamadoService(
            ChamadoRepository chamadoRepository,
            MoradorRepository moradorRepository,
            UnidadeRepository unidadeRepository,
            TipoChamadoRepository tipoChamadoRepository,
            StatusChamadoRepository statusChamadoRepository,
            AuthenticatedUserValidator authenticatedUserValidator
    ) {
        this.chamadoRepository = chamadoRepository;
        this.moradorRepository = moradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.tipoChamadoRepository = tipoChamadoRepository;
        this.statusChamadoRepository = statusChamadoRepository;
        this.authenticatedUserValidator = authenticatedUserValidator;
    }

    @Override
    @Transactional
    public Chamado abrirChamado(AuthenticatedUser morador, UUID unidadeId, UUID tipoChamadoId, String descricao) {
        authenticatedUserValidator.assertMorador(morador);
        if (descricao == null || descricao.isBlank()) {
            throw new BusinessRuleException("Descricao do chamado e obrigatoria");
        }
        if (!moradorRepository.existsByIdAndUnidadeId(morador.id(), unidadeId)) {
            throw new BusinessRuleException("Morador nao pode abrir chamado para esta unidade");
        }

        Morador moradorEntity = moradorRepository.findByIdAndAtivoTrue(morador.id())
                .orElseThrow(() -> new ResourceNotFoundException("Morador nao encontrado"));
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada"));
        TipoChamado tipoChamado = tipoChamadoRepository.findById(tipoChamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de chamado nao encontrado"));
        StatusChamado statusInicial = statusChamadoRepository.findByInicialPadraoTrue()
                .orElseThrow(() -> new BusinessRuleException("Nenhum status inicial padrao foi configurado"));

        Chamado chamado = new Chamado();
        chamado.setDescricao(descricao);
        chamado.setMorador(moradorEntity);
        chamado.setUnidade(unidade);
        chamado.setTipoChamado(tipoChamado);
        chamado.setStatus(statusInicial);
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.setDataFinalizacao(null);
        return chamadoRepository.save(chamado);
    }

    @Override
    public PageResult<Chamado> listarChamadosParaAdmin(
            AuthenticatedUser admin,
            UUID statusId,
            String moradorNome,
            PageRequest pageRequest
    ) {
        authenticatedUserValidator.assertAdministrador(admin);
        return PageResultMapper.fromPage(chamadoRepository.buscarParaAdmin(admin.id(), statusId, moradorNome, pageRequest));
    }

    @Override
    public PageResult<Chamado> listarChamadosParaColaborador(
            AuthenticatedUser colaborador,
            UUID statusId,
            UUID tipoChamadoId,
            String unidadeIdentificacao,
            PageRequest pageRequest
    ) {
        authenticatedUserValidator.assertColaborador(colaborador);
        return PageResultMapper.fromPage(
                chamadoRepository.buscarParaColaborador(
                        colaborador.id(),
                        statusId,
                        tipoChamadoId,
                        unidadeIdentificacao,
                        pageRequest
                )
        );
    }

    @Override
    public PageResult<Chamado> listarChamadosDoMorador(AuthenticatedUser morador, PageRequest pageRequest) {
        authenticatedUserValidator.assertMorador(morador);
        return PageResultMapper.fromPage(chamadoRepository.findByMoradorId(morador.id(), pageRequest));
    }

    @Override
    public Chamado buscarChamadoParaAdmin(AuthenticatedUser admin, UUID chamadoId) {
        authenticatedUserValidator.assertAdministrador(admin);
        return chamadoRepository.findByIdAndAdminId(admin.id(), chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado nao encontrado para o administrador"));
    }

    @Override
    public Chamado buscarChamadoParaColaborador(AuthenticatedUser colaborador, UUID chamadoId) {
        authenticatedUserValidator.assertColaborador(colaborador);
        return chamadoRepository.findByIdAndColaboradorId(colaborador.id(), chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado nao encontrado para o colaborador"));
    }

    @Override
    public Chamado buscarChamadoDoMorador(AuthenticatedUser morador, UUID chamadoId) {
        authenticatedUserValidator.assertMorador(morador);
        return chamadoRepository.findByIdAndMoradorId(chamadoId, morador.id())
                .orElseThrow(() -> new ResourceNotFoundException("Chamado nao encontrado para o morador"));
    }

    @Override
    @Transactional
    public Chamado atualizarStatusComoAdmin(AuthenticatedUser admin, UUID chamadoId, UUID statusId) {
        authenticatedUserValidator.assertAdministrador(admin);
        Chamado chamado = buscarChamadoParaAdmin(admin, chamadoId);
        return atualizarStatus(chamado, statusId);
    }

    @Override
    @Transactional
    public Chamado atualizarStatusComoColaborador(AuthenticatedUser colaborador, UUID chamadoId, UUID statusId) {
        authenticatedUserValidator.assertColaborador(colaborador);
        Chamado chamado = buscarChamadoParaColaborador(colaborador, chamadoId);
        return atualizarStatus(chamado, statusId);
    }

    @Override
    @Transactional
    public Chamado finalizarComoAdmin(AuthenticatedUser admin, UUID chamadoId) {
        authenticatedUserValidator.assertAdministrador(admin);
        Chamado chamado = buscarChamadoParaAdmin(admin, chamadoId);
        return finalizarChamado(chamado);
    }

    @Override
    @Transactional
    public Chamado finalizarComoColaborador(AuthenticatedUser colaborador, UUID chamadoId) {
        authenticatedUserValidator.assertColaborador(colaborador);
        Chamado chamado = buscarChamadoParaColaborador(colaborador, chamadoId);
        return finalizarChamado(chamado);
    }

    private Chamado atualizarStatus(Chamado chamado, UUID statusId) {
        if (chamado.getDataFinalizacao() != null) {
            throw new BusinessRuleException("Nao e permitido alterar o status de um chamado finalizado");
        }

        StatusChamado status = statusChamadoRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status de chamado nao encontrado"));

        chamado.setStatus(status);
        return chamadoRepository.save(chamado);
    }

    private Chamado finalizarChamado(Chamado chamado) {
        if (chamado.getDataFinalizacao() != null) {
            throw new BusinessRuleException("Chamado ja foi finalizado");
        }

        StatusChamado statusFinalizado = statusChamadoRepository.findByNome("Finalizado")
                .orElseThrow(() -> new BusinessRuleException("Status Finalizado nao foi configurado"));

        chamado.setStatus(statusFinalizado);
        chamado.setDataFinalizacao(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }
}
