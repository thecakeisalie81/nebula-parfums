package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.CreateOrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.model.Orden;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrdenService {
    public List<Orden> getOrdenes();
    public Orden getOrdenById(Integer ordenId);
    public List<Orden> getOrdenesUsuario(Integer id);
    public void saveOrden(Orden orden);
    public void deleteOrden(Integer ordenId);
    public void editOrden(OrdenDTO orden);
    public void crearOrden(CreateOrdenDTO createOrden);
    public List<Orden> getUltimasOrdenesPendiente();
    public Page<Orden> filtrarOrden(Pageable pageable,
                                      @Param("estado") String estado,
                                      @Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);
    public Double sumaTotalesMes(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    public ProductosPendientesProceso getPendientesProcesos();
}
