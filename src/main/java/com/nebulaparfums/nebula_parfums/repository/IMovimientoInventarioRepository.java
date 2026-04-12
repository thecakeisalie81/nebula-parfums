package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.fecha_movimiento DESC")
    List<MovimientoInventario> ultimosMovimientoInventario(Pageable pageable);

}
