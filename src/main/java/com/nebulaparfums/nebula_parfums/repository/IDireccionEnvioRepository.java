package com.nebulaparfums.nebula_parfums.repository;


import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDireccionEnvioRepository extends JpaRepository<DireccionEnvio, Integer> {
}
