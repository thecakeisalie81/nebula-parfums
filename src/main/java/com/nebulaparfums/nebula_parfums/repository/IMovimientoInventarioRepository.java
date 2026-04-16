package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IMovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.fecha_movimiento DESC")
    List<MovimientoInventario> ultimosMovimientoInventario(Pageable pageable);

    @Query("""
    SELECT m
    FROM MovimientoInventario m
    WHERE (:producto IS NULL OR LOWER(m.producto.nombre) LIKE LOWER(CONCAT('%', :producto, '%')))
      AND (:tipo IS NULL OR m.tipo_movimiento = :tipo)
      AND (:fechaInicio IS NULL OR m.fecha_movimiento >= :fechaInicio)
      AND (:fechaFin IS NULL OR m.fecha_movimiento <= :fechaFin)
          ORDER BY m.fecha_movimiento DESC""")
    Page<MovimientoInventario> filtrarMovimientos(
            Pageable pageable,
            @Param("producto") String producto,
            @Param("tipo") String tipo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("""
    SELECT m
    FROM MovimientoInventario m
    WHERE (:fechaInicio IS NULL OR m.fecha_movimiento >= :fechaInicio)
      AND (:fechaFin IS NULL OR m.fecha_movimiento <= :fechaFin)
          ORDER BY m.fecha_movimiento DESC""")
    List<MovimientoInventario> filtrarMovimientosReportes(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

}
