package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrdenDetalleRepository extends JpaRepository<OrdenDetalle, Integer> {

    @Query("""
    SELECT new com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO(
               od.producto.nombre,
               SUM(od.cantidad)
           )
    FROM OrdenDetalle od
    WHERE od.orden.estado <> 'CANCELADO'
      AND (:fechaInicio IS NULL OR od.orden.fecha_creacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR od.orden.fecha_creacion <= :fechaFin)
    GROUP BY od.producto.nombre
    ORDER BY SUM(od.cantidad) DESC
    """)
    List<ProductoCantidadDTO> contarCantidadPorProducto(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

}
