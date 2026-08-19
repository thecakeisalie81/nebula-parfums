package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IMovimientoInventarioService {
    public Page<MovimientoDTO> getMovimientoInventario(Pageable pageable);
    public MovimientoDTO saveMovimientoInventario(MovimientoDTO movimientoInventario);
    public MovimientoDTO registrarSalida(MovimientoDTO movimientoInventario);
    public MovimientoDTO registrarEntrada(MovimientoDTO movimientoInventario);
    public MovimientoDTO registrarRegistroProducto(MovimientoDTO movimientoDTO);
    public MovimientoDTO registrarEdicionProducto(MovimientoDTO movimientoDTO);
    public List<MovimientoDTO> ultimosMovimientos(Integer limite);
    public Page<MovimientoDTO> filtrarMovimientos(
            Pageable pageable,
            String producto,
            String tipo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    public List<MovimientoDTO> filtrarMovimientosReportes(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    byte[] exportarMovimientosPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    byte[] exportarMovimientosExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
