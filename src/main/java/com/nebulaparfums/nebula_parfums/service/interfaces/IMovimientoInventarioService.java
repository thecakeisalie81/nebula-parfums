package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;

import java.util.List;

public interface IMovimientoInventarioService {
    public List<MovimientoInventario> getMovimientoInventario();
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario);
    public String registrarSalida(Integer productoId, int cantidad);
    public String registrarEntrada(Integer productoId, int cantidad);
    public String registrarRegistroProducto(MovimientoDTO movimientoDTO);
    public List<MovimientoInventario> ultimos5Movimientos();
}
