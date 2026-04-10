package br.com.dunnastecnologia.chamados.infrastructure.repository;

import br.com.dunnastecnologia.chamados.domain.model.Morador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, UUID> {

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
