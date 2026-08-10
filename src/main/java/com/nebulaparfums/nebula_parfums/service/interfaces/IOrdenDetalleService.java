package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdenDetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrdenDetalleService {
    public OrdenDetalleDTO saveOrdenDetalle(OrdenDetalleDTO ordenDetalle);
    public void deleteOrdenDetalleById(Integer id);
    public OrdenDetalleDTO editOrdenDetalle(Integer id, OrdenDetalleDTO ordenDetalle);
    public OrdenDetalle getOrdenDetalleById(Integer id);
    public List<ProductoCantidadDTO> getProductoCantidadDTO(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                            @Param("fechaFin") LocalDateTime fechaFin);

    byte[] exportarVentasExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    byte[] exportarVentasPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
