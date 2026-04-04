package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_producto;
    private String nombre;
    private String descripcion;
    private Double precio;
    private int stock_actual;
    private int stock_minimo;
    private LocalDate fecha_registro;

    @OneToOne
    @JoinColumn(name = "categoria", referencedColumnName = "id_categoria")
    private Integer id_categoria;

    @OneToOne
    @JoinColumn(name = "proveedor", referencedColumnName = "id_proveedor")
    private Integer id_proveedor;

    public Producto() {
    }
}
