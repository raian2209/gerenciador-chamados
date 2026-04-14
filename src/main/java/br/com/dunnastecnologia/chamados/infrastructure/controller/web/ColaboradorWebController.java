package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import br.com.dunnastecnologia.chamados.application.UserCase.ColaboradorUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoComentarioUseCases;
import br.com.dunnastecnologia.chamados.application.UserCase.ComentarioUseCase;
import br.com.dunnastecnologia.chamados.infrastructure.controller.web.form.AtualizarStatusForm;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/colaborador")
@PreAuthorize("hasRole('COLABORADOR')")
public class ColaboradorWebController {

    private final ColaboradorUseCases colaboradorUseCases;
    private final AnexoChamadoUseCases anexoChamadoUseCases;
    private final AnexoComentarioUseCases anexoComentarioUseCases;
    private final ComentarioUseCase comentarioUseCase;
    private final WebControllerSupport support;

    public ColaboradorWebController(
            ColaboradorUseCases colaboradorUseCases,
            AnexoChamadoUseCases anexoChamadoUseCases,
            AnexoComentarioUseCases anexoComentarioUseCases,
            ComentarioUseCase comentarioUseCase,
            WebControllerSupport support
    ) {
        this.colaboradorUseCases = colaboradorUseCases;
        this.anexoChamadoUseCases = anexoChamadoUseCases;
        this.anexoComentarioUseCases = anexoComentarioUseCases;
        this.comentarioUseCase = comentarioUseCase;
        this.support = support;
    }

    @ModelAttribute("atualizarStatusForm")
    public AtualizarStatusForm atualizarStatusForm() {
        return new AtualizarStatusForm();
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
        var chamados = colaboradorUseCases.buscarChamados(currentUser, null, null, null, support.pageRequest(0, 5));

        model.addAttribute("pageTitle", "Painel do Colaborador");
        model.addAttribute("chamados", support.mapContent(chamados.content(), support::toChamadoMap));
        model.addAttribute("totalChamadosAbertos", chamados.totalElements());
        return "colaborador/dashboard";
    }

    @GetMapping("/chamados")
    @Transactional(readOnly = true)
    public String listarChamados(
            Authentication authentication,
            @RequestParam(required = false) UUID statusId,
            @RequestParam(required = false) UUID tipoChamadoId,
            @RequestParam(required = false) String unidade,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamados = colaboradorUseCases.buscarChamados(
                currentUser,
                statusId,
                tipoChamadoId,
                unidade,
                support.pageRequest(page, size)
        );
        var status = colaboradorUseCases.listarStatusDisponiveis(currentUser, support.pageRequest(0, 100));
        var tipos = colaboradorUseCases.listarTiposChamadoDisponiveis(currentUser, support.pageRequest(0, 100));

        model.addAttribute("pageTitle", "Chamados em Atendimento");
        model.addAttribute("chamados", support.mapContent(chamados.content(), support::toChamadoMap));
        model.addAttribute("chamadosPage", support.pageMetadata(chamados));
        model.addAttribute("statusDisponiveis", support.mapContent(status.content(), support::toStatusChamadoMap));
        model.addAttribute("tiposChamadoDisponiveis", support.mapContent(tipos.content(), support::toTipoChamadoMap));
        model.addAttribute("filtroStatusId", statusId);
        model.addAttribute("filtroTipoChamadoId", tipoChamadoId);
        model.addAttribute("filtroUnidade", unidade);
        return "colaborador/chamados/lista";
    }

    @GetMapping("/chamados/{chamadoId}")
    @Transactional(readOnly = true)
    public String detalharChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            Model model
    ) {
        var currentUser = support.authenticatedUser(authentication);
        var chamado = colaboradorUseCases.buscarChamadoPorId(currentUser, chamadoId);
        var status = colaboradorUseCases.listarStatusDisponiveis(currentUser, support.pageRequest(0, 100));
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
        return "colaborador/chamados/detalhe";
    }

    @PostMapping("/chamados/{chamadoId}/status")
    public String atualizarStatus(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            @ModelAttribute AtualizarStatusForm atualizarStatusForm,
            RedirectAttributes redirectAttributes
    ) {
        var chamadoAtualizado = colaboradorUseCases.atualizarStatusChamado(
                support.authenticatedUser(authentication),
                chamadoId,
                atualizarStatusForm.getStatusId()
        );
        redirectAttributes.addFlashAttribute("successMessage", "Status atualizado.");
        if (chamadoAtualizado.getDataFinalizacao() != null) {
            return "redirect:/colaborador/chamados";
        }
        return "redirect:/colaborador/chamados/" + chamadoId;
    }

    @PostMapping("/chamados/{chamadoId}/finalizar")
    public String finalizarChamado(
            Authentication authentication,
            @PathVariable UUID chamadoId,
            RedirectAttributes redirectAttributes
    ) {
        colaboradorUseCases.finalizarChamado(support.authenticatedUser(authentication), chamadoId);
        redirectAttributes.addFlashAttribute("successMessage", "Chamado finalizado.");
        return "redirect:/colaborador/chamados";
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
        var comentario = colaboradorUseCases.comentarChamado(
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

        redirectAttributes.addFlashAttribute("successMessage", "Comentario registrado.");
        return "redirect:/colaborador/chamados/" + chamadoId;
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
}
