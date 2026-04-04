package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class LogActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_log;
    private String accion;
    private LocalDate fecha_actualizacion;
    private String detalle;

    @OneToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario")
    private Integer id_usuario;

    public LogActividad() {
    }
}
