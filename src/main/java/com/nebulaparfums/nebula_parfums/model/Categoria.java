package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id_categoria;
    String nombre;
    String descripcion;

    @OneToMany(mappedBy = "categoria")
    List<Producto> productos;

    public Categoria() {
    }


}
