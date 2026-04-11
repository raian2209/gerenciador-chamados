package br.com.dunnastecnologia.chamados.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "colaboradores")
public class Colaborador extends Usuario {
    @Override
    public String getRole() {
        return "ROLE_COLABORADOR";
    }
}