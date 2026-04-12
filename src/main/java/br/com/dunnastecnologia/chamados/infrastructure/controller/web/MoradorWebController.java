package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.ComentarioUseCase;
import br.com.dunnastecnologia.chamados.application.UserCase.MoradorUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.TipoChamadoUseCase;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.AbrirChamadoForm;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.ComentarioForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/morador")
@PreAuthorize("hasRole('MORADOR')")
public class MoradorWebController {

    private final MoradorUseCases moradorUseCases;
    private final TipoChamadoUseCase tipoChamadoUseCase;
    private final AnexoChamadoUseCases anexoChamadoUseCases;
    private final ComentarioUseCase comentarioUseCase;
    private final WebControllerSupport support;

    public MoradorWebController(
            MoradorUseCases moradorUseCases,
            TipoChamadoUseCase tipoChamadoUseCase,
            AnexoChamadoUseCases anexoChamadoUseCases,
            ComentarioUseCase comentarioUseCase,
            WebControllerSupport support
    ) {
        this.moradorUseCases = moradorUseCases;
        this.tipoChamadoUseCase = tipoChamadoUseCase;
        this.anexoChamadoUseCases = anexoChamadoUseCases;
        this.comentarioUseCase = comentarioUseCase;
        this.support = support;
    }

    @ModelAttribute("abrirChamadoForm")
    public AbrirChamadoForm abrirChamadoForm() {
        return new AbrirChamadoForm();
    }

    @ModelAttribute("comentarioForm")
    public ComentarioForm comentarioForm() {
        return new ComentarioForm();
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String dashboard(
            Authentication authentication,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var unidades = moradorUseCases.listarMinhasUnidades(currentUser, support.pageRequest(0, 20));
        var chamados = moradorUseCases.listarMeusChamados(currentUser, support.pageRequest(0, 5));

        model.addAttribute("pageTitle", "Meu Painel");
        model.addAttribute("minhasUnidades", support.mapContent(unidades.content(), support::toUnidadeMap));
        model.addAttribute("meusChamados", support.mapContent(chamados.content(), support::toChamadoMap));
        model.addAttribute("totalUnidades", unidades.totalElements());
        model.addAttribute("totalChamados", chamados.totalElements());
        return "morador/dashboard";
    }

    @GetMapping("/chamados")
    @Transactional(readOnly = true)
    public String listarChamados(
            Authentication authentication,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamados = moradorUseCases.listarMeusChamados(currentUser, support.pageRequest(page, size));

        model.addAttribute("pageTitle", "Meus Chamados");
        model.addAttribute("chamados", support.mapContent(chamados.content(), support::toChamadoMap));
        model.addAttribute("chamadosPage", support.pageMetadata(chamados));
        return "morador/chamados/lista";
    }

    @GetMapping("/chamados/novo")
    @Transactional(readOnly = true)
    public String novoChamado(
            Authentication authentication,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var unidades = moradorUseCases.listarMinhasUnidades(currentUser, support.pageRequest(0, 100));
        var tipos = tipoChamadoUseCase.listarTiposChamado(support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Abrir Chamado");
        model.addAttribute("unidades", support.mapContent(unidades.content(), support::toUnidadeMap));
        model.addAttribute("tiposChamado", support.mapContent(tipos.content(), support::toTipoChamadoMap));
        return "morador/chamados/novo";
    }

    @PostMapping("/chamados")
    public String abrirChamado(
            Authentication authentication,
            @ModelAttribute AbrirChamadoForm abrirChamadoForm,
            RedirectAttributes redirectAttributes
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamado = moradorUseCases.abrirChamado(
                currentUser,
                abrirChamadoForm.getUnidadeId(),
                abrirChamadoForm.getTipoChamadoId(),
                abrirChamadoForm.getDescricao()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Chamado aberto com sucesso.");
        return "redirect:/morador/chamados/" + chamado.getId();
    }

    @GetMapping("/chamados/{chamadoId}")
    @Transactional(readOnly = true)
    public String detalharChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamado = moradorUseCases.buscarMeuChamadoPorId(currentUser, chamadoId);
        var comentarios = comentarioUseCase.listarComentariosDoChamado(currentUser, chamadoId, support.pageRequest(0, 100));
        var anexos = anexoChamadoUseCases.listarAnexosDoChamado(currentUser, chamadoId, support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Detalhes do Chamado");
        model.addAttribute("chamado", support.toChamadoMap(chamado));
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
        return "morador/chamados/detalhe";
    }

    @PostMapping("/chamados/{chamadoId}/comentarios")
    public String comentarChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @ModelAttribute ComentarioForm comentarioForm,
            RedirectAttributes redirectAttributes
    ) {
        moradorUseCases.comentarChamado(
                support.authenticatedUser(authentication),
                chamadoId,
                comentarioForm.getMensagem()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Comentario registrado.");
        return "redirect:/morador/chamados/" + chamadoId;
    }

    @PostMapping("/chamados/{chamadoId}/anexos")
    public String adicionarAnexo(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @RequestParam("arquivo") MultipartFile arquivo,
            RedirectAttributes redirectAttributes
    ) throws Exception {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione um arquivo para anexar.");
        }

        anexoChamadoUseCases.adicionarAnexo(
                support.authenticatedUser(authentication),
                chamadoId,
                arquivo.getOriginalFilename(),
                arquivo.getContentType(),
                arquivo.getSize(),
                arquivo.getBytes()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Anexo enviado com sucesso.");
        return "redirect:/morador/chamados/" + chamadoId;
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
}
