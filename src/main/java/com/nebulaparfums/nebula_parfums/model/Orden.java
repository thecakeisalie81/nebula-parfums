package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_orden;
    private String estado;
    private Double total;
    private LocalDate fecha_creacion;

    @OneToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "direccion", referencedColumnName = "id_direccion")
    private DireccionEnvio direccion;

    @OneToMany(mappedBy = "orden")
    private List<OrdenDetalle>  listaOrdenDetalle;

    public Orden() {
    }

    public Orden(Integer id_orden, String estado, Double total, LocalDate fecha_creacion, Usuario usuario, DireccionEnvio direccion, List<OrdenDetalle> listaOrdenDetalle) {
        this.id_orden = id_orden;
        this.estado = estado;
        this.total = total;
        this.fecha_creacion = fecha_creacion;
        this.usuario = usuario;
        this.direccion = direccion;
        this.listaOrdenDetalle = listaOrdenDetalle;
    }
}
