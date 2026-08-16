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
    public List<OrdenDTO> getOrdenes();
    public Orden getOrdenById(Integer ordenId);
    public List<OrdenDTO> getOrdenesUsuario(Integer id);
    public OrdenDTO saveOrden(OrdenDTO orden);
    public void deleteOrden(Integer ordenId);
    public OrdenDTO editOrden(Integer id, OrdenDTO orden);
    public List<OrdenDTO> getUltimasOrdenesPendiente(Pageable pageable);
    public Page<OrdenDTO> filtrarOrden(Pageable pageable, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    public Double sumaTotalesMes(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    public ProductosPendientesProceso getPendientesProcesos();
    public List<OrdenDTO> listarDatosOrdenes(LocalDate fechaInicio, LocalDate fechaFin);

    byte[] exportarPedidosPdf(LocalDate fechaInicio, LocalDate fechaFin);
    byte[] exportarPedidosExcel(LocalDate fechaInicio, LocalDate fechaFin);

}
