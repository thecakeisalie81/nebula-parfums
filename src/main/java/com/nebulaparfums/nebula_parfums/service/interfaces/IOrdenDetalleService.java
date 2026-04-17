package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrdenDetalleService {
    public void saveOrdenDetalle(OrdenDetalle ordenDetalle);
    public void deleteOrdenDetalleById(Integer id);
    public void editOrdenDetalle(OrdenDetalle ordenDetalle);
    public OrdenDetalle getOrdenDetalleById(Integer id);
    public void createOrdenDetalle(OrdendetalleDTO detalleDTO);
    public List<ProductoCantidadDTO> getProductoCantidadDTO(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                            @Param("fechaFin") LocalDateTime fechaFin);

    byte[] exportarVentasExcel(LocalDate fechaInicio, LocalDate fechaFin);

    byte[] exportarVentasPdf(LocalDate fechaInicio, LocalDate fechaFin);
}
