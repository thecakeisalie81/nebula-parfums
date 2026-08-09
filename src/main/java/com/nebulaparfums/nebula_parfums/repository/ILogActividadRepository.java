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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ILogActividadRepository extends JpaRepository<LogActividad, Integer> {
    /**
     *Cuenta la cantidad de logs que hay registrations en la fecha actual
     * @return Número de logs registrados entre esas fechas
     */
    @Query("""
    SELECT COUNT(l)
    FROM LogActividad l
    WHERE DATE(l.fecha_actualizacion) = CURRENT_DATE
    """)
    long contarLogsHoy();

    /**
     * Filtra los logs guardados por fecha y tipo de accion, y los devuelve paginados
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param accion tipo de acción realizada que tiene registrado el log
     * @param fechaInicio fecha inicial de filtrado
     * @param fechaFin fecha final del filtrado
     * @return Página con los logs que coinciden con el filtrado
     */
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


    /**
     * Filtra los logs guardados por fecha y los devuelve todos
     * @param fechaInicio fecha inicial de filtrado
     * @param fechaFin fecha final del filtrado
     * @return Lista con todos los logs en el rango de fechas
     */
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
