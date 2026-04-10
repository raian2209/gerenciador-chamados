package br.com.dunnastecnologia.chamados.infrastructure.repository;

import br.com.dunnastecnologia.chamados.domain.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, UUID> {

    /**
     * Busca um colaborador pelo e-mail para autenticacao e validacoes cadastrais.
     */
    @Query("""
            select c
            from Colaborador c
            where lower(c.email) = lower(:email)
            """)
    Optional<Colaborador> findByEmail(@Param("email") String email);
}
