package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CarritoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_carrito_detalle;
    private Integer id_producto;
    private int cantidad;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "id_carrito_detalle")
    private Carrito carrito;


    public CarritoDetalle() {
    }
}
