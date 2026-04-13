package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ILogActividadRepository extends JpaRepository<LogActividad, Integer> {
    @Query("""
    SELECT l
    FROM LogActividad l
    WHERE (:accion IS NULL OR LOWER(l.accion) LIKE LOWER(CONCAT('', :accion, '')))
      AND (:fechaInicio IS NULL OR l.fecha_actualizacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR l.fecha_actualizacion <= :fechaFin)
          ORDER BY l.fecha_actualizacion DESC
    """)
    Page<LogActividad> filtrarLogActividades(
            Pageable pageable,
            @Param("accion") String accion,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("""
    SELECT l
    FROM LogActividad l
    WHERE (:fechaInicio IS NULL OR l.fecha_actualizacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR l.fecha_actualizacion <= :fechaFin)
          ORDER BY l.fecha_actualizacion DESC
    """)
    List<LogActividad> filtrarLogsPDF(@Param("fechaInicio") LocalDateTime fechaInicio,
                                      @Param("fechaFin") LocalDateTime fechaFin);

}
