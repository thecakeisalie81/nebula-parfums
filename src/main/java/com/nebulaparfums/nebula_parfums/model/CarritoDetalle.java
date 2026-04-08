package com.nebulaparfums.nebula_parfums.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private int cantidad;
    private Double precio;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    public CarritoDetalle() {
    }

    public CarritoDetalle(Integer id_carrito_detalle, int cantidad, Double precio, Carrito carrito, Producto producto) {
        this.id_carrito_detalle = id_carrito_detalle;
        this.cantidad = cantidad;
        this.precio = precio;
        this.carrito = carrito;
        this.producto = producto;
    }
}
