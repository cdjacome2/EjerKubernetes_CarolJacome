package com.espe.micro_usuarios.controller;

import com.espe.micro_usuarios.models.entities.Usuario;
import com.espe.micro_usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios API", description = "Operaciones relacionadas con la gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista de todos los usuarios registrados en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario específico basado en su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Usuario.class))),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.of(service.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Recibe los datos de un usuario, valida la información y lo guarda en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Usuario.class))),
                    @ApiResponse(responseCode = "400", description = "Errores de validación")
            }
    )
    public ResponseEntity<?> create(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Usuario usuarioCreado = service.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un usuario existente",
            description = "Recibe el ID de un usuario y los datos actualizados, valida la información y realiza los cambios en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Usuario.class))),
                    @ApiResponse(responseCode = "400", description = "Errores de validación"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<Usuario> usuarioOptional = service.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioDb = usuarioOptional.get();
            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setApellido(usuario.getApellido());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setTelefono(usuario.getTelefono());
            usuarioDb.setFechaNacimiento(usuario.getFechaNacimiento());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(usuarioDb));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Usuario no encontrado"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un usuario",
            description = "Recibe el ID de un usuario y lo elimina de la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = service.findById(id);
        if (usuarioOptional.isPresent()) {
            service.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usuario eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Usuario no encontrado"));
    }
}
