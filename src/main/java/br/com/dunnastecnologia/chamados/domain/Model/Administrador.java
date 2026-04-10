package br.com.dunnastecnologia.chamados.domain.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario{
}
