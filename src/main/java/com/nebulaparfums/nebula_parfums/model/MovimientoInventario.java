package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_movimiento;
    private String tipo_movimiento;
    private Integer cantidad;
    private LocalDateTime fecha_movimiento;

    @ManyToOne
    @JoinColumn(name = "producto", referencedColumnName = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
    public MovimientoInventario() {
    }

    public MovimientoInventario(Integer id_movimiento, String tipo_movimiento, Integer cantidad, LocalDateTime fecha_movimiento, Producto producto, Usuario usuario) {
        this.id_movimiento = id_movimiento;
        this.tipo_movimiento = tipo_movimiento;
        this.cantidad = cantidad;
        this.fecha_movimiento = fecha_movimiento;
        this.producto = producto;
        this.usuario = usuario;
    }
}
