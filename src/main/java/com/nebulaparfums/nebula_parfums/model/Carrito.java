package com.nebulaparfums.nebula_parfums.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_carrito;
    private LocalDate fecha_actualizacion;

    @OneToMany(mappedBy = "carrito")
    @JsonManagedReference
    private List<CarritoDetalle> listaCarritoDetalles;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    public Carrito() {
    }

    public Carrito(Integer id_carrito, List<CarritoDetalle> listaCarritoDetalles, Usuario usuario) {
        this.id_carrito = id_carrito;
        this.fecha_actualizacion = LocalDate.now();
        this.listaCarritoDetalles = listaCarritoDetalles;
        this.usuario = usuario;
    }
}
