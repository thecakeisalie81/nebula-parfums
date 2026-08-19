package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.model.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrdenService {
    List<OrdenDTO> getOrdenes();
    Orden getOrdenById(Integer ordenId);
    List<OrdenDTO> getOrdenesUsuario(Integer id);
    OrdenDTO saveOrden(OrdenDTO orden);
    void deleteOrden(Integer ordenId);
    OrdenDTO editOrden(Integer id, OrdenDTO orden);
    List<OrdenDTO> getUltimasOrdenesPendiente(Pageable pageable);
    Page<OrdenDTO> filtrarOrden(Pageable pageable, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Double sumaTotalesMes(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    ProductosPendientesProceso getPendientesProcesos();
    List<OrdenDTO> listarDatosOrdenes(LocalDate fechaInicio, LocalDate fechaFin);

    byte[] exportarPedidosPdf(LocalDate fechaInicio, LocalDate fechaFin);
    byte[] exportarPedidosExcel(LocalDate fechaInicio, LocalDate fechaFin);

}
