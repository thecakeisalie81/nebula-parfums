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
