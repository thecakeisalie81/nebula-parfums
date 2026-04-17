package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.model.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {

    @Query("SELECT o FROM Orden o WHERE estado = 'PENDIENTE'")
    List<Orden> ultimasOrdenesPendiente(Pageable pageable);

    @Query("""
    SELECT o
    FROM Orden o
    WHERE (:estado IS NULL OR o.estado = :estado)
      AND (:fechaInicio IS NULL OR o.fecha_creacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR o.fecha_creacion <= :fechaFin)
    ORDER BY o.fecha_creacion DESC
    """)
    Page<Orden> filtrarOrden(
                Pageable pageable,
                @Param("estado") String estado,
                @Param("fechaInicio") LocalDateTime fechaInicio,
                @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT o FROM Orden o WHERE o.usuario.id_usuario = :idUsuario ORDER BY o.fecha_creacion DESC")
    List<Orden> getOrdenesUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("""
    SELECT COALESCE(SUM(o.total), 0)
    FROM Orden o
    WHERE o.estado <> 'CANCELADO'
      AND (:fechaInicio IS NULL OR o.fecha_creacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR o.fecha_creacion < :fechaFin)
    """)
    Double sumaTotalesMesActual(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    Integer countByEstado(String s);
}
