package com.espe.micro_cursos.controller;

import com.espe.micro_cursos.models.Usuario;
import com.espe.micro_cursos.models.entities.Curso;
import com.espe.micro_cursos.services.CursoService;
import feign.FeignException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos API", description = "Operaciones relacionadas con la gestión de cursos y asignación de usuarios")
public class CursoController {

    @Autowired
    private CursoService service;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo curso",
            description = "Recibe los datos de un curso y lo guarda en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Curso creado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class))),
                    @ApiResponse(responseCode = "400", description = "Errores de validación")
            }
    )
    public ResponseEntity<?> create(@Valid @RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Curso cursoDb = service.save(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDb);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los cursos",
            description = "Devuelve una lista de todos los cursos registrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
            }
    )
    public ResponseEntity<List<Curso>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener curso por ID",
            description = "Devuelve los datos de un curso basado en su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class))),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Curso> cursoOptional = service.findById(id);
        if (cursoOptional.isPresent()) {
            return ResponseEntity.ok(cursoOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Curso no encontrado"));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un curso existente",
            description = "Recibe los datos de un curso y actualiza la información en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class))),
                    @ApiResponse(responseCode = "400", description = "Errores de validación"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<?> update(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Optional<Curso> cursoOptional = service.findById(id);
        if (cursoOptional.isPresent()) {
            Curso cursoDb = cursoOptional.get();
            cursoDb.setNombre(curso.getNombre());
            cursoDb.setDescripcion(curso.getDescripcion());
            cursoDb.setCreditos(curso.getCreditos());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(cursoDb));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Curso no encontrado"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un curso",
            description = "Elimina un curso basado en su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Curso eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Curso> curso = service.findById(id);
        if (curso.isPresent()) {
            service.delete(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Curso eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Curso no encontrado"));
    }

    @PostMapping("/{id}/usuarios")
    @Operation(
            summary = "Asignar un usuario a un curso",
            description = "Asigna un usuario existente al curso especificado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario asignado correctamente al curso"),
                    @ApiResponse(responseCode = "404", description = "Usuario o curso no encontrado"),
                    @ApiResponse(responseCode = "409", description = "El usuario ya está matriculado en este curso"),
                    @ApiResponse(responseCode = "500", description = "Error en la comunicación con el servicio de usuarios")
            }
    )
    public ResponseEntity<?> assignUser(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            Optional<Usuario> usuarioFeign = service.findUsuarioById(usuario.getId());
            if (usuarioFeign.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Usuario no encontrado"));
            }

            Optional<Curso> cursoOptional = service.findById(id);
            if (cursoOptional.isPresent()) {
                Curso curso = cursoOptional.get();

                // Validar si el usuario ya está matriculado
                boolean isAlreadyEnrolled = curso.getCursoUsuarios().stream()
                        .anyMatch(cursoUsuario -> cursoUsuario.getUsuarioId().equals(usuario.getId()));
                if (isAlreadyEnrolled) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("message", "El usuario ya está matriculado en este curso"));
                }

                // Matricular usuario si no está ya matriculado
                curso.addUsuario(usuario);
                service.save(curso);
                return ResponseEntity.status(HttpStatus.CREATED).body(curso);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Curso no encontrado"));
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error en la comunicación con el servicio de usuarios"));
        }
    }

    @DeleteMapping("/{id}/usuarios/{usuarioId}")
    @Operation(
            summary = "Desasignar un usuario de un curso",
            description = "Elimina la matrícula de un usuario de un curso.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario desasignado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Curso o usuario no encontrado")
            }
    )
    public ResponseEntity<?> unassignUser(@PathVariable Long id, @PathVariable Long usuarioId) {
        Optional<Curso> cursoOptional = service.findById(id);
        if (cursoOptional.isPresent()) {
            Curso curso = cursoOptional.get();
            curso.removeUsuario(usuarioId);
            service.save(curso);
            return ResponseEntity.ok(Collections.singletonMap("message", "Matrícula eliminada correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Matrícula no encontrada"));
    }

    @GetMapping("/{id}/usuarios")
    @Operation(
            summary = "Listar usuarios por curso",
            description = "Devuelve una lista de usuarios matriculados en un curso específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
            }
    )
    public ResponseEntity<?> listUsersByCourse(@PathVariable Long id) {
        Optional<Curso> cursoOptional = service.findById(id);
        if (cursoOptional.isPresent()) {
            return ResponseEntity.ok(cursoOptional.get().getUsuarios());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Curso no encontrado"));
    }

    @PostMapping("/usuarios")
    @Operation(
            summary = "Crear un usuario en el sistema",
            description = "Crea un usuario en el sistema a través del servicio de usuarios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
                    @ApiResponse(responseCode = "500", description = "Error al crear el usuario")
            }
    )
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioCreado = service.addUsuarioToSystem(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error al crear el usuario"));
        }
    }
}
