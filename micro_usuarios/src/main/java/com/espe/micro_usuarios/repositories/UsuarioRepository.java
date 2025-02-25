package com.espe.micro_usuarios.repositories;

import com.espe.micro_usuarios.models.entities.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

}

