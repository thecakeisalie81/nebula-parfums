package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IMovimientoInventarioService {
    MovimientoDTO saveMovimientoInventario(MovimientoDTO movimientoInventario);
    MovimientoDTO registrarSalida(MovimientoDTO movimientoInventario);
    MovimientoDTO registrarEntrada(MovimientoDTO movimientoInventario);
    MovimientoDTO registrarRegistroProducto(MovimientoDTO movimientoDTO);
    MovimientoDTO registrarEdicionProducto(MovimientoDTO movimientoDTO);
    List<MovimientoDTO> ultimosMovimientos(Integer limite);
    Page<MovimientoDTO> filtrarMovimientos(
            Pageable pageable,
            String producto,
            String tipo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    List<MovimientoDTO> filtrarMovimientosReportes(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    byte[] exportarMovimientosPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    byte[] exportarMovimientosExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
