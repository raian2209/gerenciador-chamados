package br.com.dunnastecnologia.chamados.infrastructure.repository;

import br.com.dunnastecnologia.chamados.domain.model.Morador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, UUID> {

    @Query("""
            select m
            from Morador m
            where m.unidades is empty
            """)
    Page<Morador> findByUnidadesIsEmpty(Pageable pageable);

    @Query("""
            select m
            from Morador m
            where lower(m.email) like lower(concat(:prefixoEmail, '%'))
            """)
    Page<Morador> findByEmailStartingWithIgnoreCase(
            @Param("prefixoEmail") String prefixoEmail,
            Pageable pageable
    );

    @Query("""
            select m
            from Morador m
            where m.unidades is empty
              and lower(m.email) like lower(concat(:prefixoEmail, '%'))
            """)
    Page<Morador> findByUnidadesIsEmptyAndEmailStartingWithIgnoreCase(
            @Param("prefixoEmail") String prefixoEmail,
            Pageable pageable
    );

    /**
     * Busca um morador pelo e-mail para autenticacao e validacoes cadastrais.
     */
    @Query("""
            select m
            from Morador m
            where lower(m.email) = lower(:email)
            """)
    Optional<Morador> findByEmail(@Param("email") String email);

    /**
     * Verifica no banco se o morador esta vinculado a unidade informada.
     */
    @Query(value = """
            select fn_morador_possui_unidade(:moradorId, :unidadeId)
            """,
            nativeQuery = true)
    boolean existsByIdAndUnidadeId(
            @Param("moradorId") UUID moradorId,
            @Param("unidadeId") UUID unidadeId
    );
}
