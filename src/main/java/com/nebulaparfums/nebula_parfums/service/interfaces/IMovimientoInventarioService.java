package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IMovimientoInventarioService {
    public Page<MovimientoInventario> getMovimientoInventario(Pageable pageable);
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario);
    public String registrarSalida(Integer productoId, int cantidad);
    public String registrarEntrada(Integer productoId, int cantidad);
    public String registrarRegistroProducto(MovimientoDTO movimientoDTO);
    public List<MovimientoInventario> ultimos5Movimientos();
    public Page<MovimientoInventario> filtrarMovimientos(
            Pageable pageable,
            String producto,
            String tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}
