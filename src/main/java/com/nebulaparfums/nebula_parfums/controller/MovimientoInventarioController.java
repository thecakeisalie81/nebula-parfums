package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.service.IMovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovimientoInventarioController {
    @Autowired
    private IMovimientoInventarioService iMovimientoInventarioService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/movimiento/traer")
    public List<MovimientoInventario> getMovimientoInventario() {
        return iMovimientoInventarioService.getMovimientoInventario();
    }

    @PostMapping("/movimiento/crear")
    public String crearMovimientoInventario(@RequestBody MovimientoInventario movimientoInventario) {
        iMovimientoInventarioService.saveMovimientoInventario(movimientoInventario);
        return "Movimiento Inventario creado con sucesso";
    }
}
