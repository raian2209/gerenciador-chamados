package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import br.com.dunnastecnologia.chamados.application.UserCase.AdminUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminWebController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebControllerSupport.class)
class AdminWebControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminUseCases adminUseCases;

    @MockitoBean
    private AnexoChamadoUseCases anexoChamadoUseCases;

    @MockitoBean
    private ComentarioUseCase comentarioUseCase;

    @Test
    void listarBlocosDeveEncaminharPaginacaoParaCasoDeUso() throws Exception {
        PageRequest pageRequest = PageRequest.of(2, 15);
        when(adminUseCases.listarBlocos(pageRequest))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 2, 15));

        mockMvc.perform(
                        get("/admin/blocos")
                                .with(authentication(WebTestAuthenticationFactory.administrador()))
                                .param("page", "2")
                                .param("size", "15")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/blocos/lista"))
                .andExpect(model().attributeExists("blocos", "blocosPage"));

        verify(adminUseCases).listarBlocos(pageRequest);
    }

    @Test
    void listarChamadosDevePropagarFiltrosParaCamadaDeAplicacao() throws Exception {
        UUID statusId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        PageRequest pageRequest = PageRequest.of(1, 5);
        when(adminUseCases.buscarChamados(
                new br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "admin@condominio.local",
                        "ROLE_ADMINISTRADOR"
                ),
                statusId,
                "Ana",
                pageRequest
        )).thenReturn(new PageResult<>(List.of(), 0, 0, 1, 5));
        when(adminUseCases.listarStatus(PageRequest.of(0, 100)))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 0, 100));

        mockMvc.perform(
                        get("/admin/chamados")
                                .with(authentication(WebTestAuthenticationFactory.administrador()))
                                .param("statusId", statusId.toString())
                                .param("moradorNome", "Ana")
                                .param("page", "1")
                                .param("size", "5")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/chamados/lista"))
                .andExpect(model().attributeExists("chamados", "chamadosPage", "statusDisponiveis"));

        verify(adminUseCases).buscarChamados(
                new br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "admin@condominio.local",
                        "ROLE_ADMINISTRADOR"
                ),
                statusId,
                "Ana",
                pageRequest
        );
    }

    @Test
    void removerUsuarioDeveRedirecionarParaListaAposSoftDelete() throws Exception {
        UUID usuarioId = UUID.fromString("00000000-0000-0000-0000-000000000020");

        mockMvc.perform(
                        post("/admin/usuarios/{usuarioId}/remover", usuarioId)
                                .with(authentication(WebTestAuthenticationFactory.administrador()))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios"));

        verify(adminUseCases).removerUsuario(
                new br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "admin@condominio.local",
                        "ROLE_ADMINISTRADOR"
                ),
                usuarioId
        );
    }
}
