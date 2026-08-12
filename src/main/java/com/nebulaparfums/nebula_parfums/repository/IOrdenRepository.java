package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.model.Orden;
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
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {

    /**
     * Trae las órdenes pendientes de la base de datos de forma paginada
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @return Página de las órdenes con estado pendiente
     */
    @Query("SELECT o FROM Orden o WHERE estado = 'PENDIENTE' ORDER BY o.fecha_creacion DESC")
    List<Orden> ultimasOrdenesPendiente(Pageable pageable);

    /**
     *Filtra las órdenes por fecha y las devuelve como una lista
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return lista de DTO filtrados por fecha
     */
    @Query("""
    SELECT o
    FROM Orden o
    WHERE (:fechaInicio IS NULL OR o.fecha_creacion >= :fechaInicio)
      AND (:fechaFin IS NULL OR o.fecha_creacion <= :fechaFin)
    ORDER BY o.fecha_creacion DESC
    """)
    List<OrdenDTO> findOrdenesByFecha(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );


    /**
     * Filtra las órdenes por diferentes atributos y los devuelve de forma paginada
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param estado estado actual de la orden
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return  Página de las órdenes filtradas
     */
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

    /**
     *Devuelve todas las órdenes que ha realizado un usuario en forma de lista
     * @param idUsuario id del usuario que hizo la orden
     * @return lista de órdenes de un usuario
     */
    @Query("SELECT o FROM Orden o WHERE o.usuario.id_usuario = :idUsuario ORDER BY o.fecha_creacion DESC")
    List<Orden> getOrdenesUsuario(@Param("idUsuario") Integer idUsuario);


    /**
     *Devuelve el total ganado en un rango de tiempo tomando en cuenta las órdenes finalizadas
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Suma de las ganancias por el rango de fecha
     */
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

    /**
     * Cuenta cuantas órdenes hay que tengan el estado que se le pasa como parámetro
     * @param estado estado de la orden
     * @return cantidad de órdenes del estado
     */
    Integer countByEstado(String estado);
}
