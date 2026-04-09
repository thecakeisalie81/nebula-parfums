package com.nebulaparfums.nebula_parfums.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class OrdenDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_orden_detalle;
    private int cantidad;
    private Double precio;

    @OneToOne
    @JoinColumn(name = "producto", referencedColumnName = "id_producto")
    private Producto producto;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_orden")
    private Orden orden;


    public OrdenDetalle() {
    }

    public OrdenDetalle(Integer id_orden_detalle, int cantidad, Double precio, Producto producto, Orden orden) {
        this.id_orden_detalle = id_orden_detalle;
        this.cantidad = cantidad;
        this.precio = precio;
        this.producto = producto;
        this.orden = orden;
    }
}
