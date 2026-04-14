package br.com.dunnastecnologia.chamados.infrastructure.service;

import br.com.dunnastecnologia.chamados.application.Security.AuthenticatedUser;
import br.com.dunnastecnologia.chamados.application.UserCase.AnexoChamadoUseCases;
import br.com.dunnastecnologia.chamados.application.pagination.PageResult;
import br.com.dunnastecnologia.chamados.domain.model.AnexoChamado;
import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import br.com.dunnastecnologia.chamados.infrastructure.exception.BusinessRuleException;
import br.com.dunnastecnologia.chamados.infrastructure.exception.ResourceNotFoundException;
import br.com.dunnastecnologia.chamados.infrastructure.repository.AnexoChamadoRepository;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.ChamadoAccessSupport;
import br.com.dunnastecnologia.chamados.infrastructure.service.support.PageResultMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AnexoChamadoService implements AnexoChamadoUseCases {

    private final AnexoChamadoRepository anexoChamadoRepository;
    private final ChamadoAccessSupport chamadoAccessSupport;

    public AnexoChamadoService(
            AnexoChamadoRepository anexoChamadoRepository,
            ChamadoAccessSupport chamadoAccessSupport
    ) {
        this.anexoChamadoRepository = anexoChamadoRepository;
        this.chamadoAccessSupport = chamadoAccessSupport;
    }

    @Override
    @Transactional
    public AnexoChamadoInfo adicionarAnexo(
            AuthenticatedUser usuario,
            UUID chamadoId,
            String nomeArquivo,
            String contentType,
            long tamanhoBytes,
            byte[] conteudo
    ) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new BusinessRuleException("Nome do arquivo e obrigatorio");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new BusinessRuleException("Content type do anexo e obrigatorio");
        }
        if (tamanhoBytes <= 0 || conteudo == null || conteudo.length == 0) {
            throw new BusinessRuleException("Conteudo do anexo e obrigatorio");
        }

        Chamado chamado = chamadoAccessSupport.findAccessibleChamado(usuario, chamadoId);

        AnexoChamado anexoChamado = new AnexoChamado();
        anexoChamado.setChamado(chamado);
        anexoChamado.setNomeArquivo(nomeArquivo);
        anexoChamado.setContentType(contentType);
        anexoChamado.setTamanhoBytes(tamanhoBytes);
        anexoChamado.setConteudo(conteudo);

        AnexoChamado savedAnexo = anexoChamadoRepository.save(anexoChamado);
        return new AnexoChamadoInfo(
                savedAnexo.getId(),
                chamadoId,
                savedAnexo.getNomeArquivo(),
                savedAnexo.getContentType(),
                savedAnexo.getTamanhoBytes()
        );
    }

    @Override
    @Transactional
    public PageResult<AnexoChamadoInfo> listarAnexosDoChamado(
            AuthenticatedUser usuario,
            UUID chamadoId,
            PageRequest pageRequest
    ) {
        chamadoAccessSupport.findAccessibleChamado(usuario, chamadoId);
        return PageResultMapper.fromPage(
                anexoChamadoRepository.findByChamadoId(chamadoId, pageRequest)
                        .map(anexo -> new AnexoChamadoInfo(
                                anexo.getId(),
                                chamadoId,
                                anexo.getNomeArquivo(),
                                anexo.getContentType(),
                                anexo.getTamanhoBytes()
                        ))
        );
    }

    @Override
    @Transactional
    public AnexoChamadoConteudo buscarAnexoPorId(
            AuthenticatedUser usuario,
            UUID chamadoId,
            UUID anexoId
    ) {
        chamadoAccessSupport.findAccessibleChamado(usuario, chamadoId);
        AnexoChamado anexoChamado = anexoChamadoRepository.findByIdAndChamadoId(anexoId, chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo nao encontrado para o chamado"));

        return new AnexoChamadoConteudo(
                anexoChamado.getId(),
                chamadoId,
                anexoChamado.getNomeArquivo(),
                anexoChamado.getContentType(),
                anexoChamado.getTamanhoBytes(),
                anexoChamado.getConteudo()
        );
    }
}
