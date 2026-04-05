package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.repository.IMovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioService implements IMovimientoInventarioService {

    @Autowired
    private IMovimientoInventarioRepository iMovimientoInventarioRepository;

    @Override
    public List<MovimientoInventario> getMovimientoInventario() {
        List<MovimientoInventario> listaMovimientos =  iMovimientoInventarioRepository.findAll();
        return listaMovimientos;
    }

    @Override
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario) {
        iMovimientoInventarioRepository.save(movimientoInventario);
    }
}
