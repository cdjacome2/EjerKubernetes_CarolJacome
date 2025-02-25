package com.espe.micro_usuarios.services;

import com.espe.micro_usuarios.models.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
}