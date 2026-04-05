package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;

import java.util.List;

public interface IMovimientoInventarioService {
    public List<MovimientoInventario> getMovimientoInventario();
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario);
}
