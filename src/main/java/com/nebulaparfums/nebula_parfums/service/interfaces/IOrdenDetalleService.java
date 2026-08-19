package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdenDetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrdenDetalleService {
    OrdenDetalleDTO saveOrdenDetalle(OrdenDetalleDTO ordenDetalle);
    void deleteOrdenDetalleById(Integer id);
    OrdenDetalleDTO editOrdenDetalle(Integer id, OrdenDetalleDTO ordenDetalle);
    OrdenDetalle getOrdenDetalleById(Integer id);
    List<ProductoCantidadDTO> getProductoCantidadDTO(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                            @Param("fechaFin") LocalDateTime fechaFin);

    byte[] exportarVentasExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    byte[] exportarVentasPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
