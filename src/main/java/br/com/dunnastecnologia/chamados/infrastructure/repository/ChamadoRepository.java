package br.com.dunnastecnologia.chamados.infrastructure.repository;

import br.com.dunnastecnologia.chamados.domain.model.Chamado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {

    /**
     * Lista os chamados visiveis para o administrador, inclusive os ja finalizados.
     */
    @Query(value = """
            select *
            from fn_listar_chamados_para_admin(:adminId, :statusId, :moradorNome)
            """,
            countQuery = """
            select count(*)
            from fn_listar_chamados_para_admin(:adminId, :statusId, :moradorNome)
            """,
            nativeQuery = true)
    Page<Chamado> buscarParaAdmin(
            @Param("adminId") UUID adminId,
            @Param("statusId") UUID statusId,
            @Param("moradorNome") String moradorNome,
            Pageable pageable
    );

    /**
     * Lista apenas os chamados do proprio morador vinculados a unidades que ele possui acesso.
     */
    @Query(value = """
            select *
            from fn_listar_chamados_do_morador(:moradorId)
            """,
            countQuery = """
            select count(*)
            from fn_listar_chamados_do_morador(:moradorId)
            """,
            nativeQuery = true)
    Page<Chamado> findByMoradorId(@Param("moradorId") UUID moradorId, Pageable pageable);

    /**
     * Lista os chamados acessiveis ao colaborador, ocultando registros ja finalizados.
     */
    @Query(value = """
            select *
            from fn_listar_chamados_do_colaborador(:colaboradorId, :statusId, :tipoChamadoId, :unidadeIdentificacao)
            """,
            countQuery = """
            select count(*)
            from fn_listar_chamados_do_colaborador(:colaboradorId, :statusId, :tipoChamadoId, :unidadeIdentificacao)
            """,
            nativeQuery = true)
    Page<Chamado> buscarParaColaborador(
            @Param("colaboradorId") UUID colaboradorId,
            @Param("statusId") UUID statusId,
            @Param("tipoChamadoId") UUID tipoChamadoId,
            @Param("unidadeIdentificacao") String unidadeIdentificacao,
            Pageable pageable
    );

    /**
     * Busca um chamado especifico do morador validando no banco se ele pode acessar esse registro.
     */
    @Query(value = """
            select *
            from fn_buscar_chamado_do_morador(:moradorId, :chamadoId)
            """,
            nativeQuery = true)
    Optional<Chamado> findByIdAndMoradorId(
            @Param("chamadoId") UUID chamadoId,
            @Param("moradorId") UUID moradorId
    );

    /**
     * Busca um chamado especifico para o colaborador somente se ele ainda nao estiver finalizado.
     */
    @Query(value = """
            select *
            from fn_buscar_chamado_para_colaborador(:colaboradorId, :chamadoId)
            """,
            nativeQuery = true)
    Optional<Chamado> findByIdAndColaboradorId(
            @Param("colaboradorId") UUID colaboradorId,
            @Param("chamadoId") UUID chamadoId
    );

    /**
     * Busca um chamado especifico para o administrador usando a funcao de autorizacao do banco.
     */
    @Query(value = """
            select *
            from fn_buscar_chamado_para_admin(:adminId, :chamadoId)
            """,
            nativeQuery = true)
    Optional<Chamado> findByIdAndAdminId(
            @Param("adminId") UUID adminId,
            @Param("chamadoId") UUID chamadoId
    );
}
