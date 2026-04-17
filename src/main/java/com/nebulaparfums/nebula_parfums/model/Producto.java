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
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    private Double precio;
    private int stock_actual;
    private int stock_minimo;
    private LocalDate fecha_registro;
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "proveedor")
    private Proveedor proveedor;

    public Producto() {
    }

    public Producto(Integer id_producto, String nombre, String descripcion, Double precio, int stock_actual, int stock_minimo, LocalDate fecha_registro, Categoria categoria, Proveedor proveedor) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock_actual = stock_actual;
        this.stock_minimo = stock_minimo;
        this.fecha_registro = fecha_registro;
        this.categoria = categoria;
        this.proveedor = proveedor;
    }
}
