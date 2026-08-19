package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimiento")
public class MovimientoInventarioController {
    @Autowired
    private IMovimientoInventarioService iMovimientoInventarioService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @GetMapping("/movimientos")
    public ResponseEntity<Page<MovimientoDTO>> getMovimientoInventario(
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fechaFin,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(iMovimientoInventarioService.filtrarMovimientos(
                pageable,
                producto,
                tipo,
                fechaInicio,
                fechaFin
        ));
    }

    @GetMapping("/movimiento/ultimos")
    public ResponseEntity<List<MovimientoDTO>> getUltimosMovimientoInventario(Integer limite) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iMovimientoInventarioService.ultimosMovimientos(limite));
    }
}
