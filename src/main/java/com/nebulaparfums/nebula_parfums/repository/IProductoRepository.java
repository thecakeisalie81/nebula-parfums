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

    /**
     * Filtra productos según criterios opcionales.
     * <p>
     * La consulta admite múltiples filtros: nombre parcial, categoría, proveedor,
     * estado del stock (sin, bajo, suficiente), rango de precios y disponibilidad.
     * Si un parámetro es nulo, el filtro correspondiente no se aplica.
     * </p>
     *
     * @param pageable     información de paginación (número de página, tamaño, orden)
     * @param nombre       nombre parcial del producto (puede ser null)
     * @param idCategoria  identificador de la categoría (puede ser null)
     * @param idProveedor  identificador del proveedor (puede ser null)
     * @param estadoStock  estado del stock: "sin" (stock = 0), "bajo" (stock ≤ mínimo),
     *                     "suficiente" (stock > mínimo). Puede ser null.
     * @param precioMinimo precio mínimo para filtrar (puede ser null)
     * @param precioMaximo precio máximo para filtrar (puede ser null)
     * @param disponible   valor mínimo de stock actual para considerar disponible (puede ser null)
     * @return Page con productos filtrados según los criterios y metadatos de paginación
     */
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

    /**
     * Cuenta los productos cuyo stock actual es menor al mínimo,
     * pero mayor que cero (stock bajo).
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock_actual < p.stock_minimo AND p.stock_actual > 0")
    Integer countProductosConStockBajo();

    /**
     * Busca los productos que tengan bajo stock,
     * pero mayor que cero (stock bajo).
     */
    @Query("SELECT p FROM Producto p WHERE p.stock_actual < p.stock_minimo AND p.stock_actual > 0")
    Page<Producto> findProductosConStockBajo(Pageable pageable);

    /**
     * Cuenta cuantos productos que tienen 0 en stock
     * @return número de productos sin stock
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock_actual = 0")
    Integer countProductosSinStock();
}
