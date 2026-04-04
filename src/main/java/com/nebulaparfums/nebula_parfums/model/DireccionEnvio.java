package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DireccionEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_direccion;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String codigo_postal;
    private String telefono;

    public DireccionEnvio() {
    }
}
