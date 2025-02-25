package com.espe.micro_usuarios.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Calendar;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras y espacios")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras y espacios")
    @Column(nullable = false)
    private String apellido;

    @NotEmpty(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe tener un formato válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotEmpty(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener exactamente 10 dígitos")
    @Column(nullable = false)
    private String telefono;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate fechaNacimiento;

    @Column(name = "creado_en", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = new Date();
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null) {
            LocalDate fechaInicio = LocalDate.of(1930, 1, 1);
            LocalDate fechaActual = LocalDate.now();

            if (fechaNacimiento.isBefore(fechaInicio) || fechaNacimiento.isAfter(fechaActual)) {
                throw new IllegalArgumentException("La fecha de nacimiento debe estar entre 1930 y el año actual");
            }
        }
        this.fechaNacimiento = fechaNacimiento;
    }


    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }
}
