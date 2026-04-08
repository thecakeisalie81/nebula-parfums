package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class LogActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_log;
    private String accion;
    private LocalDateTime fecha_actualizacion;
    private String detalle;

    @ManyToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    public LogActividad() {
    }

    public LogActividad(Integer id_log, String accion, LocalDateTime fecha_actualizacion, String detalle, Usuario usuario) {
        this.id_log = id_log;
        this.accion = accion;
        this.fecha_actualizacion = fecha_actualizacion;
        this.detalle = detalle;
        this.usuario = usuario;
    }
}
