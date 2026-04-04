package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Integer id_categoria;
    private Integer id_proveedor;
    private Boolean activo;
    private LocalDate fecha_registro;

    public Producto() {
    }
}
