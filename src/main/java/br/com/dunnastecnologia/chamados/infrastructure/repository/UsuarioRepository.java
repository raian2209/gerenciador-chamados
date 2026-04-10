package br.com.dunnastecnologia.chamados.infrastructure.repository;

import br.com.dunnastecnologia.chamados.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca um usuario pelo e-mail para autenticacao e garantia de unicidade.
     */
    @Query("""
            select u
            from Usuario u
            where lower(u.email) = lower(:email)
            """)
    Optional<Usuario> findByEmail(@Param("email") String email);
}
