package com.espe.micro_cursos.services;

import com.espe.micro_cursos.clients.UsuarioClientRest;
import com.espe.micro_cursos.models.Usuario;
import com.espe.micro_cursos.models.entities.Curso;
import com.espe.micro_cursos.models.entities.CursoUsuario;
import com.espe.micro_cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImpl implements CursoService {
    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClientRest clientRest;

    @Override
    public List<Curso> findAll() {
        List<Curso> cursos = (List<Curso>) repository.findAll();

        // Llenar el campo usuarios para cada curso
        cursos.forEach(curso -> {
            List<Usuario> usuarios = curso.getCursoUsuarios().stream()
                    .map(cursoUsuario -> {
                        try {
                            return clientRest.findById(cursoUsuario.getUsuarioId());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(usuario -> usuario != null)
                    .toList();

            curso.setUsuarios(usuarios);
        });

        return cursos;
    }


    @Override
    public Optional<Curso> findById(Long id) {
        Optional<Curso> optionalCurso = repository.findById(id);
        if (optionalCurso.isPresent()) {
            Curso curso = optionalCurso.get();

            // Llenar el campo usuarios con datos del microservicio de usuarios
            List<Usuario> usuarios = curso.getCursoUsuarios().stream()
                    .map(cursoUsuario -> {
                        try {
                            return clientRest.findById(cursoUsuario.getUsuarioId());
                        } catch (Exception e) {
                            return null; // Manejar errores si el usuario no existe
                        }
                    })
                    .filter(usuario -> usuario != null) // Filtrar nulos si hubo errores
                    .toList();

            curso.setUsuarios(usuarios); // Asignar la lista de usuarios
            return Optional.of(curso);
        }
        return Optional.empty();
    }


    @Override
    public Curso save(Curso curso) {
        return repository.save(curso);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findUsuarioById(Long id) {
        try {
            Usuario usuario = clientRest.findById(id);
            return Optional.ofNullable(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> addUser(Usuario usuario, Long cursoId) {
        Optional<Curso> optional = repository.findById(cursoId);
        if (optional.isPresent()) {
            try {
                Usuario usuarioTemp = clientRest.findById(usuario.getId());
                if (usuarioTemp == null) {
                    return Optional.empty();
                }

                Curso curso = optional.get();
                CursoUsuario cursoUsuario = new CursoUsuario();
                cursoUsuario.setUsuarioId(usuarioTemp.getId());

                curso.addCursoUsuario(cursoUsuario);
                repository.save(curso);
                return Optional.of(usuarioTemp);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public void addUsuarioToCurso(Long cursoId, Usuario usuario) {
        Optional<Curso> optional = repository.findById(cursoId);
        if (optional.isPresent()) {
            Curso curso = optional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuario.getId());
            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
        }
    }

    @Override
    public void removeUsuarioFromCurso(Long cursoId, Long usuarioId) {
        Optional<Curso> optional = repository.findById(cursoId);
        if (optional.isPresent()) {
            Curso curso = optional.get();
            curso.removeCursoUsuario(usuarioId);
            repository.save(curso);
        }
    }

    @Override
    public Usuario addUsuarioToSystem(Usuario usuario) {
        return clientRest.createUsuario(usuario);
    }

}
