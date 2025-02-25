package com.espe.micro_cursos.models.entities;

import com.espe.micro_cursos.models.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre del curso no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre del curso solo puede contener letras, números y espacios")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "La descripción del curso no puede estar vacía")
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    @Column(nullable = false)
    private String descripcion;

    @NotNull(message = "Los créditos no pueden estar vacíos")
    @Min(value = 6, message = "Los créditos deben ser al menos 6")
    @Max(value = 9, message = "Los créditos no pueden ser mayores a 9")
    @Column(nullable = false)
    private Integer creditos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curso_id")
    private List<CursoUsuario> cursoUsuarios = new ArrayList<>();

    @Transient
    private List<Usuario> usuarios = new ArrayList<>();

    public Curso() {}

    // Métodos para manejar la relación con CursoUsuario
    public void addCursoUsuario(CursoUsuario cursoUsuario) {
        this.cursoUsuarios.add(cursoUsuario);
    }

    public void removeCursoUsuario(Long usuarioId) {
        this.cursoUsuarios.removeIf(cursoUsuario -> cursoUsuario.getUsuarioId().equals(usuarioId));
    }
    public void addUsuario(Usuario usuario) {
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(usuario.getId());
        this.cursoUsuarios.add(cursoUsuario);
    }

    public void removeUsuario(Long usuarioId) {
        this.cursoUsuarios.removeIf(cursoUsuario -> cursoUsuario.getUsuarioId().equals(usuarioId));
    }
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }


    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<CursoUsuario> getCursoUsuarios() {
        return cursoUsuarios;
    }

    public void setCursoUsuarios(List<CursoUsuario> cursoUsuarios) {
        this.cursoUsuarios = cursoUsuarios;
    }

}
