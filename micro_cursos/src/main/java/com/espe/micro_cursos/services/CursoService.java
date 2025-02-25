package com.espe.micro_cursos.services;

import com.espe.micro_cursos.models.Usuario;
import com.espe.micro_cursos.models.entities.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> findAll();
    Optional<Curso> findById(Long id);
    Curso save(Curso curso);
    void delete(Long id);

    Optional<Usuario> findUsuarioById(Long id);
    Optional<Usuario> addUser(Usuario usuario, Long cursoId);
    void addUsuarioToCurso(Long cursoId, Usuario usuario);
    void removeUsuarioFromCurso(Long cursoId, Long usuarioId);
    Usuario addUsuarioToSystem(Usuario usuario);

}
