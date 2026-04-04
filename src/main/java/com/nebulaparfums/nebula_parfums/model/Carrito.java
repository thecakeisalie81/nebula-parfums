package com.nebulaparfums.nebula_parfums.model;

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
    private List<CarritoDetalle> listaCarritoDetalles;

    public Carrito() {
    }


}
