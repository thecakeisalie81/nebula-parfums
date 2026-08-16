package com.nebulaparfums.nebula_parfums.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_orden;
    private Double total;
    private LocalDateTime fecha_creacion;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;


    @ManyToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrdenDetalle>  listaOrdenDetalle;

    public Orden() {
    }

    public Orden(Integer id_orden, Double total, LocalDateTime fecha_creacion, String direccion, EstadoOrden estado, Usuario usuario, List<OrdenDetalle> listaOrdenDetalle) {
        this.id_orden = id_orden;
        this.total = total;
        this.fecha_creacion = fecha_creacion;
        this.direccion = direccion;
        this.estado = estado;
        this.usuario = usuario;
        this.listaOrdenDetalle = listaOrdenDetalle;
    }
}
