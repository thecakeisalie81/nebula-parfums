package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MovimientoInventarioController {
    @Autowired
    private IMovimientoInventarioService iMovimientoInventarioService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/movimiento/traer")
    public Page<MovimientoInventario> getMovimientoInventario(
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Pageable pageable
    ) {
        return iMovimientoInventarioService.filtrarMovimientos(
                pageable,
                producto,
                tipo,
                fechaInicio,
                fechaFin
        );
    }

    @GetMapping("/movimiento/ultimos")
    public List<MovimientoInventario> getUltimosMovimientoInventario() {
        return iMovimientoInventarioService.ultimos5Movimientos();
    }

    @PostMapping("/movimiento/salida")
    public ResponseEntity<?> movimietoSalida(@RequestBody MovimientoDTO movimientoDTO) {
        iMovimientoInventarioService.registrarSalida(movimientoDTO.getId_producto(), movimientoDTO.getCantidad());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/movimiento/entrada")
    public ResponseEntity<?> movimietoEntrada(@RequestBody MovimientoDTO movimientoDTO) {
        iMovimientoInventarioService.registrarEntrada(movimientoDTO.getId_producto(), movimientoDTO.getCantidad());
        return ResponseEntity.ok().build();
    }
}
