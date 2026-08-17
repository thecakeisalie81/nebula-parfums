package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.OrdenDetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ordendetalle")
public class OrdenDetalleController {
    @Autowired
    private IOrdenDetalleService iOrdenDetalleService;

    @GetMapping("/buscar")
    public ResponseEntity<OrdenDetalleDTO> buscarOrdenDetalleById(@RequestParam Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Mapper.toDTO(iOrdenDetalleService.getOrdenDetalleById(id)));
    }

    @PostMapping("/crear")
    public ResponseEntity<OrdenDetalleDTO> crearOrdenDetalle(@RequestBody OrdenDetalleDTO ordendetalleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iOrdenDetalleService.saveOrdenDetalle(ordendetalleDTO));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<OrdenDetalleDTO> editarOrdenDetalle(@PathVariable Integer id, @RequestBody OrdenDetalleDTO ordenDetalle) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenDetalleService.editOrdenDetalle(id, ordenDetalle));
    }

    @GetMapping("/contar")
    public ResponseEntity<List<ProductoCantidadDTO>> getProductoCantidadDTO(@RequestParam(required = false) LocalDateTime fechaInicio,
                                                            @RequestParam(required = false) LocalDateTime fechaFinal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenDetalleService.getProductoCantidadDTO(fechaInicio, fechaFinal));
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarOrdenDetalle(@PathVariable Integer id) {
        iOrdenDetalleService.deleteOrdenDetalleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Detalle de la orden eliminada");
    }
}
