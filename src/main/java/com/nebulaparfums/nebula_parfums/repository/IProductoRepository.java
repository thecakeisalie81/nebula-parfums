package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {


    @Query("""
    SELECT p
    FROM Producto p
    WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
      AND (:idCategoria IS NULL OR p.categoria.id_categoria = :idCategoria)
      AND (:idProveedor IS NULL OR p.proveedor.id_proveedor = :idProveedor)
      AND (
            :estadoStock IS NULL
            OR (:estadoStock = 'sin' AND p.stock_actual = 0)
            OR (:estadoStock = 'bajo' AND p.stock_actual > 0 AND p.stock_actual <= p.stock_minimo)
            OR (:estadoStock = 'suficiente' AND p.stock_actual > p.stock_minimo)
      )
      AND (:precioMinimo IS NULL OR p.precio >= :precioMinimo)
      AND (:precioMaximo IS NULL OR p.precio <= :precioMaximo)
      AND (:disponible IS NULL OR p.stock_actual > :disponible)
""")
    Page<Producto> filtrarProductos(
            Pageable pageable,
            @Param("nombre") String nombre,
            @Param("idCategoria") Integer idCategoria,
            @Param("idProveedor") Integer idProveedor,
            @Param("estadoStock") String estadoStock,
            @Param("precioMinimo") Integer precioMinimo,
            @Param("precioMaximo") Integer precioMaximo,
            @Param("disponible") Integer disponible
    );



    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE CONCAT('%', :nombre, '%')")
    Page<Producto> findByNombre(Pageable pageable, @Param("nombre") String nombre);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id_categoria = :idCategoria")
    List<Producto> findByCategoriaId_categoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock_actual < p.stock_minimo AND p.stock_actual > 0")
    Integer countProductosConStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.stock_actual < p.stock_minimo AND p.stock_actual > 0")
    List<Producto> findProductosConStockBajo(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock_actual = 0")
    Integer countProductosSinStock();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock_actual > 0")
    Integer countProductosConStock();
}
