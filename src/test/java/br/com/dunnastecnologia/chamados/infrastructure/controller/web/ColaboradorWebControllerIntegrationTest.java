package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.ColaboradorUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.ComentarioUseCase;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ColaboradorWebController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebControllerSupport.class)
class ColaboradorWebControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColaboradorUseCases colaboradorUseCases;

    @MockitoBean
    private AnexoChamadoUseCases anexoChamadoUseCases;

    @MockitoBean
    private ComentarioUseCase comentarioUseCase;

    @Test
    void listarChamadosDevePropagarFiltrosEPaginacaoDoColaborador() throws Exception {
        UUID statusId = UUID.fromString("00000000-0000-0000-0000-000000000030");
        UUID tipoChamadoId = UUID.fromString("00000000-0000-0000-0000-000000000031");
        PageRequest pageRequest = PageRequest.of(1, 12);
        var colaborador = new br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser(
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "colaborador@condominio.local",
                "ROLE_COLABORADOR"
        );

        when(colaboradorUseCases.buscarChamados(colaborador, statusId, tipoChamadoId, "A-101", pageRequest))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 1, 12));
        when(colaboradorUseCases.listarStatusDisponiveis(colaborador, PageRequest.of(0, 100)))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 0, 100));
        when(colaboradorUseCases.listarTiposChamadoDisponiveis(colaborador, PageRequest.of(0, 100)))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 0, 100));

        mockMvc.perform(
                        get("/colaborador/chamados")
                                .with(authentication(WebTestAuthenticationFactory.colaborador()))
                                .param("statusId", statusId.toString())
                                .param("tipoChamadoId", tipoChamadoId.toString())
                                .param("unidade", "A-101")
                                .param("page", "1")
                                .param("size", "12")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("colaborador/chamados/lista"))
                .andExpect(model().attributeExists("chamados", "chamadosPage", "statusDisponiveis", "tiposChamadoDisponiveis"));

        verify(colaboradorUseCases).buscarChamados(colaborador, statusId, tipoChamadoId, "A-101", pageRequest);
    }
}
