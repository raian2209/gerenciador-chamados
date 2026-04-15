package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import br.com.dunnastecnologia.chamados.application.UserCase.AdminUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoComentarioUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.ComentarioUseCase;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.Administrador;
import br.com.dunnastecnologia.chamados.domain.model.Colaborador;
import br.com.dunnastecnologia.chamados.domain.model.Morador;
import br.com.dunnastecnologia.chamados.domain.model.Unidade;
import br.com.dunnastecnologia.chamados.domain.model.Usuario;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.AtualizarStatusForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.BlocoForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.ComentarioForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.StatusChamadoForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.TipoChamadoForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.UsuarioForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.VincularColaboradorTipoChamadoForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.VincularMoradorUnidadeForm;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminWebController {

    private static final Set<String> STATUS_RESERVADOS = Set.of("Finalizado", "Atrasado", "Solicitado");

    private final AdminUseCases adminUseCases;
    private final AnexoChamadoUseCases anexoChamadoUseCases;
    private final AnexoComentarioUseCases anexoComentarioUseCases;
    private final ComentarioUseCase comentarioUseCase;
    private final WebControllerSupport support;

    public AdminWebController(
            AdminUseCases adminUseCases,
            AnexoChamadoUseCases anexoChamadoUseCases,
            AnexoComentarioUseCases anexoComentarioUseCases,
            ComentarioUseCase comentarioUseCase,
            WebControllerSupport support
    ) {
        this.adminUseCases = adminUseCases;
        this.anexoChamadoUseCases = anexoChamadoUseCases;
        this.anexoComentarioUseCases = anexoComentarioUseCases;
        this.comentarioUseCase = comentarioUseCase;
        this.support = support;
    }

    @ModelAttribute("blocoForm")
    public BlocoForm blocoForm() {
        return new BlocoForm();
    }

    @ModelAttribute("usuarioForm")
    public UsuarioForm usuarioForm() {
        return new UsuarioForm();
    }

    @ModelAttribute("tipoChamadoForm")
    public TipoChamadoForm tipoChamadoForm() {
        return new TipoChamadoForm();
    }

    @ModelAttribute("statusChamadoForm")
    public StatusChamadoForm statusChamadoForm() {
        return new StatusChamadoForm();
    }

    @ModelAttribute("atualizarStatusForm")
    public AtualizarStatusForm atualizarStatusForm() {
        return new AtualizarStatusForm();
    }

    @ModelAttribute("comentarioForm")
    public ComentarioForm comentarioForm() {
        return new ComentarioForm();
    }

    @ModelAttribute("vincularMoradorUnidadeForm")
    public VincularMoradorUnidadeForm vincularMoradorUnidadeForm() {
        return new VincularMoradorUnidadeForm();
    }

    @ModelAttribute("vincularColaboradorTipoChamadoForm")
    public VincularColaboradorTipoChamadoForm vincularColaboradorTipoChamadoForm() {
        return new VincularColaboradorTipoChamadoForm();
    }

    @ModelAttribute("tiposUsuario")
    public Map<String, String> tiposUsuario() {
        return Map.of(
                "ADMINISTRADOR", "Administrador",
                "COLABORADOR", "Colaborador",
                "MORADOR", "Morador"
        );
    }

    @GetMapping({"", "/"})
    @Transactional(readOnly = true)
    public String dashboard(
            Authentication authentication,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        PageResult<?> blocos = adminUseCases.listarBlocos(support.pageRequest(0, 1));
        PageResult<?> usuarios = adminUseCases.listarUsuarios(support.pageRequest(0, 1));
        PageResult<?> tipos = adminUseCases.listarTiposChamado(support.pageRequest(0, 1));
        PageResult<?> status = adminUseCases.listarStatus(support.pageRequest(0, 1));
        var statusDisponiveis = adminUseCases.listarStatus(support.pageRequest(0, 100));
        var chamados = adminUseCases.buscarChamados(currentUser, null, null, null, support.pageRequest(0, 5));
        UUID statusAtrasadoId = statusDisponiveis.content().stream()
                .filter(statusChamado -> "Atrasado".equalsIgnoreCase(statusChamado.getNome()))
                .map(br.com.dunnastecnologia.chamados.domain.model.StatusChamado::getId)
                .findFirst()
                .orElse(null);
        long totalChamadosAtrasados = statusAtrasadoId == null
                ? 0
                : adminUseCases.buscarChamados(currentUser, statusAtrasadoId, null, null, support.pageRequest(0, 1)).totalElements();

        model.addAttribute("pageTitle", "Painel do Administrador");
        model.addAttribute("totalBlocos", blocos.totalElements());
        model.addAttribute("totalUsuarios", usuarios.totalElements());
        model.addAttribute("totalTiposChamado", tipos.totalElements());
        model.addAttribute("totalStatus", status.totalElements());
        model.addAttribute("totalChamados", chamados.totalElements());
        model.addAttribute("totalChamadosAtrasados", totalChamadosAtrasados);
        model.addAttribute("chamadosRecentes", support.mapContent(chamados.content(), support::toChamadoMap));
        return "admin/dashboard";
    }

    @GetMapping({"/vinculos-morador", "/vinculos-morador/"})
    @Transactional(readOnly = true)
    public String gerenciarVinculosMorador(
            @RequestParam(required = false) UUID moradorId,
            @RequestParam(required = false) UUID blocoId,
            @RequestParam(required = false) String moradorEmail,
            @RequestParam(required = false) String cadastradosEmail,
            @RequestParam(defaultValue = "0") Integer cadastradosPage,
            @RequestParam(defaultValue = "10") Integer cadastradosSize,
            @RequestParam(required = false) String semUnidadeEmail,
            @RequestParam(defaultValue = "0") Integer semUnidadePage,
            @RequestParam(defaultValue = "10") Integer semUnidadeSize,
            Model model
    ) {
        var moradores = adminUseCases.listarMoradoresPorPrefixoEmail(moradorEmail, support.pageRequest(0, 100));
        var moradoresCadastrados = adminUseCases.listarMoradoresPorPrefixoEmail(
                cadastradosEmail,
                support.pageRequest(cadastradosPage, cadastradosSize)
        );
        var blocos = adminUseCases.listarBlocos(support.pageRequest(0, 100));
        var moradoresSemUnidade = adminUseCases.listarMoradoresSemUnidadePorPrefixoEmail(
                semUnidadeEmail,
                support.pageRequest(semUnidadePage, semUnidadeSize)
        );

        model.addAttribute("pageTitle", "Vincular Morador");
        model.addAttribute("moradoresDisponiveis", support.mapContent(moradores.content(), support::toUsuarioMap));
        model.addAttribute("moradoresCadastrados", support.mapContent(moradoresCadastrados.content(), support::toUsuarioMap));
        model.addAttribute("moradoresCadastradosPage", support.pageMetadata(moradoresCadastrados));
        model.addAttribute("blocosDisponiveis", support.mapContent(blocos.content(), support::toBlocoMap));
        model.addAttribute("moradoresSemUnidade", support.mapContent(moradoresSemUnidade.content(), support::toUsuarioMap));
        model.addAttribute("moradoresSemUnidadePage", support.pageMetadata(moradoresSemUnidade));
        model.addAttribute("moradorSelecionadoId", moradorId);
        model.addAttribute("blocoSelecionadoId", blocoId);
        model.addAttribute("filtroMoradorEmail", moradorEmail);
        model.addAttribute("filtroCadastradosEmail", cadastradosEmail);
        model.addAttribute("filtroSemUnidadeEmail", semUnidadeEmail);

        if (moradorId != null) {
            carregarVinculosMorador(model, moradorId, blocoId);
        }

        return "admin/vinculos-morador/lista";
    }

    @GetMapping({"/escopo-colaborador", "/escopo-colaborador/"})
    @Transactional(readOnly = true)
    public String gerenciarEscopoColaborador(
            @RequestParam(required = false) UUID colaboradorId,
            @RequestParam(required = false) String colaboradorEmail,
            Model model
    ) {
        var colaboradores = adminUseCases.listarColaboradoresPorPrefixoEmail(
                colaboradorEmail,
                support.pageRequest(0, 100)
        );
        var tiposChamado = adminUseCases.listarTiposChamado(support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Designar Colaborador");
        model.addAttribute("colaboradoresDisponiveis", support.mapContent(colaboradores.content(), support::toUsuarioMap));
        model.addAttribute("tiposChamadoDisponiveis", support.mapContent(tiposChamado.content(), support::toTipoChamadoMap));
        model.addAttribute("colaboradorSelecionadoId", colaboradorId);
        model.addAttribute("filtroColaboradorEmail", colaboradorEmail);

        if (colaboradorId != null) {
            carregarEscopoColaborador(model, colaboradorId);
        }

        return "admin/escopo-colaborador/lista";
    }

    @GetMapping({"/blocos", "/blocos/"})
    @Transactional(readOnly = true)
    public String listarBlocos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model
    ) {
        var blocos = adminUseCases.listarBlocos(support.pageRequest(page, size));
        model.addAttribute("pageTitle", "Blocos");
        model.addAttribute("blocos", support.mapContent(blocos.content(), support::toBlocoMap));
        model.addAttribute("blocosPage", support.pageMetadata(blocos));
        return "admin/blocos/lista";
    }

    @PostMapping("/blocos")
    public String cadastrarBloco(
            Authentication authentication,
            @ModelAttribute BlocoForm blocoForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.cadastrarBloco(
                support.authenticatedUser(authentication),
                blocoForm.getIdentificacao(),
                defaultInteger(blocoForm.getQuantidadeAndares()),
                defaultInteger(blocoForm.getApartamentosPorAndar())
        );
        redirectAttributes.addFlashAttribute("successMessage", "Bloco cadastrado com sucesso.");
        return "redirect:/admin/blocos";
    }

    @GetMapping("/blocos/{blocoId}")
    @Transactional(readOnly = true)
    public String detalharBloco(
            @PathVariable UUID blocoId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            Model model
    ) {
        var bloco = adminUseCases.buscarBlocoPorId(blocoId);
        var unidades = adminUseCases.listarUnidadesDoBloco(blocoId, support.pageRequest(page, size));
        var moradoresPorUnidade = adminUseCases.listarMoradoresPorUnidadeIds(
                unidades.content().stream().map(Unidade::getId).toList()
        );

        model.addAttribute("pageTitle", "Detalhes do Bloco");
        model.addAttribute("bloco", support.toBlocoMap(bloco));
        model.addAttribute(
                "unidades",
                unidades.content().stream()
                        .map(unidade -> {
                            var values = support.toUnidadeMap(unidade);
                            var moradores = moradoresPorUnidade.getOrDefault(unidade.getId(), java.util.List.of());
                            values.put(
                                    "moradores",
                                    moradores.stream().map(support::toUsuarioMap).toList()
                            );
                            return values;
                        })
                        .toList()
        );
        model.addAttribute("unidadesPage", support.pageMetadata(unidades));
        return "admin/blocos/detalhe";
    }

    @GetMapping({"/usuarios", "/usuarios/"})
    @Transactional(readOnly = true)
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model
    ) {
        var usuarios = adminUseCases.listarUsuarios(support.pageRequest(page, size));

        model.addAttribute("pageTitle", "Usuarios");
        model.addAttribute("usuarios", support.mapContent(usuarios.content(), support::toUsuarioMap));
        model.addAttribute("usuariosPage", support.pageMetadata(usuarios));
        return "admin/usuarios/lista";
    }

    @PostMapping("/usuarios")
    public String cadastrarUsuario(
            Authentication authentication,
            @ModelAttribute UsuarioForm usuarioForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.cadastrarUsuario(support.authenticatedUser(authentication), novoUsuario(usuarioForm));
        redirectAttributes.addFlashAttribute("successMessage", "Usuario cadastrado com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/{usuarioId}")
    @Transactional(readOnly = true)
    public String detalharUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) UUID blocoId,
            Model model
    ) {
        Usuario usuario = adminUseCases.buscarUsuarioPorId(usuarioId);
        var usuarioForm = new UsuarioForm();
        usuarioForm.setNome(usuario.getNome());
        usuarioForm.setEmail(usuario.getEmail());
        usuarioForm.setTipo(resolveTipoUsuario(usuario));

        model.addAttribute("pageTitle", "Detalhes do Usuario");
        model.addAttribute("usuario", support.toUsuarioMap(usuario));
        model.addAttribute("usuarioForm", usuarioForm);

        if (usuario instanceof Morador) {
            var unidadesMorador = adminUseCases.listarUnidadesDoMorador(usuarioId, support.pageRequest(0, 100));
            var blocos = adminUseCases.listarBlocos(support.pageRequest(0, 100));
            var unidadesMoradorContent = unidadesMorador.content();
            Set<UUID> unidadeIdsVinculadas = unidadesMoradorContent.stream()
                    .map(unidade -> unidade.getId())
                    .collect(Collectors.toSet());

            model.addAttribute("unidadesMorador", support.mapContent(unidadesMorador.content(), support::toUnidadeMap));
            model.addAttribute("blocosDisponiveis", support.mapContent(blocos.content(), support::toBlocoMap));

            if (blocoId != null) {
                var unidadesBloco = adminUseCases.listarUnidadesDoBloco(blocoId, support.pageRequest(0, 100));
                var form = new VincularMoradorUnidadeForm();
                form.setBlocoId(blocoId);
                model.addAttribute("blocoSelecionadoId", blocoId);
                model.addAttribute("vincularMoradorUnidadeForm", form);
                model.addAttribute(
                        "unidadesBloco",
                        unidadesBloco.content().stream()
                                .map(unidade -> {
                                    var values = support.toUnidadeMap(unidade);
                                    values.put("vinculadaAoMorador", unidadeIdsVinculadas.contains(unidade.getId()));
                                    return values;
                                })
                                .toList()
                );
            }
        }

        if (usuario instanceof Colaborador) {
            var tiposChamado = adminUseCases.listarTiposChamado(support.pageRequest(0, 100));
            var tiposResponsaveis = adminUseCases.listarTiposChamadoDoColaborador(usuarioId, support.pageRequest(0, 100));
            Set<UUID> tiposResponsaveisIds = tiposResponsaveis.content().stream()
                    .map(tipoChamado -> tipoChamado.getId())
                    .collect(Collectors.toSet());

            model.addAttribute("tiposChamadoDisponiveis", support.mapContent(tiposChamado.content(), support::toTipoChamadoMap));
            model.addAttribute("tiposChamadoColaborador", support.mapContent(tiposResponsaveis.content(), support::toTipoChamadoMap));
            model.addAttribute("tiposChamadoResponsaveisIds", tiposResponsaveisIds);
        }

        return "admin/usuarios/detalhe";
    }

    @PostMapping("/usuarios/{usuarioId}")
    public String atualizarUsuario(
            Authentication authentication,
            @PathVariable UUID usuarioId,
            @ModelAttribute UsuarioForm usuarioForm,
            RedirectAttributes redirectAttributes
    ) {
        Usuario existente = adminUseCases.buscarUsuarioPorId(usuarioId);
        adminUseCases.atualizarUsuario(
                support.authenticatedUser(authentication),
                usuarioId,
                atualizarUsuarioExistente(existente, usuarioForm)
        );
        redirectAttributes.addFlashAttribute("successMessage", "Usuario atualizado com sucesso.");
        return "redirect:/admin/usuarios/" + usuarioId;
    }

    @PostMapping("/usuarios/{usuarioId}/remover")
    public String removerUsuario(
            Authentication authentication,
            @PathVariable UUID usuarioId,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.removerUsuario(support.authenticatedUser(authentication), usuarioId);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario removido com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/moradores/{moradorId}/unidades/{unidadeId}/vincular")
    public String vincularMoradorUnidade(
            Authentication authentication,
            @PathVariable UUID moradorId,
            @PathVariable UUID unidadeId,
            @RequestParam(required = false) UUID blocoId,
            @RequestParam(required = false, defaultValue = "false") boolean dashboard,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.vincularMoradorUnidade(support.authenticatedUser(authentication), moradorId, unidadeId);
        redirectAttributes.addFlashAttribute("successMessage", "Unidade vinculada ao morador.");
        if (dashboard) {
            return redirectVinculosMorador(moradorId, blocoId);
        }
        return redirectUsuarioMorador(moradorId, blocoId);
    }

    @PostMapping("/moradores/{moradorId}/unidades/vincular")
    public String vincularMoradorUnidadePorFormulario(
            Authentication authentication,
            @PathVariable UUID moradorId,
            @ModelAttribute VincularMoradorUnidadeForm vincularMoradorUnidadeForm,
            @RequestParam(required = false, defaultValue = "false") boolean dashboard,
            RedirectAttributes redirectAttributes
    ) {
        if (vincularMoradorUnidadeForm.getUnidadeId() == null) {
            throw new BusinessRuleException("Selecione uma unidade para vincular ao morador.");
        }

        adminUseCases.vincularMoradorUnidade(
                support.authenticatedUser(authentication),
                moradorId,
                vincularMoradorUnidadeForm.getUnidadeId()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Unidade vinculada ao morador.");
        if (dashboard) {
            return redirectVinculosMorador(moradorId, vincularMoradorUnidadeForm.getBlocoId());
        }
        return redirectUsuarioMorador(moradorId, vincularMoradorUnidadeForm.getBlocoId());
    }

    @PostMapping("/moradores/{moradorId}/unidades/{unidadeId}/desvincular")
    public String desvincularMoradorUnidade(
            Authentication authentication,
            @PathVariable UUID moradorId,
            @PathVariable UUID unidadeId,
            @RequestParam(required = false) UUID blocoId,
            @RequestParam(required = false, defaultValue = "false") boolean dashboard,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.desvincularMoradorUnidade(support.authenticatedUser(authentication), moradorId, unidadeId);
        redirectAttributes.addFlashAttribute("successMessage", "Unidade desvinculada do morador.");
        if (dashboard) {
            return redirectVinculosMorador(moradorId, blocoId);
        }
        return redirectUsuarioMorador(moradorId, blocoId);
    }

    @PostMapping("/colaboradores/{colaboradorId}/tipos-chamado")
    public String vincularColaboradorTipoChamado(
            Authentication authentication,
            @PathVariable UUID colaboradorId,
            @ModelAttribute VincularColaboradorTipoChamadoForm vincularColaboradorTipoChamadoForm,
            @RequestParam(required = false, defaultValue = "false") boolean dashboard,
            RedirectAttributes redirectAttributes
    ) {
        if (vincularColaboradorTipoChamadoForm.getTipoChamadoId() == null) {
            throw new BusinessRuleException("Selecione um tipo de chamado para vincular ao colaborador.");
        }

        adminUseCases.vincularColaboradorTipoChamado(
                support.authenticatedUser(authentication),
                colaboradorId,
                vincularColaboradorTipoChamadoForm.getTipoChamadoId()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de chamado vinculado ao colaborador.");
        if (dashboard) {
            return redirectEscopoColaborador(colaboradorId);
        }
        return "redirect:/admin/usuarios/" + colaboradorId;
    }

    @PostMapping("/colaboradores/{colaboradorId}/tipos-chamado/{tipoChamadoId}/remover")
    public String desvincularColaboradorTipoChamado(
            Authentication authentication,
            @PathVariable UUID colaboradorId,
            @PathVariable UUID tipoChamadoId,
            @RequestParam(required = false, defaultValue = "false") boolean dashboard,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.desvincularColaboradorTipoChamado(
                support.authenticatedUser(authentication),
                colaboradorId,
                tipoChamadoId
        );
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de chamado desvinculado do colaborador.");
        if (dashboard) {
            return redirectEscopoColaborador(colaboradorId);
        }
        return "redirect:/admin/usuarios/" + colaboradorId;
    }

    @GetMapping({"/tipos-chamado", "/tipos-chamado/"})
    @Transactional(readOnly = true)
    public String listarTiposChamado(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) UUID tipoId,
            Model model
    ) {
        var tipos = adminUseCases.listarTiposChamado(support.pageRequest(page, size));
        model.addAttribute("pageTitle", "Tipos de Chamado");
        model.addAttribute("tiposChamado", support.mapContent(tipos.content(), support::toTipoChamadoMap));
        model.addAttribute("tiposChamadoPage", support.pageMetadata(tipos));

        if (tipoId != null) {
            var tipo = adminUseCases.buscarPorId(tipoId);
            var form = new TipoChamadoForm();
            form.setTitulo(tipo.getTitulo());
            form.setPrazoHoras(tipo.getPrazoHoras());
            model.addAttribute("tipoChamadoEdicao", support.toTipoChamadoMap(tipo));
            model.addAttribute("tipoChamadoForm", form);
        }

        return "admin/tipos-chamado/lista";
    }

    @PostMapping("/tipos-chamado")
    public String cadastrarTipoChamado(
            Authentication authentication,
            @ModelAttribute TipoChamadoForm tipoChamadoForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.cadastrarTipoChamado(
                support.authenticatedUser(authentication),
                tipoChamadoForm.getTitulo(),
                defaultInteger(tipoChamadoForm.getPrazoHoras())
        );
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de chamado cadastrado com sucesso.");
        return "redirect:/admin/tipos-chamado";
    }

    @PostMapping("/tipos-chamado/{tipoId}")
    public String atualizarTipoChamado(
            Authentication authentication,
            @PathVariable UUID tipoId,
            @ModelAttribute TipoChamadoForm tipoChamadoForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.atualizarTipoChamado(
                support.authenticatedUser(authentication),
                tipoId,
                tipoChamadoForm.getTitulo(),
                defaultInteger(tipoChamadoForm.getPrazoHoras())
        );
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de chamado atualizado com sucesso.");
        return "redirect:/admin/tipos-chamado?tipoId=" + tipoId;
    }

    @GetMapping({"/status-chamado", "/status-chamado/"})
    @Transactional(readOnly = true)
    public String listarStatusChamado(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) UUID statusId,
            Model model
    ) {
        var status = adminUseCases.listarStatus(support.pageRequest(page, size));
        model.addAttribute("pageTitle", "Status de Chamado");
        model.addAttribute(
                "statusChamado",
                status.content().stream()
                        .map(statusChamado -> {
                            Map<String, Object> values = new LinkedHashMap<>(support.toStatusChamadoMap(statusChamado));
                            values.put("editavel", !isStatusReservado(statusChamado.getNome()));
                            return values;
                        })
                        .toList()
        );
        model.addAttribute("statusChamadoPage", support.pageMetadata(status));

        if (statusId != null) {
            var statusSelecionado = adminUseCases.buscarStatusPorId(statusId);
            var form = new StatusChamadoForm();
            form.setNome(statusSelecionado.getNome());
            model.addAttribute("statusEdicao", support.toStatusChamadoMap(statusSelecionado));
            model.addAttribute("statusEdicaoBloqueada", isStatusReservado(statusSelecionado.getNome()));
            model.addAttribute("statusChamadoForm", form);
        }

        return "admin/status-chamado/lista";
    }

    @PostMapping("/status-chamado")
    public String cadastrarStatusChamado(
            Authentication authentication,
            @ModelAttribute StatusChamadoForm statusChamadoForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.cadastrarStatus(
                support.authenticatedUser(authentication),
                statusChamadoForm.getNome()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Status cadastrado com sucesso.");
        return "redirect:/admin/status-chamado";
    }

    @PostMapping("/status-chamado/{statusId}")
    public String atualizarStatusChamado(
            Authentication authentication,
            @PathVariable UUID statusId,
            @ModelAttribute StatusChamadoForm statusChamadoForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.atualizarStatus(
                support.authenticatedUser(authentication),
                statusId,
                statusChamadoForm.getNome()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Status atualizado com sucesso.");
        return "redirect:/admin/status-chamado?statusId=" + statusId;
    }

    @PostMapping("/status-chamado/{statusId}/inicial-padrao")
    public String definirStatusInicialPadrao(
            Authentication authentication,
            @PathVariable UUID statusId,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.definirStatusInicialPadrao(support.authenticatedUser(authentication), statusId);
        redirectAttributes.addFlashAttribute("successMessage", "Status inicial padrao atualizado.");
        return "redirect:/admin/status-chamado";
    }

    @GetMapping({"/chamados", "/chamados/"})
    @Transactional(readOnly = true)
    public String listarChamados(
            Authentication authentication,
            @RequestParam(required = false) UUID statusId,
            @RequestParam(required = false) String moradorNome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAbertura,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamados = adminUseCases.buscarChamados(
                currentUser,
                statusId,
                moradorNome,
                dataAbertura,
                support.pageRequest(page, size)
        );
        var status = adminUseCases.listarStatus(support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Chamados");
        model.addAttribute("chamados", support.mapContent(chamados.content(), support::toChamadoMap));
        model.addAttribute("chamadosPage", support.pageMetadata(chamados));
        model.addAttribute("statusDisponiveis", support.mapContent(status.content(), support::toStatusChamadoMap));
        model.addAttribute("filtroStatusId", statusId);
        model.addAttribute("filtroMoradorNome", moradorNome);
        model.addAttribute("filtroDataAbertura", dataAbertura);
        return "admin/chamados/lista";
    }

    @GetMapping("/chamados/{chamadoId}")
    @Transactional(readOnly = true)
    public String detalharChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamado = adminUseCases.buscarChamadoPorId(currentUser, chamadoId);
        var status = adminUseCases.listarStatus(support.pageRequest(0, 100));
        var comentarios = comentarioUseCase.listarComentariosDoChamado(currentUser, chamadoId, support.pageRequest(0, 100));
        var anexos = anexoChamadoUseCases.listarAnexosDoChamado(currentUser, chamadoId, support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Detalhes do Chamado");
        model.addAttribute("chamado", support.toChamadoMap(chamado));
        model.addAttribute("statusDisponiveis", support.mapContent(status.content(), support::toStatusChamadoMap));
        model.addAttribute("comentarios", support.mapContent(comentarios.content(), support::toComentarioMap));
        model.addAttribute(
                "anexos",
                anexos.content().stream()
                        .map(anexo -> support.toAnexoMap(
                                anexo.id(),
                                anexo.nomeArquivo(),
                                anexo.contentType(),
                                anexo.tamanhoBytes()
                        ))
                        .toList()
        );
        return "admin/chamados/detalhe";
    }

    @PostMapping("/chamados/{chamadoId}/status")
    public String atualizarStatusChamadoDoAdmin(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @ModelAttribute AtualizarStatusForm atualizarStatusForm,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.atualizarStatusChamado(
                support.authenticatedUser(authentication),
                chamadoId,
                atualizarStatusForm.getStatusId()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Status do chamado atualizado.");
        return "redirect:/admin/chamados/" + chamadoId;
    }

    @PostMapping("/chamados/{chamadoId}/finalizar")
    public String finalizarChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            RedirectAttributes redirectAttributes
    ) {
        adminUseCases.finalizarChamado(support.authenticatedUser(authentication), chamadoId);
        redirectAttributes.addFlashAttribute("successMessage", "Chamado finalizado.");
        return "redirect:/admin/chamados/" + chamadoId;
    }

    @PostMapping("/chamados/{chamadoId}/comentarios")
    public String comentarChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @ModelAttribute ComentarioForm comentarioForm,
            @RequestParam(name = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes redirectAttributes
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var comentario = adminUseCases.comentarChamado(
                currentUser,
                chamadoId,
                comentarioForm.getMensagem()
        );

        if (arquivo != null && !arquivo.isEmpty()) {
            try {
                anexoComentarioUseCases.adicionarAnexoAoComentario(
                        currentUser,
                        chamadoId,
                        comentario.getId(),
                        arquivo.getOriginalFilename(),
                        arquivo.getContentType(),
                        arquivo.getSize(),
                        arquivo.getBytes()
                );
            } catch (Exception exception) {
                throw new IllegalArgumentException("Nao foi possivel anexar o arquivo ao comentario.", exception);
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Comentario adicionado ao chamado.");
        return "redirect:/admin/chamados/" + chamadoId;
    }

    @GetMapping("/chamados/{chamadoId}/comentarios/{comentarioId}/anexos/{anexoId}")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> baixarAnexoDoComentario(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @PathVariable UUID comentarioId,
            @PathVariable UUID anexoId
    ) {
        var anexo = anexoComentarioUseCases.buscarAnexoPorId(
                support.authenticatedUser(authentication),
                chamadoId,
                comentarioId,
                anexoId
        );

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (anexo.contentType() != null && !anexo.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(anexo.contentType());
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(anexo.nomeArquivo()).build().toString()
                )
                .contentType(mediaType)
                .contentLength(anexo.tamanhoBytes())
                .body(anexo.conteudo());
    }

    @GetMapping("/chamados/{chamadoId}/anexos/{anexoId}")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> baixarAnexo(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @PathVariable UUID anexoId
    ) {
        var anexo = anexoChamadoUseCases.buscarAnexoPorId(
                support.authenticatedUser(authentication),
                chamadoId,
                anexoId
        );

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (anexo.contentType() != null && !anexo.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(anexo.contentType());
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(anexo.nomeArquivo()).build().toString()
                )
                .contentType(mediaType)
                .contentLength(anexo.tamanhoBytes())
                .body(anexo.conteudo());
    }

    private Usuario novoUsuario(UsuarioForm usuarioForm) {
        Usuario usuario = switch (usuarioForm.getTipo()) {
            case "ADMINISTRADOR" -> new Administrador();
            case "COLABORADOR" -> new Colaborador();
            case "MORADOR" -> new Morador();
            default -> throw new BusinessRuleException("Tipo de usuario invalido");
        };
        usuario.setNome(usuarioForm.getNome());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setSenha(usuarioForm.getSenha());
        return usuario;
    }

    private Usuario atualizarUsuarioExistente(Usuario existente, UsuarioForm usuarioForm) {
        Usuario usuario = switch (resolveTipoUsuario(existente)) {
            case "ADMINISTRADOR" -> new Administrador();
            case "COLABORADOR" -> new Colaborador();
            case "MORADOR" -> new Morador();
            default -> throw new BusinessRuleException("Tipo de usuario invalido");
        };
        usuario.setNome(usuarioForm.getNome());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setSenha(usuarioForm.getSenha());
        return usuario;
    }

    private String resolveTipoUsuario(Usuario usuario) {
        return switch (usuario.getRole()) {
            case "ROLE_ADMINISTRADOR" -> "ADMINISTRADOR";
            case "ROLE_COLABORADOR" -> "COLABORADOR";
            case "ROLE_MORADOR" -> "MORADOR";
            default -> throw new BusinessRuleException("Tipo de usuario nao suportado");
        };
    }

    private void carregarVinculosMorador(Model model, UUID moradorId, UUID blocoId) {
        Usuario usuario = adminUseCases.buscarUsuarioPorId(moradorId);
        if (!(usuario instanceof Morador)) {
            throw new BusinessRuleException("O usuario selecionado nao e um morador.");
        }

        var unidadesMorador = adminUseCases.listarUnidadesDoMorador(moradorId, support.pageRequest(0, 100));
        var unidadesMoradorContent = unidadesMorador.content();
        Set<UUID> unidadeIdsVinculadas = unidadesMoradorContent.stream()
                .map(unidade -> unidade.getId())
                .collect(Collectors.toSet());

        model.addAttribute("moradorSelecionado", support.toUsuarioMap(usuario));
        model.addAttribute("unidadesMorador", support.mapContent(unidadesMoradorContent, support::toUnidadeMap));

        if (blocoId != null) {
            var unidadesBloco = adminUseCases.listarUnidadesDoBloco(blocoId, support.pageRequest(0, 100));
            model.addAttribute(
                    "unidadesBloco",
                    unidadesBloco.content().stream()
                            .map(unidade -> {
                                var values = support.toUnidadeMap(unidade);
                                values.put("vinculadaAoMorador", unidadeIdsVinculadas.contains(unidade.getId()));
                                return values;
                            })
                            .toList()
            );
        }
    }

    private void carregarEscopoColaborador(Model model, UUID colaboradorId) {
        Usuario usuario = adminUseCases.buscarUsuarioPorId(colaboradorId);
        if (!(usuario instanceof Colaborador)) {
            throw new BusinessRuleException("O usuario selecionado nao e um colaborador.");
        }

        var tiposResponsaveis = adminUseCases.listarTiposChamadoDoColaborador(colaboradorId, support.pageRequest(0, 100));
        Set<UUID> tiposResponsaveisIds = tiposResponsaveis.content().stream()
                .map(tipoChamado -> tipoChamado.getId())
                .collect(Collectors.toSet());

        model.addAttribute("colaboradorSelecionado", support.toUsuarioMap(usuario));
        model.addAttribute("tiposChamadoColaborador", support.mapContent(tiposResponsaveis.content(), support::toTipoChamadoMap));
        model.addAttribute("tiposChamadoResponsaveisIds", tiposResponsaveisIds);
    }

    private String redirectUsuarioMorador(UUID moradorId, UUID blocoId) {
        if (blocoId == null) {
            return "redirect:/admin/usuarios/" + moradorId;
        }
        return "redirect:/admin/usuarios/" + moradorId + "?blocoId=" + blocoId;
    }

    private String redirectVinculosMorador(UUID moradorId, UUID blocoId) {
        if (blocoId == null) {
            return "redirect:/admin/vinculos-morador?moradorId=" + moradorId;
        }
        return "redirect:/admin/vinculos-morador?moradorId=" + moradorId + "&blocoId=" + blocoId;
    }

    private String redirectEscopoColaborador(UUID colaboradorId) {
        return "redirect:/admin/escopo-colaborador?colaboradorId=" + colaboradorId;
    }

    private int defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean isStatusReservado(String nome) {
        if (nome == null) {
            return false;
        }
        return STATUS_RESERVADOS.stream().anyMatch(reservado -> reservado.equalsIgnoreCase(nome));
    }
}
