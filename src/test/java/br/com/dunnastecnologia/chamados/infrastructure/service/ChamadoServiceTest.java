package br.com.dunnastecnologia.chamados.infrastructure.service;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import br.com.dunnastecnologia.chamados.domain.model.Morador;
import br.com.dunnastecnologia.chamados.domain.model.StatusChamado;
import br.com.dunnastecnologia.chamados.domain.model.TipoChamado;
import br.com.dunnastecnologia.chamados.domain.model.Unidade;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import br.com.dunnastecnologia.chamados.infrastructure.repository.ChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.MoradorRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.StatusChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.TipoChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.repository.UnidadeRepository;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.AuthenticatedUserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private MoradorRepository moradorRepository;
    @Mock
    private UnidadeRepository unidadeRepository;
    @Mock
    private TipoChamadoRepository tipoChamadoRepository;
    @Mock
    private StatusChamadoRepository statusChamadoRepository;
    @Mock
    private AuthenticatedUserValidator authenticatedUserValidator;

    @InjectMocks
    private ChamadoService chamadoService;

    @Test
    void abrirChamadoDeveCriarChamadoComStatusInicialPadrao() {
        UUID moradorId = UUID.randomUUID();
        UUID unidadeId = UUID.randomUUID();
        UUID tipoChamadoId = UUID.randomUUID();
        AuthenticatedUser moradorAutenticado = new AuthenticatedUser(moradorId, "morador@cond.local", "ROLE_MORADOR");

        Morador morador = new Morador();
        morador.setId(moradorId);

        Unidade unidade = new Unidade();
        unidade.setId(unidadeId);

        TipoChamado tipoChamado = new TipoChamado();
        tipoChamado.setId(tipoChamadoId);

        StatusChamado statusInicial = new StatusChamado();
        statusInicial.setNome("Solicitado");
        statusInicial.setInicialPadrao(Boolean.TRUE);

        when(moradorRepository.existsByIdAndUnidadeId(moradorId, unidadeId)).thenReturn(true);
        when(moradorRepository.findByIdAndAtivoTrue(moradorId)).thenReturn(Optional.of(morador));
        when(unidadeRepository.findById(unidadeId)).thenReturn(Optional.of(unidade));
        when(tipoChamadoRepository.findById(tipoChamadoId)).thenReturn(Optional.of(tipoChamado));
        when(statusChamadoRepository.findByInicialPadraoTrue()).thenReturn(Optional.of(statusInicial));
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chamado chamado = chamadoService.abrirChamado(moradorAutenticado, unidadeId, tipoChamadoId, "Vazamento");

        assertEquals("Vazamento", chamado.getDescricao());
        assertEquals(morador, chamado.getMorador());
        assertEquals(unidade, chamado.getUnidade());
        assertEquals(tipoChamado, chamado.getTipoChamado());
        assertEquals(statusInicial, chamado.getStatus());
        assertNotNull(chamado.getDataAbertura());
    }

    @Test
    void abrirChamadoDeveFalharQuandoMoradorNaoTemAcessoAUnidade() {
        UUID moradorId = UUID.randomUUID();
        UUID unidadeId = UUID.randomUUID();
        UUID tipoChamadoId = UUID.randomUUID();
        AuthenticatedUser moradorAutenticado = new AuthenticatedUser(moradorId, "morador@cond.local", "ROLE_MORADOR");

        when(moradorRepository.existsByIdAndUnidadeId(moradorId, unidadeId)).thenReturn(false);

        assertThrows(
                BusinessRuleException.class,
                () -> chamadoService.abrirChamado(moradorAutenticado, unidadeId, tipoChamadoId, "Descricao")
        );
    }

    @Test
    void finalizarComoColaboradorDeveAplicarStatusFinalizadoEDataFinalizacao() {
        UUID colaboradorId = UUID.randomUUID();
        UUID chamadoId = UUID.randomUUID();
        AuthenticatedUser colaborador = new AuthenticatedUser(colaboradorId, "colab@cond.local", "ROLE_COLABORADOR");

        Chamado chamado = new Chamado();
        chamado.setId(chamadoId);

        StatusChamado statusFinalizado = new StatusChamado();
        statusFinalizado.setNome("Finalizado");

        when(chamadoRepository.findByIdAndColaboradorId(colaboradorId, chamadoId)).thenReturn(Optional.of(chamado));
        when(statusChamadoRepository.findByNome("Finalizado")).thenReturn(Optional.of(statusFinalizado));
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chamado chamadoFinalizado = chamadoService.finalizarComoColaborador(colaborador, chamadoId);

        assertEquals(statusFinalizado, chamadoFinalizado.getStatus());
        assertNotNull(chamadoFinalizado.getDataFinalizacao());
        assertTrue(chamadoFinalizado.getDataFinalizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
